package com.bot.ETRA.discord.listeners.help_listeners;

import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Component;

@Component
public class ETRAHelpListenerImpl implements ETRAHelpListener {
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if(messageCreateEvent.getMessageContent().equals("`help")){
            messageCreateEvent.getChannel().sendMessage("http://localhost:404");
        }
    }
}
