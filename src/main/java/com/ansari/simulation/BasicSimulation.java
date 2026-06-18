package com.ansari.simulation;

public class BasicSimulation {
    static void main(String[] args) {
        Body sun = new Body(1.0e12, 0, 0, 0, 0);
        Body earth = new Body(1.0e8, 150, 0, 0, 20);

        //run the simulation for 5 time steps (dt=1)
        double dt = 1.0;
        for(int step=1 ; step<=5 ; step++){

            //Step 1: reset forces
            sun.resetForce();
            earth.resetForce();

            //Step 2: calculate mutual gravitational pull
            sun.addForce(earth);
            earth.addForce(sun);

            //Step 3: update positions based on forces
            sun.update(dt);
            earth.update(dt);

            //Step 4: print output to check whether the engine works properly
            System.out.printf("Step %d | Earth Position: X = %.2f, Y = %.2f | Earth velocity: Vx = %.2f, Vy = %.2f",
                    step, earth.x, earth.y, earth.vx, earth.vy);
            System.out.println();
        }

    }
}
