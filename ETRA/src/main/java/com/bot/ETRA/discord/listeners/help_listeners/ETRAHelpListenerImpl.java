package com.bot.ETRA.discord.listeners.help_listeners;

import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ETRAHelpListenerImpl implements ETRAHelpListener {
    @Value("${server.port}")
    private String port;

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if(messageCreateEvent.getMessageContent().equals("`help")){
            messageCreateEvent.getChannel().sendMessage("http://localhost:" + port);
        }
    }
}
