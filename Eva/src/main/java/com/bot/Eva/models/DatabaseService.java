package com.bot.Eva.models;

import com.bot.Eva.models.active_commands.ActiveCommand;
import com.bot.Eva.models.active_commands.ActiveCommandRepository;
import com.bot.Eva.models.servers.Server;
import com.bot.Eva.models.servers.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DatabaseService {

    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private ActiveCommandRepository activeCommandRepository;



    //Server Functions
    public void addServer(Server server){serverRepository.save(server);}
    public void deleteServer(Server server){serverRepository.delete(server);}

    public List<Server> getAllServers(){return serverRepository.findAll();}
    public Server findByServerId(Long id){ return serverRepository.findByServerId(id);}




    //ActiveCommands Functions
    public void addActiveCommand(ActiveCommand activeCommand){activeCommandRepository.save(activeCommand);}
    public void deleteActiveCommand(ActiveCommand activeCommand){activeCommandRepository.delete(activeCommand);}

    public List<ActiveCommand> getAllActiveCommands(){return activeCommandRepository.findAll();}
    public Optional<ActiveCommand> findActiveCommandById(String id){return activeCommandRepository.findById(id);}
    public ActiveCommand findActiveCommandByNumericalValue(String value){return activeCommandRepository.findByNumericalValue(value);}
    public ActiveCommand findActiveCommandByLiteralValue(String value){return activeCommandRepository.findByLiteralValue(value);}

}
