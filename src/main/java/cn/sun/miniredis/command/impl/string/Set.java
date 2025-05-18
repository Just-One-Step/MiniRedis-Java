package cn.sun.miniredis.command.impl.string;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.datastructure.RedisString;
import cn.sun.miniredis.internal.Sds;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.protocal.SimpleString;
import cn.sun.miniredis.server.core.RedisCore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Set implements Command {

    private RedisBytes key;
    private RedisBytes value;
    private RedisCore redisCore;

    public Set(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.SET;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 3) {
            throw new IllegalArgumentException("lack of arguments");
        }
        key = ((BulkString) array[1]).getContent();
        value = ((BulkString) array[2]).getContent();
    }

    @Override
    public Resp handle() {
        if (redisCore.get(key) != null) {
            RedisData data = redisCore.get(key);
            if (data instanceof RedisString) {
                RedisString redisString = (RedisString) data;
                redisString.setSds(new Sds(value.getBytes()));
                return new SimpleString("OK");
            }
        }
        redisCore.put(key, new RedisString(new Sds(value.getBytes())));
        log.info("set key:{} value:{}", key, value);
        return new SimpleString("OK");
    }
}
