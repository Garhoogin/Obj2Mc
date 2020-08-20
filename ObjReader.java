package com.garhoogin.obj2minecraft;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;


/**
 * This class contains methods for reading an OBJ file.
 * 
 * @author Declan Moore
 */
public class ObjReader {
    
    /**
     * Read an integer from a string. Essentially the same as {@code atoi} in C.
     * 
     * @param s the input string
     * @return  the string interpreted as an integer
     */
    public static int parse(String s){
        int nlen = 0;
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c >= '0' && c <= '9') nlen++;
            else break;
        }
        return Integer.parseInt(s.substring(0, nlen));
    }
    
    @SuppressWarnings({"CallToPrintStackTrace"})
    static void readMtl(String path, List<Texture> textures) throws IOException{
        try{
            File f = new File(path);
            Scanner sc = new Scanner(new FileInputStream(f));
            String currentTexture = "";
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] args = line.split(" ");
                if(args[0].equals("newmtl")){
                    currentTexture = args[1];
                } else if(args[0].equals("map_Kd")){
                    textures.add(new Texture(ImageIO.read(new File(f.getAbsoluteFile().getParent() + "/" + args[1])), currentTexture));
                }
            }
        } catch(Exception ex){
            System.err.println("Error reading MTL file.");
            ex.printStackTrace();
            try{
                Thread.sleep(1000);
            } catch(InterruptedException ex2){}
        }
    }
    
    
    /**
     * Expand all polygons in an input OBJ file into triangles.
     * 
     * @param sc a scanner that is reading an OBJ file
     * @return   a string containing a new OBJ file where all polygons are
     *           expanded into triangles.
     */
    public static String expandPoly(Scanner sc){
        StringBuilder sb = new StringBuilder();
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String cmd = line.split(" ")[0];
            if(!cmd.equals("f")){
                sb.append(line + "\n");
                continue;
            }
            String[] args = line.substring(2).trim().split(" ");
            for(int i = 0; i < args.length - 2; i++){
                sb.append("f " + args[0] + " " + args[i + 1] + " " + args[i + 2] + "\n");
            }
        }
        sc.close();
        return sb.toString();
    }
    
    /**
     * Read an OBJ file given by path, and writes to textureMap name/value pairs
     * for all textures that end up being read.
     * 
     * @param path         the path to an OBJ file to be read
     * @param textureMap   the map of textures that is filled by this method
     * @return             an array of triangles read from the given path
     * @throws IOException if one or more files fail to be read
     */
    public static Triangle[] read(String path, Map<String, Texture> textureMap) throws IOException{
        
        File f = new File(path);
        Scanner sc = new Scanner(new FileInputStream(f));
        String fixed = expandPoly(sc);
        sc = new Scanner(new ByteArrayInputStream(fixed.getBytes()));
        List<Triangle> triangles = new ArrayList<>();
        List<Vec3> points = new ArrayList<>();
        List<Coord> texCoords = new ArrayList<>();
        List<Texture> textures = new ArrayList<>();
        String currentMtl = null;
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] args = line.split(" ");
            if(args[0].equals("v")){
                points.add(new Vec3(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3])));
            } else if(args[0].equals("vt")){
                float u = Float.parseFloat(args[1]);
                float v = Float.parseFloat(args[2]);
                texCoords.add(new Coord(u, v));
            } else if(args[0].equals("mtllib")){
                String mtl = args[1];
                readMtl(f.getAbsoluteFile().getParent() + "/" + mtl, textures);
            } else if(args[0].equals("usemtl")){
                if(!textures.isEmpty()) currentMtl = args[1];
            } else if(args[0].equals("f")){
                String[] comp1 = args[1].split("/");
                String[] comp2 = args[2].split("/");
                String[] comp3 = args[3].split("/");
                
                int u1 = 0, v1 = 0, u2 = 0, v2 = 0, u3 = 0, v3 = 0;
                if(comp1.length > 1 && comp2.length > 1 && comp3.length > 1){
                    
                    int i1 = Integer.parseInt(comp1[0]) - 1;
                    int i2 = Integer.parseInt(comp2[0]) - 1;
                    int i3 = Integer.parseInt(comp3[0]) - 1;
                    
                    Coord uv1 = texCoords.get(Integer.parseInt(comp1[1]) - 1);
                    Coord uv2 = texCoords.get(Integer.parseInt(comp2[1]) - 1);
                    Coord uv3 = texCoords.get(Integer.parseInt(comp3[1]) - 1);
                    Triangle t = new Triangle(points.get(i1), uv1, points.get(i2), uv2, points.get(i3), uv3, currentMtl);
                    triangles.add(t);
                    triangles.add(Triangle.flip(t));
                } else {
                
                    int i1 = Integer.parseInt(comp1[0]) - 1;
                    int i2 = Integer.parseInt(comp2[0]) - 1;
                    int i3 = Integer.parseInt(comp3[0]) - 1;
                    
                    Triangle t = new Triangle(points.get(i1), points.get(i2), points.get(i3));
                    triangles.add(t);
                    triangles.add(Triangle.flip(t));
                }
            }
        }
        sc.close();
        for (Texture texture : textures) {
            textureMap.put(texture.name, texture);
        }
        Triangle[] toReturn = new Triangle[triangles.size()];
        return (Triangle[]) triangles.toArray(toReturn);
    }
    
}
