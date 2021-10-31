package com.example.DiscordTestBot1.listeners.impl;

import com.example.DiscordTestBot1.listeners.WebSocketMessageListener;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.web.socket.TextMessage;

public class WebSocketMessageListenerImpl implements WebSocketMessageListener {
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
       // sampleClient.getClientSession().sendMessage(new TextMessage("ewrerw!"));
    }
}
