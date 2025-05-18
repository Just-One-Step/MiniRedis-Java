package cn.sun.miniredis.protocal;

import cn.sun.miniredis.datastructure.RedisBytes;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class BulkString implements Resp {

    public static final byte[] NULL_BYTES = "-1\r\n".getBytes();
    public static final byte[] EMPTY_BYTES = "0\r\n\r\n".getBytes();
    private final RedisBytes content;

    public BulkString(RedisBytes content) {
        this.content = content;
    }

    public BulkString(byte[] content) {
        this.content = new RedisBytes(content);
    }

    @Override
    public void encode(Resp resp, ByteBuf byteBuf) {
        byteBuf.writeByte('$');
        RedisBytes content = ((BulkString) resp).getContent();
        if (content == null) {
            byteBuf.writeBytes(NULL_BYTES);
        } else if (content.getBytes().length == 0) {
            byteBuf.writeBytes(EMPTY_BYTES);
        } else {
            byteBuf.writeBytes(String.valueOf(content.getBytes().length).getBytes());
            byteBuf.writeBytes(Resp.CRLF);
            byteBuf.writeBytes(content.getBytes());
            byteBuf.writeBytes(Resp.CRLF);
        }
    }
}
