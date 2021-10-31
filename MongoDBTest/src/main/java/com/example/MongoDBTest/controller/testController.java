package com.example.MongoDBTest.controller;


import com.example.MongoDBTest.model.EVEServerInfo;
import com.example.MongoDBTest.service.EVEServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class testController {
    @Autowired
    private EVEServerInfoService eveServerInfoService;

    public List<EVEServerInfo> getListOfServerInfo(){ return eveServerInfoService.findAll(); }
}
