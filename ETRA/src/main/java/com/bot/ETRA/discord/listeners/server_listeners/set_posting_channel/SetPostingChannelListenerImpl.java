package com.bot.ETRA.discord.listeners.server_listeners.set_posting_channel;

import com.bot.ETRA.models.DatabaseService;
import com.bot.ETRA.models.servers.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SetPostingChannelListenerImpl implements SetPostingChannelListener{
    @Autowired
    private DatabaseService databaseService;



    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if(messageCreateEvent.getMessageContent().equals("`sp")){
            if(!messageCreateEvent.getMessageAuthor().isServerAdmin()){
                messageCreateEvent.getChannel().sendMessage("**(!)** This command able only for \\`Server Admins\\`." +
                        "\n*Talk with them if you need that.*");
            } else {
                long serverId = messageCreateEvent.getServer().get().getId();
                long channelId = messageCreateEvent.getChannel().getId();
                Server server = databaseService.findByServerId(serverId);
                if (server != null) {
                    server.setChannelId(channelId);
                    databaseService.saveServer(server);
                } else {
                    databaseService.saveServer(new Server(serverId,channelId,new ArrayList<>()));
                }
                messageCreateEvent.getChannel().sendMessage("Oookay,now all posts will be sent here. (￢ ￢)");
            }
        }
    }
}
