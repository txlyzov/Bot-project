package com.bot.ETRA.utils.support_and_test_tools.test_listener;

import com.bot.ETRA.discord.servises.MessagingServises;
import com.bot.ETRA.models.DatabaseService;
import com.bot.ETRA.utils.support_and_test_tools.test_listener.TestListener;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TestListenerImpl implements TestListener {
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private MessagingServises messagingServises;

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        String messageContent = messageCreateEvent.getMessageContent();

        Matcher correctMatcher = Pattern.compile("^`qwe$").matcher(messageContent);
        while (correctMatcher.find()) {
            User user = messageCreateEvent.getMessageAuthor().asUser().orElseThrow();
            //new MessageBuilder().setContent("test").send(user);
        }
    }
}
