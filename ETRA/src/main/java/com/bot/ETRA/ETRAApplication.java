package com.bot.ETRA;



import com.bot.ETRA.discord.DiscordApiValue;
import com.bot.ETRA.connections.websockets.zkb_websocket.ZKBWebSocketsServices;
import com.bot.ETRA.utils.support_and_test_tools.TestFeaturesEnabling;
import com.bot.ETRA.utils.debugs.ConsoleDebugs;
import lombok.SneakyThrows;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;

@SpringBootApplication
public class ETRAApplication {

	@Autowired
	private TestFeaturesEnabling testFeaturesEnabling;
	@Autowired
	private DiscordApiValue discordApiValue;
	@Autowired
	private Environment env;



	private static final LocalDateTime LAUNCHING_TIME = LocalDateTime.now();
	private static final boolean DEBUG_MESSAGES_INTO_CONSOLE = true;
	private static final boolean NICE_TIME_OUTPUT = false;
	@Autowired
	private ConsoleDebugs CD;
	@Autowired
	private ZKBWebSocketsServices zkbWebSocketsServices;

	public static void main(String[] args) {
		SpringApplication.run(ETRAApplication.class, args);
	}

	@Bean
	@SneakyThrows
	@ConfigurationProperties(value = "discord-api")
	public DiscordApi discordApi() {

		//----------------------------------------------------------------------------------------------
		//Discord settings section
		//----------------------------------------------------------------------------------------------



		discordApiValue.setApi(new DiscordApiBuilder().setToken(env.getProperty("TOKEN"))
				.setAllNonPrivilegedIntents()
				.login()
				.join());
		discordApiValue.addFeatures();
		discordApiValue.addWIPFeatures();



		//----------------------------------------------------------------------------------------------
		//WebSocket settings section
		//----------------------------------------------------------------------------------------------



		/*if(NICE_TIME_OUTPUT){
			while (true){
				if (LocalDateTime.now().getSecond()%15 == 0) {
					break;
				}
			}
		}*/

		CD.evaApplicationConsoleDebug11();
		zkbWebSocketsServices.startWebSocketConnection();
		CD.evaApplicationConsoleDebug12();
		zkbWebSocketsServices.webSocketConnectionsService();


		zkbWebSocketsServices.logsTimerService();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				CD.evaApplicationConsoleDebugFinish11(LAUNCHING_TIME, zkbWebSocketsServices);
			}
		}));



		//----------------------------------------------------------------------------------------------
		//Test functions and changing connection
		//----------------------------------------------------------------------------------------------



		testFeaturesEnabling.testFeaturesEnabling();



		return discordApiValue.getApi();
	}


}
