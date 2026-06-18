package com.ansari.simulation;

public class Quad {
    public double x, y; //center of the square
    public double length; //length of a side of square

    public Quad(double x, double y, double length) {
        this.x = x;
        this.y = y;
        this.length = length;
    }

    //checks if a specific body falls inside this square's boudaries
    public boolean contains(Body b){
        double halfLen = length/2.0;
        return (b.x >= this.x - halfLen && b.x <= this.x + halfLen &&
                b.y >= this.y - halfLen && b.y <= this.y + halfLen);
    }

    //Methods to subdivide the current square into four equal smaller ones
    public Quad NW(){
        return new Quad(x-length/4, y+length/4, length/2);
    }
    public Quad NE(){
        return new Quad(x+length/4, y+length/4, length/2);
    }
    public Quad SW(){
        return new Quad(x-length/4, y-length/4, length/2);
    }
    public Quad SE(){
        return new Quad(x+length/4, y-length/4, length/2);
    }
}
