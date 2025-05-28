package cn.sun.miniredis.datastructure;

import cn.sun.miniredis.internal.Dict;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RedisHash implements RedisData {
    private volatile long timeout = -1;
    private Dict<RedisBytes, RedisBytes> hash;

    public RedisHash() {
        hash = new Dict<>();
    }

    @Override
    public long timeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int put(RedisBytes field, RedisBytes value) {
        return hash.put(field, value) == null ? 1 : 0;
    }

    public Dict<RedisBytes, RedisBytes> getHash() {
        return hash;
    }

    public int del(List<RedisBytes> fields) {
        return (int) fields.stream().filter(key -> hash.remove(key) != null).count();
    }

}
