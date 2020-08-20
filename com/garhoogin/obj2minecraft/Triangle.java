package com.garhoogin.obj2minecraft;

import java.awt.Color;


/**
 * This class represents a triangle in a 3D space.
 * 
 * @author Declan Moore
 */
public class Triangle {
    
    /**
     * point 1
     */
    public Vec3 a;
    
    /**
     * texture coordinate 1
     */
    public Coord tc1;
    
    /**
     * point 2
     */
    public Vec3 b;
    
    /**
     * texture coordinate 2
     */
    public Coord tc2;
    
    /**
     * point 3
     */
    public Vec3 c;
    
    /**
     * texture coordinate 3
     */
    public Coord tc3;
    
    /**
     * surface normal
     */
    public Vec3 normal;
    
    /**
     * texture name
     */
    public String texture;
    
    /**
     * triangle color
     */
    public Color color;
    
    private Vec3 minVector;
    private Vec3 maxVector;
    private Vec3 ca;
    private Vec3 ba;
    private Vec3 bacrossca;
    private float invAreaABC;
    
    
    /**
     * Create a new instance of {@code Triangle} from three points.
     * 
     * @param v1 point 1
     * @param v2 point 2
     * @param v3 point 3
     */
    public Triangle(Vec3 v1, Vec3 v2, Vec3 v3){
        this(v1, new Coord(0, 0), v2, new Coord(0, 0), v3, new Coord(0, 0), null, null, null);
    }
    
    
    /**
     * Create a new instance of {@code Triangle} from three points and a color.
     * 
     * @param v1 point 1
     * @param v2 point 2
     * @param v3 point 3
     * @param c  the color
     */
    public Triangle(Vec3 v1, Vec3 v2, Vec3 v3, Color c){
        this(v1, new Coord(0, 0), v2, new Coord(0, 0), v3, new Coord(0, 0), null, c, null);
    }
    
    
    /**
     * Create a new instance of {@code Triangle} from three points, three
     * texture coordinates, and a texture name.
     * 
     * @param v1      point 1
     * @param tc1     texture coordinate 1
     * @param v2      point 2
     * @param tc2     texture coordinate 2
     * @param v3      point 3
     * @param tc3     texture coordinate 3
     * @param texture the texture name
     */
    public Triangle(Vec3 v1, Coord tc1, Vec3 v2, Coord tc2, Vec3 v3, Coord tc3, String texture){
        this(v1, tc1, v2, tc2, v3, tc3, null, null, texture);
    }

    
    private Triangle(Vec3 v1, Coord tc1, Vec3 v2, Coord tc2, Vec3 v3, Coord tc3, Vec3 normal, Color color, String texture){
        this.a = v1;
        this.b = v2;
        this.c = v3;
        this.color = color;
        this.tc1 = tc1;
        this.tc2 = tc2;
        this.tc3 = tc3;
        this.texture = texture;
        if(normal != null) this.normal = normal;
        else this.normal = this.b.subtract(this.a).cross(this.c.subtract(this.a)).normalize();
        this.maxVector = new Vec3(Math.max(Math.max(v1.x, v2.x), v3.x), Math.max(Math.max(v1.y, v2.y), v3.y), Math.max(Math.max(v1.z, v2.z), v3.z));
        this.minVector = new Vec3(Math.min(Math.min(v1.x, v2.x), v3.x), Math.min(Math.min(v1.y, v2.y), v3.y), Math.min(Math.min(v1.z, v2.z), v3.z));
        this.ca = this.c.subtract(this.a);
        this.ba = this.b.subtract(this.a);
        this.bacrossca = this.ba.cross(this.ca);
        this.invAreaABC = 1.0f / this.normal.dot(this.bacrossca);
    }
    
    
    /**
     * Get the lower coordinate of the bounding box of multiple triangles.
     * 
     * @param t the array of triangles
     * @return  the lower coordinate of a box that contains all the given
     *          triangles
     */
    public static Vec3 getMin(Triangle[] t){
        Vec3 minCoord = new Vec3(0, 0, 0);
        for(int i = 0; i < t.length; i++){
            Triangle c = t[i];
            if(i == 0) minCoord = c.minVector;
            else minCoord = minCoord.min(c.minVector);
        }
        return minCoord;
    }
    
    
    /**
     * Get the upper coordinate of the bounding box of multiple triangles.
     * 
     * @param t the array of triangles
     * @return  the greater coordinate of a box that contains all the given
     *          triangles
     */
    public static Vec3 getMax(Triangle[] t){
        Vec3 maxCoord = new Vec3(0, 0, 0);
        for(int i = 0; i < t.length; i++){
            Triangle c = t[i];
            if(i == 0) maxCoord = c.maxVector;
            else maxCoord = maxCoord.max(c.maxVector);
        }
        return maxCoord;
    }
    
    
    /**
     * Gets the point at which a {@code Line} will intersect this triangle. This
     * method does not check bounds, so the point may lie outside the triangle
     * if the line does not intersect the triangle.
     * 
     * @param line the line going through the triangle
     * @return     the point of intersection between the line and this triangle
     */
    public Vec3 intersection(Line line){
        float tp = -bacrossca.dot(line.q1.subtract(a)) / bacrossca.dot(line.q2.subtract(line.q1));
        return line.q1.add(line.q2.subtract(line.q1).multiply(tp));
    }
    
    
    /**
     * Get the position of the closest point on this triangle to another point.
     * 
     * @param point the input point
     * @return      the closest point on this triangle to the input point
     */
    public Vec3 perpendicular(Vec3 point){
        return intersection(new Line(point, point.add(bacrossca)));
    }
    
    
    /**
     * Get the barycentric coordinates for a point on this triangle.
     * 
     * @param p the input point
     * @return  the input point in barycentric coordinates for this triangle
     */
    public Vec3 getBarycentric(Vec3 p){
        float areaPBC = normal.dot(b.subtract(p).cross(c.subtract(p)));
        float areaPCA = normal.dot(c.subtract(p).cross(a.subtract(p)));
        
        float x = areaPBC * invAreaABC;
        float y = areaPCA * invAreaABC;
        return new Vec3(x, y, 1.0f - x - y);
    }
    
    
    /**
     * Get the texture coordinate from a point on this triangle in barycentric
     * coordinates.
     * 
     * @param barycentric the input point
     * @return            the interpolated texture coordinates
     */
    public Coord getTexCoord(Vec3 barycentric){
        float u = tc1.x * barycentric.x + tc2.x * barycentric.y + tc3.x * barycentric.z;
        float v = tc1.y * barycentric.x + tc2.y * barycentric.y + tc3.y * barycentric.z;
        return new Coord(u, v);
    }
    
    
    /**
     * Creates a new instance of {@code Triangle} which is identical to the
     * input triangle, except that it has a flipped normal.
     * 
     * @param t the input triangle
     * @return  the input triangle but flipped
     */
    public static Triangle flip(Triangle t){
        return new Triangle(t.b, t.tc2, t.a, t.tc1, t.c, t.tc3, t.normal.invert(), t.color, t.texture);
    }
    
    /**
     * Get the lower coordinate of a bounding box that contains this triangle.
     * 
     * @return the lower coordinate of a bounding box that contains this
     *         triangle
     */
    public Vec3 getMinimum(){
        return minVector;
    }
    
    
    /**
     * Get the higher coordinate of a bounding box that contains this triangle.
     * 
     * @return the higher coordinate of a bounding box that contains this
     *         triangle
     */
    public Vec3 getMaximum(){
        return maxVector;
    }
    
    
    /**
     * Get a textual representation of this {@code Triangle}.
     * 
     * @return a textual representation of this triangle.
     */
    @Override
    public String toString(){
        return a + " " + b + " " + c;
    }
}
