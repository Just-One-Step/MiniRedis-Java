package cn.sun.miniredis.server.handler;

import cn.sun.miniredis.protocal.Resp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RespDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        try {
            // 标记当前数据开始位置，当数据不完整时，重置读取位置
            if (byteBuf.readableBytes() > 0) {
                byteBuf.markReaderIndex();
            }

            // 数据不完整
            if (byteBuf.readableBytes() < 4) {
                return;
            }

            try {

                Resp resp = Resp.decode(byteBuf);
                if (resp != null) {
                    log.debug("decode resp: {}", resp);
                    out.add(resp);
                }

            } catch (Exception e) {
                log.error("decode error", e);
                // 数据不完整，重置读取位置
                byteBuf.resetReaderIndex();
            }
        } catch (Exception e) {
            log.error("decode error", e);
        }
    }
}
