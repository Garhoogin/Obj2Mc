package com.garhoogin.obj2minecraft.world;

/**
 * The {@code Block} class contains simple information about a block, including
 * only its position in 3D space, and its name.
 * 
 * @author Declan
 */
public class Block {
    
    /**
     * the name of the block
     */
    public String name;
    
    /**
     * the x coordinate of the block
     */
    public int x;
    
    /**
     * the y coordinate of the block
     */
    public int y;
    
    /**
     * the z coordinate of the block
     */
    public int z;
    
    
    /**
     * Creates a new instance of {@code Block}.
     * 
     * @param name the name of the block
     * @param x    the x coordinate of the block
     * @param y    the y coordinate of the block
     * @param z    the z coordinate of the block
     */
    public Block(String name, int x, int y, int z){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
}
