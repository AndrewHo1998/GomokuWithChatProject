package Gomoku;

import javax.swing.*;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class Server extends AbstractSocket {
    private boolean waitingForResponse; // 是否正在等待 client 回应
    private int waitingForResponseClientId; // 是否正在等待的 client 的 ID
    private Board board; // 棋盘
    private int player1ClientId; // 玩家 1 的客户端号
    private Client client1; // client1，如果在构造函数里面就可以初始化的话可以设置为 final 属性。
    private Client client2; // client2，如果在构造函数里面就可以初始化的话可以设置为 final 属性。
    
    
    // TODO 初始化
    public Server(/* args */) {
        super();
        board = new Board();
        waitingForResponse = false;
        waitingForResponseClientId = 0;
        player1ClientId = 0;
    }
    
    
    /**
     * 从其他 Socket 接受报文，返回是否接收成功。
     *
     * @param source  源 Socket
     * @param message 报文内容
     */
    @Override
    public boolean receive(AbstractSocket source, String message) {
        // TODO 接收消息
        super.receive(source, message);
        if (waitingForResponse) {
            // TODO 错误处理
            if ((waitingForResponseClientId == 1 && source == client1) || (waitingForResponseClientId == 2 && source == client2)) {
                waitingForResponse = false; // 不再等待 client 回应
                waitingForResponseClientId = 0;
            }
        }
        return true;
    }
    
    
    /**
     * server 向双方 client 发送新建游戏命令
     * server 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleNewGame(String message) {
    }
    
    
    /**
     * client 请求新建游戏，server 直接转发对方 client。
     * client 弹出窗口，让用户选择是否开始。
     * server 直接转发对方 client
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleRequireToNewGame(String message) {
        // TODO 从 message 解析 clientId
        int clientId = 1;
        waitingForResponse = true; // 等待对方 client 回应
        waitingForResponseClientId = 3 - clientId; // 对方的 clientId
        // TODO 直接转发对方 client（报文头可能需要稍作修改）
    }
    
    
    /**
     * client 同意新建游戏，server 新建游戏，并向双方 client 发送新建游戏命令。
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleAcceptToNewGame(String message) {
        // 接收函数已保证从正确的 client 接收消息
        // TODO 从 message 解析 clientId
        int clientId = 1;
        board.newGame();
        player1ClientId = 3 - clientId; // 请求新建游戏的玩家的编号为 1，同意新建游戏的玩家的编号为 2（就是本函数 message 的来源）。
        /**
         * message
         * @arg playerNumber
         */
        // TODO 向双方 client 发送新建游戏命令
    }
    
    
    /**
     * client 拒绝新建游戏，server 直接转发对方 client。
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleRejectToNewGame(String message) {
        // 接收函数已保证从正确的 client 接收消息
        // TODO 直接转发对方 client（报文头可能需要稍作修改）
    }
    
    
    /**
     * server 向双方 client 发送游戏结束命令
     * server 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleGameOver(String message) {
    }
    
    
    /**
     * client 认输，server 结束游戏，server 接收后向双方 client 发送游戏结束命令。
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleAdmitDefeat(String message) {
        // TODO 从 message 解析 clientId
        int clientId = 1;
        int winnerNumber = (clientId == player1ClientId ? 2 : 1);
        List<Integer> indexOfRowStones = board.getIndexOfRowStones();
        List<Stone> rowStones = new ArrayList<Stone>();
        for (int index : indexOfRowStones) {
            try {
                rowStones.add(board.getStoneFromIndex(index));
            }
            catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        board.reset();
        /**
         * message
         * @arg winnerNumber     胜者编号
         * @arg indexOfRowStones 连珠的棋子编号
         * @arg rowStones        连珠的棋子
         */
        // TODO 向双方 client 发送游戏结束命令
    }
    
    
    /**
     * server 向双方 client 发送落子命令
     * server 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handlePutStone(String message) {
    }
    
    
    /**
     * client 请求落子，server 进行处理，若可以落子则向双方 client 发送落子命令。
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleRequireToPutStone(String message) {
        // TODO 从 message 解析 clientId
        int clientId = 1;
        int playerNumber = (clientId == player1ClientId ? 1 : 2);
        if (playerNumber != board.getNextPlayerNumber())
            return;
        try {
            // TODO 从 message 解析 (i, j)
            int i = 0;
            int j = 0;
            Stone previousStone;
            try {
                previousStone = board.getLastStone();
            }
            catch (EmptyStackException ignored) {
                previousStone = null;
            }
            board.putStone(i, j);
            Stone stone = board.getLastStone();
            int historySize = board.getHistorySize();
            /**
             * message
             * @arg stone         落子的 stone
             * @arg previousStone 落子的 stone 的前一个 stone，若没有则传入 null。
             * @arg historySize   落子完成后棋盘上的棋子数
             */
            // TODO 向双方 client 发送落子命令
            // 若没有选择玩家颜色
            if (!board.isPlayerColorChosen() && (board.getHistorySize() == 3 || board.getHistorySize() == 5)) {
                waitingForResponse = true; // 等待 client 回应
                waitingForResponseClientId = 3 - clientId; // 对方的 clientId
            }
            else { // 没有选择玩家颜色不可能出现连珠 所以直接用了 else
                // 检查是否连珠
                List<Integer> indexOfRowStones = board.getIndexOfRowStones();
                if (board.isGameOver()) {
                    List<Stone> rowStones = new ArrayList<Stone>();
                    for (int index : indexOfRowStones) {
                        try {
                            rowStones.add(board.getStoneFromIndex(index));
                        }
                        catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                    }
                    int winnerNumber;
                    if (indexOfRowStones.size() >= 5)
                        winnerNumber = 3 - board.getNextPlayerNumber();
                    else
                        winnerNumber = 0; // 平局
                    /**
                     * message
                     * @arg winnerNumber     胜者编号
                     * @arg indexOfRowStones 连珠的棋子编号
                     * @arg rowStones        连珠的棋子
                     */
                    // TODO 向双方 client 发送游戏结束命令
                }
            }
        }
        catch (GameNotStartedException | BadInputStoneException ignored) {
        }
    }
    
    
    /**
     * server 向双方 client 发送悔棋命令
     * server 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleRetractStone(String message) {
    }
    
    
    /**
     * client 请求悔棋，server 直接转发对方 client。
     * client 弹出窗口，让用户选择是否同意。
     * server 直接转发对方 client
     *
     * @param message 报文
     */
    @Override
    protected void handleRequireToRetractStone(String message) {
        // TODO 从 message 解析 clientId
        int clientId = 1;
        waitingForResponse = true; // 等待对方 client 回应
        waitingForResponseClientId = 3 - clientId; // 对方的 clientId
        // TODO 直接转发对方 client（报文头可能需要稍作修改）
    }
    
    
    /**
     * client 同意悔棋，server 悔棋，并向双方 client 发送悔棋命令。
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleAcceptToRetractStone(String message) {
        // 接收函数已保证从正确的 client 接收消息
        try {
            Stone stone = board.retractStone();
            Stone previousStone = board.getLastStone();
            int historySize = board.getHistorySize();
            /**
             * message
             * @arg stone         被移走的 stone
             * @arg previousStone 被移走的 stone 的前一个 stone，因为可以悔棋时棋盘上至少有 4 个棋子，必然是非 null。
             * @arg historySize   悔棋完成后棋盘上的棋子数
             */
            // TODO 向双方 client 发送悔棋命令
        }
        catch (GameNotStartedException ignored) {
        }
    }
    
    
    /**
     * client 拒绝悔棋，server 直接转发对方 client。
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleRejectToRetractStone(String message) {
        // 接收函数已保证从正确的 client 接收消息
        // TODO 直接转发对方 client（报文头可能需要稍作修改）
    }
    
    
    /**
     * 选择执子颜色
     *
     * @param message 接收到的报文
     */
    @Override
    protected void handleChoosePlayerColor(String message) {
        // TODO 从 message 解析 state （按钮按键）（0执黑 1执白 2继续）
        int state = 0;
        if (board.getHistorySize() == 3) {
            if (state == JOptionPane.YES_OPTION)
                board.choosePlayer1Color(StoneType.WHITE);
            else if (state == JOptionPane.NO_OPTION)
                board.choosePlayer1Color(StoneType.BLACK);
        }
        else if (!board.isPlayerColorChosen() && board.getHistorySize() == 5) {
            if (state == JOptionPane.YES_OPTION)
                board.choosePlayer1Color(StoneType.BLACK);
            else
                board.choosePlayer1Color(StoneType.WHITE);
        }
        
        if (board.isPlayerColorChosen()) {
            int presetStoneNumber = board.getHistorySize();
            /**
             * message
             * @arg presetStoneNumber 预先放置的棋子数
             * @arg type              玩家棋子类型
             */
            // TODO 向双方 client 发送对应的棋子执子
        }
    }
}
