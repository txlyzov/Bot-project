package com.bot.ETRA.connections.websockets.zkb_websocket;


import com.bot.ETRA.connections.api.esi_evetech_api.EsiEvetechApi;
import com.bot.ETRA.connections.api.zkb_api.ZKBApi;
import com.bot.ETRA.connections.websockets.zkb_websocket.parsers.ZKBWebsocketParser;
import com.bot.ETRA.discord.DiscordApiValue;
import com.bot.ETRA.discord.servises.MessagingServises;
import com.bot.ETRA.models.DatabaseService;
import com.bot.ETRA.utils.debugs.ConsoleDebugs;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class ZKBWebSocketsServices {

    private static final int WS_CONNECTIONS_CHECK_CYCLE_TIME = 100; // = ~0,1 sec = 100 millis
    private static final int WS_CONNECTION_RETRY_CYCLE_TIME = 29950; // = ~29,95 sec = 29950 millis
    private static final int OPTIMIZED_LOGS_TIMER_RESTART_CYCLE_TIME = 3599950; // = ~60 min = 3599900 millis
    private static final int OPTIMIZED_LOGS_TIMER_PERIOD_CYCLE_TIME = 14950; // = 14.95 sec = 14950 millis
    private static final String CONSOLE_DEBUGS_FILE = "consoleDebugs1Impl";

    @Getter
    private ZKBWebSocket webSocketSession;
    @Getter
    private int killsCounter;
    @Getter
    private int sessionReconnects;
    @Getter
    private LocalDateTime connectionLostDate;


    @Autowired
    @Qualifier(CONSOLE_DEBUGS_FILE)
    private ConsoleDebugs CD;
    /*@Autowired
    private ConsoleDebugs1Impl CD;*/
    @Autowired
    DiscordApiValue discordApiValue;
    @Autowired
    private ZKBWebsocketParser zkbWebsocketParser;
    @Autowired
    private EsiEvetechApi esiEvetechApi;
    @Autowired
    private ZKBApi zkbApi;
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private MessagingServises messagingServises;





    public ZKBWebSocketsServices(){
        this.killsCounter =0; //2
        this.sessionReconnects=0;
    }





    public void logsTimerService(){
        Thread logsTimerServiceThread = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true){
                    Timer webSocketOptimizedLogsTimer = new Timer();
                    webSocketOptimizedLogsTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            CD.ZKBWebSocketServicesLogsTimerService21();

                            while(true){
                                if (LocalDateTime.now().getSecond()%15 == 0){
                                    CD.ZKBWebSocketServicesLogsTimerService11(webSocketSession);
                                    break;
                                }
                            }

                            CD.ZKBWebSocketServicesLogsTimerService22();
                        }
                    }, 0, OPTIMIZED_LOGS_TIMER_PERIOD_CYCLE_TIME);

                    Thread.sleep(OPTIMIZED_LOGS_TIMER_RESTART_CYCLE_TIME);

                    CD.ZKBWebSocketServicesLogsTimerService12(OPTIMIZED_LOGS_TIMER_RESTART_CYCLE_TIME);

                    webSocketOptimizedLogsTimer.cancel();
                    webSocketOptimizedLogsTimer.purge();
                }

            }
        });
        logsTimerServiceThread.start();
    }





    public void webSocketConnectionsService(){
        Thread SocketConnectionsServiceThread = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true){
                    Thread.sleep(WS_CONNECTIONS_CHECK_CYCLE_TIME);
                    if (!webSocketSession.getWebSocketSession().isOpen()){
                        CD.ZKBWebSocketServicesConnectionsService11();
                        closeWebSocketConnection();
                        startWebSocketConnection();
                    }
                }
            }
        });
        SocketConnectionsServiceThread.start();
    }





    @SneakyThrows
    public void startWebSocketConnection(){
        if(webSocketSession!=null){
            connectionLostDate = webSocketSession.getConnectionLostDate();
            killsCounter = webSocketSession.getKillsCounter();
        }
        try {
            this.webSocketSession =  new ZKBWebSocket(killsCounter,CD,discordApiValue,zkbWebsocketParser,esiEvetechApi,zkbApi,databaseService,messagingServises);
            switch (sessionReconnects) {
                case 0 -> {
                }
                default -> CD.ZKBWebSocketServicesConnection11(connectionLostDate);
            }
            sessionReconnects++;
        } catch (Exception e){
            e.printStackTrace();
            switch (sessionReconnects) {
                case 0 -> {
                    CD.ZKBWebSocketServicesConnection12();
                    Thread.sleep(WS_CONNECTION_RETRY_CYCLE_TIME / 6);
                }
                default -> {
                    CD.ZKBWebSocketServicesConnection13();
                    Thread.sleep(WS_CONNECTION_RETRY_CYCLE_TIME);
                }
            }
        }
    }





    public void closeWebSocketConnection(){
        this.webSocketSession.disconnect();
    }

}
