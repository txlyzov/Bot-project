package com.example.MongoDBTest;

import com.example.MongoDBTest.controller.testController;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MongoDbTestApplication {

	@Autowired
	private testController testController;

	public static void main(String[] args) {
		SpringApplication.run(MongoDbTestApplication.class, args);
	}

	@Bean
	public void app(){
		val listOfServerInfo = testController.getListOfServerInfo();
		for (int i = 0;i<listOfServerInfo.size();i++){
			System.out.println((i+1) + ") " + listOfServerInfo.get(i).getTqStatus() + " " + listOfServerInfo.get(i).getTqOnline() + " " + listOfServerInfo.get(i).getTqKillsLastHour() );
			System.out.println(listOfServerInfo.get(i).getClass2().v1());
		}
		//System.out.println(testController.getListOfServerInfo());
	}

}
