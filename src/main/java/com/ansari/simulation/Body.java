package com.ansari.simulation;

public class Body {
    public static final double G = 6.674e-11;

    //specifying properties as public to reduce time to read/write using getter/setter
    public double mass;
    public double x, y;
    public double vx, vy;
    public double fx, fy;

    public Body(double mass, double x, double y, double vx, double vy) {
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.fx = 0.0;
        this.fy = 0.0;
    }

    //calculating the gravitational pull from another body and adding to the net force
    public void addForce(Body other){
        double dx = other.x - this.y;
        double dy = other.y - this.y;

        //straight line distance
        double distance = Math.sqrt(dx*dx + dy*dy);

        //prevent division by zero if bodies collide
        if(distance==0) return;

        //total gravitational force
        double force = (G*this.mass*other.mass)/(distance*distance);

        //resolve the force into x and y components
        this.fx += (force*dx)/distance;
        this.fy += (force*dy)/distance;
    }

    //updating velocity and position based on the accumulated net force
    //dt here is the time step, like 1 unit of time per frame
    public void update(double dt){
        double ax = fx/mass;
        double ay = fy/mass;

        //updating velocity (v=u+at ; assuming u=0)
        vx += ax*dt;
        vy += ay*dt;

        //updating position (s=vt)
        x += vx*dt;
        y += vy*dt;
    }

    //rests the net force at the start of every new calculation cycle
    public void resetForce(){
        fx = 0.0;
        fy = 0.0;
    }
}
