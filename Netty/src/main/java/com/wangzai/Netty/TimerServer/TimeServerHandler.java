package com.wangzai.Netty.TimerServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServerHandler extends ChannelHandlerAdapter{


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


            ByteBuf buf = (ByteBuf)msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);

            String body = new String(req, "UTF-8");


            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                    new SimpleDateFormat("yyyy年-mm月-dd日:hh:ss").format(new Date(System.currentTimeMillis())).toString() :
                    "BAD ORDER";
            //response
            ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
            //异步发送应答消息给客户端: 这里并没有把消息直接写入SocketChannel,而是放入发送缓冲数组中
            ctx.writeAndFlush(resp);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

            //将发送缓冲区中数据全部写入SocketChannel
            //ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //释放资源
            ctx.close();
        }
    }

//    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
//            new SimpleDateFormat("yyyy年-mm月-dd日:hh:ss").format(new Date(System.currentTimeMillis())).toString() :
//            "BAD ORDER"