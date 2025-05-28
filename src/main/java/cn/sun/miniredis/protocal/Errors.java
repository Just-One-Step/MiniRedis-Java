package cn.sun.miniredis.protocal;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
public class Errors implements Resp {

    private final String content;

    public Errors(String content) {
        this.content = content;
    }

    @Override
    public void encode(Resp resp, ByteBuf byteBuf) {
        byteBuf.writeByte('-');
        String content =  ((Errors) resp).getContent();
        byteBuf.writeBytes(content.getBytes(StandardCharsets.UTF_8));
        byteBuf.writeBytes(Resp.CRLF);
    }
}
