package com.ansari.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultithreadingSimulation {
    static void main(String[] args) {
        int NUM_BODIES = 15000;
        int STEPS = 10;
        double dt = 1.0;
        long totalTime = 0;

        List<Body> bodies = new ArrayList<>();
        Random rand = new Random();

        //generating galaxy of random bodies
        System.out.println("Generating "+NUM_BODIES+" bodies in the galaxy.");
        for(int i=0 ; i<NUM_BODIES ; i++){
            double mass = rand.nextDouble()*1.0e10;
            double x = rand.nextDouble()*1000;
            double y = rand.nextDouble()*1000;
            double vx = rand.nextDouble()*2-1;
            double vy = rand.nextDouble()*2-1;
            bodies.add(new Body(mass, x, y, vx, vy));
        }

        //---------Multi-threading setup------------
        //Step 1: asking system no of hw threads available
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Booting Engine with "+cores+" CPU Threads....\n");

        //Step 2: Create a Thread Pool to manage our parallel tasks
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        //Step 3: Run the simulation
        for(int step=1 ; step<=STEPS ; step++){
            long startTime = System.currentTimeMillis();

            //resetting forces
            for(Body b : bodies){
                b.resetForce();
            }

            //---The Concurrent Calculation---
            List<Callable<Void>> tasks = new ArrayList<>();
            int chunkSize = NUM_BODIES/cores;

            //dividing the bodies array into chunks
            for(int i=0 ; i<cores ; i++){
                final int start = i*chunkSize;
                //ensure the last thread picks up any remaining bodies
                final int end = (i==cores-1) ? NUM_BODIES : (i+1)*chunkSize;

                tasks.add(()-> {
                    for(int j=0 ; j<end ; j++){
                        for(int k=0 ; k<bodies.size() ; k++){
                            if(j!=k){
                                bodies.get(j).addForce(bodies.get(k));
                            }
                        }
                    }
                    return null;
                });
            }

            //executing all tasks simul. and waiting for them to finish
            try {
                executor.invokeAll(tasks);
            }catch(Exception ex){
                ex.getStackTrace();
            }

            //update positions based on the newly calculated forces
            for(Body b : bodies){
                b.update(dt);
            }

            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            totalTime += timeTaken;
            System.out.println("Step "+step+" | Calculated "+(NUM_BODIES*NUM_BODIES)+
                    " interactions in "+timeTaken+"ms");
        }
        System.out.println("Average time taken in each step: "+totalTime/STEPS+"ms");
    }
}
