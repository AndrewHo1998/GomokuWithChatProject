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
		//�˲������̵߳ȴ��ͻ�������
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

//server��ͼ�ν����ȷ����ˣ�from ���
class MyPanel extends JFrame implements Runnable,ActionListener
{
    
    //�������
    String sender,getter;
    //MyBaseServer mbs = new MyBaseServer();
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
        
        jt.setCaretColor(Color.RED);//jt��ɫ���
        jt.setForeground(Color.RED);//jt��ɫ����
        
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
        //�������
        //�������
        if(getter == null) //��ϢΪ�գ���������
        {
            continue;
        }
        /*if(mbs.get_ino != null)
        {
            String s_board = this.jta.getText() + getter;        //ͨ���������ȡ��¼
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
            //������Ϣ
            //mbs.send_ino = sender;        //������Ϣ����
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
		   //����20���յ�server�ȴ�����
	   }
	   }catch(Exception ioe)
	   {
		   System.out.println(ioe.getMessage());
	   }
	   
   }
}


