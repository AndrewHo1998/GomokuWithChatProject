/**
 * @author 潘学海
 */

package Gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

public class Gomoku extends JFrame {
    public static final String swap2Rule = "一. 假先方在棋盘任意下三手（二黑一白），假后方有三种选择：\n" +
                                           "     1. 选黑。\n" +
                                           "     2. 选白。\n" +
                                           "     3. 下四、五两手（一黑一白），再假先方选择黑或白。\n" +
                                           "二. 黑白双方轮流落子。\n" +
                                           "三. 首选在横、竖、斜方向上成五（连续五个己方棋子）者为胜。\n" +
                                           "四. 超过五子以上不算赢也不算输。";
    private final Board board;
    private final Display display;
    private final JButton retract;
    private final JButton newGame;
    private final JButton loadOrSaveGame;
    private final JButton showRule;
    
    
    public Gomoku() {
        super("五子棋");
        display = new Display(60, 60);
        setContentPane(display);
    
        board = display.getBoard();
        
        newGame = new JButton("新游戏");
        loadOrSaveGame = new JButton("载入游戏");
        retract = new JButton("悔棋");
        showRule = new JButton("游戏规则");
        retract.setEnabled(false);
    
        Font font = new Font(Font.DIALOG, Font.PLAIN, Display.sideLength);
        newGame.setFont(font);
        loadOrSaveGame.setFont(font);
        retract.setFont(font);
        showRule.setFont(font);
        
        newGame.addActionListener(e -> {
            if (!board.isGameStarted())
                display.newGame();
            else
                display.admitDefeat();
        });
        loadOrSaveGame.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (board.isGameStarted()) {
                int state = fileChooser.showSaveDialog(this);
                if (state != JFileChooser.APPROVE_OPTION)
                    return;
                try {
                    display.saveGame(fileChooser.getSelectedFile());
                }
                catch (IOException exception) {
                    JOptionPane.showMessageDialog(this, "保存游戏失败");
                }
            }
            else {
                int state = fileChooser.showOpenDialog(this);
                if (state == JFileChooser.CANCEL_OPTION)
                    return;
                try {
                    display.loadGame(fileChooser.getSelectedFile());
                }
                catch (IOException exception) {
                    JOptionPane.showMessageDialog(this, "文件读取错误，载入游戏失败。");
                }
                catch (BadInputStoneException exception) {
                    JOptionPane.showMessageDialog(this, "文件数据损坏，载入游戏失败。");
                }
            }
        });
        retract.addActionListener(e -> display.removeStone());
        showRule.addActionListener(e -> JOptionPane.showMessageDialog(this, swap2Rule, "Swap2 规则", JOptionPane.INFORMATION_MESSAGE));
        board.addGameStartedChangeListener(evt -> {
            if ((Boolean) evt.getNewValue()) {
                newGame.setText("认输");
                loadOrSaveGame.setText("保存游戏");
            }
            else {
                newGame.setText("新游戏");
                loadOrSaveGame.setText("载入游戏");
            }
            retract.setEnabled(board.canRetractStone());
        });
        board.addHistorySizeChangeListener(evt -> {
            retract.setEnabled(board.canRetractStone());
        });
        
        display.setLayout(null);
        setSize(960, 700);
        newGame.setBounds(display.getCornerXR() + 2 * Display.sideLength, display.getCornerYU() + 3 * Display.sideLength, 220, 2 * Display.sideLength);
        loadOrSaveGame.setBounds(display.getCornerXR() + 2 * Display.sideLength, display.getCornerYU() + 6 * Display.sideLength, 220, 2 * Display.sideLength);
        retract.setBounds(display.getCornerXR() + 2 * Display.sideLength, display.getCornerYU() + 9 * Display.sideLength, 220, 2 * Display.sideLength);
        showRule.setBounds(display.getCornerXR() + 2 * Display.sideLength, display.getCornerYU() + 12 * Display.sideLength, 220, 2 * Display.sideLength);
        display.add(newGame);
        display.add(loadOrSaveGame);
        display.add(retract);
        display.add(showRule);
        
        display.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }
            
            
            public void mousePressed(MouseEvent e) {
                try {
                    display.putStoneFromMouse(e.getX(), e.getY());
                }
                catch (GameNotStartedException | BadInputStoneException ignored) {
                }
            }
            
            
            public void mouseReleased(MouseEvent e) {
            }
            
            
            public void mouseEntered(MouseEvent e) {
            }
            
            
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