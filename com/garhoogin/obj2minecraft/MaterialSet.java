package com.garhoogin.obj2minecraft;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The {@code MaterialSet} class contains a set of materials. This allows a
 * material to be looked up by name and match blocks to it.
 * 
 * @author Declan Moore
 */
public class MaterialSet {
    
    private final Map<String, Material> materials;
    
    
    /**
     * Create a new instance of {@code MaterialSet}.
     */
    public MaterialSet(){
        this.materials = new HashMap<>();
    }
    
    
    /**
     * Adds a material to this {@code MaterialSet}.
     * 
     * @param m the material to add
     */
    private void addMaterial(Material m){
        materials.put(m.getName(), m);
    }
    
    
    /**
     * Reads a {@code MaterialSet} from a file. The file format is of the
     * material definition file format.
     * 
     * @param path         the path of the material definition file
     * @return             a new material set based off the description in the
     *                     input file
     * @throws IOException if the input file could not be read
     */
    public static MaterialSet readMaterialSet(String path) throws IOException{
        MaterialSet set = new MaterialSet();
        InputStream in = new FileInputStream(new File(path));
        Scanner sc = new Scanner(in);
        
        Material currentMaterial = null;
        
        while(sc.hasNextLine()){
            String line = sc.nextLine().trim();
            if(line.length() == 0 || line.startsWith("#")) continue;
            String[] tokens = line.split(" ");
            String cmd = tokens[0];
            if(cmd.equals("material")){
                if(currentMaterial != null) set.addMaterial(currentMaterial);
                currentMaterial = new Material(tokens[1]);
            } else if(cmd.equals("opaque")){
                String block = tokens[1];
                int r = Integer.parseInt(tokens[2]);
                int g = Integer.parseInt(tokens[3]);
                int b = Integer.parseInt(tokens[4]);
                if(currentMaterial != null) currentMaterial.add(block, r, g, b);
            } else if(cmd.equals("translucent")){
                String block = tokens[1];
                int r = Integer.parseInt(tokens[2]);
                int g = Integer.parseInt(tokens[3]);
                int b = Integer.parseInt(tokens[4]);
                int a = Integer.parseInt(tokens[5]);
                if(currentMaterial != null) currentMaterial.add(block, r, g, b, a);
            }
        }
        
        if(currentMaterial != null) set.addMaterial(currentMaterial);
        
        in.close();
        return set;
    }
    
    
    /**
     * Get the material given by a name.
     * 
     * @param name the name of the material
     * @return     the material of the given name
     */
    public Material getMaterial(String name){
        return materials.get(name);
    }
    
    
    /**
     * Uses a material name to match a color to that material's closest block.
     * 
     * @param c            the color to match
     * @param materialName the name of the material
     * @return             the name of the block in the given material that most
     *                     closely represents the input color
     */
    public String getBlock(Color c, String materialName){
        Material material = getMaterial(materialName);
        if(material == null) material = Material.getDefaultMaterial();
        return material.matchColor(c);
    }
    
}
