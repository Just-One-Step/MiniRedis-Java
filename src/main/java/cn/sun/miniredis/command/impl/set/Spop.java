package cn.sun.miniredis.command.impl.set;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.datastructure.RedisSet;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Errors;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.protocal.RespArray;
import cn.sun.miniredis.server.core.RedisCore;

import java.util.List;

public class Spop implements Command {

    private RedisCore redisCore;
    private RedisBytes key;
    private int count = 1;

    public Spop(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.SPOP;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 2) {
            throw new IllegalArgumentException("SPOP command lack of arguments");
        }
        key = ((BulkString) array[1]).getContent();
        if (array.length > 2) {
            try  {
                count = Integer.parseInt(((BulkString) array[2]).getContent().toString());
            } catch (Exception e) {
                throw new IllegalArgumentException("count must be positive");
            }
        }
    }

    @Override
    public Resp handle() {
        RedisData data = redisCore.get(key);
        if (data == null) {
            if (count == 1) {
                return new BulkString((RedisBytes) null);
            } else {
                return new RespArray(new Resp[0]);
            }
        }
        if (data instanceof RedisSet) {
            RedisSet redisSet = (RedisSet) data;
            if (redisSet.size() == 0) {
                if (count == 1) return new BulkString((RedisBytes) null);
                else return new RespArray(new Resp[0]);
            }

            List<RedisBytes> poppedElements = redisSet.pop(count);
            if (poppedElements.size() == 0) {
                return new RespArray(new Resp[0]);
            }
            redisCore.put(key, redisSet);
            return new RespArray(poppedElements.stream().map(BulkString::new).toArray(Resp[]::new));
        }

        return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");
    }
}
