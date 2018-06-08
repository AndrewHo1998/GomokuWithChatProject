package server;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.*;
import java.net.*;

public class server
{
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        server demoQQ_Server = new server();
    }
    public server()
    {
        
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
class MyBaseServer
{
    //定义参数，务必让发送信息和接收信息的参数共享
    static Socket s;
    static ServerSocket ss;
    static String send_ino,get_ino;
}

class MyServer_writer extends MyBaseServer implements Runnable //QQ写者
{

    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        while(true)
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            sendIno();
            send_ino = null;
            //get_ino = null;
        }
    }
    //发送信息
        public void sendIno()
        {
            if(send_ino == null)
            {
                return;
            }
            try
            {
                //发送数据
                PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
                //获取发送内容
                pw.println(send_ino);
                send_ino = "Client :" + send_ino +"\n";
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
}

class MyServer extends MyBaseServer implements Runnable
{        
    public MyServer()
    {
        //下面是主要功能
        try
        {
            get_ino = "welcome to chat!\n";
            ss = new ServerSocket(9999);
            s = ss.accept();
        }
        catch (IOException e)
        {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }        
    }

    //传递返回信息
    public void getIno()
    {
        //要拥有一个判断去跳出这个读取，否则一旦进入try块，如果没有信息进来，读者会一直占用cpu而不会进入到写者，除非写者也开多线程
        /*
        if(get_ino == null)
        {
            return;
        }
        */
        try
        {
            //读数据
            InputStreamReader inr = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(inr);
            get_ino = "Opponent :"+bf.readLine()+"\n";
        }
        catch (IOException e)
        {
            return;
        }
    }
    
    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        while(true)
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
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


//界面类
class MyPanel extends JFrame implements Runnable,ActionListener
{
    
    //定义参数
    String sender,getter;
    MyBaseServer mbs = new MyBaseServer();
    //定义组件
    JPanel jp;
    JTextArea jta;
    JButton jb;
    JTextField jt;
    JScrollPane jsp;
    
    public MyPanel()
    {
        

        //创建组件
        jp = new JPanel();
        jta = new JTextArea();
        jb = new JButton("发送");
        jt = new JTextField();
        jsp = new JScrollPane(jta);
        
        jt.setCaretColor(Color.RED);//jt红色光标
        jt.setForeground(Color.RED);//jt红色字体
        
        //信息区只能读
        jta.setEditable(false);
        //添加监听
        jb.setActionCommand("send");
        
        
        //注册监听
        jb.addActionListener(this);
        
        //添加组件
        jp.setLayout(new GridLayout(2,1));
        
        jp.add(jt);
        jp.add(jb);
        
        this.setLayout(new GridLayout(2,1));
        this.add(jsp);
        this.add(jp);
        
        this.setSize(400,400);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("chatS");
        
    
    }
    public void paint(Graphics g)
    {
        super.paint(g);
    }
    @Override
public void run()
{
    while(true)
    {
        try
        {
            Thread.sleep(500);
           
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sender = this.jt.getText();
        getter = mbs.get_ino;
        //更新面板
        //更新面板
        if(getter == null) //信息为空，跳过更新
        {
            continue;
        }
        if(mbs.get_ino != null)
        {
            String s_board = this.jta.getText() + getter;        //通过面板来获取记录
            this.jta.setText(s_board);
            mbs.get_ino = null;
        }
        this.repaint();    
    }
}
    @Override
  public void actionPerformed(ActionEvent e)
  {
        if(e.getActionCommand() == "send")
        {
            //发送信息
            mbs.send_ino = sender;        //更新消息缓存
            String nwmes="You :" +sender +"\n";
            int len=nwmes.length();
            for(int i=0;i<80-len/4;i++) {
            	nwmes=" "+nwmes;
            }
            String s_board = this.jta.getText() + nwmes;        //通过面板来获取记录              
            this.jta.setText(s_board);
            jt.setText(null);//清空内容
            this.repaint();    
        }
  }
}