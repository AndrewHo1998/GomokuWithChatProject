package server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.util.ArrayList;
import java.util.List;
//import java.util.thread;
 
class myServerThread implements Runnable
{
    private ServerSocket serv;
    private Socket client=null;
    public int ServerN=0;
    String clientName;
    
    public myServerThread (int listN) throws Exception
    {
    	this.ServerN=listN;
    	serv=new ServerSocket(8000+listN);
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//serv.bind("127.0.0.1");
		try
		{
		this.client=serv.accept();
		//此步阻塞线程等待客户端连接
		InputStream is=client.getInputStream();
		OutputStream os=client.getOutputStream();
		
		
		}catch (IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	
    char[] ConectionEstablished(String clientName, String clientID)
    {
    	char[] Buffer=new char[50];
    	Buffer[0]='E';
    	clientName.getChars(1, clientName.length(), Buffer, 1);
    	clientID.getChars(0, clientID.length(), Buffer, 30);
    	return Buffer;
    }
    
    char[] Disconnection(String clientName, String clientID)
    {
    	char[] Buffer=new char[50];
    	Buffer[0]='D';
    	clientName.getChars(1, clientName.length(), Buffer, 1);
    	clientID.getChars(0, clientID.length(), Buffer, 30);
    	return Buffer;
    }
    
    char[] SendMessage(String toWhom, String context)
    {
    	char[] Buffer=new char[200];
    	Buffer[0]='M';
    	toWhom.getChars(0, toWhom.length(), Buffer, 1);
    	if(context.length()>=150)
    		return null;
    	context.getChars(0, context.length(), Buffer, 30);
    	return Buffer;
    }
    
}

//server的图形界面先放弃了！from 章昕
class MyPanel extends JFrame implements Runnable,ActionListener
{
    
    //定义参数
    String sender,getter;
    //MyBaseServer mbs = new MyBaseServer();
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
        /*if(mbs.get_ino != null)
        {
            String s_board = this.jta.getText() + getter;        //通过面板来获取记录
            this.jta.setText(s_board);
            mbs.get_ino = null;
        }*/
        this.repaint();    
    }
}
    @Override
  public void actionPerformed(ActionEvent e)
  {
        if(e.getActionCommand() == "send")
        {
            //发送信息
            //mbs.send_ino = sender;        //更新消息缓存
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

public class server {
   public static void main(String[] args) {
	   List<myServerThread> servList=new ArrayList<myServerThread>();
	   try{
	   ServerSocket listen=new ServerSocket(9000);
	   for(int i=0;i<20;i++)
	   {
		   myServerThread serv=new myServerThread(i);
		   servList.add(serv);
		   serv.run();
		   //创建20个空的server等待连接
	   }
	   }catch(Exception ioe)
	   {
		   System.out.println(ioe.getMessage());
	   }
	   
   }
}


