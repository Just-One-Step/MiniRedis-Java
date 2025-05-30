package cn.sun.miniredis.datastructure;

import cn.sun.miniredis.internal.Dict;
import cn.sun.miniredis.internal.SkipList;

import java.util.List;

public class RedisZset implements RedisData {
    private volatile long timeout = -1;
    private SkipList skipList;
    private Dict<Double, Object> dict;

    public RedisZset() {
        skipList = new SkipList();
        dict = new Dict<>();
    }

    @Override
    public long timeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean add(double score, Object member) {
        if (dict.contains(score, member)) {
            return false;
        }
        dict.put(score, member);
        skipList.insert(score, (Comparable) member);
        return true;
    }

    public List<SkipList.SkipListNode> getRange(int start, int end) {
        return skipList.getElementByRankRange(start, end);
    }

    public int size() {
        return dict.size();
    }
}
