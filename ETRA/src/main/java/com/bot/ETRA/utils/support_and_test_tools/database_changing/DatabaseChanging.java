package com.bot.ETRA.utils.support_and_test_tools.database_changing;

import com.bot.ETRA.models.DatabaseService;
import com.bot.ETRA.models.servers.Server;
import com.bot.ETRA.models.servers.server_settings.ServerSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseChanging {
    @Autowired
    private DatabaseService databaseService;

    public void changeDatabase(){
        List<Server> servers = databaseService.getAllServers();
        for (Server server: servers){
            server.setServerSettings(new ServerSettings());
            //databaseService.saveServer(server);
        }
    }
}
