package server;
//需要测试！
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

final int port=9000;
class ThreadCommu//进程间信息交换
{
	
	int toID; //发送给谁
	String context; //内容
	char[] flag=new char[2]; //报文类别
	
	public ThreadCommu()
	{
		
	}
	
	public void writeCommu(int to, byte[]buffer) //写
	{
			this.toID=to;
			context=buffer.toString();		
	}
	public void readCommu(byte[] buffer) //都
	{
		context=buffer.toString();
	}
}

class myServerThread implements Runnable
{
    private ServerSocket serv;
    private Socket client=null;
    public int ServerN=0;
    String clientName;
    byte[] buffer=new byte[200];
    Queue<ThreadCommu> toMain=new SynchronousQueue<ThreadCommu>(); //向Main进程发送进程交流信息，进程安全，先进先出
    Queue<ThreadCommu> fromMain=new SynchronousQueue<ThreadCommu>(); //Main向本进程的信息
    
    public myServerThread (int listN) throws Exception
    {
    	this.ServerN=listN;
    	serv=new ServerSocket(9000+listN);//连接
    }
    
    public boolean read(byte[] buffer) throws IOException
    {
    	InputStream is=this.client.getInputStream();
 
    		is.read(buffer);
    		if(buffer.length!=0)//没有读到信息
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
		
		//告诉客户端客户端的编号
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
				String context=new String();
				for(int j=2;j<buffer.length;j++)
					context=context+buffer[j];
				
				commuWithMain.context=context;
				this.toMain.add(commuWithMain);//读到信息写进程通讯
				
			}
			for(int i=0;i<fromMain.size();i++)
			{
				OutputStream os=client.getOutputStream();
				fromMain.poll().readCommu(buffer);//从main对此进程的信息中读取，然后发送给client
				os.write(buffer);
				
			}
		}
		//此步阻塞线程等待客户端连接	
		}catch (IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	

    
    
    
}

//server的图形界面先放弃了！from 章昕


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
		   //创建2个空的server等待连接
	   }
	   while(!listen.isClosed())
	   {
		   for(int i=0;i<2;i++)
		   {
		   myServerThread tmp=servList.get(i);
		   while(!tmp.toMain.isEmpty())
		   {
			   ThreadCommu communication=tmp.toMain.poll();
			   servList.get(communication.toID).fromMain.add(communication);//把一个serverSocket接到的线程信息转给另外一个		   
		   }
		   }
	   }
	   }catch(Exception ioe)
	   {
		   System.out.println(ioe.getMessage());
	   }
	   
	   
   }
}


