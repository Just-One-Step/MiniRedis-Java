package cn.sun.miniredis.datastructure;

import cn.sun.miniredis.internal.Sds;

public class RedisString implements RedisData {

    private volatile long timeout;
    private Sds value;

    public RedisString(Sds value) {
        this.value = value;
        this.timeout = -1;
    }

    @Override
    public long timeout() {
        return 0;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public RedisBytes getValue() {
        return new RedisBytes(value.getBytes());
    }

    public void setSds(Sds sds) {
        this.value = sds;
    }

    public long increment() {
        return increment(1);
    }

    public long increment(long increment) {
        try {
            long cur = Long.parseLong(value.toString());
            long newValue = cur + increment;
            value = new Sds(String.valueOf(newValue).getBytes());
            return newValue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Value is not a number");
        }
    }
}
