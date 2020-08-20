package com.garhoogin.obj2minecraft.nbt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

/**
 * The {@code NBTTagCompound} class holds a set of name/value pairs. It can also
 * hold more nested {@code NBTTagCompound}s.
 * 
 * @author Declan Moore
 */

public class NBTTagCompound {
    
    /**
     * The end of data marker
     */
    public static final int TYPE_END = 0;
    
    /**
     * A byte
     */
    public static final int TYPE_BYTE = 1;
    
    /**
     * A short (signed 16-bit)
     */
    public static final int TYPE_SHORT = 2;
    
    /**
     * An int (signed 32-bit)
     */
    public static final int TYPE_INT = 3;
    
    /**
     * A long (signed 64-bit)
     */
    public static final int TYPE_LONG = 4;
    
    /**
     * An IEEE-754 single precision floating point
     */
    public static final int TYPE_FLOAT = 5;
    
    /**
     * An IEEE-754 double precision floating point
     */
    public static final int TYPE_DOUBLE = 6;
    
    /**
     * An array of bytes
     */
    public static final int TYPE_BYTE_ARRAY = 7;
    
    /**
     * A text string
     */
    public static final int TYPE_STRING = 8;
    
    /**
     * A list of other objects ({@code NBTTagList})
     * @see com.garhoogin.obj2minecraft.nbt.NBTTagList
     */
    public static final int TYPE_LIST = 9;
    
    /**
     * An NBT tag compound ({@code NBTTagCompound})
     */
    public static final int TYPE_COMPOUND = 10;
    
    /**
     * An int array
     */
    public static final int TYPE_INT_ARRAY = 11;
    
    /**
     * A long array
     */
    public static final int TYPE_LONG_ARRAY = 12;
    
    private final Map<String, Object> contents;
    private final Map<String, Integer> types;
    
    
    /**
     * Create a new instance of {@code NBTTagCompound}.
     */
    public NBTTagCompound(){
        this.contents = new HashMap<>();
        this.types = new HashMap<>();
    }
    
    protected static byte[] serialize(String key, Object o, int type){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(key != null){
            baos.write(type);
            baos.write((key.length() >>> 8) & 0xFF);
            baos.write(key.length() & 0xFF);
            try{
                baos.write(key.getBytes());
            } catch(IOException ex){}
        }
        switch(type){
            case TYPE_BYTE:
            {
                baos.write((byte) o);
                break;
            }
            case TYPE_SHORT:
            {
                short value = (short) o;
                baos.write(value >>> 8);
                baos.write(value & 0xFF);
                break;
            }
            case TYPE_INT:
            {
                int value = (int) o;
                baos.write(value >>> 24);
                baos.write((value >>> 16) & 0xFF);
                baos.write((value >>> 8) & 0xFF);
                baos.write(value & 0xFF);
                break;
            }
            case TYPE_LONG:
            {
                long value = (long) o;
                baos.write((byte) (value >>> 56));
                baos.write((byte) ((value >>> 48) & 0xFF));
                baos.write((byte) ((value >>> 40) & 0xFF));
                baos.write((byte) ((value >> 32) & 0xFF));
                baos.write((byte) ((value >> 24) & 0xFF));
                baos.write((byte) ((value >> 16) & 0xFF));
                baos.write((byte) ((value >> 8) & 0xFF));
                baos.write((byte) (value & 0xFF));
                break;
            }
            case TYPE_FLOAT:
            {
                float floatVal = (float) o;
                int value = Float.floatToIntBits(floatVal);
                baos.write(value >>> 24);
                baos.write((value >>> 16) & 0xFF);
                baos.write((value >>> 8) & 0xFF);
                baos.write(value & 0xFF);
                break;
            }
            case TYPE_DOUBLE:
            {
                double doubleVal = (double) o;
                long value = Double.doubleToLongBits(doubleVal);
                baos.write((byte) (value >>> 56));
                baos.write((byte) ((value >>> 48) & 0xFF));
                baos.write((byte) ((value >>> 40) & 0xFF));
                baos.write((byte) ((value >> 32) & 0xFF));
                baos.write((byte) ((value >> 24) & 0xFF));
                baos.write((byte) ((value >> 16) & 0xFF));
                baos.write((byte) ((value >> 8) & 0xFF));
                baos.write((byte) (value & 0xFF));
                break;
            }
            case TYPE_BYTE_ARRAY:
            {
                byte[] array = (byte[]) o;
                int value = array.length;
                baos.write(value >>> 24);
                baos.write((value >>> 16) & 0xFF);
                baos.write((value >>> 8) & 0xFF);
                baos.write(value & 0xFF);
                try{
                    baos.write(array);
                } catch(IOException ex){
                    throw new IllegalStateException("What?");
                }
                break;
            }
            case TYPE_STRING:
            {
                String string = (String) o;
                try{
                    baos.write(string.length() >>> 8);
                    baos.write(string.length() & 0xFF);
                    baos.write(string.getBytes());
                    //baos.write(0);
                } catch(IOException ex){
                    throw new IllegalStateException("What?");
                }
                break;
            }
            case TYPE_LIST:
            {
                NBTTagList list = (NBTTagList) o;
                try{
                    baos.write(list.serialize());
                } catch(IOException ex){
                    throw new IllegalStateException("What?");
                }
                break;
            }
            case TYPE_COMPOUND:
            {
                NBTTagCompound compound = (NBTTagCompound) o;
                try{
                    baos.write(compound.serialize());
                } catch(IOException ex){
                    throw new IllegalStateException("What?");
                }
                break;
            }
            case TYPE_INT_ARRAY:
            {
                int[] array = (int[]) o;
                int value = array.length;
                baos.write(value >>> 24);
                baos.write((value >>> 16) & 0xFF);
                baos.write((value >>> 8) & 0xFF);
                baos.write(value & 0xFF);
                for(int i = 0; i < array.length; i++){
                    value = array[i];
                    baos.write(value >>> 24);
                    baos.write((value >>> 16) & 0xFF);
                    baos.write((value >>> 8) & 0xFF);
                    baos.write(value & 0xFF);
                }
                break;
            }
            case TYPE_LONG_ARRAY:
            {
                long[] array = (long[]) o;
                long value = array.length;
                baos.write((byte) ((value >> 24) & 0xFF));
                baos.write((byte) ((value >> 16) & 0xFF));
                baos.write((byte) ((value >> 8) & 0xFF));
                baos.write((byte) (value & 0xFF));
                for(int i = 0; i < array.length; i++){
                    value = array[i];
                    baos.write((byte) (value >>> 56));
                    baos.write((byte) ((value >>> 48) & 0xFF));
                    baos.write((byte) ((value >>> 40) & 0xFF));
                    baos.write((byte) ((value >> 32) & 0xFF));
                    baos.write((byte) ((value >> 24) & 0xFF));
                    baos.write((byte) ((value >> 16) & 0xFF));
                    baos.write((byte) ((value >> 8) & 0xFF));
                    baos.write((byte) (value & 0xFF));
                }
                break;
            }
        }
        return baos.toByteArray();
    }
    
        
    /**
     * Serialize this {@code NBTTagCompound} to a byte array.
     * 
     * @return a byte array describing this compound
     */
    public byte[] serialize(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //TODO: put things here
        int nChildren = contents.size();
        for(Map.Entry<String, Object> entry : contents.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            int type = types.get(key);
            try{
                baos.write(serialize(key, value, type));
            } catch(IOException ex){
                throw new IllegalStateException("What?");
            }
        }
        baos.write(0);
        return baos.toByteArray();
    }
    
    
    /**
     * Serializes this {@code NBTTagCompound}, but applies GZip compression.
     * 
     * @return a compressed byte array representing this compound
     */
    public byte[] fileSerialize(){
        try{
            byte[] bytes = serialize("", this, TYPE_COMPOUND);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream zipStream = new GZIPOutputStream(baos);
            zipStream.write(bytes);
            zipStream.close();
            return baos.toByteArray();
        } catch(IOException ex){
            throw new IllegalStateException("What?");
        }
    }
    
    
    /**
     * Serializes this compound as it would appear in a region file.
     * 
     * @return a compressed byte array that describes this compound
     */
    public byte[] chunkSerialize(){
        try{
            byte[] bytes = serialize("", this, TYPE_COMPOUND);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteArrayOutputStream header = new ByteArrayOutputStream();
            Deflater deflater = new Deflater();
            deflater.setInput(bytes);
            deflater.finish();
            baos.write(0x2);
            int compressedLength = 1;
            while(!deflater.finished()){
                byte[] b = new byte[4096];
                int n = deflater.deflate(b);
                compressedLength += n;
                baos.write(b, 0, n);
            }
            
            header.write((compressedLength >>> 24) & 0xFF);
            header.write((compressedLength >>> 16) & 0xFF);
            header.write((compressedLength >>> 8) & 0xFF);
            header.write(compressedLength & 0xFF);
            header.write(baos.toByteArray());
            
            //pad
            int writtenBytes = compressedLength + 4;
            if((writtenBytes & (4096 - 1)) != 0){
                int nPad = 4096 - (writtenBytes & (4096 - 1));
                byte[] p = new byte[nPad];
                header.write(p);
            }
            
            return header.toByteArray();
        } catch(IOException ex){
            throw new IllegalStateException("What?");
        }
    }
    
    
    
    //GETTERS
    
    /**
     * Get the byte given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public byte getByte(String key){
        return (byte) contents.get(key);
    }
    
    
    /**
     * Get the short given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public short getShort(String key){
        return (short) contents.get(key);
    }
    
    
    /**
     * Get the int given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public int getInt(String key){
        return (int) contents.get(key);
    }
    
    
    /**
     * Get the long given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public long getLong(String key){
        return (long) contents.get(key);
    }
    
    
    /**
     * Get the float given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public float getFloat(String key){
        return (float) contents.get(key);
    }
    
    
    /**
     * Get the double given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public double getDouble(String key){
        return (double) contents.get(key);
    }
    
    
    /**
     * Get the byte array given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public byte[] getByteArray(String key){
        return (byte[]) contents.get(key);
    }
    
    
    /**
     * Get the string given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public String getString(String key){
        return (String) contents.get(key);
    }
    
    
    /**
     * Get the list given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public NBTTagList getList(String key){
        return (NBTTagList) contents.get(key);
    }
    
    
    /**
     * Get the compound given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public NBTTagCompound getCompound(String key){
        return (NBTTagCompound) contents.get(key);
    }
    
    
    /**
     * Get the int array given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public int[] getIntArray(String key){
        return (int[]) contents.get(key);
    }
    
    
    /**
     * Get the long array given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the value given by key
     */
    public long[] getLongArray(String key){
        return (long[]) contents.get(key);
    }
    
    
    /**
     * Get the type of data given by the specified key.
     * 
     * @param key the key of the value to read
     * @return    the type given by key
     */
    public int getType(String key){
        return types.get(key);
    }
    
    //SETTERS
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setByte(String key, byte value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_BYTE);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_BYTE);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setShort(String key, short value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_SHORT);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_SHORT);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setInt(String key, int value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_INT);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_INT);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setLong(String key, long value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_LONG);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_LONG);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setFloat(String key, float value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_FLOAT);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_FLOAT);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setDouble(String key, double value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_DOUBLE);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_DOUBLE);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setByteArray(String key, byte[] value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_BYTE_ARRAY);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_BYTE_ARRAY);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setString(String key, String value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_STRING);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_STRING);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setList(String key, NBTTagList value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_LIST);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_LIST);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setCompound(String key, NBTTagCompound value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_COMPOUND);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_COMPOUND);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setIntArray(String key, int[] value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_INT_ARRAY);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_INT_ARRAY);
        }
    }
    
    
    /**
     * Sets or creates a key with the specified value.
     * 
     * @param key   the key name to set
     * @param value the value to set
     */
    public void setLongArray(String key, long[] value){
        if(contents.containsKey(key)){
            contents.replace(key, value);
            types.replace(key, TYPE_LONG_ARRAY);
        } else {
            contents.put(key, value);
            types.put(key, TYPE_LONG_ARRAY);
        }
    }
    
}
