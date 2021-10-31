package com.example.DiscordTestBot1.websockets;

import lombok.Getter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.concurrent.ExecutionException;

public class WebSocketTestClass extends TextWebSocketHandler {
    @Getter
    private WebSocketSession clientSession;

    public WebSocketTestClass() throws ExecutionException, InterruptedException {
        var webSocketClient = new StandardWebSocketClient();
       // this.clientSession = webSocketClient.doHandshake(this, new WebSocketHttpHeaders(), URI.create("wss://demo.piesocket.com/v3/channel_1?api_key=oCdCMcMPQpbvNjUIzqtvF1d2X2okWpDQj4AwARJuAgtjhzKxVEjQU6IdCjwm&notify_self")).get();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println(message.getPayload());
    }

}
