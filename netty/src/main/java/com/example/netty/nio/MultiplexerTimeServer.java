package com.example.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by wangshun on 2018/7/16
 **/
public class MultiplexerTimeServer implements Runnable {
    private Selector selector;

    private ServerSocketChannel servChannel;

    private volatile boolean stop;


    public MultiplexerTimeServer(int port) {
        try {
            // create Selector ,ServerSocketChannel
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            // config Channel --->  no    and TCP
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port),1024);
            // The Channel registered to selector and listening to the SelectionKey.OP_ACCEPT
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is Start in port : " + port);
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
                // 每隔1秒唤醒一次
                selector.select(1000);
                // 当有就绪状态的channel时， selector 返回 channel的SelectorKey集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                SelectionKey key = null;
                // 对channel集合进行迭代，网络的异步读写操作
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Throwable t) {   // ??
                t.printStackTrace();
            }

        }

        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {

        if (key.isValid()) {
            if (key.isAcceptable()) {
                // accept the new connection
                ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
                // 接受客户端连接 并创建  SocketChannel的实例    ----》 三次握手   tcp物理链路正式成立
                SocketChannel sc = ssc.accept();
                //设置为异步
                sc.configureBlocking(false);
                //add the new connection to the selector
                sc.register(selector, SelectionKey.OP_READ);
            }

            if (key.isReadable()) {
                //Read the data
                SocketChannel sc = (SocketChannel) key.channel();

                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    // 将缓冲区的limit 设置为position   position 为 0 ，用于后续对缓冲区的读取操作
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server recive order : " + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                    doWrite(sc, currentTime);
                } else if (readBytes < 0) {
                    //close
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    /**
     * 将异步消息发送给客户端
     * @param channel
     * @param response
     * @throws IOException
     */
    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {

            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }
}
