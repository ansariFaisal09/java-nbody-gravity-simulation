package com.ansari.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BarnesHutOptimizedSimulation {
    static void main(String[] args) {
        int NUM_BODIES = 15000;
        int STEPS = 10;
        double dt = 1.0;
        long totalTime=0;

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

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Booting Barnes-Hut Engine with " + cores + " CPU Threads...\n");
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        //Run the simulation
        for(int step=1 ; step<=STEPS ; step++){
            long startTime = System.currentTimeMillis();

            //Step 1: Reset forces and dynamically calculate the boundaries of the universe
            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

            for(Body b : bodies){
                b.resetForce();
                if(b.x<minX) minX=b.x;
                if(b.x>maxX) maxX=b.x;
                if(b.y<minY) minY=b.y;
                if(b.y>maxY) maxY=b.y;
            }

            //Step 2: Create the Root QuadTree with a slight buffer to prevent boundary glitches
            double universeWidth = Math.max(maxX-minX, maxY-minY);
            double centerX = (minX+maxX)/2.0;
            double centerY = (minY+maxY)/2.0;

            //we add 10 to the length of the universe to prevent boundary glitches
            Quad rootQuad = new Quad(centerX, centerY, universeWidth+10.0);
            BarnesHutTree tree = new BarnesHutTree(rootQuad);

            //Step 3: populate the tree with all the bodies (single-threaded)
            for(Body b : bodies){
                tree.insert(b);
            }

            //Step 4: Multithreaded Barnes-Hut calculation
            List<Callable<Void>> tasks = new ArrayList<>();
            int chunkSize = NUM_BODIES/cores;

            for(int i=0 ; i<cores ; i++){
                final int start = i * chunkSize;
                final int end = (i == cores - 1) ? NUM_BODIES : (i + 1) * chunkSize;

                tasks.add(() -> {
                    for (int j = start; j < end; j++) {
                        tree.updateForce(bodies.get(j));
                    }
                    return null;
                });
            }

            //Step 5: Execute all the threads
            try {
                executor.invokeAll(tasks);
            }catch(Exception ex){
                ex.getStackTrace();
            }

            //Step 6: Update actual positions based on new forces
            for(Body b : bodies){
                b.update(dt);
            }

            long endTime = System.currentTimeMillis();
            long timeTaken = endTime-startTime;
            System.out.println("Step " + step + " | QuadTree Optimization complete in "
                    + (endTime - startTime) + " ms");
            totalTime += timeTaken;
        }
        System.out.println("Average time taken in each step: "+totalTime/STEPS+"ms");
    }
}
