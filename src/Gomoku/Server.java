package Gomoku;

import javax.swing.*;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class Server extends AbstractSocket {
    private boolean waitingForResponse; // 是否正在等待 client 回应
    private int waitingForResponseClientId; // 是否正在等待的 client 的 ID （1 或 2）
    private Board board; // 棋盘
    private int player1ClientId; // 玩家 1 的客户端编号（1 或 2）
    
    
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
     */
    @Override
    public boolean receive() {
        // TODO 接收消息
        int clientId = 1; // TODO 从 message 解析 clientId
        if (waitingForResponse) {
            if (waitingForResponseClientId == clientId) {
                waitingForResponse = false; // 不再等待 client 回应
                waitingForResponseClientId = 0;
            }
            else {
                // 从错误的 client 接收消息
                // TODO 错误处理
            }
        }
        // handleMessage(message);
        return true;
    }
    
    
    /**
     * server 向双方 client 发送新建游戏命令
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = NEW_GAME
     * @implNote server 不可能接收到这个消息
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
     *
     * @implNote messageType = INQUIRE_TO_NEW_GAME
     */
    @Override
    protected void handleInquireToNewGame(String message) {
        int clientId = 1; // TODO 从 message 解析 clientId
        waitingForResponse = true; // 等待对方 client 回应
        waitingForResponseClientId = 3 - clientId; // 对方的 clientId
        /**
         * TODO 直接转发对方 client（报文头可能需要稍作修改）
         * @messageType INQUIRE_TO_NEW_GAME
         */
    }
    
    
    /**
     * client 同意新建游戏，server 新建游戏，并向双方 client 发送新建游戏命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = ACCEPT_TO_NEW_GAME
     */
    @Override
    protected void handleAcceptToNewGame(String message) {
        // 接收函数已保证从正确的 client 接收消息
        int clientId = 1; // TODO 从 message 解析 clientId
        board.newGame();
        player1ClientId = 3 - clientId; // 请求新建游戏的玩家的编号为 1，同意新建游戏的玩家的编号为 2（就是本函数 message 的来源）。
        /**
         * TODO 向双方 client 发送新建游戏命令
         * @messageType NEW_GAME
         * @messageArg playerNumber 玩家编号
         */
    }
    
    
    /**
     * client 拒绝新建游戏，server 直接转发对方 client。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = REJECT_TO_NEW_GAME
     */
    @Override
    protected void handleRejectToNewGame(String message) {
        // 接收函数已保证从正确的 client 接收消息
        /**
         * TODO 直接转发对方 client（报文头可能需要稍作修改）
         * @messageType REJECT_TO_NEW_GAME
         */
    }
    
    
    /**
     * server 向双方 client 发送游戏结束命令
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = GAME_OVER
     * @implNote server 不可能接收到这个消息
     */
    @Override
    protected void handleGameOver(String message) {
    }
    
    
    /**
     * client 认输，server 结束游戏，server 接收后向双方 client 发送游戏结束命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = ADMIT_DEFEAT
     * @implNote client 不可能接收到这个消息
     */
    @Override
    protected void handleAdmitDefeat(String message) {
        int clientId = 1; // TODO 从 message 解析 clientId
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
         * TODO 向双方 client 发送游戏结束命令
         * @messageType GAME_OVER
         * @messageArg winnerNumber     胜者编号
         * @messageArg indexOfRowStones 连珠的棋子编号
         * @messageArg rowStones        连珠的棋子
         */
    }
    
    
    /**
     * server 向双方 client 发送落子命令
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = PUT_STONE
     * @implNote server 不可能接收到这个消息
     */
    @Override
    protected void handlePutStone(String message) {
    }
    
    
    /**
     * client 请求落子，server 进行处理，若可以落子则向双方 client 发送落子命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = INQUIRE_TO_PUT_STONE
     * @implNote client 不可能接收到这个消息
     */
    @Override
    protected void handleInquireToPutStone(String message) {
        int clientId = 1; // TODO 从 message 解析 clientId
        int playerNumber = (clientId == player1ClientId ? 1 : 2);
        if (playerNumber != board.getNextPlayerNumber())
            return;
        try {
            int i = 0; // TODO 从 message 解析 (i, j)
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
             * TODO 向双方 client 发送落子命令
             * @messageType PUT_STONE
             * @messageArg stone         落子的 stone
             * @messageArg previousStone 落子的 stone 的前一个 stone，若没有则传入 null。
             * @messageArg historySize   落子完成后棋盘上的棋子数
             */
            
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
                     * TODO 向双方 client 发送游戏结束命令
                     * @messageType GAME_OVER
                     * @messageArg winnerNumber     胜者编号
                     * @messageArg indexOfRowStones 连珠的棋子编号
                     * @messageArg rowStones        连珠的棋子
                     */
                }
            }
        }
        catch (GameNotStartedException | BadInputStoneException ignored) {
        }
    }
    
    
    /**
     * server 向双方 client 发送悔棋命令
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = RETRACT_STONE
     * @implNote server 不可能接收到这个消息
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
     *
     * @implNote messageType = INQUIRE_TO_RETRACT_STONE
     */
    @Override
    protected void handleInquireToRetractStone(String message) {
        int clientId = 1; // TODO 从 message 解析 clientId
        waitingForResponse = true; // 等待对方 client 回应
        waitingForResponseClientId = 3 - clientId; // 对方的 clientId
        /**
         * TODO 直接转发对方 client（报文头可能需要稍作修改）
         * @messageType INQUIRE_TO_RETRACT_STONE
         */
    }
    
    
    /**
     * client 同意悔棋，server 悔棋，并向双方 client 发送悔棋命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = ACCEPT_TO_RETRACT_STONE
     * @implNote client 不可能接收到这个消息
     */
    @Override
    protected void handleAcceptToRetractStone(String message) {
        // 接收函数已保证从正确的 client 接收消息
        try {
            Stone stone = board.retractStone();
            Stone previousStone = board.getLastStone();
            int historySize = board.getHistorySize();
            /**
             * TODO 向双方 client 发送悔棋命令
             * @messageType RETRACT_STONE
             * @messageArg stone         被移走的 stone
             * @messageArg previousStone 被移走的 stone 的前一个 stone，因为可以悔棋时棋盘上至少有 4 个棋子，必然是非 null。
             * @messageArg historySize   悔棋完成后棋盘上的棋子数
             */
        }
        catch (GameNotStartedException ignored) {
        }
    }
    
    
    /**
     * client 拒绝悔棋，server 直接转发对方 client。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = REJECT_TO_RETRACT_STONE
     */
    @Override
    protected void handleRejectToRetractStone(String message) {
        // 接收函数已保证从正确的 client 接收消息
        /**
         * TODO 直接转发对方 client（报文头可能需要稍作修改）
         * @messageType REJECT_TO_RETRACT_STONE
         */
    }
    
    
    /**
     * client 选择执子颜色
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = CHOOSE_PLAYER_COLOR
     * @implNote client 不可能接收到这个消息
     */
    @Override
    protected void handleChoosePlayerColor(String message) {
        int state = 0; // TODO 从 message 解析 state （按钮按键）（0执黑 1执白 2继续）
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
            StoneType player1StoneType = board.getPlayer1StoneType();
            StoneType player2StoneType = (player1StoneType == StoneType.BLACK ? StoneType.WHITE : StoneType.BLACK);
            int presetStoneNumber = board.getHistorySize();
            /**
             * TODO 向双方 client 发送对应的执子颜色
             * @messageType SET_PLAYER_COLOR
             * @arg playerStoneType   玩家棋子类型
             * @arg presetStoneNumber 预先放置的棋子数
             */
        }
    }
    
    
    /**
     * server 指定玩家执子颜色
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = SET_PLAYER_COLOR
     * @implNote server 不可能接收到这个消息
     */
    @Override
    protected void handleSetPlayerColor(String message) {
    }
}
