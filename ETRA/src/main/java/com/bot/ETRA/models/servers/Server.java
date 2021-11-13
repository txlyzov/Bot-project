package com.bot.ETRA.models.servers;

import com.bot.ETRA.models.servers.server_settings.ServerSettings;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Document(value = "Servers")
public class Server {
    private static final String DATE_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss"; // 2021/09/20 13:37:00
    @Getter @Setter @Id
    private String id;
    @Getter @Setter
    private long serverId;
    @Getter @Setter
    private long channelId;
    @Getter @Setter
    private List<String> activeCommandIdsList;
    @Getter @Setter
    private String registrationTime;
    @Getter @Setter
    private ServerSettings serverSettings;


    public Server(long serverId, long channelId, List<String> activeCommandIdsList) {
        this.serverId = serverId;
        this.channelId = channelId;
        this.activeCommandIdsList = activeCommandIdsList;
        this.registrationTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
        this.serverSettings = new ServerSettings();
    }

    public void addActiveCommand(String commandId){
        this.activeCommandIdsList.add(commandId);
    }

    public void deleteActiveCommand(String commandId){
        this.activeCommandIdsList.remove(commandId);
    }
}
