package com.example.MongoDBTest.service;

import com.example.MongoDBTest.model.EVEServerInfo;
import com.example.MongoDBTest.persistence.EVEServerInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class EVEServerInfoService {
    @Autowired
    private EVEServerInfoRepository eveServerInfoRepository;

    private static List<EVEServerInfo> records = new ArrayList<>();
    static {
        records.add(new EVEServerInfo("OFFLINE", "2,443","98"));
        records.add(new EVEServerInfo("ONLINE", "7,747","103"));
        records.add(new EVEServerInfo("ONLINE", "11,593","574"));
        records.add(new EVEServerInfo("OFFLINE", "21,443","864"));
        records.add(new EVEServerInfo("ONLINE", "27,924","1,038"));
    }

 /*   @PostConstruct
    public void init(){
        eveServerInfoRepository.deleteAll();
        eveServerInfoRepository.saveAll(records);
    }
*/
    public List<EVEServerInfo> findAll(){
        return eveServerInfoRepository.findAll();
    }
}
