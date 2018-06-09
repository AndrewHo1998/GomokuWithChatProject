package Gomoku.Timer;

import java.awt.Font;
import java.text.NumberFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TimerPanel extends JPanel{

    int hour,min,sec;
    JLabel labelH, labelM, labelS;
    Start ss;
    int width,height;
    Font numberFont;

    public TimerPanel(int width, int height){

       this.width = width;
       this.height = height;
       this.setSize(width, height);
       numberFont= new Font("Consolas", Font.PLAIN, 25);

        // hour
       labelH=new JLabel("00");
       labelH.setBounds(0, height, 1/4*width, height);
       labelH.setFont(numberFont);
       labelH.setHorizontalAlignment(JLabel.CENTER);

        // colon
        JLabel m_1=new JLabel(":");
        m_1.setBounds(1/4*width, 0, 1/8*width, height);
        m_1.setFont(numberFont);

        // minute
        labelM=new JLabel("00");
        labelM.setBounds(3/8*width, 0, 1/4*width, height);
        labelM.setFont(numberFont);
        labelM.setHorizontalAlignment(JLabel.CENTER);

        // colon
        JLabel m_2=new JLabel(":");
        m_2.setBounds(5/8*width, 0, 1/8*width, height);
        m_2.setFont(numberFont);

        // second
        labelS=new JLabel("00");
        labelS.setBounds(3/4*width, 0, 1/4*width, height);
        labelS.setFont(numberFont);
        labelS.setHorizontalAlignment(JLabel.CENTER);

        this.add(labelH);
        this.add(m_1);
        this.add(labelM);
        this.add(m_2);
        this.add(labelS);
        this.setVisible(true);

    }
    public void start() {  // start or continue
    	ss=new Start(this);
        ss.start();
    }
    public void pause() {
    	 new Pause(ss).start();
    }
    public void stop() {  // refresh data
    	setTime(0,0,0);
    	showTime();
        new Pause(ss).start();
    }
    public void showTime() {
    	NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2) ;     
        this.labelH.setText(nf.format(hour));
        this.labelM.setText(nf.format(min));
        this.labelS.setText(nf.format(sec));

    }
    public void setTime(int h, int m, int s) {
    	this.hour = h;
    	this.min = m;
    	this.sec = s;
    }
    public int getH() {
    	return hour;
    }
    public int getM() {
    	return min;
    }
    public int getS() {
    	return sec;
    }
    
	class Start extends Thread{
		TimerPanel tp;
	
	    public Start(TimerPanel tp){
	        this.tp = tp;
	    }
	    @Override
	    public void run() {
	        int time_s,time_m,time_h;
	        time_s=tp.getS();
	        time_m=tp.getM();
	        time_h=tp.getH();
	
	        while (true) {
	            try {
	                Thread.sleep(1000);     
	            } catch (InterruptedException e) {
	                break;  // break loop
	            }
	            time_s++;
	            if (time_s>=60) {
	                time_m++;
	                time_s=0;  
	                if(time_m>=60){
	                    time_h++;
	                    time_m=0;
	                    if(time_m>=24){
	                        time_h=0;
	                    }
	                }
	            }
	            tp.setTime(time_h, time_m, time_s);
	            tp.showTime();
	
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
