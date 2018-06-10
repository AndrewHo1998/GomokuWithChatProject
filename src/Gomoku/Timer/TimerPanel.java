package Gomoku.Timer;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public class TimerPanel extends JPanel {
    private int hour, min, sec;
    private JLabel hourLabel, minLabel, secLabel;
    private StartManager start;
    
    
    public TimerPanel(int width, int height) {
        this.setSize(width, height);
        Font numberFont = new Font("Consolas", Font.PLAIN, height / 4);
        
        // hour
        hourLabel = new JLabel("00");
        hourLabel.setBounds(0, height, width / 4, height);
        hourLabel.setFont(numberFont);
        hourLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // colon
        JLabel m_1 = new JLabel(":");
        m_1.setBounds(width / 4, 0, width / 8, height);
        m_1.setFont(numberFont);
        
        // minute
        minLabel = new JLabel("00");
        minLabel.setBounds(3 * width / 8, 0, width / 4, height);
        minLabel.setFont(numberFont);
        minLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // colon
        JLabel m_2 = new JLabel(":");
        m_2.setBounds(5 * width / 8, 0, width / 8, height);
        m_2.setFont(numberFont);
        
        // second
        secLabel = new JLabel("00");
        secLabel.setBounds(3 * width / 4, 0, width / 4, height);
        secLabel.setFont(numberFont);
        secLabel.setHorizontalAlignment(JLabel.CENTER);
        
        this.add(hourLabel);
        this.add(m_1);
        this.add(minLabel);
        this.add(m_2);
        this.add(secLabel);
        this.setVisible(true);
    }
    
    
    public void start() {  // startManager or continue
        start = new StartManager(this);
        start.start();
    }
    
    
    public void pause() {
        new PauseManager(start).start();
    }
    
    
    public void stop() {
        // refresh data
        setTime(0, 0, 0);
        showTime();
        new PauseManager(start).start();
    }
    
    
    public void showTime() {
        NumberFormat format = NumberFormat.getInstance();
        format.setMinimumIntegerDigits(2);
        this.hourLabel.setText(format.format(hour));
        this.minLabel.setText(format.format(min));
        this.secLabel.setText(format.format(sec));
    }
    
    
    public void setTime(int hour, int min, int sec) {
        this.hour = hour;
        this.min = min;
        this.sec = sec;
    }
    
    
    public int getHour() {
        return hour;
    }
    
    
    public int getMin() {
        return min;
    }
    
    
    public int getSec() {
        return sec;
    }
    
    
    private static class StartManager extends Thread {
        TimerPanel timerPanel;
        
        
        public StartManager(TimerPanel timerPanel) {
            this.timerPanel = timerPanel;
        }
        
        
        @Override
        public void run() {
            int sec, min, hour;
            sec = timerPanel.getSec();
            min = timerPanel.getMin();
            hour = timerPanel.getHour();
            while (true) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    break;  // break loop
                }
                ++sec;
                if (sec >= 60) {
                    ++min;
                    sec = 0;
                    if (min >= 60) {
                        ++hour;
                        min = 0;
                        if (min >= 24) {
                            hour = 0;
                        }
                    }
                }
                timerPanel.setTime(hour, min, sec);
                timerPanel.showTime();
            }
        }
    }
    
    
    private static class PauseManager extends Thread {
        StartManager startManager;
        
        
        public PauseManager(StartManager startManager) {
            this.startManager = startManager;
        }
        
        
        @Override
        public void run() {
            try {
                startManager.interrupt();
            }
            catch (NullPointerException ignored) {
            }
        }
    }
}