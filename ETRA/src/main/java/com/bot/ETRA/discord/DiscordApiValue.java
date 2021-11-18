package com.bot.ETRA.discord;

import com.bot.ETRA.discord.listeners._work_in_progress.permissions_listeners.add_tracking_permission.AddTrackingPermissionListener;
import com.bot.ETRA.discord.listeners._work_in_progress.permissions_listeners.delete_tracking_permission.DeleteTrackingPermissionListener;
import com.bot.ETRA.discord.listeners._work_in_progress.permissions_listeners.reset_server_settings_permission.ResetServerSettingsPermissionListener;
import com.bot.ETRA.discord.listeners._work_in_progress.posts_settings_listeners.set_post_color.SetPostColorPatternListener;
import com.bot.ETRA.discord.listeners._work_in_progress.posts_settings_listeners.set_post_type.SetPostTypeListener;
import com.bot.ETRA.discord.listeners._work_in_progress.posts_settings_listeners.set_posts_enabling.SetPostsEnablingListener;
import com.bot.ETRA.discord.listeners._work_in_progress.reset_server_custom_settings.ResetServerCustomSettingsListener;
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

    //Features

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

    //WIP features
    @Autowired
    private AddTrackingPermissionListener addTrackingPermissionListener;
    @Autowired
    private DeleteTrackingPermissionListener deleteTrackingPermissionListener;
    @Autowired
    private ResetServerSettingsPermissionListener resetServerSettingsPermissionListener;
    @Autowired
    private SetPostColorPatternListener setPostColorPatternListener;
    @Autowired
    private SetPostTypeListener setPostTypeListener;
    @Autowired
    private SetPostsEnablingListener setPostsEnablingListener;
    @Autowired
    private ResetServerCustomSettingsListener resetServerCustomSettingsListener;


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

    public void addWIPFeatures(){
        api.addMessageCreateListener(addTrackingPermissionListener);
        api.addMessageCreateListener(deleteTrackingPermissionListener);
        api.addMessageCreateListener(resetServerSettingsPermissionListener);
        api.addMessageCreateListener(setPostColorPatternListener);
        api.addMessageCreateListener(setPostTypeListener);
        api.addMessageCreateListener(setPostsEnablingListener);
        api.addMessageCreateListener(resetServerCustomSettingsListener);
    }



}
