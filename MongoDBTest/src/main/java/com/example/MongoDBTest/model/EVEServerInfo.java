package com.example.MongoDBTest.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "EveServerInfo")
public class EVEServerInfo {
    @Getter @Setter @Id
    private String id;
    @Getter @Setter
    private String tqStatus;
    @Getter @Setter
    private String tqOnline;
    @Getter @Setter
    private String tqKillsLastHour;

    public EVEServerInfo(String tqStatus,String tqOnline,String tqKillsLastHour){
        //this.id = id;
        this.tqStatus = tqStatus;
        this.tqOnline = tqOnline;
        this.tqKillsLastHour = tqKillsLastHour;
    }

}
