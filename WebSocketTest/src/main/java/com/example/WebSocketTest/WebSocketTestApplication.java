package com.example.WebSocketTest;

import com.example.WebSocketTest.utils.ConsoleLogPattern;
import com.example.WebSocketTest.utils.impl.ConsoleLogPattern1Impl;
import com.example.WebSocketTest.websocket_servises.ZKBWebSocketsServices;
import com.example.WebSocketTest.websocket_servises.impl.ZKBWebSocketsServicesImpl;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication //logging.pattern.console=
public class WebSocketTestApplication{

	private static final LocalDateTime LAUNCHING_TIME = LocalDateTime.now();
	@Autowired
	@Qualifier("consoleLogPattern1Impl")
	private ConsoleLogPattern CLP;// = new ConsoleLogPattern1Impl();
	@Autowired
	@Qualifier("ZKBWebSocketsServicesImpl")
	private  ZKBWebSocketsServices  webSocketsServices;// = new ZKBWebSocketsServicesImpl(CLP);

//	@SneakyThrows
//	@ConfigurationProperties("classpath:com.example.WebSocketTest")
//	public static void main(String[] args){
//
//		SpringApplication.run(WebSocketTestApplication.class, args);
//		CLP.printDelimiter();
//		CLP.printString("Hey! App is starting,wait a sec ^^\n" +
//						"Its needs to establish connections with services for its full functionality.\n" +
//						"Connecting to ZKB..");
//
//		//ZKBWebSocketsServicesImpl webSocketsServices = new ZKBWebSocketsServicesImpl(CLP);
//		webSocketsServices.startWebSocketConnection();
//
//		CLP.printString("Someday probably this string will contains \"Connecting to EVEMarket\",but not now.\n" +
//						"Meh..ambitious and lazy programmers..");
//
//
//		CLP.printString("Application launched successfully ^^" +
//				"\nAll needed connections are established. Have fun!");
//		CLP.printDelimiter();
//
//
//		while (true){
//			if (LocalDateTime.now().getSecond()%15 == 0) {
//				break;
//			}
//		}
//
//		webSocketsServices.webSocketConnectionsService();
//		webSocketsServices.logsTimerService();
//
//		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//			public void run() {
//				//webSocketsServices.closeWebSocketConnection();
//				CLP.printResults(LAUNCHING_TIME,webSocketsServices.getSessionReconnects(),webSocketsServices.getTotalKills()+webSocketsServices.getWebSocketSession().getKillsCounter());
//			}
//		}));
//	}

	public static void main(String[] args) {
		SpringApplication.run(WebSocketTestApplication.class, args);
	}
	@Bean
	@SneakyThrows
	//ConfigurationProperties("classpath:com.example.WebSocketTest")
	public void app1(){
		CLP.printDelimiter();
		CLP.printString("Hey! App is starting,wait a sec ^^\n" +
				"Its needs to establish connections with services for its full functionality.\n" +
				"Connecting to ZKB..");

		//ZKBWebSocketsServicesImpl webSocketsServices = new ZKBWebSocketsServicesImpl(CLP);
		webSocketsServices.startWebSocketConnection();

		CLP.printString("Someday probably this string will contains \"Connecting to EVEMarket\",but not now.\n" +
				"Meh..ambitious and lazy programmers..");


		CLP.printString("Application launched successfully ^^" +
				"\nAll needed connections are established. Have fun!");
		CLP.printDelimiter();


		while (true){
			if (LocalDateTime.now().getSecond()%15 == 0) {
				break;
			}
		}

		webSocketsServices.webSocketConnectionsService();
		webSocketsServices.logsTimerService();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				//webSocketsServices.closeWebSocketConnection();
				CLP.printResults(LAUNCHING_TIME,webSocketsServices.getSessionReconnects(),webSocketsServices.getTotalKills()+webSocketsServices.getWebSocketSession().getKillsCounter());
			}
		}));
	}



}
