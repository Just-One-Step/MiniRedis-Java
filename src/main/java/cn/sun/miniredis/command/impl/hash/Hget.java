package cn.sun.miniredis.command.impl.hash;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.datastructure.RedisHash;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Errors;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.server.core.RedisCore;

public class Hget implements Command {

    private RedisCore redisCore;
    private RedisBytes key;
    private RedisBytes field;

    public Hget(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.HGET;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length != 3) {
            throw new IllegalArgumentException("HGET lack of arguments");
        }
        key = ((BulkString) array[1]).getContent();
        field = ((BulkString) array[2]).getContent();
    }

    @Override
    public Resp handle() {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) return new BulkString((RedisBytes) null);
        if (redisData instanceof RedisHash) {
            RedisHash redisHash = (RedisHash) redisData;
            RedisBytes value = redisHash.getHash().get(field);
            return new BulkString(value);
        }
        return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");
    }
}
