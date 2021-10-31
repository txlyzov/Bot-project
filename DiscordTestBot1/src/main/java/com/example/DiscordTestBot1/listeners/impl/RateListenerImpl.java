package com.example.DiscordTestBot1.listeners.impl;

import com.example.DiscordTestBot1.listeners.RateListener;
import com.example.DiscordTestBot1.services.MessagingServises;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RateListenerImpl implements RateListener {
    private final static Pattern pattern = Pattern.compile("!rate (\\w+)");

    @Autowired
    private MessagingServises messagingServises;

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if(messageCreateEvent.getMessageContent().startsWith("!rate")){
            Matcher matcher = pattern.matcher(messageCreateEvent.getMessageContent());
            if (matcher.matches()){
                //all fine
                int rating = (int) Math.floor(Math.random() * 100) + 1;
                messagingServises.sendMessage(messageCreateEvent.getMessageAuthor(),"Rate calculator",
                        messageCreateEvent.getMessageAuthor().getDisplayName() + " is " + rating + "% " + matcher.group(1),
                        "Rate again?","https://images-ext-2.discordapp.net/external/_Q2Wa7ZKs6ksAnxmemXY2xjoZxeGEGwSWt0tCGJl2os/https/cdn.discordapp.com/emojis/662925465610092554.gif",
                        messageCreateEvent.getChannel());
            }
            else {
                messagingServises.sendMessage(messageCreateEvent.getMessageAuthor(),"Rate calculator",
                        "Is it \"!rate\" command? Check it out: \"!rate [word]\".",
                        "Rate again?",null,
                        messageCreateEvent.getChannel());

            }
        }
    }
}
