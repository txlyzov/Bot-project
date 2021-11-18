package com.bot.ETRA.discord.listeners._work_in_progress.permissions_listeners.add_tracking_permission;

import com.bot.ETRA.models.DatabaseService;
import com.bot.ETRA.models.servers.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AddTrackingPermissionListenerImpl implements AddTrackingPermissionListener{
    @Autowired
    private DatabaseService databaseService;

    //----------------------------------------------------------------------------------------------
    //Main function
    //----------------------------------------------------------------------------------------------

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        String messageContent = messageCreateEvent.getMessageContent();
        Matcher correctMatcher = Pattern.compile("^`AddTrackingPermissionListener ((true)|(false))$").matcher(messageContent);
        while (correctMatcher.find()) {
            if(!messageCreateEvent.getMessageAuthor().isServerAdmin()){
                messageCreateEvent.getChannel().sendMessage("**(!)** This command able only for \\`Server Admins\\`." +
                        "\n*Talk with them if you need that.*");
            } else {
                String messageValue = messageContent.substring(correctMatcher.start(), correctMatcher.end())
                        .substring(31);
                long serverId = messageCreateEvent.getServer().get().getId();
                Server server = databaseService.findByServerId(serverId);
                server.getServerSettings().getEveryoneCommandsPermissions().setAddTrackingCommandPermission(Boolean.parseBoolean(messageValue));
                databaseService.saveServer(server);
                messageCreateEvent.getChannel().sendMessage("AddTrackingPermissionListener " + messageValue);
            }

        }
    }
}
