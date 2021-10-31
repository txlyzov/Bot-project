package com.example.WebSocketTest.trashbox;

import com.example.WebSocketTest.websocket_servises.websockets.ZKBWebSocket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WebSocketOptimizedLogsTimerTask extends java.util.TimerTask {

    private static final boolean DEBUG_MESSAGES_INTO_CONSOLE = false;

    private ZKBWebSocket webSocketSession;

    public WebSocketOptimizedLogsTimerTask(ZKBWebSocket webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    @Override
    public void run() {
        if(DEBUG_MESSAGES_INTO_CONSOLE){
            System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")) + "] In cycle -> WebSocketOptimizedLogsTimerTask ");
        }
        while(true){
            if (LocalDateTime.now().getSecond()%15 == 0){
                System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")) +
                        "] Server channel: " + webSocketSession.getLastServerChannelMessage());
                System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")) +
                        "] Total tracked kills: " + webSocketSession.getKillsCounter());
                break;
            }
        }
        if(DEBUG_MESSAGES_INTO_CONSOLE){
            System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")) + "] Out cycle -> WebSocketOptimizedLogsTimerTask ");
        }
    }

}
