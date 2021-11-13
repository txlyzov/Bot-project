package com.bot.ETRA.models;

import com.bot.ETRA.models.active_commands.ActiveCommand;
import com.bot.ETRA.models.active_commands.ActiveCommandRepository;
import com.bot.ETRA.models.servers.Server;
import com.bot.ETRA.models.servers.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {

    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private ActiveCommandRepository activeCommandRepository;



    //Server Functions
    public void saveServer(Server server){serverRepository.save(server);}
    public void deleteServer(Server server){serverRepository.delete(server);}

    public List<Server> getAllServers(){return serverRepository.findAll();}
    public Server findByServerId(Long id){ return serverRepository.findByServerId(id);}




    //ActiveCommands Functions
    public void saveActiveCommand(ActiveCommand activeCommand){activeCommandRepository.save(activeCommand);}
    public void deleteActiveCommand(ActiveCommand activeCommand){activeCommandRepository.delete(activeCommand);}

    public List<ActiveCommand> getAllActiveCommands(){return activeCommandRepository.findAll();}
    public ActiveCommand findActiveCommandById(String id){return activeCommandRepository.findById(id).orElseThrow();} //web controller and reset command use it
    public ActiveCommand findActiveCommandByNumericalValue(String value){return activeCommandRepository.findByNumericalValue(value);}
    public ActiveCommand findActiveCommandByLiteralValue(String value){return activeCommandRepository.findByLiteralValue(value);}

}
