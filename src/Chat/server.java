package Chat;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.*;
import java.net.*;

public class server {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        server demoQQ_Server = new server();
    }
    
    
    public server() {
        
        //初始化界面,开始界面的多线程
        MyPanel mp;
        mp = new MyPanel();
        
        Thread t1 = new Thread(mp);
        t1.start();
        
        //服务器getter
        MyServer mServer = new MyServer();
        Thread t2 = new Thread(mServer);
        t2.start();
        
        //服务器sender
        MyServer_writer myClient_writer = new MyServer_writer();
        Thread t3 = new Thread(myClient_writer);
        t3.start();
    }
}


//共享参数类
class MyBaseServer {
    //定义参数，务必让发送信息和接收信息的参数共享
    static Socket s;
    static ServerSocket ss;
    static String send_ino, get_ino;
}


class MyServer_writer extends MyBaseServer implements Runnable //QQ写者
{
    
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            sendIno();
            send_ino = null;
            //get_ino = null;
        }
    }
    
    
    //发送信息
    public void sendIno() {
        if (send_ino == null) {
            return;
        }
        try {
            //发送数据
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            //获取发送内容
            pw.println(send_ino);
            send_ino = "Client :" + send_ino + "\n";
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class MyServer extends MyBaseServer implements Runnable {
    public MyServer() {
        //下面是主要功能
        try {
            get_ino = "welcome to chat!\n";
            ss = new ServerSocket(9999);
            s = ss.accept();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    //传递返回信息
    public void getIno() {
        //要拥有一个判断去跳出这个读取，否则一旦进入try块，如果没有信息进来，读者会一直占用cpu而不会进入到写者，除非写者也开多线程
        /*
        if(get_ino == null)
        {
            return;
        }
        */
        try {
            //读数据
            InputStreamReader inr = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(inr);
            get_ino = "Opponent :" + bf.readLine() + "\n";
        }
        catch (IOException e) {
            return;
        }
    }
    
    
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            getIno();
            //sendIno();
            // send_ino = null;
            //get_ino = null;
        }
    }
}

