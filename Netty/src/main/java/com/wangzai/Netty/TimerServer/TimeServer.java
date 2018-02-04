package com.wangzai.Netty.TimerServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {

    public void bind(int port) throws Exception {
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    //功能对应于JDK NIO类库中的ServerSocketChannel类
                    .channel(NioServerSocketChannel.class)
                    //配置TCP参数，这里将backlog设置为1024
                    .option(ChannelOption.ALLOCATOR.SO_BACKLOG, 1024)
                    //绑定I/O事件的处理类ChildChannelHandler，它
                    .childHandler(new ChildChannelHandler());

            //绑定端口, 同步等待成功
            ChannelFuture future = b.bind(port).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();

        } finally {
            //释放线程资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast("timeServerHandler", new TimeServerHandler());
        }
    }


    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        new TimeServer().bind(port);
        System.out.println("Server Port:" + port);
    }
}
