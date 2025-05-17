package cn.sun.miniredis.protocal;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class BulkString implements Resp {

    private static final byte[] NULL_BYTES = "-1\r\n".getBytes();
    private static final byte[] EMPTY_BYTES = "0\r\n\r\n".getBytes();
    private final byte[] content;

    public BulkString(byte[] content) {
        this.content = content;
    }

    @Override
    public void encode(Resp resp, ByteBuf byteBuf) {
        byteBuf.writeByte('$');
        byte[] content = ((BulkString) resp).getContent();
        if (content == null) {
            byteBuf.writeBytes(NULL_BYTES);
        } else if (content.length == 0) {
            byteBuf.writeBytes(EMPTY_BYTES);
        } else {
            byteBuf.writeBytes(String.valueOf(content.length).getBytes());
            byteBuf.writeBytes(Resp.CRLF);
            byteBuf.writeBytes(content);
            byteBuf.writeBytes(Resp.CRLF);
        }
    }
}
