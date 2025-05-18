package cn.sun.miniredis.internal;

import cn.sun.miniredis.datastructure.RedisBytes;

public class Sds {
    private byte[] bytes;
    private int len;
    private int alloc;

    private static final int SDS_MAX_PREALLOC = 1024 * 1024; // 1MB

    public Sds(byte[] bytes) {
        this.len = bytes.length;
        this.alloc = calculateAllocSize(bytes.length);
        this.bytes = new byte[alloc];
        System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
    }

    private int calculateAllocSize(int length) {
        if (len <= SDS_MAX_PREALLOC) {
            return Math.max(length * 2, 8);
        }
        return length + SDS_MAX_PREALLOC;
    }

    public String toString() {
        return new String(bytes, RedisBytes.CHARSET);
    }

    public int length() {
        return len;
    }

    public void setBytes(byte[] bytes) {
        if (bytes.length > alloc) {
            this.alloc = calculateAllocSize(bytes.length);
            this.bytes = new byte[this.alloc];
        }
        System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
        this.len = bytes.length;
    }

    public void clear() {
        this.len = 0;
    }

    public Sds append(byte[] extra) {
        int newLength = len + extra.length;
        if (newLength > alloc) {
           int newAlloc = calculateAllocSize(newLength);
           byte[] newBytes = new byte[newAlloc];
           System.arraycopy(this.bytes, 0, newBytes, 0, this.len);
           bytes = newBytes;
           this.alloc = newAlloc;
        }
        System.arraycopy(extra, 0, bytes, len, extra.length);
        this.len = newLength;
        return this;
    }

    public Sds append(String str) {
        return append(str.getBytes(RedisBytes.CHARSET));
    }

    public byte[] getBytes() {
        byte[] result = new byte[len];
        System.arraycopy(bytes, 0, result, 0, len);
        return result;
    }
}
