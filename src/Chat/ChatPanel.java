package Chat;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {
    //定义参数
    String sender, getter;
    MyBaseClient mbc = new MyBaseClient();
    //定义组件
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
        jb.setActionCommand("send");
        
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
    
    
}
