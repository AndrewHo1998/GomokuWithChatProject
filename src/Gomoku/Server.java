package Gomoku;

import javax.swing.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.EmptyStackException;

public class Server extends AbstractSocket {
    public static final int PORT = 10000;
    
    private boolean waitingForResponse; // 是否正在等待 client1 回应
    private int waitingForResponseClientId; // 是否正在等待的 client1 的 ID （1 或 2）
    private Board board; // 棋盘
    private int player1ClientId; // 玩家 1 的客户端编号（1 或 2）
    private final LinkedBlockingDeque<byte[]> messageQueue;
    
    private final ServerSocket server;
    private Socket client1;
    private Socket client2;
    
    
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket clientSocket1 = new Socket("127.0.0.1", Server.PORT);
            Socket clientSocket2 = new Socket("127.0.0.1", Server.PORT);
            System.out.println("Server running on local port:" + serverSocket.getLocalPort());
            System.out.println("client1 running on local port:" + clientSocket1.getLocalPort());
            System.out.println("client2 running on local port:" + clientSocket1.getLocalPort());
            Server server = new Server(serverSocket);
            Client client1 = new Client(clientSocket1);
            client1.setClientId(1);
            Client client2 = new Client(clientSocket2);
            client2.setClientId(2);
        }
        catch (IOException ignored) {
        }
    }
    
    
    public Server(ServerSocket server) {
        super();
        socketId = 0;
        board = new Board();
        waitingForResponse = false;
        waitingForResponseClientId = 0;
        player1ClientId = 0;
        this.server = server;
        messageQueue = new LinkedBlockingDeque<byte[]>();
        
        try {
            client1 = server.accept();
            client2 = server.accept();
            Thread receiveFromClient1 = new Thread(() -> receiveFromClient(client1));
            Thread receiveFromClient2 = new Thread(() -> receiveFromClient(client2));
            receiveFromClient1.start();
            receiveFromClient2.start();
        }
        catch (IOException ignored) {
        }
        
        Thread service = new Thread(() -> {
            while (true) {
                if (messageQueue.isEmpty())
                    continue;
                else {
                    byte[] message = messageQueue.pop();
                    // System.out.println(message);
                    if (waitingForResponse) {
                        if (waitingForResponseClientId == parseSocketId(message)) {
                            waitingForResponse = false;
                            waitingForResponseClientId = 0;
                            handleMessage(message);
                        }
                        else
                            continue;
                    }
                    else
                        handleMessage(message);
                }
            }
        });
        
        service.start();
    }
    
    
    private void receiveFromClient(Socket client) {
        while (!client.isClosed()) {
            try {
                InputStream is = client.getInputStream();
                byte[] message = receivePackage(is);
                printMessage(message);
                messageQueue.add(message);
            }
            catch (IOException ignored) {
                System.out.println("exception");
            }
        }
    }
    
    
    public void sendToClient(int clientId, byte[] message) {
        try {
            if (clientId == 1)
                sendPackage(client1.getOutputStream(), message);
            else
                sendPackage(client2.getOutputStream(), message);
        }
        catch (IOException ignored) {
        }
    }
    
    
    protected void sendToPlayer(int playerId, byte[] message) {
        if (playerId == 1)
            sendToClient(player1ClientId, message);
        else
            sendToClient(3 - player1ClientId, message);
    }
    
    
    public void close() throws IOException {
        server.close();
        client1.close();
        client2.close();
    }
    
    
    /**
     * server 向双方 client1 发送新建游戏命令
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
     * client1 请求新建游戏，server 直接转发对方 client1。
     * client1 弹出窗口，让用户选择是否开始。
     * server 直接转发对方 client1
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = INQUIRE_TO_NEW_GAME
     */
    @Override
    protected void handleInquireToNewGame(byte[] message) {
        int destClientId = 3 - parseSocketId(message); // 从 message 解析 clientId
        waitingForResponse = true; // 等待对方 client1 回应
        waitingForResponseClientId = destClientId; // 对方的 clientId
        byte[] newMessage = packMessage(INQUIRE_TO_NEW_GAME, null);
        sendToClient(destClientId, newMessage);
        /**
         * TODO 直接转发对方 client（报文头可能需要稍作修改）
         * @messageType INQUIRE_TO_NEW_GAME
         */
    }
    
    
    /**
     * client1 同意新建游戏，server 新建游戏，并向双方 client1 发送新建游戏命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = ACCEPT_TO_NEW_GAME
     * @implNote client1 不可能接收到这个消息
     */
    @Override
    protected void handleAcceptToNewGame(byte[] message) {
        // 接收函数已保证从正确的 client1 接收消息
        int clientId = parseSocketId(message); // 从 message 解析 clientId
        board.newGame();
        player1ClientId = 3 - clientId; // 请求新建游戏的玩家的编号为 1，同意新建游戏的玩家的编号为 2（就是本函数 message 的来源）。
        byte[] player1Message = packNewGame(1);
        byte[] player2Message = packNewGame(2);
        sendToClient(player1ClientId, player1Message);
        sendToClient(clientId, player2Message);
        /**
         * TODO 向双方 client1 发送新建游戏命令
         * @messageType NEW_GAME
         * @messageArg playerNumber 玩家编号
         */
    }
    
    
    /**
     * client1 拒绝新建游戏，server 直接转发对方 client1。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = REJECT_TO_NEW_GAME
     */
    @Override
    protected void handleRejectToNewGame(byte[] message) {
        // 接收函数已保证从正确的 client1 接收消息
        int destClientId = 3 - parseSocketId(message);
        byte[] newMessage = packMessage(REJECT_TO_NEW_GAME, null);
        sendToClient(destClientId, newMessage);
        /**
         * TODO 直接转发对方 client1（报文头可能需要稍作修改）
         * @messageType REJECT_TO_NEW_GAME
         */
    }
    
    
    /**
     * server 向双方 client1 发送游戏结束命令
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
     * client1 认输，server 结束游戏，server 接收后向双方 client1 发送游戏结束命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = ADMIT_DEFEAT
     * @implNote client1 不可能接收到这个消息
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
        byte[] gameOverMessage = packGameOver(winnerNumber, indexOfRowStones, rowStones);
        sendToClient(1, gameOverMessage);
        sendToClient(2, gameOverMessage);
        /**
         * TODO 向双方 client1 发送游戏结束命令
         * @messageType GAME_OVER
         * @messageArg winnerNumber     胜者编号
         * @messageArg indexOfRowStones 连珠的棋子编号
         * @messageArg rowStones        连珠的棋子
         */
        board.reset();
    }
    
    
    /**
     * server 向双方 client1 发送落子命令
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
     * client1 请求落子，server 进行处理，若可以落子则向双方 client1 发送落子命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = INQUIRE_TO_PUT_STONE
     * @implNote client1 不可能接收到这个消息
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
            sendToClient(1, putStoneMessage);
            sendToClient(2, putStoneMessage);
            /**
             * TODO 向双方 client1 发送落子命令
             * @messageType PUT_STONE
             * @messageArg stone         落子的 stone
             * @messageArg previousStone 落子的 stone 的前一个 stone，若没有则传入 null。
             * @messageArg historySize   落子完成后棋盘上的棋子数
             */
            
            // 若没有选择玩家颜色
            if (!board.isPlayerColorChosen() && (board.getHistorySize() == 3 || board.getHistorySize() == 5)) {
                waitingForResponse = true; // 等待 client1 回应
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
                    byte[] gameOverMessage = packGameOver(winnerNumber, indexOfRowStones, rowStones);
                    sendToClient(1, gameOverMessage);
                    sendToClient(2, gameOverMessage);
                    /**
                     * TODO 向双方 client1 发送游戏结束命令
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
     * server 向双方 client1 发送悔棋命令
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
     * client1 请求悔棋，server 直接转发对方 client1。
     * client1 弹出窗口，让用户选择是否同意。
     * server 直接转发对方 client1
     *
     * @param message 报文
     *
     * @implNote messageType = INQUIRE_TO_RETRACT_STONE
     */
    @Override
    protected void handleInquireToRetractStone(byte[] message) {
        int destClientId = 3 - parseSocketId(message); // 从 message 解析 clientId
        waitingForResponse = true; // 等待对方 client1 回应
        waitingForResponseClientId = destClientId; // 对方的 clientId
        byte[] newMessage = packMessage(INQUIRE_TO_RETRACT_STONE, null);
        sendToClient(destClientId, newMessage);
        /**
         * TODO 直接转发对方 client1（报文头可能需要稍作修改）
         * @messageType INQUIRE_TO_RETRACT_STONE
         */
    }
    
    
    /**
     * client1 同意悔棋，server 悔棋，并向双方 client1 发送悔棋命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = ACCEPT_TO_RETRACT_STONE
     * @implNote client1 不可能接收到这个消息
     */
    @Override
    protected void handleAcceptToRetractStone(byte[] message) {
        // 接收函数已保证从正确的 client1 接收消息
        try {
            Stone stone = board.retractStone();
            Stone previousStone = board.getLastStone();
            int historySize = board.getHistorySize();
            byte[] retractStoneMessage = packRetractStone(stone, previousStone, historySize);
            sendToClient(1, retractStoneMessage);
            sendToClient(2, retractStoneMessage);
            /**
             * TODO 向双方 client1 发送悔棋命令
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
     * client1 拒绝悔棋，server 直接转发对方 client1。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = REJECT_TO_RETRACT_STONE
     */
    @Override
    protected void handleRejectToRetractStone(byte[] message) {
        // 接收函数已保证从正确的 client1 接收消息
        int destClientId = 3 - parseSocketId(message); // 从 message 解析 clientId
        byte[] newMessage = packMessage(REJECT_TO_RETRACT_STONE, null);
        sendToClient(destClientId, newMessage);
        /**
         * TODO 直接转发对方 client1（报文头可能需要稍作修改）
         * @messageType REJECT_TO_RETRACT_STONE
         */
    }
    
    
    /**
     * client1 选择执子颜色
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = CHOOSE_PLAYER_COLOR
     * @implNote client1 不可能接收到这个消息
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
            sendToPlayer(1, player1Message);
            sendToPlayer(2, player2Message);
            /**
             * TODO 向双方 client1 发送对应的执子颜色
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
     * client1 发送聊天消息，server 直接转发对方 client1。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = CHAT_TEXT
     */
    @Override
    protected void handleChatText(byte[] message) {
        Object[] messageArgs = unpackChoosePlayerColor(message);
        int destClientId = 3 - parseSocketId(message); // 从 message 解析 clientId
        String chatText = (String) messageArgs[0];
        byte[] newMessage = packChatText(chatText);
        sendToClient(destClientId, newMessage);
        // TODO server 直接转发对方 client1
    }
}
