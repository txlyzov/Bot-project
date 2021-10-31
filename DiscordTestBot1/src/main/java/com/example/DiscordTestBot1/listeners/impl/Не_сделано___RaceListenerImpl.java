package com.example.DiscordTestBot1.listeners.impl;

import com.example.DiscordTestBot1.listeners.Не_сделано___RaceListener;
import com.example.DiscordTestBot1.services.MessagingServises;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Не_сделано___RaceListenerImpl implements Не_сделано___RaceListener {

    private static boolean active = false;

    @Autowired
    private MessagingServises messagingServises;
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if(messageCreateEvent.getMessageContent().equals("!race")){
            if(!active){
                active = true;
                messagingServises.sendMessage(messageCreateEvent.getMessageAuthor(),
                        "Race begins!",
                        "Be the first to **react** to this message to win!",
                        null,
                        "https://cdn.discordapp.com/emojis/662925465610092554.gif",
                        messageCreateEvent.getChannel());
            }
        }

    }
}
