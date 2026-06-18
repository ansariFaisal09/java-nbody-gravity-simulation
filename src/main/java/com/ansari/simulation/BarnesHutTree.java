package com.ansari.simulation;

public class BarnesHutTree {
    private Quad quad;
    private Body body; //represents single body or combined center of mass
    private BarnesHutTree NW, NE, SW, SE; //four child quadrants

    //The threshold for Barnes-Hut, 0.5 is the industry-standard
    //it determines when a cluster of bodies is far enough to be treated as one single body
    private static final double THETA = 0.5;

    public BarnesHutTree(Quad quad) {
        this.quad = quad;
        this.body = null;
    }

    //Step 1: building the tree by inserting the bodies one by one
    public void insert(Body b){
        //if this space is empty, put the body here
        if(this.body==null){
            this.body = b;
            return;
        }

        //if this space is a branch (already subdivided)
        if(!isExternal()){
            //update the COM for this entire branch
            double newMass = this.body.mass + b.mass;
            double newX = (this.body.x*this.body.mass + b.x*b.mass)/newMass;
            double newY = (this.body.y*this.body.mass + b.y+b.mass)/newMass;
            this.body = new Body(newMass, newX, newY, 0, 0); //update virtual body

            putBodyInQuadrant(b);
        }

        //if this space is a leaf, but already contains a body, requires to subdivide
        else{
            //if bodies are at exact same position then return, means prevent from crashing
            if(this.body.x == b.x && this.body.y==b.y) return;

            //create the four child nodes
            NW = new BarnesHutTree(quad.NW());
            NE = new BarnesHutTree(quad.NE());
            SW = new BarnesHutTree(quad.SW());
            SE = new BarnesHutTree(quad.SE());

            putBodyInQuadrant(this.body);

            //update this node to become the center of mass (COM)
            double newMass = this.body.mass + b.mass;
            double newX = (this.body.x*this.body.mass + b.x*b.mass)/newMass;
            double newY = (this.body.y*this.body.mass + b.y+b.mass)/newMass;
            this.body = new Body(newMass, newX, newY, 0, 0); //update virtual body

            //put the body in the quadrant
            putBodyInQuadrant(b);
        }
    }

    private void putBodyInQuadrant(Body b){
        if(NW.quad.contains(b)) NW.insert(b);
        else if(NE.quad.contains(b)) NE.insert(b);
        else if(SW.quad.contains(b)) SW.insert(b);
        else if(SE.quad.contains(b)) SE.insert(b);
    }

    public boolean isExternal(){
        return (NW==null && NE==null && SW==null && SE==null);
    }

    //Step 2: Calculating the gravity using the tree optimization
    public void updateForce(Body target){
        if(this.body==null || target==this.body) return;

        if(isExternal()){
            //calculate directly if its a single body
            target.addForce(this.body);
        }
        else{
            //calculate distance from target to this quadrant's COM
            double dx = this.body.x - target.x;
            double dy = this.body.y - target.y;
            double d = Math.sqrt(dx*dx + dy*dy);

            //Barnes-Hut condition: (width of region/distance)<THETA
            if((quad.length/d)<THETA){
                //the cluster is far enough to treat as one giant mass
                target.addForce(this.body);
            }
            else{
                //the cluster is too close, need to look at individual children
                NW.updateForce(target);
                NE.updateForce(target);
                SW.updateForce(target);
                SE.updateForce(target);
            }
        }
    }
}
