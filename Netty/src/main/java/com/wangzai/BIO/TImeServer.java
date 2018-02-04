package com.wangzai.BIO;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//时间服务器
public class TImeServer {
    /**

     * @param args

     * @throws IOException

     */

    public static void main(String[] args) throws IOException {
        //端口
        int port = 8080;

        if (args.length > 0 && args != null) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        //服务端socket
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.print("timeserver port :" + port);
            //定义出客户端socket
            Socket socket = null;
            //如果没有客户端接入,操作阻塞在accept
            while (true) {
                //监听并获取客户端socket
                socket = serverSocket.accept();
                //调用处理程序,多线程
                new Thread(new TimerHandlerServer(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("the server close");
                serverSocket = null;
            }
        }


    }
}
