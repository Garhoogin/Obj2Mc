package com.garhoogin.obj2minecraft;

/**
 * The {@code Line} class defines a line that goes through two points. The class
 * encapsulates these two points.
 * 
 * @author Declan Moore
 */
public class Line {
    
    /**
     * Point 1.
     */
    public Vec3 q1;
    
    /**
     * Point 2.
     */
    public Vec3 q2;
    
    
    /**
     * Create a line through two 3-dimensional points.
     * 
     * @param q1 a point
     * @param q2 another point
     */
    public Line(Vec3 q1, Vec3 q2){
        this.q1 = q1;
        this.q2 = q2;
    }
    
}
