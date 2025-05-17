package cn.sun.miniredis.command;

import cn.sun.miniredis.protocal.Resp;

public interface Command {

    CommandType getType();

    void setContext(Resp[] array);

    Resp handle();

}
