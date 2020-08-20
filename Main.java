package com.garhoogin.obj2minecraft;
import static com.garhoogin.obj2minecraft.Main.tricube_overlap;
import java.awt.Color;
import java.io.*;
import java.util.*;
import com.garhoogin.obj2minecraft.world.World;

/**
 * The main class for the Obj2Mc program.
 * 
 * @author Declan Moore
 */
public class Main {
    
    static boolean axis_test(float a1, float a2, float b1, float b2, float c1, float c2, float half){
	float p = a1 * b1 + a2 * b2;
	float q = a1 * c1 + a2 * c2;
	float r = half * (Math.abs(a1) + Math.abs(a2));
	return Math.min(p, q) > r || Math.max(p, q) < -r;
    }
    
    static boolean pointInBox(Vec3 position, Vec3 boxMin, Vec3 boxMax){
        return !(position.x < boxMin.x || position.x > boxMax.x
                || position.y < boxMin.y || position.y > boxMax.y
                || position.z < boxMin.z || position.z > boxMax.z);
    }
    
    //based off of code by Gericom
    static boolean tricube_overlap_one(Triangle t, Vec3 position_, float half){
        Vec3 position = position_.add(new Vec3(half, half, half));//position += new Vector3(half, half, half);
        Vec3 v0 = t.a.subtract(position); //test 2: swap with v1
        Vec3 v1 = t.b.subtract(position); //test 2: swap with v0
        Vec3 v2 = t.c.subtract(position);
        Vec3 normal = t.normal; //test 2: negate
        float d = normal.dot(v0);
        float r = half * (Math.abs(normal.x) + Math.abs(normal.y) + Math.abs(normal.z));
        if (d > r || d < -r) return false;
        Vec3 e = v1.subtract(v0);//v1 - v0;
        if (axis_test(e.z, -e.x, v0.y, v0.z, v2.y, v2.z, half)) return false;
        if (axis_test(-e.z, e.x, v0.x, v0.z, v2.x, v2.z, half)) return false;
        if (axis_test(e.y, -e.x, v1.x, v1.y, v2.x, v2.y, half)) return false;
        e = v2.subtract(v1);//e = v2 - v1;
        if (axis_test(e.z, -e.y, v0.y, v0.z, v2.y, v2.z, half)) return false;
        if (axis_test(-e.z, e.x, v0.x, v0.z, v2.x, v2.z, half)) return false;
        if (axis_test(e.y, -e.x, v0.x, v0.y, v1.x, v1.y, half)) return false;
        e = v0.subtract(v2);//e = v0 - v2;
        if (axis_test(e.z, -e.y, v0.y, v0.z, v1.y, v1.z, half)) return false;
        if (axis_test(-e.z, e.x, v0.x, v0.z, v1.x, v1.z, half)) return false;
        if (axis_test(e.y, -e.x, v1.x, v1.y, v2.x, v2.y, half)) return false;
        return true;
    }
    
    //original function only seemed to work when triangles faced in a certain direction
    static boolean tricube_overlap(Triangle t, Vec3 position, float boxSize){
        Vec3 triangleMin = t.getMinimum().subtract(new Vec3(boxSize, boxSize, boxSize));
        Vec3 triangleMax = t.getMaximum().add(new Vec3(boxSize, boxSize, boxSize));
        
        if(position.x < triangleMin.x || position.x > triangleMax.x
                || position.y < triangleMin.y || position.y > triangleMax.y
                || position.z < triangleMin.z || position.z > triangleMax.z) return false;
        
        return tricube_overlap_one(t, position, 0.5f * boxSize);
    }
    
    static boolean tricube_overlap2(Triangle t, Vec3 position, float boxSize){
        float half = 0.5f * boxSize;
        Vec3 triangleMin = t.getMinimum().subtract(new Vec3(boxSize, boxSize, boxSize));
        Vec3 triangleMax = t.getMaximum().add(new Vec3(boxSize, boxSize, boxSize));
        if(!pointInBox(position, triangleMin, triangleMax)) return false;
        
        if(tricube_overlap_one(t, position, half)) return true;
        Triangle t1 = Triangle.flip(t);
        
        return tricube_overlap_one(t1, position, half);
    }
    
    /**
     * Samples a color of a triangle at a given position, which is a block
     * position at the center.
     * 
     * @param triangle the triangle being sampled
     * @param textures the texture list
     * @param position the position to sample
     * @param boxSize  the size of a box in model space
     * @return         the sampled color of the triangle
     */
    public static Color sampleColor(Triangle triangle, Map<String, Texture> textures, Vec3 position, Vec3 boxSize){
        //make a list of colors.
        List<Color> colors = new ArrayList<>();
        Vec3 base = position.subtract(boxSize.multiply(0.25f));
        float halfSize = boxSize.x * 0.5f;
        float thirdSize = boxSize.x * 0.6666667f;
        float quarterSize = 0.5f * halfSize;
        Texture texture = textures.get(triangle.texture);
        
        int nFilled = 0;
        
        {
            Vec3 onTri = triangle.perpendicular(position);
            Vec3 bary = triangle.getBarycentric(onTri);
            Coord uv = triangle.getTexCoord(bary);
            Color c = texture.sample(uv.x, uv.y);
            colors.add(c);
        }
        
        for(int x = 0; x < 3; x++){
            for(int y = 0; y < 3; y++){
                for(int z = 0; z < 3; z++){
                    Vec3 newPosition = base.add(new Vec3(x * quarterSize, y * quarterSize, z * quarterSize));
                    if(!tricube_overlap2(triangle, newPosition, boxSize.x * 0.5f)) continue; //.75 should be .5
                    //this sub-cube overlaps. Sample it.
                    Vec3 onTri = triangle.perpendicular(newPosition);
                    Vec3 bary = triangle.getBarycentric(onTri);
                    Coord uv = triangle.getTexCoord(bary);
                    Color c = texture.sample(uv.x, uv.y);
                    colors.add(c);
                    nFilled++;
                }
            }
        }
        //boolean overlap = tricube_overlap(triangle, position, boxSize.x);
        //if(overlap) nFilled++;
        
        if(nFilled == 0){
            //System.out.println("???");
            //System.out.println("\tBox Size: " + boxSize + "\n\tPosition: " + position + "\n\tTriangle: " + triangle
            //    + "\n\tHalf box size: " + new Vec3(halfSize, halfSize, halfSize));
            //System.exit(0);
        }
        
        return Texture.avg(colors.toArray(new Color[0]));
    }
    
    
    /**
     * the maximum number of threads to create for layering
     */
    public static final int MAX_THREADS = 16;
    
    public static void main(String[] args) throws IOException{
        Scanner sc = new Scanner(System.in);
        String path;
        if(args.length > 0) path = args[0];
        else {
            System.out.print("Input OBJ: ");
            path = sc.nextLine();
        }
        if(!new File(path).exists()){
            System.err.println("Path not found.");
            System.exit(1);
        }
        int setHeight = 126;
        System.out.print("Height: ");
        setHeight = Integer.parseInt(sc.nextLine());

        
        Map<String, Texture> textures = new HashMap<>();
        Triangle[] t = ObjReader.read(path, textures);
        
        if(args.length > 1){
            List<Triangle> triangleList = new ArrayList<>(t.length);
            triangleList.addAll(Arrays.asList(t));
            
            int nExtraFiles = args.length - 2;
            for(int i = 2; i < args.length; i++){
                path = args[i];
                File f = new File(path);
                if(!f.exists()){
                    System.err.println("File not found: " + path);
                    System.exit(1);
                }
                Triangle[] arr = ObjReader.read(path, textures);
                System.out.println("Adding " + arr.length + " triangles.");
                triangleList.addAll(Arrays.asList(arr));
            }
            t = triangleList.toArray(new Triangle[0]);
        }
        
        System.out.print("Material data (leave blank to skip): ");
        String materialPath = sc.nextLine();
        
        MaterialSet materialSet;
        
        //read material data
        if(materialPath.trim().length() > 0){
            materialSet = MaterialSet.readMaterialSet(materialPath);
        } else {
            materialSet = new MaterialSet();
        }
        
        Vec3 size = Triangle.getMax(t).subtract(Triangle.getMin(t));
        int cubesY = setHeight;
        float width = size.x;
        float height = size.y;
        float cubeSize = height / ((float) (cubesY - 1));
        int cubesX = (int) Math.ceil(size.x / cubeSize);
        int cubesZ = (int) Math.ceil(size.z / cubeSize);
        
        System.out.println("size: " + size);
        System.out.println("Min: " + Triangle.getMin(t));
        System.out.println("Max: " + Triangle.getMax(t));
        System.out.println("Dimensions: " + cubesX + ", " + cubesY + ", " + cubesZ);
        System.out.println("Triangles: " + t.length);
        
        Vec3 min = Triangle.getMin(t);
        cubesX += 2; cubesZ += 2;
        World world = new World();
        
        int nThreads = (cubesY + 15) / 16;
        if(nThreads > MAX_THREADS) nThreads = MAX_THREADS;
        
        int layersToThread = cubesY;
        //round up to a multiple of nThreads
        if((layersToThread % nThreads) != 0){
            layersToThread += nThreads - (layersToThread % nThreads);
        }
        //dish out threads
        
        List<Object> completeLayers = new ArrayList<>();
        
        int layersPerThread = layersToThread / nThreads;
        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < nThreads; i++){
            LayererThread t1 = new LayererThread(t, world, materialSet, 
                    textures, cubeSize, min, layersPerThread * i, cubesX, 
                    cubesY, cubesZ, layersPerThread, completeLayers);
            Thread th1 = t1.begin();
            threads.add(th1);
        }
        
        for(Thread th : threads){
            try{
                th.join();
            } catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
        
        System.out.println("Begin generating world");
        File region = new File("region");
        if(region.exists()) region.delete();
        region.mkdir();
        world.save();
        System.out.println("Done.");
    }
    
}

class LayererThread implements Runnable{
    
    public final int minY;
    public final int nLayers;
    private final Triangle[] t;
    private final World world;
    private final int cubesX;
    private final int cubesY;
    private final int cubesZ;
    private final float cubeSize;
    private final Vec3 min;
    private final Map<String, Texture> textures;
    public boolean done;
    private final MaterialSet materialSet;
    private Thread thread;
    private final List<Object> completeLayers;
    
    
    /**
     * Create a new layerer. 
     * 
     * @param triangles      the full set of triangles
     * @param world          the world object to write to
     * @param materialSet    the set of materials
     * @param textures       the set of textures
     * @param cubeSize       the size of a block in model space
     * @param min            the smallest coordinate in the model
     * @param minY           the starting Y coordinate to start layering at
     * @param cubesX         the number of cubes in the X direction
     * @param cubesY         the number of cubes in the Y direction
     * @param cubesZ         the number of cubes in the Z direction
     * @param nLayers        the number of layers to make
     * @param completeLayers a list for keeping track of complete layers
     */
    public LayererThread(Triangle[] triangles, World world, MaterialSet materialSet, Map<String, Texture> textures, float cubeSize, Vec3 min, int minY, int cubesX, int cubesY, int cubesZ, int nLayers, List<Object> completeLayers){
        if(minY == 0){
            minY = -1;
            nLayers++;
        } //little hack
        this.minY = minY;
        this.nLayers = nLayers;
        this.world = world;
        this.done = false;
        this.cubesX = cubesX;
        this.cubesY = cubesY;
        this.cubesZ = cubesZ;
        this.cubeSize = cubeSize;
        this.min = min;
        this.textures = textures;
        this.materialSet = materialSet;
        this.completeLayers = completeLayers;
        //determine which triangles can actually be seen from this section
        List<Triangle> triangleList = new ArrayList<>();
        float minModelY = minY * cubeSize + min.y + 0.5f * cubeSize;
        float maxModelY = (minY + nLayers) * cubeSize + min.y + 0.5f * cubeSize;
        Vec3 halfBox = new Vec3(0.5f * cubeSize, 0.5f * cubeSize, 0.5f * cubeSize);
        for(Triangle t1 : triangles){
            Vec3 t1min = t1.getMinimum().subtract(halfBox);
            Vec3 t1max = t1.getMaximum().add(halfBox);
            if(t1min.y > maxModelY) continue;
            if(t1max.y < minModelY) continue;
            triangleList.add(t1);
        }
        this.t = triangleList.toArray(new Triangle[0]);
    }
    
    @Override
    public void run(){
        int nDone = 0;
        System.out.println("Enter new thread. Queued layers: " + nLayers + " from y=" + minY + ". Triangles: " + t.length);
        for(int y = minY; y <= cubesY; y++){
            synchronized(completeLayers){
                System.out.println("Generating layer " + completeLayers.size() + " / " + cubesY);
                completeLayers.add(null);
            }
            
            for(int x = 0; x < cubesX; x++){
                for(int z = 0; z < cubesZ; z++){
                    Vec3 position = new Vec3(x * cubeSize + min.x + 0.5f * cubeSize, y * cubeSize + min.y + 0.5f * cubeSize, z * cubeSize + min.z + 0.5f * cubeSize);
                    Triangle theTriangle = null;
                    Color sampleColor = new Color(127, 127, 127);
                    for (Triangle t1 : t) {
                        if (tricube_overlap(t1, position, cubeSize * 1.2f)) {
                            if(t1.texture != null){
                                String texture = t1.texture;
                                /*Vec3 barycentric = t1.getBarycentric(t1.perpendicular(position));
                                Coord texCoord = t1.getTexCoord(barycentric);
                                Color cl = textures.get(texture).sample(texCoord.x, texCoord.y);*/
                                Color cl = Main.sampleColor(t1, textures, position, new Vec3(cubeSize, cubeSize, cubeSize));
                                if(cl.getAlpha() < 85) continue;
                                sampleColor = cl;
                            } else {
                                sampleColor = new Color(127, 127, 127);
                            }
                            theTriangle = t1;
                            break;
                        }
                    }
                    if(theTriangle != null){
                        
                        world.addBlock(x, y + 1, z, materialSet, theTriangle.texture, sampleColor);
                    }
                }
            
                
            }
            nDone++;
            
            if(nDone == nLayers) break;
        }
        done = true;
        System.out.println("Thread finish");
    }
    
    
    /**
     * Start running this layerer, and returns its thread.
     * 
     * @return the thread the layerer is running in
     */
    public Thread begin(){
        Thread th = new Thread(this);
        th.start();
        thread = th;
        return th;
    }
    
    
    /**
     * Get the layerer's thread.
     * 
     * @return the layerer's thread
     */
    public Thread getThread(){
        return thread;
    }
    
}