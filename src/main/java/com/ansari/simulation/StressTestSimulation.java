package com.ansari.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StressTestSimulation {
    static void main(String[] args) {
        //scaling upto 3000 bodies, means in O(N2) loop 9000000 force calc per step
        int NUM_BODIES = 15000;
        int STEPS = 10;
        double dt = 1.0;

        List<Body> bodies = new ArrayList<>();
        Random rand = new Random();
        long totalTime = 0;
        //generating galaxy of random bodies
        System.out.println("Generating "+NUM_BODIES+" bodies in the galaxy:");
        for(int i=0 ; i<NUM_BODIES ; i++){
            double mass = rand.nextDouble()*1.0e10;
            double x = rand.nextDouble()*1000;
            double y = rand.nextDouble()*1000;
            double vx = rand.nextDouble()*2-1;
            double vy = rand.nextDouble()*2-1;
            bodies.add(new Body(mass, x, y, vx, vy));
        }

        //simulating in brute-force way
        System.out.println("Starting brute-force simulation:");
        for(int step=1 ; step<=STEPS ; step++){
            long startTime = System.currentTimeMillis();

            //reset forces of every body
            for(Body b : bodies){
                b.resetForce();
            }

            //calculate forces
            for(int i=0 ; i<bodies.size() ; i++){
                for(int j=0 ; j<bodies.size() ; j++){
                    if(i!=j){
                        bodies.get(i).addForce(bodies.get(j));
                    }
                }
            }

            //updating positions for all bodies
            for(Body b : bodies){
                b.update(dt);
            }

            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            totalTime += timeTaken;
            System.out.println("Step "+step+" | Calculated "+(NUM_BODIES*NUM_BODIES)+
                    " interactions in "+timeTaken+"ms");
        }
        System.out.println("Average Time taken in each step: "+totalTime/STEPS+"ms");
    }
}
