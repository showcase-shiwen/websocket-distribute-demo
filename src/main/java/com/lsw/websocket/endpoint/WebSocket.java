package com.lsw.websocket.endpoint;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocket {

    private String userId;

    // 用来存在线连接用户信息
    private static ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<String, Session>();

    private Session session;
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        try {
            this.session=session;
            sessionPool.put(userId, session);
            this.userId=userId;
            broadcastMSg("欢迎【"+userId+"】上线");
        } catch (Exception e) {
        }
    }

    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            sessionPool.remove(userId);
            System.out.println("【websocket消息】连接断开，总数为:" + sessionPool.size());
        } catch (Exception e) {
        }
    }

    /**
     *  FIXME 心跳暂不实现 客户端 起一个 setInterval
     *  window.setInterval(function(){
     *       socket.send("heartbeat")
     *  },1000)
     * 收到客户端消息后调用的方法
     *
     * @param message
     */
    @OnMessage
    public void onMessage( @PathParam(value = "userId") String userId,String message) throws IOException {

        // a b 不在同一节点时
        // 遍历 sessionPoos 转发其它节点
        System.out.println("【websocket消息】收到客户端消息:" + message);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String str=simpleDateFormat.format(new Date())+" "+userId+" :"+message;
//        session.getBasicRemote().sendText();

        broadcastMSg(null,str);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("用户错误,原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 广播消息
     * @param msg
     */
    public void broadcastMSg(String system,String msg) {
        sessionPool.values().forEach(session->{
            if(session.isOpen()){
                try {
                    if(system==null||system.equals("")){
                        session.getBasicRemote().sendText(msg);
                    }else{
                        session.getBasicRemote().sendText(system+"："+msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public int getOnlineCount() {
       return sessionPool.size();
    }

    public void broadcastMSg(String msg) {
        broadcastMSg("系统",msg);
    }
}
