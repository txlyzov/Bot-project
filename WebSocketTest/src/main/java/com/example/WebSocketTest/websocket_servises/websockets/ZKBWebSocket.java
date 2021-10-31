package com.example.WebSocketTest.websocket_servises.websockets;


import com.example.WebSocketTest.utils.ConsoleLogPattern;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.time.LocalDateTime;

//@Component
public class ZKBWebSocket extends TextWebSocketHandler {

    private static final boolean DEBUG_MESSAGES_INTO_CONSOLE = false;
    private static final String URI_LINK_VALUE = "wss://zkillboard.com/websocket/"; //5
    private final ConsoleLogPattern clu;

    @Getter
    private WebSocketSession webSocketSession;
    @Getter
    private int killsCounter;
    @Getter
    private LocalDateTime lastMessageTime;
    @Getter
    private LocalDateTime connectionLostDate;
    @Getter
    private String lastServerChannelMessage;

    @SneakyThrows
    public ZKBWebSocket(ConsoleLogPattern clu) {
        connectionLostDate = null;
        this.clu=clu;
        lastMessageTime = LocalDateTime.now();
        var webSocketClient = new StandardWebSocketClient();
        //this.webSocketSession = webSocketClient.doHandshake(this, new WebSocketHttpHeaders(), URI.create("wss://demo.piesocket.com/v3/channel_1?api_key=oCdCMcMPQpbvNjUIzqtvF1d2X2okWpDQj4AwARJuAgtjhzKxVEjQU6IdCjwm&notify_self")).get();
        webSocketSession = webSocketClient.doHandshake(this, new WebSocketHttpHeaders(), URI.create(URI_LINK_VALUE)).get();
        webSocketSession.sendMessage(new TextMessage("{\"action\":\"sub\",\"channel\":\"killstream\"}"));
        webSocketSession.sendMessage(new TextMessage("{\"action\":\"sub\",\"channel\":\"public\"}"));
        killsCounter=0;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        if(DEBUG_MESSAGES_INTO_CONSOLE){
            clu.printString("New message.");
        }
        lastMessageTime = LocalDateTime.now();
        if (message.getPayload().contains("{\"action")){
            lastServerChannelMessage = message.getPayload();
        } else if (message.getPayload().contains("{\"attackers")){
            killsCounter++;
           clu.printString("Killstream channel: " + message.getPayload());
        }
    }

    @SneakyThrows
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        super.afterConnectionEstablished(session);

        if(DEBUG_MESSAGES_INTO_CONSOLE){
            clu.printString("Connection established(ZKB).");
        }

    }

    @Override
    @SneakyThrows
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        super.afterConnectionClosed(session, status);

        if(DEBUG_MESSAGES_INTO_CONSOLE){
            clu.printString("Connection closed(ZKB).");
        }

        connectionLostDate = LocalDateTime.now();
    }




    @SneakyThrows
    public void disconnect() {
        if (webSocketSession != null) {
            if(DEBUG_MESSAGES_INTO_CONSOLE){
                clu.printString("Closing connection(ZKB)...");
            }
            webSocketSession.close();
        }
    }

}
