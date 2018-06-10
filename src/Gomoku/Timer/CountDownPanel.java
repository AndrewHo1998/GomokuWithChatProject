package Gomoku.Timer;

import Gomoku.Display;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public class CountDownPanel extends JPanel {
    private Display display;
    private int remainSec;
    public final static int maxTime = 15;
    private JLabel secLabel;
    private StartManager startManager;
    
    
    public CountDownPanel(Display display, int width, int height) {
        this.display = display;
        this.setSize(width, height);
        Font numberFont = new Font("Consolas", Font.BOLD, 3 * height / 4);
        
        remainSec = maxTime;
        secLabel = new JLabel("");
        secLabel.setBounds(0, 0, width, height);
        secLabel.setFont(numberFont);
        secLabel.setHorizontalAlignment(JLabel.CENTER);
        
        this.add(secLabel);
        this.setVisible(true);
    }
    
    
    public void start() {
        startManager = new StartManager(this);
        startManager.start();
    }
    
    
    public void pause() {
        new PauseManager(startManager).start();
    }
    
    
    public void stop() {
        new PauseManager(startManager).start();
        setTime(maxTime);
        secLabel.setText("");
    }
    
    
    public void showTime() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);
        secLabel.setText(nf.format(remainSec));
    }
    
    
    public void setTime(int sec) {
        
        this.remainSec = sec;
    }
    
    
    public int getSec() {
        return remainSec;
    }
    
    
    private static class StartManager extends Thread {
        private CountDownPanel countDownPanel;
        
        
        public StartManager(CountDownPanel countDownPanel) {
            this.countDownPanel = countDownPanel;
        }
        
        
        @Override
        public void run() {
            int remainSec;
            remainSec = countDownPanel.getSec();
            countDownPanel.showTime();
            
            while (true) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    break;
                }
                --remainSec;
                if (remainSec < 0 && countDownPanel.display.getPlayerNumber() == countDownPanel.display.getNextPlayerNumber()) {
                    new Thread(() -> countDownPanel.display.admitDefeat()).start();    // time over!
                    break;
                }
                countDownPanel.setTime(remainSec);
                countDownPanel.showTime();
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