package server;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//import java.util.thread;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import server.myServerThread.ThreadCommu; 




class myServerThread implements Runnable
{
    //private Socket serv;
	private ServerSocket serv;
    private Socket client;
    private Socket client2;
    //public int ServerN=0;
    String clientName;
    byte[] buffer=new byte[200];
    class ThreadCommu//���̼���Ϣ����
    {
    	byte from;
    	int toID; //���͸�˭
    	String context; //����
    	char[] flag=new char[2]; //�������
    	
    	public ThreadCommu()
    	{
    		
    	}
    	
    	public void writeCommu(int to, byte[]buffer) //д
    	{
    			this.toID=to;
    			context=new String();
    			for(int i=0;i<buffer.length;i++)
    				context=context+buffer[i];

    	}
    	public void readCommu(byte[] buffer) //��
    	{
    		for(int i=0;i<this.context.length();i++)
    			buffer[i]=(byte)context.charAt(i);
    	}
    }
    Queue<ThreadCommu> toC1=new SynchronousQueue<ThreadCommu>(); //��Main���̷��ͽ��̽�����Ϣ�����̰�ȫ���Ƚ��ȳ�
    Queue<ThreadCommu> toC2=new SynchronousQueue<ThreadCommu>(); //Main�򱾽��̵���Ϣ
    
    public myServerThread (int listN, ServerSocket servMain) throws Exception
    {
    	//this.ServerN=listN;
    	this.serv=servMain;
    	this.client=new Socket();
    	//����
    	System.out.println("Socket established."+listN);
    }
    
    public void setFrom(byte firstBy, ThreadCommu commu)
    {
    	commu.from=firstBy;
    }
    
    public int retrunLength(byte[] head) throws IOException
    {
    	int i=(int)(head[1]<<12)& 0xFF +(head[2]<<8)&0xFF +(head[3]<<4)&0xFF +head[3]&0xFF;
    	return i;
    }
    public int tryRead(byte[] buffer) throws IOException
    {
    	InputStream is=this.client.getInputStream();
    	for(int i=0;i<5;i++)
    	{
    		buffer[i]=(byte)is.read();
    	}
    	if(buffer.length==0)
    		return -1;
    	else
    	{
    		
    		int length=(int)(buffer[1]<<12+buffer[2]<<8+buffer[3]<<4+buffer[4]);
    		return length;
    	}
    }
    public void read(byte[] buffer) throws IOException
    {
    	InputStream is=this.client.getInputStream();
    		is.read(buffer);
    }
    
    public void sendto(byte[] message,int clientN) throws IOException
    {
    	if(clientN==1)
    		
    		{
    		OutputStream os=this.client.getOutputStream();
    		os.write(message);
    		}
    	else
    	{
    		OutputStream os =this.client2.getOutputStream();
    		os.write(message);
    	}
    	//os.write(message);
    }
    
    public void sendtoPlayer(byte[] message)
    {
    	
    }
    
    public void close() throws IOException
    {
    	this.client.close();
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try
		{
			//System.out.println("Server running on "+client.getLocalPort());
			client=serv.accept();
			System.out.println("client1 running on "+client.getLocalPort());
			//this.client=serv.
			client2=serv.accept();
			System.out.println("client2 running on "+client.getLocalPort());
		//���߿ͻ��˿ͻ��˵ı��
		buffer[0]=0x0;//����server
		buffer[1]=0x01;
		buffer[2]=0x0;
		buffer[3]=0x0;
		buffer[4]=0x0;//����Ϊ1byte
		
		
		//this.send(buffer);
		
		while(!client.isClosed())
		{
			ThreadCommu commuWithMain=new ThreadCommu();
			byte[] headBuf=new byte[5];
			int length=tryRead(headBuf);
			if(-1!=length)
			{
				System.out.println("Recieved message from client");
				this.setFrom(headBuf[0], commuWithMain);
				byte[] restBuf=new byte[length];
				this.read(restBuf);
					
				String context=new String();
				for(int j=2;j<buffer.length;j++)
					context=context+buffer[j];
				
				commuWithMain.context=context;
				this.toC1.add(commuWithMain);//������Ϣд����ͨѶ
				
			}//����Ϣ
			
			for(int i=0;i<toC2.size();i++)
			{
				OutputStream os=client.getOutputStream();
				toC2.poll().readCommu(buffer);//��main�Դ˽��̵���Ϣ�ж�ȡ��Ȼ���͸�client
				os.write(buffer);
				
			}//����Ϣ
		}
		//�˲������̵߳ȴ��ͻ�������	
		}catch (IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	

    
    
    
}

//server��ͼ�ν����ȷ����ˣ�from ���


public class server {
	public static Map<String,Integer>clientMap=new HashMap<String, Integer>();
   public static void main(String[] args) {
	   List<myServerThread> servList=new ArrayList<myServerThread>();
	   try{
	   ServerSocket listen=new ServerSocket(9001);
	   //listen.bind("127.0.0.1");
	   for(int i=0;i<2;i++)
	   {
		   myServerThread serv=new myServerThread(i,listen);
		   servList.add(serv);
		   serv.run();
		   //����2���յ�server�ȴ�����
	   }
	   while(!listen.isClosed())
	   {
		   for(int i=0;i<2;i++)
		   {
		   myServerThread tmp=servList.get(i);
		   while(!tmp.toC1.isEmpty())
		   {
			   ThreadCommu communication=tmp.toC1.poll();
			   servList.get(communication.toID).toC2.add(communication);//��һ��serverSocket�ӵ����߳���Ϣת������һ��		   
		   }
		   }
		   listen.close();
		   //servList.get(0).close();
		   //servList.get(1).close();
		
	   }
	   }catch(Exception ioe)
	   {
		   System.out.println(ioe.getMessage());
	   }
	   
	   
   }
}


