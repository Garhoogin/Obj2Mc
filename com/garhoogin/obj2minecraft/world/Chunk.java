package com.garhoogin.obj2minecraft.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.garhoogin.obj2minecraft.nbt.NBTTagCompound;
import com.garhoogin.obj2minecraft.nbt.NBTTagList;

/**
 * This class contains a set of blocks. It can take its set of blocks and create
 * an {@code NBTTagCompound}.
 * 
 * @author Declan Moore
 */
public class Chunk {
    
    List<Block> blocks;
    int xInRegion;
    int zInRegion;
    int blockX;
    int blockZ;
    
    /**
     * Creates a new instance of {@code Chunk} containing blocks from the given
     * list, only within its chunk boundaries. Blocks consumed by this chunk
     * are removed from the input block list.
     * 
     * @param blockX the block X coordinate of this chunk
     * @param blockZ the block Z coordinate of this chunk
     * @param blocks the input list of blocks
     */
    public Chunk(int blockX, int blockZ, List<Block> blocks){
        System.out.println("Chunk at " + blockX + ", " + blockZ);
        this.blocks = new ArrayList<>();
        this.xInRegion = (blockX / 16) % 32;
        this.zInRegion = (blockZ / 16) % 32;
        this.blockX = blockX;
        this.blockZ = blockZ;
        for(int i = 0; i < blocks.size(); i++){
            Block b = blocks.get(i);
            if(b.x < blockX) continue;
            if(b.z < blockZ) continue;
            if(b.x > blockX + 15) continue;
            if(b.z > blockZ + 15) continue;
            this.blocks.add(b);
            blocks.remove(b);
            i--;
        }
    }
    
    static <String, Long> Map<String, Long> sortByValue(Map<String, Long> map){
        List<Map.Entry<String, Long>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (Object o1, Object o2) -> ((Comparable<Long>) ((Map.Entry<String, Long>) o1).getValue()).compareTo(((Map.Entry<String, Long>) o2).getValue()));
        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<String, Long> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    /**
     * Create an NBT tag compound that describes this chunk.
     * 
     * @return an NBT tag compound that describes this chunk
     */
    public NBTTagCompound getCompound(){
        if(blocks.isEmpty()) return null;
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInt("DataVersion", 1968);
        
        NBTTagCompound level = new NBTTagCompound();
        level.setString("Status", "full");
        level.setByte("isLightOn", (byte) 1);
        level.setLong("InhabitedTime", 0);
        level.setLong("LastUpdate", 0);
        level.setInt("xPos", xInRegion);
        level.setInt("zPos", zInRegion);
        level.setIntArray("Biomes", new int[256]);
        level.setList("TileTicks", new NBTTagList(NBTTagCompound.TYPE_LONG));
        level.setList("TileEntities", new NBTTagList(NBTTagCompound.TYPE_COMPOUND));
        level.setList("Entities", new NBTTagList(NBTTagCompound.TYPE_COMPOUND));
        level.setList("LiquidTicks", new NBTTagList(NBTTagCompound.TYPE_LONG));
        
        NBTTagList postProcessing = new NBTTagList(NBTTagCompound.TYPE_LIST);
        for(int i = 0; i < 16; i++) postProcessing.add(new NBTTagList(NBTTagCompound.TYPE_BYTE));
        level.setList("PostProcessing", postProcessing);
        
        NBTTagCompound structures = new NBTTagCompound();
        NBTTagCompound heightMaps = new NBTTagCompound();
        
        NBTTagCompound references = new NBTTagCompound();
        NBTTagCompound starts = new NBTTagCompound();
        
        String[] structureNames = {"Buried_Treasure", "Desert_Pyramid",
            "Igloo", "Jungle_Pyramid", "Mansion", "Mineshaft",
            "Monument", "Ocean_Ruin", "Pillager_Outpost",
            "Shipwreck", "Stronghold", "Swamp_Hut", "Village"};
        for(String structure : structureNames){
            references.setLongArray(structure, new long[0]);
            NBTTagCompound start = new NBTTagCompound();
            start.setString("id", "INVALID");
            starts.setCompound(structure, start);
        }
        
        structures.setCompound("References", references);
        structures.setCompound("Starts", starts);
        
        level.setCompound("Structures", structures);
        
        long[] motionBlocking = new long[36];
        long[] motionBlockingNoLeaves = new long[36];
        long[] oceanFloor = new long[36];
        long[] worldSurface = new long[36];
        for(int i = 0; i < 36; i++){
            //TODO: actually do something here
            motionBlocking[i] = (long) 0xFFFFFFFFFFFFFFFFl;
            motionBlockingNoLeaves[i] = (long) 0xFFFFFFFFFFFFFFFFl;
            oceanFloor[i] = (long) 0xFFFFFFFFFFFFFFFFl;
            worldSurface[i] = (long) 0xFFFFFFFFFFFFFFFFl;
        }
        
        heightMaps.setLongArray("MOTION_BLOCKING", motionBlocking);
        heightMaps.setLongArray("MOTION_BLOCKING_NO_LEAVES", motionBlockingNoLeaves);
        heightMaps.setLongArray("OCEAN_FLOOR", oceanFloor);
        heightMaps.setLongArray("WORLD_SURFACE", worldSurface);
        level.setCompound("Heightmaps", heightMaps);
        
        NBTTagList sections = new NBTTagList(NBTTagCompound.TYPE_COMPOUND);
        NBTTagCompound theVoid = new NBTTagCompound();
        theVoid.setByte("Y", (byte) -1);
        sections.add(theVoid);
        for(int i = 0; i < 16; i++){
            int yBase = i * 16;
            int yMax = i * 16 + 15;
            NBTTagCompound section = new NBTTagCompound();
            section.setByte("Y", (byte) i);
            
            byte[] blockLight = new byte[2048];
            for(int j = 0; j < 2048; j++) blockLight[j] = (byte) 0xFF;
            section.setByteArray("BlockLight", blockLight);
            
            //get all blocks in this section.
            String[] sectionBlocks = new String[16 * 16 * 16];
            for(int j = 0; j < 16 * 16 * 16; j++){
                sectionBlocks[j] = "air";
            }
            for(Block b : blocks){
                if(b.y < yBase) continue;
                if(b.y > yMax) continue;
                sectionBlocks[(b.x - blockX) + 16 * (b.y - yBase) + 256 * (b.z - blockZ)] = b.name;
            }
            //create a frequency map
            Map<String, Long> freq = Stream.of(sectionBlocks).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            freq = sortByValue(freq);
            Set<Map.Entry<String, Long>> blockEntries = freq.entrySet();
            
            List<String> usedBlocks = new ArrayList<>();
            for(Map.Entry<String, Long> entry : blockEntries){
                if(usedBlocks.size() == 256) break;
                usedBlocks.add(entry.getKey());
            }
            
            if(usedBlocks.size() <= 16){
                long[] blockStates = new long[256];

                for(int y = 0; y < 16; y++){
                    for(int z = 0; z < 16; z++){
                        long val = 0;
                        for(int x = 0; x < 16; x++){
                            String name = sectionBlocks[x + 16 * y + 256 * z];
                            int index = usedBlocks.indexOf(name) & 0xF;
                            val |= ((long) index) << (x * 4);
                        }
                        blockStates[z + y * 16] = val;
                    }
                }
                section.setLongArray("BlockStates", blockStates);
            } else if(usedBlocks.size() <= 256){
                long[] blockStates = new long[512];

                for(int y = 0; y < 16; y++){
                    for(int z = 0; z < 16; z++){
                        long val = 0;
                        for(int x = 0; x < 8; x++){
                            String name = sectionBlocks[x + 16 * y + 256 * z];
                            int index = usedBlocks.indexOf(name) & 0xFF;
                            val |= ((long) index) << (x * 8);
                        }
                        blockStates[2 * (z + y * 16)] = val;
                        
                        val = 0;
                        for(int x = 0; x < 8; x++){
                            String name = sectionBlocks[x + 8 + 16 * y + 256 * z];
                            int index = usedBlocks.indexOf(name) & 0xFF;
                            val |= ((long) index) << (x * 8);
                        }
                        blockStates[1 + 2 * (z + y * 16)] = val;
                    }
                }
                section.setLongArray("BlockStates", blockStates);
            }
            
            NBTTagList palette = new NBTTagList(NBTTagCompound.TYPE_COMPOUND);
            /*NBTTagCompound air = new NBTTagCompound();
            air.setString("Name", "minecraft:air");
            NBTTagCompound stone = new NBTTagCompound();
            stone.setString("Name", "minecraft:stone");
            palette.add(air);
            palette.add(stone);*/
            for(String blockName : usedBlocks){
                NBTTagCompound block = new NBTTagCompound();
                block.setString("Name", "minecraft:" + blockName);
                palette.add(block);
            }
            
            section.setList("Palette", palette);
            
            sections.add(section);
        }
        
        level.setList("Sections", sections);
        
        compound.setCompound("Level", level);
        return compound;
    }
}
