package cn.sun.miniredis.datastructure;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RedisList implements RedisData {

    private volatile long timeout = -1;
    private LinkedList<RedisBytes> list;

    public RedisList() {
        this.list = new LinkedList<>();
    }

    @Override
    public long timeout() {
        return 0;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int size() {
        return list.size();
    }

    public void lpush(RedisBytes... values) {
        for (RedisBytes redisBytes : values) {
            list.addFirst(redisBytes);
        }
    }

    public RedisBytes lpop() {
        return list.pollFirst();
    }

    public List<RedisBytes> lrange(int start, int end) {
        int size = list.size();
        start = Math.max(0, start);
        end = Math.min(size - 1, end);

        if (start <= end) {
            return list.subList(start, end + 1);
        }
        return Collections.emptyList();
    }

    public void rpush(RedisBytes... values) {
        for (RedisBytes redisBytes : values) {
            list.addLast(redisBytes);
        }
    }

    public RedisBytes rpop() {
        return list.pollLast();
    }
}
