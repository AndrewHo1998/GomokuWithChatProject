package Timer;

public class TimeManager {
    private CountDownPanel countDown;
    private TimerPanel timerPanel;
    
    
    public TimeManager(CountDownPanel countDown, TimerPanel timerPanel) {
        this.countDown = countDown;
        this.timerPanel = timerPanel;
    }
    
    
    public void OnNewGame() {
        countDown.start();
        timerPanel.stop();
        timerPanel.start();
    }
    
    
    public void OnGameOver() {
        countDown.stop();
        timerPanel.pause();
    }
    
    
    public void OnReset() {
        countDown.stop();
        timerPanel.stop();
    }
    
    
    public void OnPutStone() {
        countDown.stop();
        countDown.start();
    }
    
    
    public void OnRetractStone() {
        countDown.stop();
        countDown.start();
    }
    
    
    public void OnAdmitDefeat() {
        countDown.pause();
        timerPanel.pause();
    }
    
    
    public void OnDialog() {
        countDown.pause();
        timerPanel.pause();
    }
    
    
    public void OnDialogClose() {
        countDown.start();
        timerPanel.start();
    }
}