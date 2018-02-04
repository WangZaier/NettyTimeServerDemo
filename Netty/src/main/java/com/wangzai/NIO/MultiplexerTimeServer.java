package com.wangzai.NIO;


import javax.xml.ws.BindingType;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    //多路复用器
    private Selector selector;

    //监听客户端连接,他是所有客户端连接父管道
    private ServerSocketChannel serverChannel;

    //停止命令 volatile是为了立刻停止运行,防止stop无法刷新到主存
    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            //多路复用器启动
            selector = Selector.open();
            //打开服务器通道
            serverChannel = ServerSocketChannel.open();

            //设置参数
            //设置客户端连接为非堵塞模式
            serverChannel.configureBlocking(false);
            //绑定监听端口 , blocklog:队列容量
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            //注册通道到多路复用器上，开启监听事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.print("SERVER PORT:" + port);


        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                //设置超时
                selector.select(1000);
                //访问“已选择键集 , 这个对象代表了注册到该Selector的通道
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                for (SelectionKey key : selectionKeys) {
                    try {

                        handleInput(key);

                        if (key != null) {
                            key.cancel();
                            //关闭通道
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        //多路复用器关闭以后,所有Channel,pipe都会自动注册关闭
        if (selector != null) {
            try {
                selector.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //处理新接入消息
            if (key.isAcceptable()) {
                //获取通道
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                //阻塞,等待请求
                SocketChannel sc = ssc.accept();
                //设置非堵塞
                sc.configureBlocking(false);
                //注册复用器,设置可读
                sc.register(selector, SelectionKey.OP_READ);
            }

            if (key.isReadable()) {
                //读取数据
                SocketChannel sc = (SocketChannel) key.channel();
                //开启缓冲区
                ByteBuffer bf = ByteBuffer.allocate(1024);
                //将数据写入缓冲区
                int read = sc.read(bf);
                if (read > 0) {
                    //反转缓冲区（复位）
                    bf.flip();
                    //定义btye数组
                    byte[] bytes = new byte[bf.remaining()];
                    //接受缓冲区数据
                    bf.get(bytes);
                    String body = new String(bytes, "UTF-8").trim();
                    System.out.println(body);
                    System.out.println("Accept Order:" + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                            new SimpleDateFormat("yyyy年-mm月-dd日:hh:ss").format(new Date(System.currentTimeMillis())).toString() :
                            "BAD ORDER";
                    //客户端回写数据 , 提供通道,数据
                    dowrite(sc, currentTime);

                } else if (read < 0) {
                    //关闭链路
                    key.cancel();
                    sc.close();
                } else {
                    //读到0字节
                }
            }
        }
    }

    //数据回写
    private void dowrite(SocketChannel channel, String response) throws IOException {
        //数据体不为空
        if (response != null && response.trim().length() > 0) {
            System.out.println(response);
            //转byte
            byte[] bytes = response.getBytes();
            //开启缓冲区
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            //缓冲区
            writeBuffer.put(bytes);
            //重置
            writeBuffer.flip();
            //数据回写
            channel.write(writeBuffer);
        }
    }


}
