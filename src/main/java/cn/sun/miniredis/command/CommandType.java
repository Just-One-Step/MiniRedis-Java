package cn.sun.miniredis.command;

import cn.sun.miniredis.command.impl.Ping;
import cn.sun.miniredis.command.impl.hash.Hdel;
import cn.sun.miniredis.command.impl.hash.Hget;
import cn.sun.miniredis.command.impl.hash.Hset;
import cn.sun.miniredis.command.impl.list.Lpop;
import cn.sun.miniredis.command.impl.list.Lpush;
import cn.sun.miniredis.command.impl.list.Lrange;
import cn.sun.miniredis.command.impl.string.Get;
import cn.sun.miniredis.command.impl.string.Set;
import cn.sun.miniredis.command.impl.set.*;
import cn.sun.miniredis.server.core.RedisCore;
import lombok.Getter;

import java.util.function.Function;

@Getter
public enum CommandType {
    PING (core -> new Ping()),
    SET (Set::new),
    GET (Get::new),
    SADD (Sadd::new),
    SPOP (Spop::new),
    SREM (Srem::new),
    LPUSH(Lpush::new),
    LPOP(Lpop::new),
    LRANGE(Lrange::new),
    HSET(Hset::new),
    HGET(Hget::new),
    HDEL(Hdel::new);

    private final Function<RedisCore, Command> supplier;

    CommandType(Function<RedisCore, Command> supplier) {
        this.supplier = supplier;
    }
}
