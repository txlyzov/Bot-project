package com.example.DiscordTestBot1;

import com.example.DiscordTestBot1.listeners.PingListener;
import com.example.DiscordTestBot1.listeners.RateListener;
import com.example.DiscordTestBot1.listeners.WebSocketMessageListener;
import com.example.DiscordTestBot1.websockets.WebSocketTestClass;
import lombok.SneakyThrows;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;


import org.springframework.web.socket.TextMessage;

@SpringBootApplication
public class DiscordTestBot1Application {

	@Autowired
	private Environment env;

	@Autowired
	private PingListener pingListener;

	@Autowired
	private RateListener rateListener;

//	@Autowired
//	private WebSocketMessageListener webSocketMessageListener;

//	@Autowired
//	private val sampleClient;

	public static void main(String[] args) {
		SpringApplication.run(DiscordTestBot1Application.class, args);
	}

	@Bean
	@SneakyThrows
	@ConfigurationProperties(value = "discord-api")
	public DiscordApi discordApi() {
		String token = env.getProperty("TOKEN");
		//val sampleClient =  new WebSocketTestClass();

		DiscordApi api = new DiscordApiBuilder().setToken(token)
				.setAllNonPrivilegedIntents()
				.login()
				.join();
		api.addMessageCreateListener(pingListener);
		api.addMessageCreateListener(rateListener);
		//api.addMessageCreateListener(webSocketMessageListener);
		//sampleClient.getClientSession().sendMessage(new TextMessage("Discord Hello!"));
		return api;
	}

}
