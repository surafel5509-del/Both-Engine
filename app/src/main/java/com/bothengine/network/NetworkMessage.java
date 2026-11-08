package com.bothengine.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * NetworkMessage
 * Represents a network message that can be sent between clients and server
 * Supports various data types and serialization
 */
public class NetworkMessage {
    private int type;
    private byte[] data;
    private int position;
    
    public NetworkMessage() {
        this.type = 0;
        this.data = new byte[1024];
        this.position = 0;
    }
    
    public NetworkMessage(int type) {
        this();
        this.type = type;
    }
    
    /**
     * Create a network message with initial capacity
     */
    public static NetworkMessage create(int type, int capacity) {
        NetworkMessage message = new NetworkMessage(type);
        message.data = new byte[capacity];
        return message;
    }
    
    /**
     * Read a network message from a data input stream
     */
    public static NetworkMessage read(DataInputStream input) throws IOException {
        NetworkMessage message = new NetworkMessage();
        
        // Read data length
        int length = input.readInt();
        if (length > 0) {
            message.data = new byte[length];
            input.readFully(message.data);
        }
        
        return message;
    }
    
    /**
     * Write the message to a data output stream
     */
    public void write(DataOutputStream output) throws IOException {
        // Write data length
        output.writeInt(position);
        
        // Write data
        if (position > 0) {
            output.write(data, 0, position);
        }
    }
    
    /**
     * Write a byte to the message
     */
    public void writeByte(byte value) {
        ensureCapacity(1);
        data[position++] = value;
    }
    
    /**
     * Write a boolean to the message
     */
    public void writeBoolean(boolean value) {
        writeByte((byte) (value ? 1 : 0));
    }
    
    /**
     * Write a short to the message
     */
    public void writeShort(short value) {
        ensureCapacity(2);
        data[position++] = (byte) (value >> 8);
        data[position++] = (byte) value;
    }
    
    /**
     * Write an integer to the message
     */
    public void writeInt(int value) {
        ensureCapacity(4);
        data[position++] = (byte) (value >> 24);
        data[position++] = (byte) (value >> 16);
        data[position++] = (byte) (value >> 8);
        data[position++] = (byte) value;
    }
    
    /**
     * Write a long to the message
     */
    public void writeLong(long value) {
        ensureCapacity(8);
        data[position++] = (byte) (value >> 56);
        data[position++] = (byte) (value >> 48);
        data[position++] = (byte) (value >> 40);
        data[position++] = (byte) (value >> 32);
        data[position++] = (byte) (value >> 24);
        data[position++] = (byte) (value >> 16);
        data[position++] = (byte) (value >> 8);
        data[position++] = (byte) value;
    }
    
    /**
     * Write a float to the message
     */
    public void writeFloat(float value) {
        writeInt(Float.floatToIntBits(value));
    }
    
    /**
     * Write a double to the message
     */
    public void writeDouble(double value) {
        writeLong(Double.doubleToLongBits(value));
    }
    
    /**
     * Write a string to the message
     */
    public void writeString(String value) {
        if (value == null) {
            writeInt(-1);
            return;
        }
        
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeInt(bytes.length);
        ensureCapacity(bytes.length);
        System.arraycopy(bytes, 0, data, position, bytes.length);
        position += bytes.length;
    }
    
    /**
     * Write a byte array to the message
     */
    public void writeByteArray(byte[] value) {
        if (value == null) {
            writeInt(-1);
            return;
        }
        
        writeInt(value.length);
        ensureCapacity(value.length);
        System.arraycopy(value, 0, data, position, value.length);
        position += value.length;
    }
    
    /**
     * Write a float array to the message
     */
    public void writeFloatArray(float[] value) {
        if (value == null) {
            writeInt(-1);
            return;
        }
        
        writeInt(value.length);
        ensureCapacity(value.length * 4);
        for (float f : value) {
            writeFloat(f);
        }
    }
    
    /**
     * Write an integer array to the message
     */
    public void writeIntArray(int[] value) {
        if (value == null) {
            writeInt(-1);
            return;
        }
        
        writeInt(value.length);
        ensureCapacity(value.length * 4);
        for (int i : value) {
            writeInt(i);
        }
    }
    
    /**
     * Read a byte from the message
     */
    public byte readByte() {
        if (position + 1 > data.length) {
            throw new IndexOutOfBoundsException("Read out of bounds");
        }
        return data[position++];
    }
    
    /**
     * Read a boolean from the message
     */
    public boolean readBoolean() {
        return readByte() != 0;
    }
    
    /**
     * Read a short from the message
     */
    public short readShort() {
        if (position + 2 > data.length) {
            throw new IndexOutOfBoundsException("Read out of bounds");
        }
        short value = (short) (((data[position] & 0xFF) << 8) | (data[position + 1] & 0xFF));
        position += 2;
        return value;
    }
    
    /**
     * Read an integer from the message
     */
    public int readInt() {
        if (position + 4 > data.length) {
            throw new IndexOutOfBoundsException("Read out of bounds");
        }
        int value = ((data[position] & 0xFF) << 24) | 
                    ((data[position + 1] & 0xFF) << 16) | 
                    ((data[position + 2] & 0xFF) << 8) | 
                    (data[position + 3] & 0xFF);
        position += 4;
        return value;
    }
    
    /**
     * Read a long from the message
     */
    public long readLong() {
        if (position + 8 > data.length) {
            throw new IndexOutOfBoundsException("Read out of bounds");
        }
        long value = ((long) (data[position] & 0xFF) << 56) | 
                     ((long) (data[position + 1] & 0xFF) << 48) | 
                     ((long) (data[position + 2] & 0xFF) << 40) | 
                     ((long) (data[position + 3] & 0xFF) << 32) | 
                     ((long) (data[position + 4] & 0xFF) << 24) | 
                     ((long) (data[position + 5] & 0xFF) << 16) | 
                     ((long) (data[position + 6] & 0xFF) << 8) | 
                     (long) (data[position + 7] & 0xFF);
        position += 8;
        return value;
    }
    
    /**
     * Read a float from the message
     */
    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }
    
    /**
     * Read a double from the message
     */
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }
    
    /**
     * Read a string from the message
     */
    public String readString() {
        int length = readInt();
        if (length < 0) {
            return null;
        }
        
        if (position + length > data.length) {
            throw new IndexOutOfBoundsException("Read out of bounds");
        }
        
        String value = new String(data, position, length, StandardCharsets.UTF_8);
        position += length;
        return value;
    }
    
    /**
     * Read a byte array from the message
     */
    public byte[] readByteArray() {
        int length = readInt();
        if (length < 0) {
            return null;
        }
        
        if (position + length > data.length) {
            throw new IndexOutOfBoundsException("Read out of bounds");
        }
        
        byte[] value = new byte[length];
        System.arraycopy(data, position, value, 0, length);
        position += length;
        return value;
    }
    
    /**
     * Read a float array from the message
     */
    public float[] readFloatArray() {
        int length = readInt();
        if (length < 0) {
            return null;
        }
        
        float[] value = new float[length];
        for (int i = 0; i < length; i++) {
            value[i] = readFloat();
        }
        return value;
    }
    
    /**
     * Read an integer array from the message
     */
    public int[] readIntArray() {
        int length = readInt();
        if (length < 0) {
            return null;
        }
        
        int[] value = new int[length];
        for (int i = 0; i < length; i++) {
            value[i] = readInt();
        }
        return value;
    }
    
    /**
     * Ensure the data array has enough capacity
     */
    private void ensureCapacity(int additionalBytes) {
        if (position + additionalBytes > data.length) {
            // Double the size or add enough for the additional bytes
            int newSize = Math.max(data.length * 2, position + additionalBytes);
            byte[] newData = new byte[newSize];
            System.arraycopy(data, 0, newData, 0, position);
            data = newData;
        }
    }
    
    /**
     * Reset the message for reuse
     */
    public void reset() {
        type = 0;
        position = 0;
    }
    
    /**
     * Clear the message
     */
    public void clear() {
        type = 0;
        position = 0;
        data = new byte[1024];
    }
    
    /**
     * Get the message as bytes
     */
    public byte[] toByteArray() {
        byte[] result = new byte[position];
        System.arraycopy(data, 0, result, 0, position);
        return result;
    }
    
    /**
     * Create a network message from bytes
     */
    public static NetworkMessage fromByteArray(byte[] bytes) {
        NetworkMessage message = new NetworkMessage();
        if (bytes != null) {
            message.data = bytes.clone();
            message.position = bytes.length;
        }
        return message;
    }
    
    // Getters and setters
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    public int getLength() {
        return position;
    }
    
    @Override
    public String toString() {
        return "NetworkMessage{" +
                "type=" + type +
                ", length=" + position +
                '}';
    }
    
    /**
     * Network message type constants
     */
    public static class NetworkMessageType {
        public static final int CONNECT = 0;
        public static final int DISCONNECT = 1;
        public static final int PING = 2;
        public static final int PONG = 3;
        public static final int SPAWN = 4;
        public static final int DESTROY = 5;
        public static final int STATE_UPDATE = 6;
        public static final int RPC = 7;
        public static final int COMMAND = 8;
        public static final int CUSTOM = 100;
    }
}
