package cn.sun.miniredis.command.impl.zset;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.datastructure.RedisZset;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Errors;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.protocal.RespInteger;
import cn.sun.miniredis.server.core.RedisCore;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Zadd implements Command {
    private RedisCore redisCore;
    private RedisBytes key;
    private List<Double> scores;
    private List<RedisBytes> members;

    public Zadd(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    public CommandType getType() {
        return CommandType.ZADD;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 4 || array.length % 2 != 0) {
            throw new IllegalArgumentException("ZADD arguments error");
        }
        key = ((BulkString) array[1]).getContent();
        scores = new ArrayList<>();
        members = new ArrayList<>();
        for (int i = 2; i < array.length; i += 2) {
            RedisBytes scoreBytes = ((BulkString) array[i]).getContent();
            scores.add(Double.parseDouble(scoreBytes.toString()));
            members.add(((BulkString) array[i + 1]).getContent());
        }
    }

    @Override
    public Resp handle() {
        try {
            RedisZset zset = null;
            RedisData data = redisCore.get(key);
            if (data == null) zset = new RedisZset();
            else if (data instanceof RedisZset) zset = (RedisZset) data;
            if (zset == null) return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");

            int count = 0;
            for (int i = 0; i < scores.size(); i++) {
                if (zset.add(scores.get(i), members.get(i))) {
                    count++;
                }
            }
            redisCore.put(key, zset);
            return new RespInteger(count);
        } catch (Exception e) {
            log.error("ZADD error", e);
            return new Errors("ZADD error " + e.getMessage());
        }
    }
}
