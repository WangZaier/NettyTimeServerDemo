package com.wangzai.BIO;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

//时间服务器处理
public class TimerHandlerServer implements Runnable {

    Socket socket = null;

    public TimerHandlerServer(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        //读取缓冲区
        BufferedReader reader = null;
        //输出
        PrintWriter writer = null;

        try {
            //从socket中读取流
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            //从socket中读取输出流
            writer = new PrintWriter(this.socket.getOutputStream(),true);

            String currentTime = null;
            String body = null;

            while (true) {
                //逐行读取
                body = reader.readLine();
                //如果内容体("命令")为空则推出循环
                if (body == null)
                    break;

                System.out.print("Client Order:" + body);
                //根据命令获取时间
                currentTime = "QUERY TIME ORDER".equals(body) ?
                        new SimpleDateFormat("yyyy年-mm月-dd日:hh:ss").format(new Date(System.currentTimeMillis())).toString() :
                        "Bad ORDER";
                //输出流 数据回显示
                writer.println(currentTime);

            }

        } catch (IOException e) {
            //关闭读取流
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (writer != null) {
                writer.close();
            }
        }


    }
}
