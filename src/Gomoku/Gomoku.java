/**
 * @author 潘学海
 */

package Gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Gomoku extends JFrame {
    private Borad borad;
    private Display display;
    private JButton retract;
    private JButton newGame;
    private JButton showRule;
    public static String swap2Rule = "一. 假先方在棋盘任意下三手（二黑一白）假后方有三种选择：\n" +
                                     "     1. 选黑。\n" +
                                     "     2. 选白。\n" +
                                     "     3. 下四、五两手（一黑一白）再假先方选择黑或白。\n" +
                                     "二. 黑白双方轮流落子。\n" +
                                     "三. 首选在横、竖、斜方向上成五（连续五个己方棋子）者为胜。\n" +
                                     "四. 超过五子以上不算赢也不算输。";
    
    
    public Gomoku() {
        super("五子棋");
        display = new Display(60, 60);
        borad = display.getBorad();
        
        setContentPane(display);
        newGame = new JButton("新游戏");
        retract = new JButton("悔棋");
        showRule = new JButton("游戏规则");
        retract.setEnabled(false);
        
        Font font = new Font("TimesRoman", Font.BOLD, Display.sideLength);
        newGame.setFont(font);
        retract.setFont(font);
        showRule.setFont(font);
        
        newGame.addActionListener(e -> {
            display.newGame();
            retract.setEnabled(false);
        });
        retract.addActionListener(e -> {
            display.removeStone();
            retract.setEnabled(borad.canRetractStone());
        });
        showRule.addActionListener(e -> JOptionPane.showMessageDialog(this, swap2Rule, "Swap2 规则", JOptionPane.INFORMATION_MESSAGE));
        
        display.setLayout(null);
        setSize(960, 700);
        newGame.setBounds(display.getCornerXR() + 2 * Display.sideLength, display.getCornerYU() + 3 * Display.sideLength, 220, 3 * Display.sideLength);
        retract.setBounds(display.getCornerXR() + 2 * Display.sideLength, display.getCornerYU() + 7 * Display.sideLength, 220, 3 * Display.sideLength);
        showRule.setBounds(display.getCornerXR() + 2 * Display.sideLength, display.getCornerYU() + 11 * Display.sideLength, 220, 3 * Display.sideLength);
        display.add(newGame);
        display.add(retract);
        display.add(showRule);
        
        display.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }
            
            
            public void mousePressed(MouseEvent e) {
                try {
                    display.putStoneFromMouse(e.getX(), e.getY());
                    retract.setEnabled(borad.canRetractStone());
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


