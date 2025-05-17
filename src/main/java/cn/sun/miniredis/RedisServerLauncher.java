package cn.sun.miniredis;

import cn.sun.miniredis.server.RedisMiniServer;
import cn.sun.miniredis.server.RedisServer;

public class RedisServerLauncher {

    public static void main(String[] args) {
        RedisServer redisServer = new RedisMiniServer("localhost", 6379);
        redisServer.start();
    }

}
