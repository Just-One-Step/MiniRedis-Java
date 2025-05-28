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
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class Srem implements Command {

    private RedisCore redisCore;
    private RedisBytes key;
    private List<RedisBytes> memebers;

    public Srem(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.SREM;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 3) {
            throw new IllegalArgumentException("SREM command lack of arguments");
        }
        key = ((BulkString) array[1]).getContent();
        memebers  = Stream.of(array).skip(2).map(item -> ((BulkString) item).getContent()).toList();
    }

    @Override
    public Resp handle() {
        RedisData data = redisCore.get(key);
        if (data == null) return new Errors("ERR no suck key");
        if (data instanceof RedisSet) {
            RedisSet redisSet = (RedisSet) data;
            return new RespInteger(redisSet.remove(memebers));
        }
        return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");
    }
}
