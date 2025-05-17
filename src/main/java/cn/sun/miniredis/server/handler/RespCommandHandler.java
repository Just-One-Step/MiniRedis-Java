package cn.sun.miniredis.server.handler;

import cn.sun.miniredis.command.Command;
import cn.sun.miniredis.command.CommandType;
import cn.sun.miniredis.protocal.BulkString;
import cn.sun.miniredis.protocal.Errors;
import cn.sun.miniredis.protocal.Resp;
import cn.sun.miniredis.protocal.RespArray;
import cn.sun.miniredis.server.core.RedisCore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RespCommandHandler extends SimpleChannelInboundHandler<Resp> {

    private final RedisCore redisCore;

    public RespCommandHandler(RedisCore redisCore) {
        this.redisCore = redisCore;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Resp resp) throws Exception {

        if (resp instanceof RespArray respArray) {
            Resp response = processCommand(respArray);

            if (response != null) {
                channelHandlerContext.channel().writeAndFlush(response);
            }
        } else {
            channelHandlerContext.channel().writeAndFlush(new Errors("Not supported command"));
        }
    }

    private Resp processCommand(RespArray respArray) {
        // 命令为空
        if (respArray.getContent().length == 0) {
            return new Errors("Empty command");
        }

        try {
            // 获取命令和参数
            Resp[] array = respArray.getContent();
            // 获取命令名称
            String commandName = new String(((BulkString) array[0]).getContent());
            // 兼容小写
            commandName = commandName.toUpperCase();

            CommandType commandType;

            try {
                commandType = CommandType.valueOf(commandName);
            } catch (IllegalArgumentException e) {
                return new Errors("Command not found");
            }

            Command command = commandType.getSupplier().apply(redisCore);
            command.setContext(array);
            return command.handle();

        } catch (Exception e) {
            log.error("process command error", e);
            return new Errors("Process command error");
        }
    }


}
