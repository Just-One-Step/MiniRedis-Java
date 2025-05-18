package cn.sun.miniredis.datastructure;

public interface RedisData {
    long timeout();
    void setTimeout(long timeout);
}
