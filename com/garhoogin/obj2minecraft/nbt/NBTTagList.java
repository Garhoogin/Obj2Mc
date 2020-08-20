package com.garhoogin.obj2minecraft.nbt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code NBTTagList} contains a list of values, all of the same type.
 * 
 * @author Declan Moore
 */
public class NBTTagList {
    
    private final int type;
    List<Object> contents;
    
    
    /**
     * Create a new {@code NBTTagList} of the specified type.
     * 
     * @param type the type of the data stored in the list.
     * @see com.garhoogin.obj2minecraft.nbt.NBTTagCompound
     */
    public NBTTagList(int type){
        this.type = type;
        this.contents = new ArrayList<>();
    }
    
    
    /**
     * Add an object to this list.
     * 
     * @param o the object to add to this list
     */
    public void add(Object o){
        contents.add(o);
    }
    
    
    /**
     * Serialize this {@code NBTTagList}.
     * 
     * @return a byte array that describes this list
     */
    public byte[] serialize(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(type);
        
        int length = contents.size();
        baos.write((length >>> 24) & 0xFF);
        baos.write((length >>> 16) & 0xFF);
        baos.write((length >>> 8) & 0xFF);
        baos.write(length & 0xFF);
        
        for(Object o : contents){
            try{
                baos.write(NBTTagCompound.serialize(null, o, type));
            } catch(IOException ex){
                throw new IllegalStateException("What?");
            }
        }
        
        return baos.toByteArray();
    }
    
}
