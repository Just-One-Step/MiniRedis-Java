package cn.sun.miniredis.datastructure;

import cn.sun.miniredis.internal.Dict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RedisSet implements RedisData {

    private volatile long timeout = -1;
    private Dict<RedisBytes, Object> setCore;

    public RedisSet() {
        this.setCore = new Dict<>();
    }

    @Override
    public long timeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int add(List<RedisBytes> memebers) {
        int count = 0;
        for (RedisBytes member : memebers) {
            if (!setCore.containsKey(member)) {
                setCore.put(member, null);
                count ++;
            }
        }
        return count;
    }

    public int remove(List<RedisBytes> memebers) {
        int count = 0;
        for (RedisBytes member : memebers) {
            if (setCore.containsKey(member)) {
                setCore.remove(member);
                count ++;
            }
        }
        return count;
    }

    public int remove(RedisBytes memeber) {
        if (setCore.containsKey(memeber)) {
            setCore.remove(memeber);
            return 1;
        }
        return 0;
    }

    public List<RedisBytes> pop(int count) {
        if (setCore.size() == 0) {
            return Collections.emptyList();
        }
        count = Math.min(count, setCore.size());
        List<RedisBytes> poppedElements = new ArrayList<>(count);

        Random random = new Random();
        for (int i = 0; i < count; i ++) {
            RedisBytes memeber = (RedisBytes) setCore.keySet().toArray()[random.nextInt(setCore.size())];
            poppedElements.add(memeber);
            setCore.remove(memeber);
        }
        return poppedElements;
    }

    public int size() {
        return setCore.size();
    }
}
