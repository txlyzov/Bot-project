package com.bot.ETRA.discord;

import com.bot.ETRA.discord.listeners.tracking_listeners.add_tracking.AddTrackingListener;
import com.bot.ETRA.discord.listeners.server_listeners.reset_settings.ResetServerSettingsListener;
import com.bot.ETRA.discord.listeners.tracking_listeners.delete_tracking.DeleteTrackingListener;
import com.bot.ETRA.discord.listeners.server_listeners.set_posting_channel.SetPostingChannelListener;
import com.bot.ETRA.discord.listeners.help_listeners.ETRAHelpListener;
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


    /*public DiscordApiValue() {
        this.setApi(new DiscordApiBuilder().setToken(System.getenv("TOKEN"))
        //this.setApi(new DiscordApiBuilder().setToken(env.getProperty("TOKEN"))
                .setAllNonPrivilegedIntents()
                .login()
                .join());
        this.addFeatures();
    }*/

    public void addFeatures(){
        api.addMessageCreateListener(ETRAHelpListener);
        api.addMessageCreateListener(setPostingChannelListener);
        api.addMessageCreateListener(addTrackingListener);
        api.addMessageCreateListener(deleteTrackingListener);
        api.addMessageCreateListener(resetServerSettingsListener);
    }



}
