package cn.sun.miniredis.command;

import cn.sun.miniredis.command.impl.Ping;
import cn.sun.miniredis.server.core.RedisCore;
import lombok.Getter;

import java.util.function.Function;

@Getter
public enum CommandType {
    PING (core -> new Ping());

    private final Function<RedisCore, Command> supplier;

    CommandType(Function<RedisCore, Command> supplier) {
        this.supplier = supplier;
    }
}
