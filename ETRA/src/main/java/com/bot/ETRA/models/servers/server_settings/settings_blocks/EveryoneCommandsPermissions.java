package com.bot.ETRA.models.servers.server_settings.settings_blocks;

import lombok.Getter;
import lombok.Setter;

public class EveryoneCommandsPermissions {
    @Getter @Setter
    private boolean addTrackingCommandPermission;
    @Getter @Setter
    private boolean deleteTrackingCommandPermission;
    @Getter @Setter
    private boolean resetServerSettingsPermission;

    public EveryoneCommandsPermissions(){
        this.addTrackingCommandPermission = true;
        this.deleteTrackingCommandPermission = false;
        this.resetServerSettingsPermission = false;
    }
}
