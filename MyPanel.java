package ChattingPanel;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.*;


public class MyPanel extends JPanel implements Runnable,ActionListener {
	
	static class MyBaseClient
	{
	    //�������������÷�����Ϣ�ͽ�����Ϣ�Ĳ�������
	    
	    static String send_ino,get_ino;
	    
	    //���������ط��͵���Ϣ
	    public String getsend(String s) {
	    	return send_ino;
	    }
	    //�õ��Է�������Ҫ��ʾ��message
	    public void receive(String s) {
	    	get_ino=s;
	    }

	}
	

	MyBaseClient mbs = new MyBaseClient();
	
	//���������ط��͵���Ϣ
	public String getsend(String s) {
		return this.mbs.getsend(s);
	}
	
	//�õ��Է�������Ҫ��ʾ��message
	public void receive (String s) {
		this.mbs.receive(s);
	}
	
	String sender,getter;
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
           
    }
    
    
   public MyPanel(int x, int y, int width, int height)
   {
       this();
       super.setBounds(x, y, width, height);
   }
   
   
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
           if(mbs.get_ino != null)
           {   
               String s_board = this.setmessage(getter);        //ͨ���������ȡ��¼
               this.jta.setText(s_board);
               mbs.get_ino = null;
           }
           this.repaint();    
       }
   }
    
   
   public void actionPerformed(ActionEvent e)
   {
         if(e.getActionCommand() == "send")
         {   
        	 mbs.send_ino = sender;
             String s_board = setmessage(sender);        //ͨ���������ȡ��¼              
             this.jta.setText(s_board);
             jt.setText(null);//�������
             this.repaint();    
         }
   }   
 
  //��ʽ����������ַ� 
public String setmessage(String input) {
	String output= new String();
	String s_board = this.jta.getText();
	input=input.trim();
    
	 while(input.length()>20) {
		 output=input.substring(0, 20);
		 input=input.substring(20);
		 for(int i=0;i<70;i++) {
          	output=" "+output;
          }
		 s_board=s_board+output+"\n";
	 }
	 for(int i=0;i<70;i++) {
       	input=" "+input;
       }
	 s_board=s_board+input+"\n";
	return s_board;
}
   

}
