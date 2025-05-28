package cn.sun.miniredis.command.impl.list;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.datastructure.RedisList;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Errors;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.protocal.RespArray;
import cn.sun.miniredis.server.core.RedisCore;

import java.util.List;

public class Lrange implements Command {

    private RedisCore redisCore;
    private RedisBytes key;
    private int start;
    private int end;

    public Lrange(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.LRANGE;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 4) {
            throw new IllegalArgumentException("Lrange lack of arguments");
        }
        key = ((BulkString) array[1]).getContent();
        start = Integer.parseInt(((BulkString) array[2]).getContent().toString());
        end =  Integer.parseInt(((BulkString) array[3]).getContent().toString());
    }

    @Override
    public Resp handle() {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) return new BulkString((RedisBytes) null);
        if (redisData instanceof RedisList) {
            RedisList redisList = (RedisList) redisData;
            List<RedisBytes> lrange = redisList.lrange(start, end);
            Resp[] respArray = new Resp[lrange.size()];
            for (int i = 0; i < lrange.size(); i++) {
                respArray[i] = new BulkString(lrange.get(i));
            }
            return new RespArray(respArray);
        }
        return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");
    }
}
