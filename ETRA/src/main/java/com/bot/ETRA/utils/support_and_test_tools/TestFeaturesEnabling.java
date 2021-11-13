package com.bot.ETRA.utils.support_and_test_tools;

import com.bot.ETRA.discord.DiscordApiValue;
import com.bot.ETRA.utils.support_and_test_tools.database_changing.DatabaseChanging;
import com.bot.ETRA.utils.support_and_test_tools.test_listener.TestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestFeaturesEnabling {

    @Autowired
    private DiscordApiValue discordApiValue;
    @Autowired
    private TestListener testListener;

    @Autowired
    private DatabaseChanging databaseChanging;

    public void testFeaturesEnabling(){
        //discordApiValue.getApi().addMessageCreateListener(testListener);
        //databaseChanging.changeDatabase();
    }
}
