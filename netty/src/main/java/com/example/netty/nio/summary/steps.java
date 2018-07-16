package com.example.netty.nio.summary;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by wangshun on 2018/7/16
 **/
public class steps {

    public static void main(String[] args) {
        try {
            int port = 8080;
            //步骤1  打开ServerSocketChannel,用于监听客户端的连接，他是所有客户端连接的父管道
            ServerSocketChannel accpetorSvr = ServerSocketChannel.open();

            //步骤2  绑定监听端口，设置连接为非阻塞模式，
            accpetorSvr.socket().bind(new InetSocketAddress(InetAddress.getByName("IP"), port));
            accpetorSvr.configureBlocking(false);

            //步骤3  创建Reactor线程，创建多路复用器并启动线程
            Selector selector = Selector.open();
            new Thread(new ReactorTask()).start();

            //步骤4  将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听ACCEPT事件，
            SelectionKey key = accpetorSvr.register(selector, SelectionKey.OP_ACCEPT, ioHandler);

            //步骤5  多路复用器在线程run方法的无限循环体内轮询准备就绪的Key
            int num = selector.select();
            Set selectedKeys = selector.selectedKeys();
            Iterator it = selectedKeys.iterator();
            while (it.hasNext()) {
                SelectionKey keys = (SelectionKey) it.next();
            }

            /* ************************************** */


            //步骤6  多路复用器监听到有新的客户端接入，处理新的接入请求，完成TCP三次握手，建立物理链路
            SocketChannel channel = svrChannel.accpet();

            //步骤7  设置客户端链路为非阻塞模式
            channel.configureBlocking(false);
            channel.socket().setReuseAddress(true);

            //步骤8  将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读写操作，读取客户端发送的网络消息
            SelectionKey key2 = channel.register(selector, SelectionKey.OP_READ, ioHandler);


            //步骤9  异步读取客户端请求消息到缓冲区
            int readNumber = channel.read(receivedBuffer);


            //步骤10  对ByteBuffer进行编解码，如果有半包消息指针reset，继续读取后续的报文，将解析成功的消息封装成task，投递到业务线程池中，进行业务逻辑编排
            Object message = null;
            while (buffer.hasRemain()) {
                byteBuuffer
            }

            //步骤11


            //步骤12


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
