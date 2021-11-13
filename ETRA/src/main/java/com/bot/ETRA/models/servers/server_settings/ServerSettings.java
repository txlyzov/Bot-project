package com.bot.ETRA.models.servers.server_settings;

import com.bot.ETRA.models.servers.server_settings.settings_blocks.EveryoneCommandsPermissions;
import com.bot.ETRA.models.servers.server_settings.settings_blocks.PostsSettings;
import lombok.Getter;
import lombok.Setter;



public class ServerSettings {

    @Getter @Setter
    private PostsSettings postsSettings;
    @Getter @Setter
    private EveryoneCommandsPermissions everyoneCommandsPermissions;

    public ServerSettings() {
        this.postsSettings = new PostsSettings();
        this.everyoneCommandsPermissions = new EveryoneCommandsPermissions();
    }

}
