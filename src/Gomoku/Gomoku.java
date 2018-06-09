
/**
 * @author 潘学海
 */

package Gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Gomoku extends JFrame {
    private final Board board;
    private final Display display;
    private final JButton retractButton;
    private final JButton newGameButton;
    private final JButton showRuleButton;
    private final JTextArea chatTextArea;
    private final JTextField chatTextField;
    private final JButton sendButton;

    
    public static final String swap2Rule = "一. 假先方在棋盘任意下三手（二黑一白），假后方有三种选择：\n" +
                                           "     1. 选黑。\n" +
                                           "     2. 选白。\n" +
                                           "     3. 下四、五两手（一黑一白），再假先方选择黑或白。\n" +
                                           "二. 黑白双方轮流落子。\n" +
                                           "三. 首选在横、竖、斜方向上成五（连续五个己方棋子）者为胜。\n" +
                                           "四. 超过五子以上不算赢也不算输。";
    
    
    public Gomoku() {
        super("五子棋");
        board = new Board();
        display = new Display(60, 60, board);
        
        setContentPane(display);
        
        newGameButton = new JButton("新游戏");
        retractButton = new JButton("悔棋");
        showRuleButton = new JButton("游戏规则");
        chatTextArea = new JTextArea("这里是聊天记录窗口");
        chatTextField = new JTextField("这里是聊天发送窗口");
        sendButton = new JButton("发送");
        retractButton.setEnabled(false);
        
        Font font = new Font(Font.DIALOG, Font.PLAIN, 3 * Display.sideLength / 4);
        newGameButton.setFont(font);
        retractButton.setFont(font);
        showRuleButton.setFont(font);
        
        newGameButton.addActionListener(e -> {
            if (!board.isGameStarted()) {
            	display.newGame();
            }        
            else {
            	display.admitDefeat();    	 
            }
               
        });
        retractButton.addActionListener(e -> display.removeStone());
        showRuleButton.addActionListener(e -> JOptionPane.showMessageDialog(this, swap2Rule, "Swap2 规则", JOptionPane.INFORMATION_MESSAGE));
        sendButton.addActionListener(e -> {
            chatTextArea.setText(chatTextArea.getText().trim() + '\n' + chatTextField.getText().trim());
            chatTextField.setText("");
        });
        board.addGameStartedChangeListener(evt -> {
            if ((Boolean) evt.getNewValue())
                newGameButton.setText("认输");
            else
                newGameButton.setText("新游戏");
            retractButton.setEnabled(board.canRetractStone());
        });
        board.addHistorySizeChangeListener(evt -> retractButton.setEnabled(board.canRetractStone()));
        
        display.setLayout(null);
        setSize(display.getBoardBoundXR() + 15 * Display.sideLength, 700);
        newGameButton.setBounds(display.getBoardBoundXR() + 2 * Display.sideLength, display.getBoardBoundYU() + 2 * Display.sideLength, 4 * Display.sideLength, 2 * Display.sideLength);
        retractButton.setBounds(display.getBoardBoundXR() + 6 * Display.sideLength, display.getBoardBoundYU() + 2 * Display.sideLength, 4 * Display.sideLength, 2 * Display.sideLength);
        showRuleButton.setBounds(display.getBoardBoundXR() + 10 * Display.sideLength, display.getBoardBoundYU() + 2 * Display.sideLength, 4 * Display.sideLength, 2 * Display.sideLength);
        chatTextArea.setBounds(display.getBoardBoundXR() + 2 * Display.sideLength, display.getBoardBoundYU() + 4 * Display.sideLength + Display.sideLength / 2, 12 * Display.sideLength, 8 * Display.sideLength);
        chatTextField.setBounds(display.getBoardBoundXR() + 2 * Display.sideLength, display.getBoardBoundYU() + 13 * Display.sideLength, 10 * Display.sideLength, Display.sideLength);
        sendButton.setBounds(display.getBoardBoundXR() + 12 * Display.sideLength, display.getBoardBoundYU() + 13 * Display.sideLength, 2 * Display.sideLength, Display.sideLength);
        
        display.add(newGameButton);
        display.add(retractButton);
        display.add(showRuleButton);
        display.add(chatTextArea);
        display.add(chatTextField);
        display.add(sendButton);
        
        display.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            
            
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    display.putStoneFromMouse(e.getX(), e.getY());
                }
                catch (GameNotStartedException | BadInputStoneException ignored) {
                }
            }
            
            
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            
            
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            
            
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    
    public static void main(String[] args) {
        Gomoku gomoku = new Gomoku();
    }
}