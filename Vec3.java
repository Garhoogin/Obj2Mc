package com.garhoogin.obj2minecraft;

/**
 * The {@code Vec3} class holds a vector.
 * 
 * @author Declan Moore
 */
public class Vec3 {
    
    public float x;
    public float y;
    public float z;
    
    
    /**
     * Creates an instance of {@code Vec3} with the specified components.
     * 
     * @param x the x component
     * @param y the y component
     * @param z the z component
     */
    public Vec3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    
    /**
     * Adds another vector to this vector.
     * 
     * @param v2 the vector to add to this vector
     * @return   the result of the vector addition
     */
    public Vec3 add(Vec3 v2){
        return new Vec3(x + v2.x, y + v2.y, z + v2.z);
    }
    
    
    /**
     * Subtracts another vector from this vector.
     * 
     * @param v2 the vector to subtract from this vector
     * @return   the result of the vector subtraction
     */
    public Vec3 subtract(Vec3 v2){
        return new Vec3(x - v2.x, y - v2.y, z - v2.z);
    }
    
    
    /**
     * Scale this vector by a scalar quantity.
     * 
     * @param scalar the amount this vector is to be multiplied
     * @return       the result of the multiplication
     */
    public Vec3 multiply(float scalar){
        return new Vec3(x * scalar, y * scalar, z * scalar);
    }
    
    
    /**
     * Normalize this vector.
     * 
     * @return the normalized vector
     */
    public Vec3 normalize(){
        float len = (float) Math.sqrt(x * x + y * y + z * z);
        return new Vec3(x / len, y / len, z / len);
    }
    
    
    /**
     * Compute the cross product between this vector and a second vector.
     * 
     * @param v2 the vector to compute the cross product with
     * @return   the result of the vector cross product
     */
    public Vec3 cross(Vec3 v2){
        return new Vec3(y * v2.z - z * v2.y, z * v2.x - x * v2.z, x * v2.y - y * v2.x);
    }
    
    
    /**
     * Compute the dot product between this vector and a second vector.
     * 
     * @param v2 the vector to compute the dot product with
     * @return   the result of the vector dot product
     */
    public float dot(Vec3 v2){
        return x * v2.x + y * v2.y + z * v2.z;
    }
    
    
    /**
     * Finds the least of each component between this vector and a second
     * vector.
     * 
     * @param v2 the second vector
     * @return   a new vector containing the least components of both input
     *           vectors
     */
    public Vec3 min(Vec3 v2){
        return new Vec3(Math.min(x, v2.x), Math.min(y, v2.y), Math.min(z, v2.z));
    }
    
    
    /**
     * Finds the greatest of each component between this vector and a second
     * vector.
     * 
     * @param v2 the second vector
     * @return   a new vector containing the greatest components of both input
     *           vectors
     */
    public Vec3 max(Vec3 v2){
        return new Vec3(Math.max(x, v2.x), Math.max(y, v2.y), Math.max(z, v2.z));
    }
    
    
    /**
     * Negates this vector.
     * 
     * @return the negated vector
     */
    public Vec3 invert(){
        return new Vec3(-x, -y, -z);
    }
    
    
    /**
     * Get a string representation of this vector.
     * 
     * @return the string representation of this vector
     */
    @Override
    public String toString(){
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
