package cn.sun.miniredis.command.impl.set;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.datastructure.RedisSet;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Errors;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.protocal.RespInteger;
import cn.sun.miniredis.server.core.RedisCore;

import java.util.List;
import java.util.stream.Stream;

public class Sadd implements Command {
    private RedisCore redisCore;
    private RedisBytes key;
    private List<RedisBytes> members;

    public Sadd(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.SADD;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 3) {
            throw new IllegalArgumentException("SADD command lack of arguments");
        }
        key = ((BulkString) array[1]).getContent();
        members = Stream.of(array).skip(2).map(item -> ((BulkString) item).getContent()).toList();
    }

    @Override
    public Resp handle() {
        RedisSet redisSet = null;
        RedisData data = redisCore.get(key);
        if (data == null) redisSet = new RedisSet();
        else if (data instanceof RedisSet) redisSet = (RedisSet) data;
        if (redisSet == null) return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");

        int add = redisSet.add(members);
        redisCore.put(key, redisSet);
        return new RespInteger(add);
    }
}
