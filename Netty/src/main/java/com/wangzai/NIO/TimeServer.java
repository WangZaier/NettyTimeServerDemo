package com.wangzai.NIO;

public class TimeServer {


    public static void main(String[] args) {
        int port = 8080;

        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        //创建多路复用器处理
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "MultiplexerTimeServer").start();

    }
}
