package cn.sun.miniredis.protocal;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class RespArray implements Resp{
    private final Resp[] content;

    public RespArray(Resp[] content) {
        this.content = content;
    }

    @Override
    public void encode(Resp resp, ByteBuf byteBuf) {
        byteBuf.writeByte('*');
        Resp[] content = ((RespArray) resp).getContent();
        byteBuf.writeBytes(Integer.toString(content.length).getBytes());
        byteBuf.writeBytes(Resp.CRLF);
        for (Resp r : content) {
            r.encode(r, byteBuf);
        }
    }
}
