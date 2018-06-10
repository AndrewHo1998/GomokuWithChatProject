package Chat;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.*;

public class ChatPanel extends JPanel implements Runnable, ActionListener {
    
    static class MyBaseClient {
        //定义参数，务必让发送信息和接收信息的参数共享
        
        static String send_ino, get_ino;
        
        
        //传出出本地发送的消息
        public String getsend(String s) {
            return send_ino;
        }
        
        
        //得到对方发的需要显示的message
        public void receive(String s) {
            get_ino = s;
        }
    }
    
    
    MyBaseClient mbs = new MyBaseClient();
    
    
    //传出出本地发送的消息
    public String getsend(String s) {
        return this.mbs.getsend(s);
    }
    
    
    //得到对方发的需要显示的message
    public void receive(String s) {
        this.mbs.receive(s);
    }
    
    
    String sender, getter;
    JPanel jp;
    JTextArea jta;
    JButton jb;
    JTextField jt;
    JScrollPane jsp;
    
    
    public ChatPanel() {
        
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
        jb.setActionCommand("sendPackage");
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
    
    
    public ChatPanel(int x, int y, int width, int height) {
        this();
        super.setBounds(x, y, width, height);
    }
    
    
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
            getter = mbs.get_ino;
            //更新面板
            //更新面板
            if (getter == null) //信息为空，跳过更新
            {
                continue;
            }
            if (mbs.get_ino != null) {
                String s_board = this.setmessage(getter);        //通过面板来获取记录
                this.jta.setText(s_board);
                mbs.get_ino = null;
            }
            this.repaint();
        }
    }
    
    
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("sendPackage")) {
            mbs.send_ino = sender;
            String s_board = setmessage(sender);        //通过面板来获取记录
            this.jta.setText(s_board);
            jt.setText(null);//清空内容
            this.repaint();
        }
    }
    
    
    //格式化输入输出字符
    public String setmessage(String input) {
        StringBuilder output;
        StringBuilder s_board = new StringBuilder(this.jta.getText());
        input = input.trim();
        
        while (input.length() > 20) {
            output = new StringBuilder(input.substring(0, 20));
            input = input.substring(20);
            for (int i = 0; i < 70; i++) {
                output.insert(0, " ");
            }
            s_board.append(output).append("\n");
        }
        StringBuilder inputBuilder = new StringBuilder(input);
        for (int i = 0; i < 70; i++) {
            inputBuilder.insert(0, " ");
        }
        input = inputBuilder.toString();
        s_board.append(input).append("\n");
        return s_board.toString();
    }
}