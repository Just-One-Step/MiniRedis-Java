package cn.sun.miniredis.protocal;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class RespInteger implements Resp {

    private final int content;

    public RespInteger(int content) {
        this.content = content;
    }

    @Override
    public void encode(Resp resp, ByteBuf byteBuf) {
        byteBuf.writeByte(':');
        int content = ((RespInteger) resp).getContent();
        byteBuf.writeBytes(String.valueOf(content).getBytes());
        byteBuf.writeBytes(Resp.CRLF);
    }
}
