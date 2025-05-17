package cn.sun.miniredis.database;

import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.internal.Dict;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RedisDB {
    private final Dict<byte[], RedisData> data;

    private final int id;

    public RedisDB(int id) {
        this.id = id;
        this.data = new Dict<>();
    }

    public Set<byte[]> keys() {
        return data.keySet();
    }

    public boolean exists(byte[] key) {
        return data.containsKey(key);
    }

    public void put(byte[] key, RedisData value) {
        data.put(key, value);
    }

    public RedisData get(byte[] key) {
        return data.get(key);
    }

    public void remove(byte[] key) {
        data.remove(key);
    }

    public int size() {
        return data.size();
    }
}
