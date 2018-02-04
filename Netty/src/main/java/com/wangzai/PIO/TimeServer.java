package com.wangzai.PIO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 支持把单个创建线程改成了线程池
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {

        int port = 8080;

        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        //开启服务端监听,创建线程池
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server port:" + port);
            Socket socket = null;
            //创建线程池
            TimeServerHandlerExecutePool pool = new TimeServerHandlerExecutePool(10, 10000);

            //开启监听
            while (true) {
                socket = serverSocket.accept();
                //将任务添加到线程池
                pool.pushExecutor(new TimerHandlerServer(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                System.out.println("The time server close");
                serverSocket.close();
                serverSocket = null;
            }

        }

    }
}
