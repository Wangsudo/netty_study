package com.example.netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: wangshun @Date: Create in 2018-07-02 11:36
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8090;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);  //通过构造函数创建ServerScoket
            System.out.println("The time server is start in port：" + port);
            Socket socket = null;
            while (true) {  //通过无限循环来监听客户端的连接
                socket = server.accept();  //如果没有客户端连接 将阻塞在 accpet这里
                new Thread(new TimeServerHandler(socket)).start();
            }
        } catch (IOException e) {
            //
        } finally {
            if (server != null) {
                System.out.println("The time server close");
                try {
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
