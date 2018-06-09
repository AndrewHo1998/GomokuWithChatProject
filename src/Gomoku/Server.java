package Gomoku;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.ArrayList;
import java.util.EmptyStackException;

public class Server extends AbstractSocket {
    private boolean waitingForResponse; // 是否正在等待 client 回应
    private int waitingForResponseClientId; // 是否正在等待的 client 的 ID （1 或 2）
    private Board board; // 棋盘
    private int player1ClientId; // 玩家 1 的客户端编号（1 或 2）
    
    
    // TODO 初始化
    public Server(/* args */) {
        super();
        socketId = 0;
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
    protected void handleNewGame(byte[] message) {
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
    protected void handleInquireToNewGame(byte[] message) {
        int clientId = parseSocketId(message); // 从 message 解析 clientId
        waitingForResponse = true; // 等待对方 client 回应
        waitingForResponseClientId = 3 - clientId; // 对方的 clientId
        byte[] newMessage = packMessage(INQUIRE_TO_NEW_GAME, null);
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
     * @implNote client 不可能接收到这个消息
     */
    @Override
    protected void handleAcceptToNewGame(byte[] message) {
        // 接收函数已保证从正确的 client 接收消息
        int clientId = parseSocketId(message); // 从 message 解析 clientId
        board.newGame();
        player1ClientId = 3 - clientId; // 请求新建游戏的玩家的编号为 1，同意新建游戏的玩家的编号为 2（就是本函数 message 的来源）。
        byte[] player1Message = packNewGame(1);
        byte[] player2Message = packNewGame(2);
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
    protected void handleRejectToNewGame(byte[] message) {
        // 接收函数已保证从正确的 client 接收消息
        byte[] newMessage = packMessage(REJECT_TO_NEW_GAME, null);
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
    protected void handleGameOver(byte[] message) {
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
    protected void handleAdmitDefeat(byte[] message) {
        int clientId = parseSocketId(message); // 从 message 解析 clientId
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
        byte[] gameOverMessage = packGameOver(winnerNumber, indexOfRowStones, rowStones);
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
    protected void handlePutStone(byte[] message) {
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
    protected void handleInquireToPutStone(byte[] message) {
        int clientId = parseSocketId(message); // 从 message 解析 clientId
        int playerNumber = (clientId == player1ClientId ? 1 : 2);
        if (playerNumber != board.getNextPlayerNumber())
            return;
        Object[] messageArgs = unpackInquireToPutStone(message);
        try {
            int i = (Integer) messageArgs[0]; // 从 message 解析 (i, j)
            int j = (Integer) messageArgs[1];
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
            byte[] putStoneMessage = packPutStone(stone, previousStone, historySize);
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
                    byte[] gameOverMessage = packGameOver(winnerNumber, indexOfRowStones, rowStones));
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
    protected void handleRetractStone(byte[] message) {
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
    protected void handleInquireToRetractStone(byte[] message) {
        int clientId = parseSocketId(message); // 从 message 解析 clientId
        waitingForResponse = true; // 等待对方 client 回应
        waitingForResponseClientId = 3 - clientId; // 对方的 clientId
        byte[] newMessage = packMessage(INQUIRE_TO_RETRACT_STONE, null);
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
    protected void handleAcceptToRetractStone(byte[] message) {
        // 接收函数已保证从正确的 client 接收消息
        try {
            Stone stone = board.retractStone();
            Stone previousStone = board.getLastStone();
            int historySize = board.getHistorySize();
            byte[] retractStoneMessage = packRetractStone(stone, previousStone, historySize);
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
    protected void handleRejectToRetractStone(byte[] message) {
        // 接收函数已保证从正确的 client 接收消息
        byte[] newMessage = packMessage(REJECT_TO_RETRACT_STONE, null);
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
    protected void handleChoosePlayerColor(byte[] message) {
        Object[] messageArgs = unpackChoosePlayerColor(message);
        int state = (Integer) messageArgs[0]; // 从 message 解析 state （按钮按键）（0执黑 1执白 2继续）
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
            byte[] player1Message = packSetPlayerColor(player1StoneType, presetStoneNumber);
            byte[] player2Message = packSetPlayerColor(player2StoneType, presetStoneNumber);
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
    protected void handleSetPlayerColor(byte[] message) {
    }
    
    
    /**
     * client 发送聊天消息，server 直接转发对方 client。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = CHAT_TEXT
     */
    @Override
    protected void handleChatText(byte[] message) {
        Object[] messageArgs = unpackChoosePlayerColor(message);
        int clientId = parseSocketId(message);
        int destClientId = 3 - clientId;
        String chatText = (String) messageArgs[0];
        byte[] newMessage = packChatText(chatText);
        // TODO server 直接转发对方 client
    }
}
