package Gomoku;

public class Client extends AbstractSocket {
    private final Gomoku gomoku;
    private Display display;
    private boolean gameStarted;
    private Server server;
    
    
    // TODO 初始化
    public Client(/* args */) {
        gomoku = new Gomoku(this);
        display = gomoku.getDisplay();
    }
    
    
    /* 向服务端请求新建游戏，服务端将请求转发到对方客户端。 */
    public boolean requireNewGame() {
        return false;
    }
    
    
    /* 向服务端请求新建游戏，服务端将请求转发到对方客户端。 */
    public boolean replyNewGame() {
        return false;
    }
    
    
    /**
     * 向 server 请求落子
     *
     * @param i 落子的横坐标
     * @param j 落子的横坐标
     */
    public void requirePutStone(int i, int j) {
        /**
         * message
         * @type AbstractSocket.REQUIRE_TO_NEW_GAME
         * @arg i
         * @arg j
         * */
        send(server, "");
    }
    
    
    /**
     * 在界面上显示落子结果
     *
     * @param stone         落子的 stone
     * @param previousStone 落子的 stone 的前一个 stone，若没有则传入 null。
     * @param historySize   落子完成后棋盘上的棋子数
     */
    private void putStone(Stone stone, Stone previousStone, int historySize) {
        display.putStone(stone, previousStone, historySize);
    }
    
    
    /**
     * 在界面上显示悔棋结果
     *
     * @param stone         被移走的 stone
     * @param previousStone 被移走的 stone 的前一个 stone，因为可以悔棋时棋盘上至少有 4 个棋子，必然是非 null。
     * @param historySize   悔棋完成后棋盘上的棋子数
     */
    private void retractStone(Stone stone, Stone previousStone, int historySize) {
        display.retractStone(stone, previousStone, historySize);
    }
}

