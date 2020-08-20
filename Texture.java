package com.garhoogin.obj2minecraft;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class is a container for a texture image.
 * 
 * @author Declan Moore
 */
public class Texture {
    
    /**
     * the width of this texture
     */
    public final int width;
    
    /**
     * the height of this texture
     */
    public final int height;
    
    /**
     * the {@code BufferedImage} backing this texture
     */
    private final BufferedImage image;
    
    /**
     * The name of this texture
     */
    public final String name;
    
    
    /**
     * Create a new instance of a texture.
     * 
     * @param i    the texture image
     * @param name the texture name
     */
    public Texture(BufferedImage i, String name){
        this.image = i;
        this.width = i.getWidth();
        this.height = i.getHeight();
        this.name = name;
    }
    
    /**
     * Sample the texture at a given x and y coordinate. The x and y coordinates
     * wrap around the width and height of the texture.
     * 
     * @param coordX the x coordinate
     * @param coordY the y coordinate
     * @return       the color at the specified coordinates
     */
    public Color sampleOne(int coordX, int coordY){
        coordX = ((coordX % width) + width) % width;
        coordY = ((coordY % height) + height) % height;
        int c = image.getRGB(coordX, coordY);
        return new Color((c >>> 16) & 0xFF, (c >>> 8) & 0xFF, c & 0xFF, (c >>> 24) & 0xFF);
    }
    
    static Color avg(Color[] colors){
        if(colors.length == 0) return new Color(0, 0, 0, 0);
        //first, find the average alpha. This is the easiest part.
        int a = 0;
        for(Color c : colors){
            a += c.getAlpha();
        }
        int totalAlpha = a;
        if(totalAlpha == 0) return new Color(0, 0, 0, 0);
        a = (a + (colors.length >>> 1)) / colors.length;
        //next, take weighted averages. each color's weight is its alpha. 
        int r = 0, g = 0, b = 0;
        for(Color c : colors){
            int ca = c.getAlpha();
            r += c.getRed() * ca;
            g += c.getGreen() * ca;
            b += c.getBlue() * ca;
        }
        //divide r,g,b by the total alpha
        r = (r + (totalAlpha >>> 1)) / totalAlpha;
        g = (g + (totalAlpha >>> 1)) / totalAlpha;
        b = (b + (totalAlpha >>> 1)) / totalAlpha;
        return new Color(r, g, b, a);
    }
    
    /**
     * Sample a 3x3 area of the texture at the given UV coordinates.
     * 
     * @param u the U coordinate
     * @param v the V coordinate
     * @return  the average color of the 3x3 area around the UV coordinate
     */
    public Color sample(float u, float v){
        int coordX = (int) (u * width);
        int coordY = (int) (v * height);
        Color[] colors = new Color[9];
        colors[0] = sampleOne(coordX - 1, coordY - 1);
        colors[1] = sampleOne(coordX, coordY - 1);
        colors[2] = sampleOne(coordX + 1, coordY - 1);
        colors[3] = sampleOne(coordX - 1, coordY);
        colors[4] = sampleOne(coordX, coordY);
        colors[5] = sampleOne(coordX + 1, coordY);
        colors[6] = sampleOne(coordX - 1, coordY + 1);
        colors[7] = sampleOne(coordX, coordY + 1);
        colors[8] = sampleOne(coordX + 1, coordY + 1);
        return avg(colors);
    }
    
    /**
     * Read a texture from a file given by {@code path}.
     * 
     * @param path         the path of the texture image
     * @param name         the name of the texture
     * @return             a new instance of {@code Texture} containing the
     *                     input image
     * @throws IOException if the texture image could not be read
     */
    public static Texture readTexture(String path, String name) throws IOException{
        return new Texture(ImageIO.read(new File(path)), name);
    }
    
}
