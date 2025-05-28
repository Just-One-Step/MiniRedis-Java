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

public class Lpop implements Command {

    private RedisCore redisCore;
    private RedisBytes key;
    private int count;

    public Lpop(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.LPOP;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 2) {
            throw new IllegalArgumentException("LPOP lack of arguments");
        }
        key = ((BulkString) array[1]).getContent();
        if (array.length == 2) {
            count = 1;
        } else {
            count = Integer.parseInt(((BulkString) array[2]).getContent().toString());
        }
    }

    @Override
    public Resp handle() {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) return new BulkString((RedisBytes) null);
        if (redisData instanceof RedisList) {
            RedisList list = (RedisList) redisData;
            count = Math.min(count, list.size());
            if (count == 0) return new BulkString((RedisBytes) null);
            else if (count == 1) {
                RedisBytes lpop = list.lpop();
                redisCore.put(key, list);
                return new BulkString(lpop);
            } else {
                Resp[] respArray = new Resp[count];
                for (int i = 0; i < count; i++) {
                    respArray[i] = new BulkString(list.lpop());
                }
                redisCore.put(key, list);
                return new RespArray(respArray);
            }
        }
        return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");
    }
}
