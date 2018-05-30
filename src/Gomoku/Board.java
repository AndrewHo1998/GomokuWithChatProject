/**
 * @author 潘学海
 */

package Gomoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

enum StoneType {
    SPACE, BLACK, WHITE
}


class Stone {
    private final Point point;
    private final StoneType type;
    
    
    Stone(int i, int j, StoneType type) throws OutOfBoardRangeException {
        if (i < 1 || i > Borad.n || j < 1 || j > Borad.n)
            throw new OutOfBoardRangeException();
        this.point = new Point(i, j);
        this.type = type;
    }
    
    
    public int getI() {
        return point.x;
    }
    
    
    public int getJ() {
        return point.y;
    }
    
    
    public StoneType getType() {
        return type;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Stone))
            return false;
        Stone stone = (Stone) o;
        return point.equals(stone.point);
    }
    
    
    @Override
    public int hashCode() {
        return point.hashCode();
    }
    
    
    public static Stone blackStone(int i, int j) throws OutOfBoardRangeException {
        return new Stone(i, j, StoneType.BLACK);
    }
    
    
    public static Stone whiteStone(int i, int j) throws OutOfBoardRangeException {
        return new Stone(i, j, StoneType.WHITE);
    }
}


class Borad {
    private boolean gameStarted;
    private boolean playerColorChosen;
    private StoneType player1StoneType, player2;
    private int preSetStones;
    private StoneType[][] borad;
    private Stack<Stone> history;
    public static final int n = 15;
    private static final int[] dx = {1, 1, 0, -1};
    private static final int[] dy = {0, 1, 1, 1};
    
    
    public Borad() {
        history = new Stack<Stone>();
        borad = new StoneType[n + 2][n + 2];
        gameStarted = false;
        playerColorChosen = false;
        player1StoneType = player2 = StoneType.SPACE;
        preSetStones = 5;
        reset();
    }
    
    
    public void newGame() {
        reset();
        gameStarted = true;
    }
    
    
    public void reset() {
        gameStarted = false;
        playerColorChosen = false;
        player1StoneType = player2 = StoneType.SPACE;
        preSetStones = 5;
        history.clear();
        for (int i = 0; i < n + 2; ++i) {
            for (int j = 0; j < n + 2; ++j)
                borad[i][j] = StoneType.SPACE;
        }
    }
    
    
    public boolean isGameStarted() {
        return gameStarted;
    }
    
    
    public boolean isGameOver() {
        return !gameStarted;
    }
    
    
    public boolean isPlayerColorChosen() {
        return playerColorChosen;
    }
    
    
    public void choosePlayer1Color(StoneType player1StoneType) {
        assert (!playerColorChosen && player1StoneType != StoneType.SPACE);
        playerColorChosen = true;
        this.player1StoneType = player1StoneType;
        preSetStones = history.size();
    }
    
    
    public Stack<Stone> getHistory() {
        return history;
    }
    
    
    public int getHistorySize() {
        return history.size();
    }
    
    
    public boolean hasNoHistory() {
        return history.isEmpty();
    }
    
    
    public Stone getLastStone() throws GameNotStartedException, EmptyStackException {
        if (!gameStarted)
            throw new GameNotStartedException();
        return history.peek();
    }
    
    
    public Stone getStoneFromIndex(int index) throws ArrayIndexOutOfBoundsException {
        return history.get(index);
    }
    
    
    public StoneType getNextStoneType() {
        return (history.size() % 2 == 0 ? StoneType.BLACK : StoneType.WHITE);
    }
    
    
    public int getNextPlayerNumber() {
        if (playerColorChosen)
            return (player1StoneType == getNextStoneType() ? 1 : 2);
        else
            return (history.size() < 3 ? 1 : 2);
    }
    
    
    public void putStone(int i, int j) throws GameNotStartedException, OutOfBoardRangeException, StoneAlreadyPlacedException {
        if (!gameStarted)
            throw new GameNotStartedException();
        Stone lastStone = new Stone(i, j, getNextStoneType());
        if (borad[i][j] != StoneType.SPACE)
            throw new StoneAlreadyPlacedException();
        borad[i][j] = lastStone.getType();
        history.push(lastStone);
    }
    
    
    public Stone removeStone() throws GameNotStartedException, EmptyStackException {
        if (!gameStarted)
            throw new GameNotStartedException();
        if (!canRetractStone())
            throw new EmptyStackException();
        Stone lastStone = history.pop();
        borad[lastStone.getI()][lastStone.getJ()] = StoneType.SPACE;
        return lastStone;
    }
    
    
    public boolean canRetractStone() {
        return (history.size() > preSetStones);
    }
    
    
    public List<Integer> checkRowStone() throws EmptyStackException {
        List<Integer> indexOfStones = new ArrayList<Integer>();
        Stone lastStone = history.peek();
        int i = lastStone.getI(), j = lastStone.getJ();
        StoneType type = lastStone.getType();
        List<Point> pointList = new ArrayList<Point>();
        for (int direction = 0; direction < 4; ++direction) {
            int forward = 0, backward = 0;
            while (borad[i + (forward + 1) * dx[direction]][j + (forward + 1) * dy[direction]] == type)
                ++forward;
            while (borad[i + (backward - 1) * dx[direction]][j + (backward - 1) * dy[direction]] == type)
                --backward;
            if (forward - backward + 1 == 5) {
                gameStarted = false;
                for (int k = backward; k <= forward; ++k)
                    pointList.add(new Point(i + k * dx[direction], j + k * dy[direction]));
            }
        }
        if (!pointList.isEmpty()) {
            
            for (Point point : pointList) {
                try {
                    indexOfStones.add(history.indexOf(Stone.blackStone(point.x, point.y)));
                }
                catch (OutOfBoardRangeException ignored) {
                }
            }
        }
        else
            indexOfStones.add(history.size() - 1);
        return indexOfStones;
    }
}


class GameNotStartedException extends Exception {
    
    public GameNotStartedException() {
    }
    
    
    public GameNotStartedException(String message) {
        super(message);
    }
}


class BadInputStoneException extends Exception {
    public BadInputStoneException() {
    }
    
    
    public BadInputStoneException(String message) {
        super(message);
    }
}


class OutOfBoardRangeException extends BadInputStoneException {
    
    public OutOfBoardRangeException() {
    }
    
    
    public OutOfBoardRangeException(String message) {
        super(message);
    }
}


class StoneAlreadyPlacedException extends BadInputStoneException {
    public StoneAlreadyPlacedException() {
    }
    
    
    public StoneAlreadyPlacedException(String message) {
        super(message);
    }
}