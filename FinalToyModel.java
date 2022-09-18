// This code was written for a Bachelor's thesis and provides a Java program to run the final toy model described there. 
// The graphic shows the evolution of threads as lines in the plane.
// Author: Anna Peter 
import java.util.ArrayList;

import javax.swing.JFrame;

import java.awt.Graphics;

import javax.swing.*;

// A utility class which can detect if a number is a Fibonacci number.
class Fibonacci { 
    // returns true if x is perfect square
    private static boolean isPerfectSquare(int x) {
        int s = (int) Math.sqrt(x);
        return (s*s == x);
    }
        
    // returns true if n is a Fibonacci Number, else false
    public static boolean isFibonacci(int n) {
        // n is Fibonacci if one of 5*n*n + 4 or 5*n*n - 4 or both is a perfect square
        return isPerfectSquare(5*n*n + 4) ||
                isPerfectSquare(5*n*n - 4);
    }
}

// The class Row represents rows in the evolution of threads, where one row corresponds to a horizontal line in the space-time diagram.
class Row {
    // We store x and y components and slopes of every starting point and evolve these as threads merge. 
    // Previous x and y positions are included to draw the threads.
    ArrayList<Integer> y;
    ArrayList<Double> x, slopes_numerators, slopes_denominators;
    ArrayList<Integer> thicknesses;
    ArrayList<Integer> last_y;
    ArrayList<Double> last_x;

    public Row(int n, double r) {
            x = new ArrayList<Double>();
            y = new ArrayList<Integer>();
            slopes_numerators = new ArrayList<Double>();
            slopes_denominators = new ArrayList<Double>();
            thicknesses = new ArrayList<Integer>();
            last_x = new ArrayList<Double>();
            last_y = new ArrayList<Integer>();

            for (int i=2; i<=n; i++) {
                x.add(i*20.0); //starting points are spaced out 20 pixels
                y.add(0);
                slopes_numerators.add( Math.floor(r*i ) - Math.floor(r*(i-1)) ); //slopes are the the difference of successive terms in lower wythoff sequence
                slopes_denominators.add(1.0);
                thicknesses.add(1);
                last_x.add(i*20.0);
                last_y.add(0);
            }

    }
    public void update() {
        // update x and y positions
        for (int i=0; i< x.size(); i++) {
            double next_x = x.get(i)+ slopes_numerators.get(i)/slopes_denominators.get(i);
            int next_y = y.get(i) +  1;
            last_x.set(i, x.get(i));
            last_y.set(i,y.get(i));
            x.set(i, next_x);
            y.set(i, next_y);
        }
        // Check if threads collide and merge the slopes by adding the numerators and denominators.
        // After merging two threads, we remove them and only keep the merged thread.
        for (int i=0; i<x.size()-1; i++) {
            if (x.get(i) >= x.get(i+1) ) {
                    int next_thickness = thicknesses.get(i) + thicknesses.get(i+1);
                    double next_slope_numerator = slopes_numerators.get(i+1) + slopes_numerators.get(i);
                    double next_slope_denominator = slopes_denominators.get(i+1) + slopes_denominators.get(i);
                    slopes_numerators.set(i, next_slope_numerator);
                    slopes_denominators.set(i, next_slope_denominator);
                    thicknesses.set(i, next_thickness);
                    x.remove(i+1);
                    y.remove(i+1);
                    last_x.remove(i+1);
                    last_y.remove(i+1);
                    slopes_numerators.remove(i+1);
                    slopes_denominators.remove(i+1);
                    thicknesses.remove(i+1);
            }
        }
     
    } 

}

// Here run the slopes system and draw the threads.
// Finetune how many starting threads, how many iterations, and how many test cases here.
class MyCanvas extends JComponent {
    // This is just to color thicknesses which aren't Fibonacci in red.
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public void paint(Graphics g) {
        double r = (1.0 + Math.sqrt(5) )/2.0;

        runThreads(r, 50, 10000, g, true, false, true, true);

        // run threads for i=1 to i=100 starting threads
         for (int i=1;i<=100; i+=1) {
            runThreads(r, i, 100000, g, false, false, true, false);
        } 
    }
    /*
     * @param r: tells us the ratio (like power of golden ratio, other number) to use for the slopes
     * @param count_fib_flag: tells us if we want to print output of how many final threads, how many Fibonacci numbers
     * @param fib_output_flag: tells us if we want to print output of the final thread thicknesses, where non-Fibonacci nrs. are printed red
     * @param info_flag: tells us if we want to print information of how many starting threads, iterations and final threads (best combined with fib_output_flag)
     * 
     * count_fib: startingThreads; numIterations; num_finalThreads; count_fib
     * fib_output: startingThreads; all resulting thicknesses
     * 
     */
    private void runThreads(double r, int startingThreads, int numIterations, Graphics g, boolean draw, boolean count_fib_flag, boolean fib_output_flag, boolean info_flag) {
        Row row = new Row(startingThreads, r);

        //draw the first 800 iterations, only if the flag "draw" is set to true
        for (int i=0;i<numIterations;i++) {
            if (i < 800 && draw) {
                for (int k=0; k < row.x.size(); k++) {
                    g.drawLine((int) Math.round(row.last_x.get(k)), row.last_y.get(k), (int) Math.round(row.x.get(k)), row.y.get(k)); /*2 is for better visualization */
                }
            }  
            row.update();
        }
        if (info_flag)
            System.out.printf("Slopes are multiples of %f. For %d starting threads and %d iterations, we have %d final threads \n",r, startingThreads,numIterations, row.x.size());
        int count_fib=0;

        if (fib_output_flag)
            System.out.print(startingThreads + "; ");
        for (int k=0; k < row.x.size(); k++) { 
            int next = row.thicknesses.get(k);
            if (Fibonacci.isFibonacci(next)) {
                if (fib_output_flag)
                    System.out.print(next + "; ");
                count_fib++;
            }
            else {
                if (fib_output_flag)
                    System.out.print(ANSI_RED + next + ANSI_RESET + "; ");
            }   
        }
        // column names: initial threads, iterations, final threads, count_fib 
        if (count_fib_flag)
            System.out.printf("%d; %d; %d; %d; ", startingThreads,numIterations,row.x.size(), count_fib);
        System.out.println();
    }

}


public class FinalToyModel {
    
  public static void main(String[] a) {
    JFrame window = new JFrame();
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setBounds(30, 30, 1200, 700);
    window.getContentPane().add(new MyCanvas());
    window.setVisible(true);
  }
}
