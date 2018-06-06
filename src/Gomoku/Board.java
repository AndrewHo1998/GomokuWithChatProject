/**
 * @author 潘学海
 */

package Gomoku;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
    
    
    Stone(int i, int j, StoneType type) throws StoneOutOfBoardRangeException {
        if (i < 1 || i > Board.n || j < 1 || j > Board.n)
            throw new StoneOutOfBoardRangeException();
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
    
    
    public static Stone blackStoneAt(int i, int j) throws StoneOutOfBoardRangeException {
        return new Stone(i, j, StoneType.BLACK);
    }
    
    
    public static Stone whiteStoneAt(int i, int j) throws StoneOutOfBoardRangeException {
        return new Stone(i, j, StoneType.WHITE);
    }
}


class Board {
    private final DataChangeSupport<Boolean> gameStartedChangeSupport;
    private final DataChangeSupport<Integer> historySizeChangeSupport;
    private final List<Integer> indexOfRowStones;
    private final StoneType[][] board;
    private final Stack<Stone> history;
    private StoneType player1StoneType;
    private int presetStoneNumber;
    private boolean rowStonesUpdated;
    
    public static final int n = 15;
    private static final int[] dI = {1, 1, 0, -1};
    private static final int[] dJ = {0, 1, 1, 1};
    
    
    public Board() {
        history = new Stack<Stone>();
        board = new StoneType[n + 2][n + 2];
        gameStartedChangeSupport = new DataChangeSupport<Boolean>(this, false);
        historySizeChangeSupport = new DataChangeSupport<Integer>(this, 0);
        player1StoneType = StoneType.SPACE;
        presetStoneNumber = 5;
        rowStonesUpdated = false;
        indexOfRowStones = new ArrayList<Integer>();
        reset();
    }
    
    
    public void newGame() {
        reset();
        gameStartedChangeSupport.setValue(true);
    }
    
    
    public void reset() {
        gameStartedChangeSupport.setValue(false);
        player1StoneType = StoneType.SPACE;
        presetStoneNumber = 5;
        rowStonesUpdated = false;
        indexOfRowStones.clear();
        history.clear();
        historySizeChangeSupport.setValue(0);
        for (int i = 0; i < n + 2; ++i) {
            for (int j = 0; j < n + 2; ++j)
                board[i][j] = StoneType.SPACE;
        }
    }
    
    
    public boolean isGameStarted() {
        return gameStartedChangeSupport.getValue();
    }
    
    
    public boolean isGameOver() {
        return !gameStartedChangeSupport.getValue();
    }
    
    
    public boolean isPlayerColorChosen() {
        return (player1StoneType != StoneType.SPACE);
    }
    
    
    public void choosePlayer1Color(StoneType player1StoneType) {
        assert (!isPlayerColorChosen() && player1StoneType != StoneType.SPACE);
        this.player1StoneType = player1StoneType;
        presetStoneNumber = history.size();
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
    
    
    public Stone getLastStone() throws EmptyStackException {
        return history.peek();
    }
    
    
    public Stone getStoneFromIndex(int index) throws ArrayIndexOutOfBoundsException {
        return history.get(index);
    }
    
    
    public StoneType getNextStoneType() {
        return (history.size() % 2 == 0 ? StoneType.BLACK : StoneType.WHITE);
    }
    
    
    public int getNextPlayerNumber() {
        if (isPlayerColorChosen())
            return (player1StoneType == getNextStoneType() ? 1 : 2);
        else
            return (history.size() < 3 ? 1 : 2);
    }
    
    
    public void putStone(int i, int j) throws GameNotStartedException, StoneOutOfBoardRangeException, StoneAlreadyPlacedException {
        if (!isGameStarted())
            throw new GameNotStartedException();
        Stone lastStone = new Stone(i, j, getNextStoneType());
        if (board[i][j] != StoneType.SPACE)
            throw new StoneAlreadyPlacedException();
        board[i][j] = lastStone.getType();
        history.push(lastStone);
        rowStonesUpdated = false;
        if (history.size() == n * n)
            gameStartedChangeSupport.setValue(false);
        historySizeChangeSupport.setValue(history.size());
    }
    
    
    public Stone retractStone() throws GameNotStartedException, EmptyStackException {
        if (!isGameStarted())
            throw new GameNotStartedException();
        if (!canRetractStone())
            throw new EmptyStackException();
        Stone lastStone = history.pop();
        board[lastStone.getI()][lastStone.getJ()] = StoneType.SPACE;
        rowStonesUpdated = false;
        historySizeChangeSupport.setValue(history.size());
        return lastStone;
    }
    
    
    public boolean canRetractStone() {
        if (isGameStarted())
            return (history.size() > presetStoneNumber);
        else
            return false;
    }
    
    
    public List<Integer> getIndexOfRowStones() {
        if (!rowStonesUpdated) {
            indexOfRowStones.clear();
            try {
                Stone lastStone = history.peek();
                int i = lastStone.getI(), j = lastStone.getJ();
                StoneType type = lastStone.getType();
                List<Point> pointList = new ArrayList<Point>();
                for (int direction = 0; direction < 4; ++direction) {
                    int forward = 0, backward = 0;
                    while (board[i + (forward + 1) * dI[direction]][j + (forward + 1) * dJ[direction]] == type)
                        ++forward;
                    while (board[i + (backward - 1) * dI[direction]][j + (backward - 1) * dJ[direction]] == type)
                        --backward;
                    if (forward - backward + 1 == 5) {
                        gameStartedChangeSupport.setValue(false);
                        for (int k = backward; k <= forward; ++k)
                            pointList.add(new Point(i + k * dI[direction], j + k * dJ[direction]));
                    }
                }
                if (!pointList.isEmpty()) {
                    pointList.forEach(point -> {
                        try {
                            indexOfRowStones.add(history.indexOf(Stone.blackStoneAt(point.x, point.y)));
                        }
                        catch (StoneOutOfBoardRangeException ignored) {
                        }
                    });
                }
                else
                    indexOfRowStones.add(history.size() - 1);
            }
            catch (EmptyStackException ignored) {
            }
        }
        rowStonesUpdated = true;
        return indexOfRowStones;
    }
    
    
    public void addGameStartedChangeListener(PropertyChangeListener listener) {
        gameStartedChangeSupport.addPropertyChangeListener(listener);
    }
    
    
    public void addHistorySizeChangeListener(PropertyChangeListener listener) {
        historySizeChangeSupport.addPropertyChangeListener(listener);
    }
}


class DataChangeSupport<T> extends PropertyChangeSupport {
    private T value;
    
    
    public DataChangeSupport(Object source, T initialValue) {
        super(source);
        value = initialValue;
    }
    
    
    public T getValue() {
        return value;
    }
    
    
    public void setValue(T newValue) {
        T oldValue = value;
        value = newValue;
        firePropertyChange("value", oldValue, newValue);
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


class StoneOutOfBoardRangeException extends BadInputStoneException {
    
    public StoneOutOfBoardRangeException() {
    }
    
    
    public StoneOutOfBoardRangeException(String message) {
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