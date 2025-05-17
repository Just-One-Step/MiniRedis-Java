package cn.sun.miniredis.server.core;

import cn.sun.miniredis.datastructure.RedisData;

import java.util.Set;

public interface RedisCore {
    Set<byte[]> keys();
    void put(byte[] key, RedisData value);
    RedisData get(byte[] key);
    void remove(byte[] key);
    void selectDB(int dbIndex);
    int getDBNum();
    void setDBNum(int dbNum);
    int getCurrentDBIndex();
}
