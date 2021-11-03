package com.bot.ETRA.models.servers;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends MongoRepository<Server, String> {

    Server findByServerId(Long serverId);

    //List<Server> findByServerId(Long serverId);
//    List<Server> findByCommandType(String commandType);

}
