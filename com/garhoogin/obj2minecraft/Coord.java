package com.garhoogin.obj2minecraft;

/**
 * The {@code Coord} class holds a 2-dimensional coordinate.
 * 
 * @author Declan Moore
 */
public class Coord {
    
    /**
     * The x coordinate.
     */
    public float x;
    
    /**
     * The y coordinate.
     */
    public float y;
    
    
    /**
     * Creates a new instance of {@code Coord} with the given coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Coord(float x, float y){
        this.x = x;
        this.y = y;
    }
    
    
    /**
     * Get the maximum of two coordinates' components.
     * 
     * @param c2 the second coordinate
     * @return   the maximum between this coordinate and the input coordinate
     * @since    1.0.1.0
     */
    public Coord max(Coord c2){
        return new Coord(Math.max(x, c2.x), Math.max(y, c2.y));
    }
    
    
    /**
     * Get the minimum of two coordinates' components.
     * 
     * @param c2 the second coordinate
     * @return   the minimum between this coordinate and the input coordinate
     * @since    1.0.1.0
     */
    public Coord min(Coord c2){
        return new Coord(Math.min(x, c2.x), Math.min(y, c2.y));
    }
    
}
