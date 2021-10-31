package com.bot.Eva.discord;

import com.bot.Eva.discord.listeners.add_tracking.AddTrackingListener;
import com.bot.Eva.discord.listeners.set_posting_channel.SetPostingChannelListener;
import com.bot.Eva.discord.listeners.test.TestListener;
import com.bot.Eva.discord.listeners.eva_help.EvaHelpListener;
import lombok.Getter;
import lombok.Setter;
import org.javacord.api.DiscordApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscordApiValue {
    @Getter @Setter
    private DiscordApi api;

   /* @Autowired
    private Environment env;*/

    @Autowired
    private EvaHelpListener evaHelpListener;
    @Autowired
    private SetPostingChannelListener setPostingChannelListener;
    @Autowired
    private AddTrackingListener addTrackingListener;


    @Autowired
    private TestListener testListener;

    /*public DiscordApiValue() {
        this.setApi(new DiscordApiBuilder().setToken(System.getenv("TOKEN"))
        //this.setApi(new DiscordApiBuilder().setToken(env.getProperty("TOKEN"))
                .setAllNonPrivilegedIntents()
                .login()
                .join());
        this.addFeatures();
    }*/

    public void addFeatures(){
        api.addMessageCreateListener(testListener);
        api.addMessageCreateListener(evaHelpListener);
        api.addMessageCreateListener(setPostingChannelListener);
        api.addMessageCreateListener(addTrackingListener);
    }



}
