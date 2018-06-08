package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class client
{
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        client demoQQ_Server_1 = new client();
    }
    public client()
    {
        
        //��ʼ������,��ʼ����Ķ��߳�
        MyPanel mp;        
        mp = new MyPanel();
        
        Thread t1 = new Thread(mp);
        t1.start();
        
        //������getter
        MyClient mServer = new MyClient();
        Thread t2 = new Thread(mServer);
        t2.start();
        
        //������sender
        MyClient_writer myClient_writer = new MyClient_writer();
        Thread t3 = new Thread(myClient_writer);
        t3.start();
        
        /*
        //�����߳�
        Mytest mytest = new Mytest();
        Thread t4 = new Thread(mytest);
        t4.start();
        */
    }
}

//���������
class MyBaseClient
{
    //�������������÷�����Ϣ�ͽ�����Ϣ�Ĳ�������
    static Socket s;
    static String send_ino,get_ino;

}

class MyClient_writer extends MyBaseClient implements Runnable //QQд��
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
        }
    }
    //������Ϣ
        public void sendIno()
        {
            if(send_ino == null)
            {
                return;
            }
            try
            {
                //��������
                PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
                //��ȡ��������
                pw.println(send_ino);
                send_ino = "You :" + send_ino +"\n";
                
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
}

class MyClient extends MyBaseClient implements Runnable
{        
    public MyClient()
    {
        //��������Ҫ����
        try
        {
            s = new Socket("127.0.0.1", 9999);
            get_ino = "welcome to chat!\n";
        }
        catch (IOException e)
        {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }        
    }

    //���ݷ�����Ϣ
    public void getIno()
    {
        try
        {
            //������
            InputStreamReader inr = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(inr);
            get_ino = "Opponent :"+bf.readLine()+"\n";
        }
        catch (IOException e)
        {
            System.out.println("Error!");
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

        }
    }
}


//������
class MyPanel extends JFrame implements Runnable,ActionListener
{
    
    //�������
    String sender,getter;
    MyBaseClient mbc = new MyBaseClient();
    //�������
    JPanel jp;
    JTextArea jta;
    JButton jb;
    JTextField jt;
    JScrollPane jsp;
    
    public MyPanel()
    {
        

        //�������
        jp = new JPanel();
        jta = new JTextArea();
        jb = new JButton("����");
        jt = new JTextField();
        jsp = new JScrollPane(jta);
        
        jt.setCaretColor(Color.BLACK);//jt�����ɫ
        jt.setForeground(Color.BLACK);//jt������ɫ
        
        //��Ϣ��ֻ�ܶ�
        jta.setEditable(false);
        //��Ӽ���
        jb.setActionCommand("send");
        //ע�����
        jb.addActionListener(this);
        
        //������
        jp.setLayout(new GridLayout(2,1));
        
        jp.add(jt);
        jp.add(jb);
        
        this.setLayout(new GridLayout(2,1));
        this.add(jsp);
        this.add(jp);
        
        this.setSize(400,400);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("chatC");
        
    
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
        getter = mbc.get_ino;
        //�������
        if(getter == null) //��ϢΪ�գ���������
        {
            continue;
        }
        if(mbc.get_ino != null)
        {
            String s_board = this.jta.getText() + getter;        //ͨ���������ȡ��¼
            this.jta.setText(s_board);
            mbc.get_ino = null;
        }
        this.repaint();    
    }
}
    @Override
  public void actionPerformed(ActionEvent e)
  {
        if(e.getActionCommand() == "send")
        {
            //������Ϣ
            mbc.send_ino = sender;        //������Ϣ����
            String nwmes="You :" +sender +"\n";
            int len=nwmes.length();
            for(int i=0;i<80-len/4;i++) {
            	nwmes=" "+nwmes;
            }
            String s_board = this.jta.getText() + nwmes;        //ͨ���������ȡ��¼
            this.jta.setText(s_board);
            jt.setText(null);//�������
            this.repaint();    
        }
  }
}


//������
class Mytest extends MyBaseClient implements Runnable
{

    int times = 0;
  public void run()
  {
        // TODO Auto-generated method stub
        try
      {
            while(true)
            {
                times++;
                Thread.sleep(100);
                System.out.println("Time :"+ times + " " + get_ino);
            }
      }
      catch (Exception e)
      {
            // TODO: handle exception
      }
  }
    
}