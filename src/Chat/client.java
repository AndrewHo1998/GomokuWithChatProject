package Chat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        client demoQQ_Server_1 = new client();
    }
    
    
    public client() {
        
        //初始化界面,开始界面的多线程
        MyPanel mp;
        mp = new MyPanel();
        
        Thread t1 = new Thread(mp);
        t1.start();
        
        //服务器getter
        MyClient mServer = new MyClient();
        Thread t2 = new Thread(mServer);
        t2.start();
        
        //服务器sender
        MyClient_writer myClient_writer = new MyClient_writer();
        Thread t3 = new Thread(myClient_writer);
        t3.start();
        
        /*
        //测试线程
        Mytest mytest = new Mytest();
        Thread t4 = new Thread(mytest);
        t4.start();
        */
    }
}


//共享参数类
class MyBaseClient {
    //定义参数，务必让发送信息和接收信息的参数共享
    static Socket s;
    static String send_ino, get_ino;
}


class MyClient_writer extends MyBaseClient implements Runnable //QQ写者
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
            send_ino = "You :" + send_ino + "\n";
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


class MyClient extends MyBaseClient implements Runnable {
    public MyClient() {
        //下面是主要功能
        try {
            s = new Socket("127.0.0.1", 9999);
            get_ino = "welcome to chat!\n";
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    //传递返回信息
    public void getIno() {
        try {
            //读数据
            InputStreamReader inr = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(inr);
            get_ino = "Opponent :" + bf.readLine() + "\n";
        }
        catch (IOException e) {
            System.out.println("Error!");
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
        }
    }
}


//界面类
class MyPanel extends JPanel implements Runnable, ActionListener {
    
    //定义参数
    String sender, getter;
    MyBaseClient mbc = new MyBaseClient();
    //定义组件
    JPanel jp;
    JTextArea jta;
    JButton jb;
    JTextField jt;
    JScrollPane jsp;
    
    
    public MyPanel() {
        
        //创建组件
        jp = new JPanel();
        jta = new JTextArea();
        jb = new JButton("发送");
        jt = new JTextField();
        jsp = new JScrollPane(jta);
        
        jt.setCaretColor(Color.BLACK);//jt光标颜色
        jt.setForeground(Color.BLACK);//jt字体颜色
        
        //信息区只能读
        jta.setEditable(false);
        //添加监听
        jb.setActionCommand("send");
        //注册监听
        jb.addActionListener(this);
        
        //添加组件
        jp.setLayout(new GridLayout(2, 1));
        
        jp.add(jt);
        jp.add(jb);
        
        this.setLayout(new GridLayout(2, 1));
        this.add(jsp);
        this.add(jp);
        
        this.setSize(400, 400);
        this.setVisible(true);
    }

    
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            sender = this.jt.getText();
            getter = mbc.get_ino;
            //更新面板
            if (getter == null) //信息为空，跳过更新
            {
                continue;
            }
            if (mbc.get_ino != null) {
                String s_board = this.jta.getText() + getter;        //通过面板来获取记录
                this.jta.setText(s_board);
                mbc.get_ino = null;
            }
            this.repaint();
        }
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "send") {
            //发送信息
            mbc.send_ino = sender;        //更新消息缓存
            String nwmes = "You :" + sender + "\n";
            int len = nwmes.length();
            for (int i = 0; i < 80 - len / 4; i++) {
                nwmes = " " + nwmes;
            }
            String s_board = this.jta.getText() + nwmes;        //通过面板来获取记录
            this.jta.setText(s_board);
            jt.setText(null);//清空内容
            this.repaint();
        }
    }
}


//测试类
class Mytest extends MyBaseClient implements Runnable {
    
    int times = 0;
    
    
    public void run() {
        // TODO Auto-generated method stub
        try {
            while (true) {
                times++;
                Thread.sleep(100);
                System.out.println("Time :" + times + " " + get_ino);
            }
        }
        catch (Exception e) {
            // TODO: handle exception
        }
    }
}