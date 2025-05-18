package cn.sun.miniredis.command.impl.string;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.datastructure.RedisString;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Errors;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.server.core.RedisCore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Get implements Command {

    private RedisBytes key;
    private RedisCore redisCore;

    public Get(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return null;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 2) {
            throw new IllegalArgumentException("lack of arguments");
        }
        key  = ((BulkString) array[1]).getContent();
    }

    @Override
    public Resp handle() {
        try {
            RedisData data = redisCore.get(key);
            if (data == null) {
                return new BulkString((RedisBytes) null);
            }
            if (data instanceof RedisString) {
                RedisString redisString = (RedisString) data;
                return new BulkString(redisString.getValue());
            }
        } catch (Exception e) {
            log.error("handler error", e);
            return new Errors("Error internal server error");
        }
        return new Errors("Error unknown error");
    }
}
