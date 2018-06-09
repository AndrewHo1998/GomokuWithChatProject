package server;


//����Ҫ�������߳̽���ͬsocket����֮�����Ϣ���͵���ȷ��server socket

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


class ThreadCommu//���̼���Ϣ����
{
	
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

class myServerThread implements Runnable
{
    private ServerSocket serv;
    private Socket client=null;
    public int ServerN=0;
    String clientName;
    byte[] buffer=new byte[200];
    Queue<ThreadCommu> toMain=new SynchronousQueue<ThreadCommu>(); //��Main���̷��ͽ��̽�����Ϣ�����̰�ȫ���Ƚ��ȳ�
    Queue<ThreadCommu> fromMain=new SynchronousQueue<ThreadCommu>(); //Main�򱾽��̵���Ϣ
    
    public myServerThread (int listN) throws Exception
    {
    	this.ServerN=listN;
    	serv=new ServerSocket(9000+listN);//����
    }
    
    public boolean read(byte[] buffer) throws IOException
    {
    	InputStream is=this.client.getInputStream();
 
    		is.read(buffer);
    		if(buffer.length!=0)//û�ж�����Ϣ
    			return true;
    		else
    			return false;
    	
    }
    
    public void send(byte[] message) throws IOException
    {
    	OutputStream os=this.client.getOutputStream();
    	os.write(message);
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try
		{
		this.client=serv.accept();
		
		//���߿ͻ��˿ͻ��˵ı��
		buffer[0]=14& 0xF;
		buffer[1]=14 & 0xF0;
		String tmp=String.valueOf(ServerN);
		byte[] adbuf=tmp.getBytes();
		for(int k=0;k<adbuf.length;k++)
			buffer[2+k]=adbuf[k];
		send(buffer);
		
		while(!client.isClosed())
		{
			if(read(buffer)==true)
			{
				System.out.println("Recieved message from No."+this.ServerN+" client.");
				
				ThreadCommu commuWithMain=new ThreadCommu();
				commuWithMain.flag[0]=(char)buffer[0];	commuWithMain.flag[1]=(char)buffer[1];
				
				if(ServerN==1)
					commuWithMain.toID=2;
				else
					commuWithMain.toID=1;
				
				String context=new String();
				for(int j=2;j<buffer.length;j++)
					context=context+buffer[j];
				
				commuWithMain.context=context;
				this.toMain.add(commuWithMain);//������Ϣд����ͨѶ
				
			}
			for(int i=0;i<fromMain.size();i++)
			{
				OutputStream os=client.getOutputStream();
				fromMain.poll().readCommu(buffer);//��main�Դ˽��̵���Ϣ�ж�ȡ��Ȼ���͸�client
				os.write(buffer);
				
			}
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
	   ServerSocket listen=new ServerSocket(9000);
	   for(int i=0;i<2;i++)
	   {
		   myServerThread serv=new myServerThread(i);
		   servList.add(serv);
		   serv.run();
		   //����2���յ�server�ȴ�����
	   }
	   while(!listen.isClosed())
	   {
		   for(int i=0;i<2;i++)
		   {
		   myServerThread tmp=servList.get(i);
		   while(!tmp.toMain.isEmpty())
		   {
			   ThreadCommu communication=tmp.toMain.poll();
			   servList.get(communication.toID).fromMain.add(communication);//��һ��serverSocket�ӵ����߳���Ϣת������һ��		   
		   }
		   }
	   }
	   }catch(Exception ioe)
	   {
		   System.out.println(ioe.getMessage());
	   }
	   
	   
   }
}


