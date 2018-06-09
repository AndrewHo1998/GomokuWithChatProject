package Gomoku;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSocket extends Socket {
    protected int socketId;
    public static final int headLength = 6;
    public static final int NEW_GAME = 0;                  // server 向双方 client 发送新建游戏命令
    public static final int INQUIRE_TO_NEW_GAME = 1;       // client 请求新建游戏，server 直接转发对方 client。
    public static final int ACCEPT_TO_NEW_GAME = 2;        // client 同意新建游戏，server 新建游戏，并向双方 client 发送新建游戏命令。
    public static final int REJECT_TO_NEW_GAME = 3;        // client 拒绝新建游戏，server 直接转发对方 client。
    public static final int GAME_OVER = 4;                 // server 向双方 client 发送游戏结束命令
    public static final int ADMIT_DEFEAT = 5;              // client 认输，server 结束游戏，server 接收后向双方 client 发送游戏结束命令。
    public static final int PUT_STONE = 6;                 // server 向双方 client 发送落子命令
    public static final int INQUIRE_TO_PUT_STONE = 7;      // client 请求落子，server 进行处理，若可以落子则向双方 client 发送落子命令。
    public static final int RETRACT_STONE = 8;             // server 向双方 client 发送悔棋命令
    public static final int INQUIRE_TO_RETRACT_STONE = 9;  // client 请求悔棋，server 直接转发对方 client。
    public static final int ACCEPT_TO_RETRACT_STONE = 10;  // client 同意悔棋，server 悔棋，并向双方 client 发送悔棋命令。
    public static final int REJECT_TO_RETRACT_STONE = 11;  // client 拒绝悔棋，server 直接转发对方 client。
    public static final int CHOOSE_PLAYER_COLOR = 12;      // client 选择执子颜色
    public static final int SET_PLAYER_COLOR = 13;         // server 指定玩家执子颜色
    public static final int CHAT_TEXT = 14;                // client 发送聊天消息，server 直接转发对方 client。
    
    
    public int getSocketId() {
        return socketId;
    }
    
    
    public void setSocketId(int socketId) {
        this.socketId = socketId;
    }
    
    
    /**
     * 向其他 Socket 发送报文，返回是否发送成功。
     *
     * @param message 报文内容
     */
    public boolean send(byte[] message) {
        boolean success = false;
        // TODO 待修改
        return success;
    }
    
    
    /**
     * 从其他 Socket 接受报文，返回是否接收成功。
     */
    public boolean receive() {
        boolean success = false;
        // TODO 待修改
        // handleMessage(message);
        return success;
    }
    
    
    /**
     * 解析接收到的报文类型
     *
     * @param message 接收到的报文
     */
    protected static int parseMessageType(byte[] message) {
        return message[headLength - 1];
    }
    
    
    /**
     * 处理接收到的报文
     *
     * @param message 接收到的报文
     */
    protected void handleMessage(byte[] message) {
        int messageType = parseMessageType(message);
        switch (messageType) {
            case NEW_GAME:                 // server 向双方 client 发送新建游戏命令
                handleNewGame(message);
                break;
            case INQUIRE_TO_NEW_GAME:      // client 请求新建游戏，server 直接转发对方 client。
                handleInquireToNewGame(message);
                break;
            case ACCEPT_TO_NEW_GAME:       // client 同意新建游戏，server 新建游戏，并向双方 client 发送新建游戏命令。
                handleAcceptToNewGame(message);
                break;
            case REJECT_TO_NEW_GAME:       // client 拒绝新建游戏，server 直接转发对方 client。
                handleRejectToNewGame(message);
                break;
            case GAME_OVER:                // server 向双方 client 发送游戏结束命令
                handleGameOver(message);
                break;
            case ADMIT_DEFEAT:             // client 认输，server 结束游戏，server 接收后向双方 client 发送游戏结束命令。
                handleAdmitDefeat(message);
                break;
            case PUT_STONE:                // server 向双方 client 发送落子命令
                handlePutStone(message);
                break;
            case INQUIRE_TO_PUT_STONE:     // client 请求落子，server 进行处理，若可以落子则向双方 client 发送落子命令。
                handleInquireToPutStone(message);
                break;
            case RETRACT_STONE:            // server 向双方 client 发送悔棋命令
                handleRetractStone(message);
                break;
            case INQUIRE_TO_RETRACT_STONE: // client 请求悔棋，server 直接转发对方 client。
                handleInquireToRetractStone(message);
                break;
            case ACCEPT_TO_RETRACT_STONE:  // client 同意悔棋，server 悔棋，并向双方 client 发送悔棋命令。
                handleAcceptToRetractStone(message);
                break;
            case REJECT_TO_RETRACT_STONE:  // client 拒绝悔棋，server 直接转发对方 client。
                handleRejectToRetractStone(message);
                break;
            case CHOOSE_PLAYER_COLOR:      // client 选择执子颜色
                handleChoosePlayerColor(message);
                break;
            case SET_PLAYER_COLOR:         // server 指定玩家执子颜色
                handleSetPlayerColor(message);
                break;
            case CHAT_TEXT:
                handleChatText(message);
                break;
        }
    }
    
    
    /**
     * server 向双方 client 发送新建游戏命令
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = NEW_GAME
     * @implNote server 不可能接收到这个消息
     */
    protected abstract void handleNewGame(byte[] message);
    
    /**
     * client 请求新建游戏，server 直接转发对方 client。
     * client 弹出窗口，让用户选择是否开始。
     * server 直接转发对方 client
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = INQUIRE_TO_NEW_GAME
     */
    protected abstract void handleInquireToNewGame(byte[] message);
    
    /**
     * client 同意新建游戏，server 新建游戏，并向双方 client 发送新建游戏命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = ACCEPT_TO_NEW_GAME
     * @implNote client 不可能接收到这个消息
     */
    protected abstract void handleAcceptToNewGame(byte[] message);
    
    /**
     * client 拒绝新建游戏，server 直接转发对方 client。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = REJECT_TO_NEW_GAME
     */
    protected abstract void handleRejectToNewGame(byte[] message);
    
    /**
     * server 向双方 client 发送游戏结束命令
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = GAME_OVER
     * @implNote server 不可能接收到这个消息
     */
    protected abstract void handleGameOver(byte[] message);
    
    /**
     * client 认输，server 结束游戏，server 接收后向双方 client 发送游戏结束命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = ADMIT_DEFEAT
     * @implNote client 不可能接收到这个消息
     */
    protected abstract void handleAdmitDefeat(byte[] message);
    
    /**
     * server 向双方 client 发送落子命令
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = PUT_STONE
     * @implNote server 不可能接收到这个消息
     */
    protected abstract void handlePutStone(byte[] message);
    
    /**
     * client 请求落子，server 进行处理，若可以落子则向双方 client 发送落子命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = INQUIRE_TO_PUT_STONE
     * @implNote client 不可能接收到这个消息
     */
    protected abstract void handleInquireToPutStone(byte[] message);
    
    /**
     * server 向双方 client 发送悔棋命令
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = RETRACT_STONE
     * @implNote server 不可能接收到这个消息
     */
    protected abstract void handleRetractStone(byte[] message);
    
    /**
     * client 请求悔棋，server 直接转发对方 client。
     * client 弹出窗口，让用户选择是否同意。
     * server 直接转发对方 client
     *
     * @param message 报文
     *
     * @implNote messageType = INQUIRE_TO_RETRACT_STONE
     */
    protected abstract void handleInquireToRetractStone(byte[] message);
    
    /**
     * client 同意悔棋，server 悔棋，并向双方 client 发送悔棋命令。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = ACCEPT_TO_RETRACT_STONE
     * @implNote client 不可能接收到这个消息
     */
    protected abstract void handleAcceptToRetractStone(byte[] message);
    
    /**
     * client 拒绝悔棋，server 直接转发对方 client。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = REJECT_TO_RETRACT_STONE
     */
    protected abstract void handleRejectToRetractStone(byte[] message);
    
    /**
     * client 选择执子颜色
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = CHOOSE_PLAYER_COLOR
     * @implNote client 不可能接收到这个消息
     */
    protected abstract void handleChoosePlayerColor(byte[] message);
    
    /**
     * server 指定玩家执子颜色
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = SET_PLAYER_COLOR
     * @implNote server 不可能接收到这个消息
     */
    protected abstract void handleSetPlayerColor(byte[] message);
    
    /**
     * client 发送聊天消息，server 直接转发对方 client。
     *
     * @param message 接收到的报文
     *
     * @implNote messageType = CHAT_TEXT
     */
    protected abstract void handleChatText(byte[] message);
    
    
    protected byte[] packMessage(int messageType, byte[] message) {
        if (message == null)
            message = new byte[0];
        byte[] packedMessage = new byte[headLength + message.length];
        System.arraycopy(message, 0, packedMessage, 0, message.length);
        packedMessage[0] = (byte) socketId;
        int length = message.length;
        packedMessage[1] = (byte) (length >> 24);
        packedMessage[2] = (byte) (length >> 16);
        packedMessage[3] = (byte) (length >> 8);
        packedMessage[4] = (byte) length;
        packedMessage[5] = (byte) messageType;
        return packedMessage;
    }
    
    
    protected byte[] packNewGame(int playerNumber) {
        byte[] message = {(byte) playerNumber};
        return packMessage(NEW_GAME, message);
    }
    
    
    protected Object[] unpackNewGame(byte[] message) {
        int playerNumber = message[headLength];
        return new Object[]{playerNumber};
    }
    
    
    protected byte[] packGameOver(int winnerNumber, List<Integer> indexOfRowStones, List<Stone> rowStones) {
        int rowStoneNumber = rowStones.size();
        byte[] message = new byte[3 + 3 * rowStoneNumber];
        message[0] = (byte) winnerNumber;
        message[1] = (byte) rowStoneNumber;
        message[2] = (byte) (rowStones.get(0).getType() == StoneType.BLACK ? 1 : 2);
        for (int index = 0; index < rowStoneNumber; ++index)
            message[2 + index] = indexOfRowStones.get(index).byteValue();
        for (int index = 0; index < rowStoneNumber; ++index) {
            message[2 + 2 * rowStoneNumber + 2 * index] = (byte) rowStones.get(index).getI();
            message[2 + 2 * rowStoneNumber + 2 * index + 1] = (byte) rowStones.get(index).getJ();
        }
        return packMessage(GAME_OVER, message);
    }
    
    
    protected Object[] unpackGameOver(byte[] message) {
        int winnerNumber = message[headLength];
        int rowStoneNumber = message[headLength + 1];
        StoneType stoneType = (message[headLength + 2] == 1 ? StoneType.BLACK : StoneType.WHITE);
        List<Integer> indexOfRowStones = new ArrayList<Integer>();
        List<Stone> rowStones = new ArrayList<Stone>();
        for (int index = 0; index < rowStoneNumber; ++index)
            indexOfRowStones.add((int) message[headLength + 3 + index]);
        for (int index = 0; index < rowStoneNumber; ++index) {
            try {
                int i = message[2 + 2 * rowStoneNumber + 2 * index];
                int j = message[2 + 2 * rowStoneNumber + 2 * index + 1];
                rowStones.add(new Stone(i, j, stoneType));
            }
            catch (StoneOutOfBoardRangeException ignored) {
            }
        }
        return new Object[]{winnerNumber, indexOfRowStones, rowStones};
    }
    
    
    protected byte[] packPutStone(Stone stone, Stone previousStone, int historySize) {
        byte[] message = new byte[7];
        message[0] = (byte) stone.getI();
        message[1] = (byte) stone.getJ();
        message[2] = (byte) (stone.getType() == StoneType.BLACK ? 1 : 2);
        message[3] = (byte) previousStone.getI();
        message[4] = (byte) previousStone.getJ();
        message[5] = (byte) (previousStone.getType() == StoneType.BLACK ? 1 : 2);
        message[6] = (byte) historySize;
        return packMessage(PUT_STONE, message);
    }
    
    
    protected Object[] unpackPutStone(byte[] message) {
        Stone stone = null, previousStone = null;
        try {
            stone = new Stone(message[headLength], message[headLength + 1], (message[headLength + 2] == 1 ? StoneType.BLACK : StoneType.WHITE));
            previousStone = new Stone(message[headLength + 3], message[headLength + 4], (message[headLength + 5] == 1 ? StoneType.BLACK : StoneType.WHITE));
        }
        catch (StoneOutOfBoardRangeException ignored) {
        }
        int historySize = message[headLength + 6];
        return new Object[]{stone, previousStone, historySize};
    }
    
    
    protected byte[] packRetractStone(Stone stone, Stone previousStone, int historySize) {
        byte[] putStoneMessage = packPutStone(stone, previousStone, historySize);
        putStoneMessage[headLength - 1] = (byte) RETRACT_STONE;
        return putStoneMessage;
    }
    
    
    protected Object[] unpackRetractStone(byte[] message) {
        return unpackPutStone(message);
    }
    
    
    protected byte[] packChoosePlayerColor(int state) {
        byte[] message = {(byte) state};
        return packMessage(CHOOSE_PLAYER_COLOR, message);
    }
    
    
    protected Object[] unpackChoosePlayerColor(byte[] message) {
        int state = message[headLength];
        return new Object[]{state};
    }
    
    
    protected byte[] packSetPlayerColor(StoneType stoneType, int presetStoneNumber) {
        byte[] message = {(byte) (stoneType == StoneType.BLACK ? 1 : 2), (byte) presetStoneNumber};
        return packMessage(SET_PLAYER_COLOR, message);
    }
    
    
    protected Object[] unpackSetPlayerColor(byte[] message) {
        StoneType stoneType = (message[headLength] == 1 ? StoneType.BLACK : StoneType.WHITE);
        int presetStoneNumber = message[headLength + 1];
        return new Object[]{stoneType, presetStoneNumber};
    }
    
    
    protected byte[] packChatText(String text) {
        return packMessage(CHAT_TEXT, text.getBytes());
    }
    
    
    protected Object[] unpackChatText(byte[] message) {
        String text = new String(message, headLength, message.length - headLength);
        return new Object[]{text};
    }
}




