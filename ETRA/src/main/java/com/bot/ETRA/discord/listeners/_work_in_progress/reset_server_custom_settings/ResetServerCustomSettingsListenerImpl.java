package com.bot.ETRA.discord.listeners._work_in_progress.reset_server_custom_settings;

import com.bot.ETRA.models.DatabaseService;
import com.bot.ETRA.models.servers.Server;
import com.bot.ETRA.models.servers.server_settings.ServerSettings;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResetServerCustomSettingsListenerImpl implements ResetServerCustomSettingsListener{
    @Autowired
    private DatabaseService databaseService;

    //----------------------------------------------------------------------------------------------
    //Main function
    //----------------------------------------------------------------------------------------------

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if(messageCreateEvent.getMessageContent().equals("`RSCS")){
            if(!messageCreateEvent.getMessageAuthor().isServerAdmin()){
                messageCreateEvent.getChannel().sendMessage("**(!)** This command able only for \\`Server Admins\\`." +
                        "\n*Talk with them if you need that.*");
            } else {
                List<Server> servers = databaseService.getAllServers();
                for (Server server: servers){
                    server.setServerSettings(new ServerSettings());
                    databaseService.saveServer(server);
                }
                messageCreateEvent.getChannel().sendMessage("RSCS done");
            }
        }
    }
}
