package cn.sun.miniredis.command.impl.zset;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;
import cn.sun.miniredis.datastructure.RedisZset;
import cn.sun.miniredis.internal.SkipList;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Errors;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.protocal.RespArray;
import cn.sun.miniredis.server.core.RedisCore;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Zrange implements Command {
    private RedisCore redisCore;
    private RedisBytes key;
    private int start;
    private int stop;
    private boolean withScores;

    public Zrange(RedisCore redisCore) {
        this.redisCore = redisCore;
        this.withScores = false; 
    }

    @Override
    public CommandType getType() {
        return CommandType.ZRANGE;
    }

    @Override
    public void setContext(Resp[] array) {
        if (array.length < 4 || array.length > 5) {
            throw new IllegalArgumentException("ZRANGE arguments error");
        }
        key = ((BulkString) array[1]).getContent();
        start = Integer.parseInt(((BulkString) array[2]).getContent().toString());
        stop =  Integer.parseInt(((BulkString) array[3]).getContent().toString());
        // 判断是否为 withscores
        if (array.length == 5) {
            RedisBytes option = ((BulkString) array[4]).getContent();
            if (option.toString().equalsIgnoreCase("withscores")) {
                withScores = true;
            }
        }
    }

    @Override
    public Resp handle() {

        try {
            // 判断key是否存在
            RedisData data = redisCore.get(key);
            if (data == null) return new RespArray(new Resp[0]);

            // 判断数据类型是否正确
            if (!(data instanceof RedisZset)) return new Errors("WRONGTYPE Operation against a key holding the wrong kind of value");

            // 判断是否为空
            RedisZset zset = (RedisZset) data;
            int size = zset.size();
            if (size == 0) return new RespArray(new Resp[0]);
            
            // 处理索引，间兼容负数
            int startIndex = start;
            int endIndex = stop;

            // 兼容负数
            if (startIndex < 0 && size + startIndex >= 0) startIndex  = size + startIndex;
            if (endIndex < 0 && size + endIndex >= 0) endIndex = size + endIndex;

            // 判断索引是否越界
            if (startIndex > endIndex || startIndex < 0) return new RespArray(new Resp[0]);


            List<SkipList.SkipListNode> range = zset.getRange(startIndex, endIndex);
            if (range == null) return new RespArray(new Resp[0]);

            log.info("now startIndex: {}, endIndex: {}, range len is {}", startIndex, endIndex, range.size());

            // 处理返回结果
            List<Resp> respList = new ArrayList<>();
            for (SkipList.SkipListNode<RedisBytes> node : range) {
                if (node == null || node.getMember() == null) continue;
                respList.add(new BulkString(node.getMember()));
                // 需要返回分数
                if (withScores) {
                    double score = node.getScore();
                    respList.add(new BulkString(new RedisBytes(Double.toString(score).getBytes())));
                }
            }
            return new RespArray(respList.toArray(new Resp[0]));
        } catch (Exception e) {
            log.error("ZRANGE error", e);
            return new Errors("ZRANGE error " + e.getMessage());
        }

    }
}
