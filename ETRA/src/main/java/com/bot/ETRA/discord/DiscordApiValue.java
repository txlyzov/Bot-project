package com.bot.ETRA.discord;

import com.bot.ETRA.discord.listeners.add_tracking.AddTrackingListener;
import com.bot.ETRA.discord.listeners.delete_settings.ResetServerSettingsListener;
import com.bot.ETRA.discord.listeners.delete_tracking.DeleteTrackingListener;
import com.bot.ETRA.discord.listeners.set_posting_channel.SetPostingChannelListener;
import com.bot.ETRA.discord.listeners.test.TestListener;
import com.bot.ETRA.discord.listeners.eva_help.ETRAHelpListener;
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
    private ETRAHelpListener ETRAHelpListener;
    @Autowired
    private SetPostingChannelListener setPostingChannelListener;
    @Autowired
    private AddTrackingListener addTrackingListener;
    @Autowired
    private DeleteTrackingListener deleteTrackingListener;
    @Autowired
    private ResetServerSettingsListener resetServerSettingsListener;


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
        api.addMessageCreateListener(ETRAHelpListener);
        api.addMessageCreateListener(setPostingChannelListener);
        api.addMessageCreateListener(addTrackingListener);
        api.addMessageCreateListener(deleteTrackingListener);
        api.addMessageCreateListener(resetServerSettingsListener);
    }



}
