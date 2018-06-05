package Gomoku;

public class Server extends AbstractSocket {
    private boolean isWaitingForResponse; // client 发送的信息是否需要对方 client 回应
    private Board board; // 棋盘
    private Client client1; // client1，如果在构造函数里面就可以初始化的话可以设置为 final 属性。
    private Client client2; // client2，如果在构造函数里面就可以初始化的话可以设置为 final 属性。
    
    
    public Server(/* args */) {
        super();
        board = new Board();
        isWaitingForResponse = false;
    }
}
