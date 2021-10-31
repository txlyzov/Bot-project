package com.bot.Eva.discord.listeners.eva_help;

import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Component;

@Component
public class EvaHelpListenerImpl implements EvaHelpListener {
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if(messageCreateEvent.getMessageContent().equals("`help")){
            messageCreateEvent.getChannel().sendMessage("http://localhost:404" +
                    "\nHelp command description will be soon.");
        }
    }
}
