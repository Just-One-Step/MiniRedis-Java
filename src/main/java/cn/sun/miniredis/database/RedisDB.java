package cn.sun.miniredis.database;

import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.internal.Dict;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RedisDB {
    private final Dict<RedisBytes, RedisData> data;

    private final int id;

    public RedisDB(int id) {
        this.id = id;
        this.data = new Dict<>();
    }

    public Set<RedisBytes> keys() {
        return data.keySet();
    }

    public boolean exists(RedisBytes key) {
        return data.containsKey(key);
    }

    public void put(RedisBytes key, RedisData value) {
        data.put(key, value);
    }

    public RedisData get(RedisBytes key) {
        return data.get(key);
    }

    public void remove(RedisBytes key) {
        data.remove(key);
    }

    public int size() {
        return data.size();
    }
}
