package com.garhoogin.obj2minecraft.world;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import com.garhoogin.obj2minecraft.nbt.NBTTagCompound;

/**
 * 
 * @author Declan Moore
 */
public class Region {
    
    /**
     * Write out the blocks in a given region by its region coordinates. Blocks
     * consumed by this region are removed from the blocks list. Files are
     * written to region/r.[x].[z].mca.
     * 
     * @param regionX      the X coordinate of this region in region coordinates
     * @param regionZ      the Z coordinate of this region in region coordinates
     * @param blocks       the input list of blocks
     * @throws IOException if a region file could not be written
     */
    public static void write(int regionX, int regionZ, List<Block> blocks) throws IOException{
        Chunk chunks[][] = new Chunk[32][32]; //[x][z]
        for(int x = 0; x < 32; x++){
            for(int z = 0; z < 32; z++){
                chunks[x][z] = new Chunk((regionX * 32 + x) * 16, (regionZ * 32 + z) * 16, blocks);
            }
        }
        NBTTagCompound region = new NBTTagCompound();
        for(int x = 0; x < 32; x++){
            for(int z = 0; z < 32; z++){
                NBTTagCompound c = chunks[x][z].getCompound();
                if(c != null) region.setCompound("Chunk [" + x + ", " + z + "]", c);
            }
        }
        long[] fileOffsets = new long[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        ByteArrayOutputStream bHeader = new ByteArrayOutputStream(8192);
        long dataOffset = 8192;
        
        int headerOffset = 0;
        for(int z = 0; z < 32; z++){
            for(int x = 0; x < 32; x++){
                NBTTagCompound c = chunks[x][z].getCompound();
                if(c != null){
                    byte[] b = c.chunkSerialize();
                    baos.write(b);
                    
                    int sectors = (int) (dataOffset / 4096);
                    bHeader.write((sectors >>> 16) & 0xFF);
                    bHeader.write((sectors >>> 8) & 0xFF);
                    bHeader.write(sectors & 0xFF);
                    int nSectors = b.length / 4096;
                    bHeader.write(nSectors & 0xFF);
                    
                    System.out.println("Writing chunk (" + x + ", " + z + ")" + " at " + dataOffset + " (" + sectors + " sector) at header offset " + headerOffset);
                    dataOffset += b.length;
                } else {
                    System.out.println("Writing empty chunk (" + x + ", " + z + ") at header offset " + headerOffset);
                    bHeader.write(0);
                    bHeader.write(0);
                    bHeader.write(0);
                    bHeader.write(0);
                }
                headerOffset += 4;
            }
        }
        byte[] padding = new byte[4096];
        bHeader.write(padding);
        bHeader.write(baos.toByteArray());
        byte[] bytes = bHeader.toByteArray();
        File f = new File("region/r." + regionX + "." + regionZ + ".mca");
        OutputStream out = new FileOutputStream(f);
        out.write(bytes);
        out.close();
    }
    
}
