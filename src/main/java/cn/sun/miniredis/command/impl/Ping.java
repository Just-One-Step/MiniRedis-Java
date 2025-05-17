package cn.sun.miniredis.command.impl;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.protocal.SimpleString;

public class Ping implements Command {
    @Override
    public CommandType getType() {
        return CommandType.PING;
    }

    @Override
    public void setContext(Resp[] array) {
        // 不需要内容
    }

    @Override
    public Resp handle() {
        return new SimpleString("PONG");
    }
}
