package cn.sun.miniredis.protocal;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class SimpleString implements Resp {

    private final String content;

    public SimpleString(String content) {
        this.content = content;
    }

    @Override
    public void encode(Resp resp, ByteBuf byteBuf) {
        byteBuf.writeByte('+');
        String content = ((SimpleString) resp).getContent();
        byteBuf.writeBytes(content.getBytes());
        byteBuf.writeBytes(Resp.CRLF);
    }
}
