// This code was written for a Bachelor's thesis and provides a Java program to run the golden ratio slopes system described there. 
// The graphic shows the evolution of threads as lines in the plane.
// Author: Anna Peter 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;

import java.awt.Graphics;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.Line2D;

// a utility class to quickly identify Fibonacci numbers
class Fibonacci { 
    // a utility method that returns true if x is perfect square
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
//the only method to be modified, really, is paint() (to see different outputs and console values)

// the class Row represents rows in the evolution of threads
// one row corresponds to a horizontal line in the space-time diagram 
class Row {
    // we store x,y components and slopes of every starting point and evolve these as threads merge
    ArrayList<Integer> y;
    ArrayList<Double> x, slopes;
    ArrayList<Integer> thicknesses;
    ArrayList<Integer> last_y;
    ArrayList<Double> last_x;

    public Row(int n, double r) {
        if (slopes == null) {
            x = new ArrayList<Double>();
            y = new ArrayList<Integer>();
            slopes = new ArrayList<Double>();
            thicknesses = new ArrayList<Integer>();
            last_x = new ArrayList<Double>();
            last_y = new ArrayList<Integer>();

            for (int i=1; i<=n; i++) {
                x.add(i*20.0); //starting points are spaced out 20 pixels
                y.add(0);
                slopes.add( (r*i ) % 1 ); //slopes are the fractional part of multiples of the golden ratio 
                thicknesses.add( 1); //initial thickness is 1
                last_x.add(i*20.0); //starting points are spaced out 20 pixels
                last_y.add(0);
            }
        }

    }
    public void update() {
        // update x and y positions
        for (int i=0; i< x.size(); i++) {
            double next_x = x.get(i)+ slopes.get(i);
            int next_y = y.get(i) +  1;
            last_x.set(i, x.get(i));
            last_y.set(i,y.get(i));
            x.set(i, next_x);
            y.set(i, next_y);
        }
        for (int i=0; i< x.size()-2; i++) {
            //3 threads merging
            if (x.get(i) >= x.get(i+1) && x.get(i) >= x.get(i+2) ) {
                int next_thickness = thicknesses.get(i) + thicknesses.get(i+1)+thicknesses.get(i+2);
                double next_slope = (slopes.get(i+1) + slopes.get(i)+ slopes.get(i+2) )/ 3.0;// resulting slope is the average
               // double next_slope = (thicknesses.get(i+1)*slopes.get(i+1) + thicknesses.get(i)* slopes.get(i))/next_thickness; //resulting slope is the weighted average
                // double next_slope = (slopes.get(i+2)*(i+2) + slopes.get(i+1)* (i+1) + slopes.get(i)*i )/ (3*i+3);// resulting slope is the weighted average by i 
                slopes.set(i, next_slope);
                thicknesses.set(i, next_thickness);
                x.remove(i+1);
                y.remove(i+1);
                last_x.remove(i+1);
                last_y.remove(i+1);
                slopes.remove(i+1);
                thicknesses.remove(i+1);
                x.remove(i+1);
                y.remove(i+1);
                last_x.remove(i+1);
                last_y.remove(i+1);
                slopes.remove(i+1);
                thicknesses.remove(i+1);
            }
            // 2 threads merging
            else if (x.get(i) >= x.get(i+1) ) {
                    int next_thickness = thicknesses.get(i) + thicknesses.get(i+1);
                    double next_slope = (slopes.get(i+1) + slopes.get(i) )/ 2.0;// resulting slope is the average
                    // double next_slope = (slopes.get(i)* thicknesses.get(i) + slopes.get(i+1)*thicknesses.get(i+1) )/ next_thickness;// resulting slope is the weighted average by thickness
                    // double next_slope = (slopes.get(i+1)* (i+1) + slopes.get(i)*i )/ (2*i+1);// resulting slope is the weighted average by i 
                    slopes.set(i, next_slope);
                    thicknesses.set(i, next_thickness);
                    x.remove(i+1);
                    y.remove(i+1);
                    last_x.remove(i+1);
                    last_y.remove(i+1);
                    slopes.remove(i+1);
                    thicknesses.remove(i+1);
            }
            //the last two threads merge
            else if (x.get(i+1) >= x.get(i+2) && (i+2 == x.size()-1)) {
                    int next_thickness = thicknesses.get(i+1) + thicknesses.get(i+2);
                    double next_slope = (slopes.get(i+1) + slopes.get(i+2) )/ 2.0;// resulting slope is the average
                    //  double next_slope = (slopes.get(i+1)* thicknesses.get(i+1) + slopes.get(i+2)*thicknesses.get(i+2) )/ next_thickness;// resulting slope is the weighted average by thickness
                    //  double next_slope = (slopes.get(i+1)* (i+1) + slopes.get(i+2)*(i+2) )/ (2*i+3);// resulting slope is the weighted average by i 
                    slopes.set(i+1, next_slope);
                    thicknesses.set(i+1, next_thickness);
                    x.remove(i+2);
                    y.remove(i+2);
                    last_x.remove(i+2);
                    last_y.remove(i+2);
                    slopes.remove(i+2);
                    thicknesses.remove(i+2);
            } 
        } 
    } 

}

// here we paint and run the threads
// finetune how many starting threads, how many iterations, and how many test cases here
class MyCanvas extends JComponent {
    // this is just to color thicknesses which aren't Fibonacci in red
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public void paint(Graphics g) {
        double r = (1.0 + Math.sqrt(5) )/2.0  ;
        // some other values for r 
        // double r_squared = Math.pow(r, 2) ; 
        // double r2 = Math.sqrt(2); // like in Wythoff paper, with a = 2
        // double r3 = (Math.sqrt(13) -1.0)/2.0     ; // like in Wythoff paper, with a = 3
        // double r4 = (Math.sqrt(5)- 1.0);
        // Random rand = new Random();
        // double randomValue = rand.nextDouble(); 
        
        //run threads for 30 starting threads and draw the output
        runThreads(r, 30, 10000, g, true, false, true, true);

        // run threads for i=1 to i=100 starting threads
         for (int i=1;i<=100; i+=1) {
            runThreads(r, i, 10000, g, false, true, false, false);
        } 
    }
    /*
     * @param r: tells us the ratio for the initial slope multiples
     * @param count_fib_flag: tells us if we want to print output of how many final threads, how many Fibonacci numbers
     * @param fib_output_flag: tells us if we want to print output of the final thread thicknesses, where non-Fibonacci numbers are printed red
     * @param info_flag: tells us if we want to print information of how many starting threads, iterations and final threads (best combined with fib_output_flag)
     * 
     * count_fib: startingThreads; numIterations; num_finalThreads; count_fib
     * fib_output: startingThreads; all resulting thicknesses
     */
    private void runThreads(double r, int startingThreads, int numIterations, Graphics g, boolean draw, boolean count_fib_flag, boolean fib_output_flag, boolean info_flag) {
        Row row = new Row(startingThreads, r);
        //draw the first 800 iterations, only if flag draw = true
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
        // to export the count of fibonacci nrs 
        // column names: initial threads, iterations, final threads, count_fib 
        if (count_fib_flag)
            System.out.printf("%d; %d; %d; %d; ", startingThreads,numIterations,row.x.size(), count_fib);
        System.out.println();
    }
}

public class GoldenRatioSlopesSystem {
    //this method is used to draw the graphics 
  public static void main(String[] a) {
    JFrame window = new JFrame();
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setBounds(30, 30, 1200, 700);
    window.getContentPane().add(new MyCanvas());
    window.setVisible(true);
  }
}
