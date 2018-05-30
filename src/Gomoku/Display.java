/**
 * @author 潘学海
 */

package Gomoku;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

class Display extends JPanel {
    private final Borad borad;
    private final int cornerXL, cornerYU, cornerXR, cornerYD;
    private final int[] stoneCenterX;
    private final int[] stoneCenterY;
    private List<Integer> indexOfHighlightedStones;
    private JLabel messageLabel;
    public static final int sideLength = 40;
    public static final int starRadius = 5;
    public static final int stoneRadius = 18;
    public static final Color backgroundColor = new Color(244, 240, 220);
    public static final Color black = new Color(32, 32, 32);
    public static final Color white = new Color(220, 220, 220);
    public static final Color gray = new Color(160, 160, 160);
    public static final Font indexFont = new Font("TimesRoman", Font.BOLD, 3 * stoneRadius / 4);
    
    
    public Display(int x, int y) {
        super();
        borad = new Borad();
        indexOfHighlightedStones = new ArrayList<Integer>();
        messageLabel = new JLabel("");
        cornerXL = x;
        cornerYU = y;
        stoneCenterX = new int[Borad.n + 2];
        stoneCenterY = new int[Borad.n + 2];
        for (int i = 0; i <= Borad.n + 1; ++i) {
            stoneCenterX[i] = cornerXL + sideLength * (i - 1);
            stoneCenterY[i] = cornerYU + sideLength * (i - 1);
        }
        cornerXR = stoneCenterX[Borad.n];
        cornerYD = stoneCenterY[Borad.n];
        
        messageLabel.setBounds(cornerXR + 7 * sideLength / 2, cornerYU, 5 * sideLength, sideLength);
        messageLabel.setFont(new Font("TimesRoman", Font.BOLD, sideLength / 2));
        add(messageLabel);
    }
    
    
    public Borad getBorad() {
        return borad;
    }
    
    
    public void newGame() {
        borad.newGame();
        indexOfHighlightedStones.clear();
        messageLabel.setText("");
        repaint();
        paintPlayer((Graphics2D) getGraphics());
    }
    
    
    public void reset() {
        borad.reset();
        indexOfHighlightedStones.clear();
    }
    
    
    public void gameOver() {
        String message = "玩家 " + (3 - borad.getNextPlayerNumber()) + " 胜利";
        messageLabel.setText(message);
        JOptionPane.showMessageDialog(this, message, "游戏结束", JOptionPane.INFORMATION_MESSAGE);
        reset();
    }
    
    
    public void choosePlayerColor() {
        if (borad.getHistorySize() == 3) {
            String message = "玩家 2 选择执子颜色";
            messageLabel.setText(message);
            String[] options = {"执黑", "执白", "继续"};
            int n = JOptionPane.showOptionDialog(this,
                                                 message,
                                                 "",
                                                 JOptionPane.YES_NO_CANCEL_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE,
                                                 null,
                                                 options,
                                                 options[0]);
            if (n == 0)
                borad.choosePlayer1Color(StoneType.WHITE);
            else if (n == 1)
                borad.choosePlayer1Color(StoneType.BLACK);
        }
        else if (!borad.isPlayerColorChosen() && borad.getHistorySize() == 5) {
            String message = "玩家 1 选择执子颜色";
            messageLabel.setText(message);
            String[] options = {"执黑", "执白"};
            int n = JOptionPane.showOptionDialog(this,
                                                 message,
                                                 "",
                                                 JOptionPane.OK_CANCEL_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE,
                                                 null,
                                                 options,
                                                 options[0]);
            if (n == 0)
                borad.choosePlayer1Color(StoneType.BLACK);
            else
                borad.choosePlayer1Color(StoneType.WHITE);
        }
    }
    
    
    public void putStone(int i, int j) throws GameNotStartedException, OutOfBoardRangeException, StoneAlreadyPlacedException {
        borad.putStone(i, j);
        Graphics2D g2D = (Graphics2D) getGraphics();
        paintStone(g2D, borad.getLastStone());
        List<Integer> indexOfRowStones = borad.checkRowStone();
        paintStoneIndexHighlight(g2D, indexOfRowStones);
        if (!borad.isPlayerColorChosen())
            choosePlayerColor();
        if (borad.isGameOver())
            gameOver();
        else
            paintPlayer(g2D);
    }
    
    
    public void removeStone() {
        try {
            Stone lastStone = borad.removeStone();
            Graphics2D g2D = (Graphics2D) getGraphics();
            eraseStone(g2D, lastStone.getI(), lastStone.getJ());
            paintStoneIndexHighlight(g2D, borad.checkRowStone());
            paintPlayer(g2D);
        }
        catch (GameNotStartedException | EmptyStackException ignored) {
        }
    }
    
    
    public void putStoneFromMouse(int x, int y) throws GameNotStartedException, OutOfBoardRangeException, StoneAlreadyPlacedException {
        int i = xToI(x), j = yToJ(y);
        int xGrid = iToX(i), yGrid = jToY(j);
        if ((x - xGrid) * (x - xGrid) + (y - yGrid) * (y - yGrid) < stoneRadius * stoneRadius)
            putStone(i, j);
    }
    
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        paintBoard(g2D);
        if (borad.isGameStarted())
            paintNextStoneColor(g2D);
    }
    
    
    private void paintBoard(Graphics2D g2D) {
        g2D.setColor(backgroundColor);
        g2D.fillRect(stoneCenterX[0], stoneCenterY[0], sideLength * (Borad.n + 1), sideLength * (Borad.n + 1));
        g2D.setColor(Color.BLACK);
        g2D.setStroke(new BasicStroke(5.0f));
        g2D.drawLine(stoneCenterX[0], stoneCenterY[0], stoneCenterX[Borad.n + 1], stoneCenterY[0]);
        g2D.drawLine(stoneCenterX[0], stoneCenterY[Borad.n + 1], stoneCenterX[Borad.n + 1], stoneCenterY[Borad.n + 1]);
        g2D.drawLine(stoneCenterX[0], stoneCenterY[0], stoneCenterX[0], stoneCenterY[Borad.n + 1]);
        g2D.drawLine(stoneCenterX[Borad.n + 1], stoneCenterY[0], stoneCenterX[Borad.n + 1], stoneCenterY[Borad.n + 1]);
        g2D.setStroke(new BasicStroke(2.0f));
        for (int i = 1; i <= Borad.n; ++i) {
            g2D.drawLine(stoneCenterX[i], cornerYU, stoneCenterX[i], cornerYD);
            g2D.drawLine(cornerXL, stoneCenterY[i], cornerXR, stoneCenterY[i]);
        }
        fillCircle(g2D, 8, 8, starRadius);
        fillCircle(g2D, 4, 4, starRadius);
        fillCircle(g2D, 4, 12, starRadius);
        fillCircle(g2D, 12, 4, starRadius);
        fillCircle(g2D, 12, 12, starRadius);
    }
    
    
    private void eraseStone(Graphics2D g2D, int i, int j) {
        try {
            int centerX = iToX(i);
            int centerY = jToY(j);
            g2D.setColor(backgroundColor);
            g2D.fillRect(centerX - sideLength / 2, centerY - sideLength / 2, sideLength, sideLength);
            g2D.setColor(Color.BLACK);
            g2D.setStroke(new BasicStroke(2.0f));
            if (i > 1)
                g2D.drawLine(centerX - sideLength / 2, centerY, centerX, centerY);
            if (i < Borad.n)
                g2D.drawLine(centerX, centerY, centerX + sideLength / 2, centerY);
            if (j > 1)
                g2D.drawLine(centerX, centerY - sideLength / 2, centerX, centerY);
            if (j < Borad.n)
                g2D.drawLine(centerX, centerY, centerX, centerY + sideLength / 2);
            
            if (isStar(i, j))
                fillCircle(g2D, i, j, starRadius);
        }
        catch (OutOfBoardRangeException ignored) {
        }
    }
    
    
    private void paintStone(Graphics2D g2D, Stone stone) {
        if (stone.getType() != StoneType.SPACE) {
            g2D.setStroke(new BasicStroke(1.0f));
            g2D.setColor(gray);
            drawCircle(g2D, stone.getI(), stone.getJ(), stoneRadius);
            g2D.setColor(typeToColor(stone.getType()));
            fillCircle(g2D, stone.getI(), stone.getJ(), stoneRadius);
        }
    }
    
    
    private void paintStoneIndex(Graphics2D g2D, Stone stone, int index, Color color) {
        g2D.setColor(color);
        try {
            drawCenteredString(g2D, Integer.toString(index + 1), iToX(stone.getI()), jToY(stone.getJ()));
        }
        catch (OutOfBoardRangeException ignored) {
        }
    }
    
    
    private void paintStoneIndex(Graphics2D g2D, Stone stone, int index, boolean highlight) {
        if (highlight)
            paintStoneIndex(g2D, stone, index, Color.RED);
        else
            paintStoneIndex(g2D, stone, index, typeToOppositeColor(stone.getType()));
    }
    
    
    private void paintStoneWithIndex(Graphics2D g2D, Stone stone, int index, boolean highlight) {
        if (stone.getType() != StoneType.SPACE) {
            paintStone(g2D, stone);
            paintStoneIndex(g2D, stone, index, highlight);
        }
    }
    
    
    private void eraseStoneIndexHighlight(Graphics2D g2D) {
        indexOfHighlightedStones.forEach(index -> {
            try {
                paintStoneIndex(g2D, borad.getStoneFromIndex(index), index, false);
            }
            catch (ArrayIndexOutOfBoundsException ignored) {
            }
        });
        indexOfHighlightedStones.clear();
    }
    
    
    private void paintStoneIndexHighlight(Graphics2D g2D, List<Integer> indexOfStones) {
        eraseStoneIndexHighlight(g2D);
        indexOfStones.forEach(index -> {
            try {
                paintStoneIndex(g2D, borad.getStoneFromIndex(index), index, true);
            }
            catch (ArrayIndexOutOfBoundsException ignored) {
            }
        });
        indexOfHighlightedStones.addAll(indexOfStones);
    }
    
    
    private void paintStonesWithIndexFromHistory(Graphics2D g2D) {
        if (borad.hasNoHistory())
            return;
        indexOfHighlightedStones.clear();
        g2D.setFont(indexFont);
        Stack<Stone> history = borad.getHistory();
        int i = 0;
        for (Stone stone : history) {
            paintStoneWithIndex(g2D, stone, i, (i < history.size()));
            ++i;
        }
        try {
            paintStoneIndexHighlight(g2D, borad.checkRowStone());
        }
        catch (EmptyStackException ignored) {
        }
    }
    
    
    private void paintNextStoneColor(Graphics2D g2D) {
        g2D.setStroke(new BasicStroke(1.0f));
        g2D.setColor(gray);
        g2D.drawOval(cornerXR + 5 * sideLength / 2 - stoneRadius, cornerYU + sideLength / 2 - stoneRadius, 2 * stoneRadius, 2 * stoneRadius);
        g2D.setColor(typeToColor(borad.getNextStoneType()));
        g2D.fillOval(cornerXR + 5 * sideLength / 2 - stoneRadius, cornerYU + sideLength / 2 - stoneRadius, 2 * stoneRadius, 2 * stoneRadius);
    }
    
    
    private void paintMessage(Graphics2D g2D, String message) {
        paintNextStoneColor(g2D);
        messageLabel.setText(message);
    }
    
    
    private void paintPlayer(Graphics2D g2D) {
        paintMessage(g2D, "玩家 " + borad.getNextPlayerNumber() + (borad.getNextStoneType() == StoneType.BLACK ? " 执黑" : " 执白"));
    }
    
    
    private void fillCircle(Graphics2D g2D, int i, int j, int r) {
        g2D.fillOval(stoneCenterX[i] - r, stoneCenterY[j] - r, 2 * r, 2 * r);
    }
    
    
    private void drawCircle(Graphics2D g2D, int i, int j, int r) {
        g2D.drawOval(stoneCenterX[i] - r, stoneCenterY[j] - r, 2 * r, 2 * r);
    }
    
    
    private static void drawCenteredString(Graphics2D g2D, String text, int x, int y) {
        FontMetrics metrics = g2D.getFontMetrics();
        x -= metrics.stringWidth(text) / 2;
        y -= metrics.getHeight() / 2 - metrics.getAscent();
        g2D.drawString(text, x, y);
    }
    
    
    public static boolean isStar(int i, int j) {
        return ((i == 8 && j == 8) ||
                (i == 4 && j == 4) ||
                (i == 4 && j == 12) ||
                (i == 12 && j == 4) ||
                (i == 12 && j == 12));
    }
    
    
    public int getCornerXL() {
        return cornerXL;
    }
    
    
    public int getCornerXR() {
        return cornerXR;
    }
    
    
    public int getCornerYU() {
        return cornerYU;
    }
    
    
    public int getCornerYD() {
        return cornerYD;
    }
    
    
    public int iToX(int i) throws OutOfBoardRangeException {
        if (i < 0 || i > Borad.n + 1)
            throw new OutOfBoardRangeException();
        return stoneCenterX[i];
    }
    
    
    public int jToY(int j) throws OutOfBoardRangeException {
        if (j < 0 || j > Borad.n + 1)
            throw new OutOfBoardRangeException();
        return stoneCenterY[j];
    }
    
    
    public int xToI(int x) {
        return Math.round(((float) (x - cornerXL)) / sideLength) + 1;
    }
    
    
    public int yToJ(int y) {
        return Math.round(((float) (y - cornerYU)) / sideLength) + 1;
    }
    
    
    public static Color typeToColor(StoneType type) {
        return (type == StoneType.BLACK ? black : white);
    }
    
    
    public static Color typeToOppositeColor(StoneType type) {
        return (type != StoneType.BLACK ? black : white);
    }
}