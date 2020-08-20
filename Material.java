package com.garhoogin.obj2minecraft;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Material} class contains a set of mappings between block names and
 * RGB values.
 * 
 * @author Declan Moore
 */
public class Material {
    
    private static final Material defaultMaterial;
    
    static{
        defaultMaterial = new Material("default");
        Color[] list = new Color[]{
            new Color(228, 230, 231),
            new Color(138, 139, 133),
            new Color(57, 61, 64),
            new Color(29, 25, 24),
            new Color(114, 63, 38),
            new Color(171, 53, 35),
            new Color(238, 130, 30),
            new Color(210, 190, 37),
            new Color(106, 166, 28),
            new Color(65, 114, 54),
            new Color(41, 142, 155),
            new Color(56, 134, 192),
            new Color(73, 64, 168),
            new Color(134, 50, 175),
            new Color(197, 87, 175),
            new Color(223, 158, 185)
        };
        String[] names = {
            "white",
            "light_gray",
            "gray",
            "black",
            "brown",
            "red",
            "orange",
            "yellow",
            "lime",
            "green",
            "cyan",
            "light_blue",
            "blue",
            "purple",
            "magenta",
            "pink"
        };
        for(int i = 0; i < list.length; i++){
            String wool = names[i] + "_wool";
            String glass = names[i] + "_stained_glass";
            Color c = list[i];
            defaultMaterial.add(wool, c.getRed(), c.getGreen(), c.getBlue());
            defaultMaterial.add(glass, c.getRed(), c.getGreen(), c.getBlue(), 127);
        }
    }
    
    /**
     * Get the default material, which matches colors to either wool or stained
     * glass.
     * 
     * @return the default material
     */
    public static Material getDefaultMaterial(){
        return defaultMaterial;
    }
    
    private final String name;
    private final List<String> opaqueBlockNames;
    private final List<Color> opaqueColors;
    private final List<String> translucentBlockNames;
    private final List<Color> translucentColors;
    
    private boolean hasTranslucent = false;
    
    
    /**
     * Create a new instance of {@code Material}.
     * 
     * @param name the name of the material
     */
    public Material(String name){
        this.name = name;
        this.opaqueColors = new ArrayList<>();
        this.opaqueBlockNames = new ArrayList<>();
        this.translucentColors = new ArrayList<>();
        this.translucentBlockNames = new ArrayList<>();
        this.hasTranslucent = false;
    }
    
    
    /**
     * Get the name of this material.
     * 
     * @return the name of this material
     */
    public String getName(){
        return name;
    }
    
    
    /**
     * Add an RGB block pair to this material.
     * 
     * @param block the block name
     * @param r     the block's red component
     * @param g     the block's green component
     * @param b     the block's blue component
     */
    public void add(String block, int r, int g, int b){
        Color c = new Color(r, g, b);
        opaqueBlockNames.add(block);
        opaqueColors.add(c);
    }
    
    
    /**
     * Add an RGBA block pair to this material.
     * 
     * @param block the block name
     * @param r     the block's red component
     * @param g     the block's green component
     * @param b     the block's blue component
     * @param a     the block's alpha component
     */
    public void add(String block, int r, int g, int b, int a){
        if(a != 255) hasTranslucent = true;
        Color c = new Color(r, g, b, a);
        if(a != 255){
            translucentBlockNames.add(block);
            translucentColors.add(c);
        } else {
            opaqueBlockNames.add(block);
            opaqueColors.add(c);
        }
    }
    
    static int distanceSquared(Color c1, Color c2){
        int c1r = c1.getRed() - c2.getRed();
        int c1g = c1.getGreen() - c2.getGreen();
        int c1b = c1.getBlue() - c2.getBlue();
        return c1r * c1r + c1g * c1g + c1b * c1b;
    }
    
    /**
     * Get the block name of the best match to the input color.
     * 
     * @param c the input color
     * @return  the block with the closest color to the input color
     */
    public String matchColor(Color c){
        int alpha = c.getAlpha();
        
        if((alpha < 128 && translucentColors.isEmpty())
                || (alpha < 85 && !translucentColors.isEmpty())) return "air";
        
        //if there are no translucent colors, or if there are but the color is too opaque
        if(translucentColors.isEmpty() || (alpha > 170 && !translucentColors.isEmpty())){
            int bestIndex = -1;
            int bestDistance = 100000;
            for(int i = 0; i < opaqueColors.size(); i++){
                Color cl = opaqueColors.get(i);
                int dst = distanceSquared(cl, c);
                if(dst < bestDistance){
                    bestIndex = i;
                    bestDistance = dst;
                }
            }
            return opaqueBlockNames.get(bestIndex);
        }
        
        //now down here we can assume the color is translucent.
        int bestIndex = -1;
        int bestDistance = 100000;
        for(int i = 0; i < translucentColors.size(); i++){
            Color cl = translucentColors.get(i);
            int dst = distanceSquared(cl, c);
            if(dst < bestDistance){
                bestIndex = i;
                bestDistance = dst;
            }
        }
        return translucentBlockNames.get(bestIndex);
    }
    
}
