package cn.sun.miniredis.command.impl.hash;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.datastructure.RedisHash;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Errors;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.protocal.RespInteger;
import cn.sun.miniredis.server.core.RedisCore;

import java.util.List;
import java.util.stream.Stream;

public class Hdel implements Command {

    private RedisCore redisCore;
    private RedisBytes key;
    private List<RedisBytes> fields;

    public Hdel(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.HDEL;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 3) {
            throw new IllegalArgumentException("HDEL lack of arguments");
        }
        key = ((BulkString) array[1]).getContent();
        fields = Stream.of(array).skip(2).map(item -> ((BulkString) item).getContent()).toList();
    }

    @Override
    public Resp handle() {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) return new BulkString((RedisBytes) null);
        if (redisData instanceof RedisHash) {
            RedisHash redisHash = (RedisHash) redisData;
            int count = redisHash.del(fields);
            return new RespInteger(count);
        }
        return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");
    }
}
