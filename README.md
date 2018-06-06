# GomokuWithChatProject
抱团求生小组の鸿篇巨制
## Socket 部分添加了一部分代码 可以作为参考

## 五子棋部分
作者：潘学海
改成了联机版本（还未完全完成），添加了注释。

## 聊天部分
需要一人完成聊天系统的图形界面设计，具体化下述类：

```Java
package ChattingPanel;

import javax.swing.*;

public abstract class AbstractChatting extends JPanel {
    /**
     * 在对话框中显示当前用户发送的的消息
     * @param message 消息内容
     */
    abstract void sendMessage(String message);

    /**
     * 在对话框中显示对手发送的消息
     * @param message 消息内容
     */
    abstract void receiveMessage(String message);

    /**
     * 初始化：设定聊天框的位置
     * @param x 横坐标
     * @param y 纵坐标
     * @param width 宽度
     * @param height 长度
     */
    AbstractChatting(int x, int y, int width, int height) {
        super();
        super.setBounds(x, y, width, height);
    }
}
```

## 计时器部分
需要一人完成计时器的图形界面设计，具体化下述类：

```Java
package TimerPanel;


import javax.swing.*;

/**
 * 一个计时器的图形元件，这个计时器按照HH:MM:SS的格式显示两个玩家对战的时长
 * 时间的具体值在Server处维护并由ServerSocket进行更新
 */
public abstract class AbstractTimer extends JPanel {
    /**
     * 执行该函数后，界面更新，显示一个时间
     * @param hour 时
     * @param min 分
     * @param sec 秒
     */
    abstract void showTime(int hour,int min, int sec);

    /**
     * 初始化：设定计时框的位置
     * @param x 横坐标
     * @param y 纵坐标
     * @param width 宽度
     * @param height 长度
     */
    AbstractTimer(int x, int y, int width, int height){
        super();
        super.setBounds(x, y, width, height);
    }
}
```

## Socket服务端
写了 `class AbstractSocket` 和 `class Server` 的框架

需要完成报文收发以及报文处理 `handleXXX` 函数里面的 `TODO`
