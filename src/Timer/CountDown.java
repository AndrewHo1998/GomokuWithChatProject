package Timer;

import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class CountDown extends JPanel{
	int remainSec;
	final static int maxTime = 30;
	JLabel labelS;
    Start ss;
    int width,height;
    Font numberFont;
    public CountDown(int width, int height){

        this.width = width;
        this.height = height;
        this.setSize(width, height);
        numberFont= new Font("Consolas", Font.BOLD, height);

         // second
         remainSec = maxTime;
         labelS=new JLabel("");
         labelS.setBounds(0, 0, width, height);
         labelS.setFont(numberFont);
         labelS.setHorizontalAlignment(JLabel.CENTER);

         this.add(labelS);
         this.setVisible(true);

     }
     public void start() {	
     	ss=new Start(this);
        ss.start();
     }
     public void pause() {
     	 new Pause(ss).start();
     }
     public void stop() {
         new Pause(ss).start();
         setTime(maxTime);
         labelS.setText("");
     }
     public void showTime() {
     	NumberFormat nf = NumberFormat.getInstance();
         nf.setMinimumIntegerDigits(2) ;
         labelS.setText(nf.format(remainSec));

     }
     public void setTime(int s) {

     	this.remainSec = s;
     }
     public int getS() {
     	return remainSec;
     } 
     
     class Start extends Thread{
		 CountDown cd;
	
	     public Start(CountDown cd){
	         this.cd = cd;
	     }
	     @Override
	     public void run() {
	         int time_s;
	         time_s = cd.getS();
	         cd.showTime();
	
	         while (true) {
	             try {
	                 Thread.sleep(1000);     
	             } catch (InterruptedException e) {
	                 break;  //结束线程
	             }
	             time_s--;
	             if (time_s < 0) {
	                break;
	             }
	             cd.setTime(time_s);
	             cd.showTime();
	         }
	     }
	
	 }

     class Pause extends Thread{
	     Start ss;
	     public Pause(Start ss) {
	         this.ss=ss;
	     }
	     @Override
	     public void run() {
	     	try {
	         ss.interrupt();
	     	}catch(java.lang.NullPointerException e) {
	     		
	     	}
	     }
	 }
	
 }

