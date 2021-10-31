package com.bot.Eva.models.servers;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerRepository extends MongoRepository<Server, String> {

    Server findByServerId(Long serverId);

    //List<Server> findByServerId(Long serverId);
//    List<Server> findByCommandType(String commandType);

}
