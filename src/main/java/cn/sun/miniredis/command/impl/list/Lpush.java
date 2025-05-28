package cn.sun.miniredis.command.impl.list;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.datastructure.RedisList;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Errors;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.protocal.RespInteger;
import cn.sun.miniredis.server.core.RedisCore;

import java.util.List;
import java.util.stream.Stream;

public class Lpush implements Command {
    private RedisCore redisCore;
    private RedisBytes key;
    private List<RedisBytes> memebers;

    public Lpush(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.LPUSH;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 3) {
            throw new IllegalArgumentException("LPUSH lack of arguments");
        }
        key = ((BulkString) array[1]).getContent();
        memebers = Stream.of(array).skip(2).map(item -> ((BulkString) item).getContent()).toList();
    }

    @Override
    public Resp handle() {
        RedisList redisList = null;
        RedisData redisData = redisCore.get(key);
        if (redisData == null) redisList = new RedisList();
        else if (redisData instanceof RedisList) redisList = (RedisList) redisData;
        if (redisList == null) return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");

        redisList.lpush(memebers.toArray(new RedisBytes[0]));
        redisCore.put(key, redisList);
        return new RespInteger(redisList.size());
    }
}
