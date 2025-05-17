package cn.sun.miniredis.protocal;

import io.netty.buffer.ByteBuf;

public interface Resp {

    public static final byte[] CRLF = "\r\n".getBytes();

    /**
     * SimpleString --> "+Message\r\n"
     * Errors --> "-Error Message\r\n"
     * RedisInteger --> ":Integer\r\n"
     * BulkString --> "$Length\r\nMessage\r\n"
     * RespArray --> "*Length\r\nMessage1\r\nMessage2\r\n..."
     */
    public static Resp decode(ByteBuf buf) {
        // 判断命令是否完整
        if (buf.readableBytes() <= 0) {
            throw new RuntimeException("Not a complete command");
        }

        char c = (char) buf.readByte();
        switch (c) {
            case '+':
                return new SimpleString(getString(buf));
            case '-':
                return new Errors(getString(buf));
            case ':':
                return new RespInteger(getInteger(buf));
            case '$':
                return new BulkString(getBytes(buf));
            case '*':
                return new RespArray(getRespArray(buf));
            default:
                throw new IllegalStateException("Not a valid command");
        }
    }

    static String getString(ByteBuf buf) {
        StringBuilder result = new StringBuilder();
        while (buf.readableBytes() > 0) {
            char c = (char) buf.readByte();
            if (c == '\r') {
                if (buf.readableBytes() <= 0 || buf.readByte() != '\n') {
                    throw new IllegalStateException("Not line break found ");
                }
            }
            result.append(c);
        }
        return result.toString();
    }

    static int getInteger(ByteBuf buf) {
        int result = 0;
        boolean positive = true;
        char c = (char) buf.readByte();
        if (c == '-') {
            positive = false;
        } else {
            result = c - '0';
        }
        while (buf.readableBytes() > 0) {
            c = (char) buf.readByte();
            if ('0' <= c && c <= '9') {
                result = result * 10 + (c - '0');
            } else if (c == '\r') {
                if (buf.readableBytes() <= 0 || buf.readByte() != '\n') {
                    throw new IllegalStateException("Not line break found ");
                }
                break;
            } else {
                throw new IllegalStateException("Not a valid integer");
            }
        }
        return positive ? result : -result;
    }

    static byte[] getBytes(ByteBuf buf) {
        // 进行长度校验
        int length = getInteger(buf);
        if (buf.readableBytes() < length + 2)  {
            throw new IllegalStateException("Not line break found");
        }

        byte[] result;
        if (length == -1)  {
            result = null;
        } else {
           result = new byte[length];
           buf.readBytes(result);
        }
        if (buf.readByte() != '\r' || buf.readByte() != '\n') {
            throw new IllegalStateException("Not line break found");
        }
        return result;
    }

    static Resp[] getRespArray(ByteBuf buf) {
        int number = getInteger(buf);
        Resp[] result = new Resp[number];
        for (int i = 0; i < number; i++) {
            result[i] = decode(buf);
        }
        return result;
    }

    public abstract void encode(Resp resp, ByteBuf byteBuf);
}
