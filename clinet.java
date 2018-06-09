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

public class clinet {
	Socket client;
	public void sendtoServer(byte[] message) throws IOException
	{
		//InputStream is=client.getInputStream();
		OutputStream os=client.getOutputStream();
		os.write(message);
	}
	
	public void recieve(byte[] message) throws IOException
	{
		InputStream is=client.getInputStream();
		is.read(message);
		
	}
}
