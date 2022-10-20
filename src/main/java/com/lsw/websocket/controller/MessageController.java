package com.lsw.websocket.controller;

import com.lsw.websocket.endpoint.WebSocket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class MessageController {

    @Resource
    private WebSocket webSocket;



    @PostMapping("sendMsg")
    public String sendMsg(String msg) {
        webSocket.broadcastMSg(msg);
        return "success";
    }
    @GetMapping("onlineCount")
    public Integer onlineCount() {
        int count= webSocket.getOnlineCount();
        return count;
    }
}
