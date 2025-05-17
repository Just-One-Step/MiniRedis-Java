package cn.sun.miniredis.server.handler;

import cn.sun.miniredis.protocal.Resp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RespEncoder extends MessageToByteEncoder<Resp> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Resp resp, ByteBuf byteBuf) throws Exception {
        try {
            resp.encode(resp, byteBuf);
        } catch (Exception e) {
            log.error("encode error", e);
            channelHandlerContext.channel().close();
        }
    }
}
