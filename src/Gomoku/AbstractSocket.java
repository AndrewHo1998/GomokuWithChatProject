package Gomoku;

import java.net.Socket;

public abstract class AbstractSocket extends Socket {
    public static final int NEW_GAME = 0;                 // server 向双方 client 发送新建游戏命令
    public static final int REQUIRE_TO_NEW_GAME = 1;      // client 请求新建游戏，server 直接转发对方 client。
    public static final int ACCEPT_TO_NEW_GAME = 2;       // client 同意新建游戏，server 新建游戏，并向双方 client 发送新建游戏命令。
    public static final int REJECT_TO_NEW_GAME = 3;       // client 拒绝新建游戏，server 直接转发对方 client。
    public static final int GAME_OVER = 4;                // server 向双方 client 发送游戏结束命令
    public static final int ADMIT_DEFEAT = 5;             // client 认输，server 接收后向双方 client 发送游戏结束命令。
    public static final int PUT_STONE = 6;                // server 向 client 发送落子命令
    public static final int REQUIRE_TO_PUT_STONE = 7;     // client 请求落子，server 进行处理，若可以落子则向双方 client 发送落子命令。
    public static final int REQUIRE_TO_RETRACT_STONE = 8; // client 请求悔棋，server 直接转发对方 client。
    public static final int RETRACT_STONE = 9;            // server 向 client 发送悔棋命令
    public static final int ACCEPT_TO_RETRACT_STONE = 10; // client 同意悔棋，server 悔棋，并向双方 client 发送悔棋命令。
    public static final int REJECT_RETRACT_STONE = 11;    // client 拒绝悔棋，server 直接转发对方 client。
    
    
    public boolean send(String message) {
        boolean success = false;
        return success;
    }
    
    
    public boolean receive(String message) {
        boolean success = false;
        return success;
    }
    
    
    protected void handleMessage(String message) {
        int type = parseMessageType(message);
        Object[] args = parseMessageArgs(message);
    }
    
    
    /**
     * 解析接收到的请求类型
     *
     * @param message 接收到的请求
     */
    protected static int parseMessageType(String message) {
        return AbstractSocket.REQUIRE_TO_NEW_GAME;
    }
    
    
    /**
     * 解析接收到的请求包含的参数
     *
     * @param message 接收到的请求
     */
    protected static Object[] parseMessageArgs(String message) {
        return new Object[]{};
    }
}




