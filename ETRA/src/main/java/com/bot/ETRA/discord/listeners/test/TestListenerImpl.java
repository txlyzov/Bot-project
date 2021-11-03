package com.bot.ETRA.discord.listeners.test;

import com.bot.ETRA.models.DatabaseService;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TestListenerImpl implements TestListener {
    @Autowired
    DatabaseService databaseService;


    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        String messageContent = messageCreateEvent.getMessageContent();

        Matcher correctMatcher = Pattern.compile("^`dlt .*$").matcher(messageContent);
        while (correctMatcher.find()) {
//            for (int i=0;i<1000000;i++){
//                databaseService.addServerCommand(new ServerSettings( messageCreateEvent.getServer().get().getId(),"settingsType " + i,"settings "+ i));
//            }
            //databaseService.cleanServerSettingsDatabase();

            String findthis = messageContent.substring(correctMatcher.start(), correctMatcher.end()).substring(5);
//            List<Server> list = databaseService.getCommandType(findthis);
//            System.out.println(list);
            messageCreateEvent.getChannel().sendMessage("Deleted all");
        }
    }
}
