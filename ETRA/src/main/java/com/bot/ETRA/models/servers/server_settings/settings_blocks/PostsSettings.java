package com.bot.ETRA.models.servers.server_settings.settings_blocks;

import lombok.Getter;
import lombok.Setter;

public class PostsSettings {
    @Getter @Setter
    private String postsType;
    @Getter @Setter
    private int postsColorPattern;
    @Getter @Setter
    private boolean isShouldBePosted;

    public PostsSettings() {
        this.postsType = "rebuilt";
        this.postsColorPattern = 1;
        this.isShouldBePosted = true;
    }
}
