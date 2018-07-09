package com.example.netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 采用线程池和任务队列可以实现伪异步的I/O通讯框架，只是服务器做出改变，客户端不变。
 *
 * 主要处理类的线程池，当收到新的客户连接的时候。将请求socket封装成一个task，然后调用线程池的execute的方法执行，从而避免了每个请求都哟创建一个新线程
 *
 * 伪异步只是
 * @Author: wangshun
 * @Date: Create in 2018-07-09 9:29
 */
public class ThreadPoolTimeServer {
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
            TimeServerHandlerExecutePool singleExecutor
                    = new TimeServerHandlerExecutePool(50,10000); //创建I/O任务线程池
            while (true) {  //通过无限循环来监听客户端的连接
                socket = server.accept();  //如果没有客户端连接 将阻塞在 accpet这里
                singleExecutor.execute(new TimeServerHandler(socket));
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
