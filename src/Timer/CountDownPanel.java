package Timer;

import java.awt.Font;
import java.text.NumberFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Gomoku.Display;

public class CountDownPanel extends JPanel {
    Display display;
    int remainSec;
    final static int maxTime = 15;
    JLabel labelS;
    StartManager startManager;
    
    
    public CountDownPanel(int width, int height) {
        this.setSize(width, height);
        Font numberFont = new Font("Consolas", Font.BOLD, 3 * height / 4);
        
        remainSec = maxTime;
        labelS = new JLabel("");
        labelS.setBounds(0, 0, width, height);
        labelS.setFont(numberFont);
        labelS.setHorizontalAlignment(JLabel.CENTER);
        
        this.add(labelS);
        this.setVisible(true);
    }
    
    
    public void setDisplay(Display display) {
        this.display = display;
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
        labelS.setText("");
    }
    
    
    public void showTime() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);
        labelS.setText(nf.format(remainSec));
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