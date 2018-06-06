package Gomoku;

import java.net.Socket;
import java.util.EmptyStackException;

public abstract class AbstractSocket extends Socket {
    public static final int NEW_GAME = 0;                  // server 向双方 client 发送新建游戏命令
    public static final int REQUIRE_TO_NEW_GAME = 1;       // client 请求新建游戏，server 直接转发对方 client。
    public static final int ACCEPT_TO_NEW_GAME = 2;        // client 同意新建游戏，server 新建游戏，并向双方 client 发送新建游戏命令。
    public static final int REJECT_TO_NEW_GAME = 3;        // client 拒绝新建游戏，server 直接转发对方 client。
    public static final int GAME_OVER = 4;                 // server 向双方 client 发送游戏结束命令
    public static final int ADMIT_DEFEAT = 5;              // client 认输，server 结束游戏，server 接收后向双方 client 发送游戏结束命令。
    public static final int PUT_STONE = 6;                 // server 向双方 client 发送落子命令
    public static final int REQUIRE_TO_PUT_STONE = 7;      // client 请求落子，server 进行处理，若可以落子则向双方 client 发送落子命令。
    public static final int RETRACT_STONE = 8;             // server 向双方 client 发送悔棋命令
    public static final int REQUIRE_TO_RETRACT_STONE = 9;  // client 请求悔棋，server 直接转发对方 client。
    public static final int ACCEPT_TO_RETRACT_STONE = 10;  // client 同意悔棋，server 悔棋，并向双方 client 发送悔棋命令。
    public static final int REJECT_TO_RETRACT_STONE = 11;  // client 拒绝悔棋，server 直接转发对方 client。
    public static final int CHOOSE_PLAYER_COLOR = 12;      // client 选择执子颜色
    public static final int SET_PLAYER_COLOR = 13;         // server 指定玩家执子颜色
    
    
    /**
     * 向其他 Socket 发送报文，返回是否发送成功。
     *
     * @param message 报文内容
     */
    public boolean send(String message) {
        boolean success = false;
        // TODO 待修改
        return success;
    }
    
    
    /**
     * 从其他 Socket 接受报文，返回是否接收成功。
     *
     * @param message 报文内容
     */
    public boolean receive(String message) {
        boolean success = false;
        // TODO 待修改
        return success;
    }
    
    
    /**
     * 解析接收到的报文类型
     *
     * @param message 接收到的报文
     */
    protected static int parseMessageType(String message) {
        // TODO 待修改
        return AbstractSocket.REQUIRE_TO_NEW_GAME;
    }
    
    
    /**
     * 处理接收到的报文
     *
     * @param message 接收到的报文
     */
    protected void handleMessage(String message) {
        int type = parseMessageType(message);
        switch (type) {
            case NEW_GAME:                 // server 向双方 client 发送新建游戏命令
                handleNewGame(message);
                break;
            case REQUIRE_TO_NEW_GAME:      // client 请求新建游戏，server 直接转发对方 client。
                handleRequireToNewGame(message);
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
            case REQUIRE_TO_PUT_STONE:     // client 请求落子，server 进行处理，若可以落子则向双方 client 发送落子命令。
                handleRequireToPutStone(message);
                break;
            case RETRACT_STONE:            // server 向双方 client 发送悔棋命令
                handleRetractStone(message);
                break;
            case REQUIRE_TO_RETRACT_STONE: // client 请求悔棋，server 直接转发对方 client。
                handleRequireToRetractStone(message);
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
            case SET_PLAYER_COLOR:
                handleSetPlayerColor(message);
                break;
        }
    }
    
    
    /**
     * server 向双方 client 发送新建游戏命令
     * server 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    protected abstract void handleNewGame(String message);
    
    /**
     * client 请求新建游戏，server 直接转发对方 client。
     * client 弹出窗口，让用户选择是否开始。
     * server 直接转发对方 client
     *
     * @param message 接收到的报文
     */
    protected abstract void handleRequireToNewGame(String message);
    
    /**
     * client 同意新建游戏，server 新建游戏，并向双方 client 发送新建游戏命令。
     *
     * @param message 接收到的报文
     */
    protected abstract void handleAcceptToNewGame(String message);
    
    /**
     * client 拒绝新建游戏，server 直接转发对方 client。
     *
     * @param message 接收到的报文
     */
    protected abstract void handleRejectToNewGame(String message);
    
    /**
     * server 向双方 client 发送游戏结束命令
     * server 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    protected abstract void handleGameOver(String message);
    
    /**
     * client 认输，server 结束游戏，server 接收后向双方 client 发送游戏结束命令。
     * client 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    protected abstract void handleAdmitDefeat(String message);
    
    /**
     * server 向双方 client 发送落子命令
     * server 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    protected abstract void handlePutStone(String message);
    
    /**
     * client 请求落子，server 进行处理，若可以落子则向双方 client 发送落子命令。
     * client 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    protected abstract void handleRequireToPutStone(String message);
    
    /**
     * server 向双方 client 发送悔棋命令
     * server 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    protected abstract void handleRetractStone(String message);
    
    /**
     * client 请求悔棋，server 直接转发对方 client。
     * client 弹出窗口，让用户选择是否同意。
     * server 直接转发对方 client
     *
     * @param message 报文
     */
    protected abstract void handleRequireToRetractStone(String message);
    
    /**
     * client 同意悔棋，server 悔棋，并向双方 client 发送悔棋命令。
     * client 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    protected abstract void handleAcceptToRetractStone(String message);
    
    /**
     * client 拒绝悔棋，server 直接转发对方 client。
     *
     * @param message 接收到的报文
     */
    protected abstract void handleRejectToRetractStone(String message);
    
    /**
     * client 选择执子颜色
     * client 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    protected abstract void handleChoosePlayerColor(String message);
    
    /**
     * server 指定玩家执子颜色
     * server 不可能接收到这个消息
     *
     * @param message 接收到的报文
     */
    protected abstract void handleSetPlayerColor(String message);
}




