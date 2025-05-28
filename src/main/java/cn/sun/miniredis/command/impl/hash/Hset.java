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

public class Hset implements Command {
    private RedisCore redisCore;
    private RedisBytes key;
    private RedisBytes[] fields;
    private RedisBytes[] values;

    public Hset(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.HSET;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length >= 4 && array.length % 2 == 0) {
            key = ((BulkString) array[1]).getContent();
            int pair_num = array.length / 2 - 1;
            fields = new RedisBytes[pair_num];
            values = new RedisBytes[pair_num];
            for (int i = 0; i < pair_num; i ++) {
                fields[i] = ((BulkString) array[2 + i * 2]).getContent();
                values[i] = ((BulkString) array[3 + i * 2]).getContent();
            }
        } else {
            throw new IllegalArgumentException("HSET arguments error");
        }
    }

    @Override
    public Resp handle() {
        RedisHash redisHash = null;
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            redisHash = new RedisHash();
            int put = 0;
            for (int i = 0; i < fields.length; i ++) {
                put += redisHash.put(fields[i], values[i]);
            }
            redisCore.put(key, redisHash);
            return new RespInteger(put);
        } else if (redisData instanceof RedisHash) {
            redisHash = (RedisHash) redisData;
            int put = 0;
            for (int i = 0; i < fields.length; i ++) {
                put += redisHash.put(fields[i], values[i]);
            }
            redisCore.put(key, redisHash);
            return new RespInteger(put);
        } else {
            return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");
        }
    }
}
