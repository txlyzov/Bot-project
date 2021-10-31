package com.example.MongoDBTest.persistence;

import com.example.MongoDBTest.model.EVEServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EVEServerInfoRepository extends MongoRepository<EVEServerInfo,String> {
}
