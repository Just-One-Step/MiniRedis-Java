package cn.sun.miniredis.datastructure;

import lombok.Getter;

import java.nio.charset.Charset;
import java.util.Arrays;

public class RedisBytes implements Comparable<RedisBytes> {

    public static final Charset CHARSET = Charset.forName("UTF-8");
    private byte[] bytes;

    public RedisBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public boolean  equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || o.getClass() != getClass()) {
            return false;
        }
        RedisBytes other = (RedisBytes) o;
        return Arrays.equals(bytes, other.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return new String(bytes, CHARSET);
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public int compareTo(RedisBytes o) {
        return Arrays.compare(bytes, o.bytes);
    }
}
