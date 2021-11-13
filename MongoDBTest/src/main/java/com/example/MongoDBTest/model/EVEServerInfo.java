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
    @Getter @Setter
    private Class2 class2;

    public EVEServerInfo(String tqStatus,String tqOnline,String tqKillsLastHour){
        //this.id = id;
        this.tqStatus = tqStatus;
        this.tqOnline = tqOnline;
        this.tqKillsLastHour = tqKillsLastHour;
        this.class2 = new Class2(false,true,3);
    }

}

/*
@Document(value = "EveServerInfo")
public record EVEServerInfo(@Id String id,String tqStatus,String tqOnline,String tqKillsLastHour,Class2 new Class2(false,true,3)){
    public EVEServerInfo(String tqStatus, String tqOnline,String tqKillsLastHour){
        this(id,tqStatus,tqOnline,tqKillsLastHour,);
    }

}*/
