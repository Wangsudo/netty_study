package com.example.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * @Author: wangshun @Date: Create in 2018-07-02 13:48
 */
public class TimeServerHandler implements Runnable {

    private Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            String currentTime = null;
            String body = null;
            while (true) {
                body = in.readLine();    //读取一行，若读取到尾部则返回null，退出循环
                if (body == null){
                    System.out.println("break");
                    break;
                }
                System.out.println("The time server receive order :" + body);
                currentTime =
                        "QUERY TIME ORDER".equalsIgnoreCase(body)
                                ? new Date(System.currentTimeMillis()).toString()
                                : "BAD ORDER";
                out.append("server accept ");
                out.println(currentTime);
                /*out.write(currentTime);
                out.close();*/
            }
        } catch (IOException e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                this.socket = null;
            }
        }
    }
}
