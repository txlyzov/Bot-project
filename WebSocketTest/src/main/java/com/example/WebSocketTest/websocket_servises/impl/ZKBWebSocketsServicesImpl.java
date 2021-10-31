package com.example.WebSocketTest.websocket_servises.impl;

import com.example.WebSocketTest.utils.ConsoleLogPattern;
import com.example.WebSocketTest.websocket_servises.ZKBWebSocketsServices;
import com.example.WebSocketTest.websocket_servises.websockets.ZKBWebSocket;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class ZKBWebSocketsServicesImpl implements ZKBWebSocketsServices {

    private static final int WS_CONNECTIONS_CHECK_CYCLE_TIME = 100; // = ~0,1 sec = 100 millis
    private static final int WS_CONNECTION_RETRY_CYCLE_TIME = 29950; // = ~29,95 sec = 29950 millis
    private static final int OPTIMIZED_LOGS_TIMER_RESTART_CYCLE_TIME = 3599950; // = ~60 min = 3599900 millis
    private static final int OPTIMIZED_LOGS_TIMER_PERIOD_CYCLE_TIME = 14950; // = 14.95 sec = 14950 millis
    private static final boolean DEBUG_MESSAGES_INTO_CONSOLE = false;

    @Getter //3
    private ZKBWebSocket webSocketSession;
    @Getter
    private int totalKills; //2
    @Getter
    private int sessionReconnects; //3

    private LocalDateTime connectionLostDate;
    
    private final ConsoleLogPattern clu;


    public ZKBWebSocketsServicesImpl(ConsoleLogPattern clu){
        this.clu=clu;
        this.totalKills=0; //2
        this.sessionReconnects=0;
    }

    @SneakyThrows
    public void startWebSocketConnection(){
        //connectionExceptions();

        //LocalDateTime connectionLostDate = null;
        if(webSocketSession!=null){
            connectionLostDate = webSocketSession.getConnectionLostDate();
        }
        try {
            this.webSocketSession =  new ZKBWebSocket(clu);
            switch (sessionReconnects) {
                case 0 -> {
                }
                default -> clu.printWithBottomDelimiter("Yey,fixed!^^ New connection established." +
                        "\nBut we lost messages between " +
                        clu.getStringDate(connectionLostDate) + " and " + clu.getStringDate(LocalDateTime.now()) + "..");
            }
            sessionReconnects++;
        } catch (Exception e){
            e.printStackTrace();
            switch (sessionReconnects) {
                case 0 -> {
                    clu.printString("Well..we didn't get ZKB WebSocket connection. Reason shown above^^" +
                            "\nWe need to wait a little and try again..Stupid connections!");
                    Thread.sleep(WS_CONNECTION_RETRY_CYCLE_TIME / 6);
                }
                default -> {
                    clu.printString("Well..it doesn't solved yet. Reason shown above^^" +
                            "\nWe need to wait a little and try again..Stupid connections!");
                    Thread.sleep(WS_CONNECTION_RETRY_CYCLE_TIME);
                }
            }
        }
    }

//    @SneakyThrows
//    public void connectionExceptions(){
//        LocalDateTime connectionLostDate = null;
//        if(webSocketSession!=null){
//            connectionLostDate = webSocketSession.getConnectionLostDate();
//        }
//        try {
//            this.webSocketSession =  new ZKBWebSocket(clu);
//            switch (sessionReconnects) {
//                case 0 -> {
//                    sessionReconnects++;
//                }
//                default -> clu.printWithBottomDelimiter("Yey,fixed!^^ New connection established." +
//                        "\nBut we lost messages between " +
//                        //clu.getStringDate(lastMessageTime) + " and " + clu.getStringDate(LocalDateTime.now()) + "..");
//                        clu.getStringDate(connectionLostDate) + " and " + clu.getStringDate(LocalDateTime.now()) + "..");
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            switch (sessionReconnects) {
//                case 0 -> {
//                    clu.printString("Well..we didn't get ZKB WebSocket connection. Reason shown above^^" +
//                            "\nWe need to wait a little and try again..Stupid connections!");
//                    Thread.sleep(WS_CONNECTION_RETRY_CYCLE_TIME / 6);
//                }
//                default -> {
//                    clu.printString("Well..it doesn't solved yet. Reason shown above^^" +
//                            "\nWe need to wait a little and try again..Stupid connections!");
//                    Thread.sleep(WS_CONNECTION_RETRY_CYCLE_TIME);
//                }
//            }
//        }
//    }

    public void closeWebSocketConnection(){
        this.webSocketSession.disconnect();
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
                            if(DEBUG_MESSAGES_INTO_CONSOLE){
                                clu.printString("In cycle -> WebSocketOptimizedLogsTimerTask");
                            }

                            while(true){
                                if (LocalDateTime.now().getSecond()%15 == 0){
                                    clu.printString("Server channel: " + webSocketSession.getLastServerChannelMessage());
                                    clu.printString("Total tracked kills: " + webSocketSession.getKillsCounter());
                                    clu.printString("Total kills:" + totalKills); //2
                                    //System.out.println(webSocketSession.getWebSocketSession().isOpen());
                                    break;
                                }
                            }

                            if(DEBUG_MESSAGES_INTO_CONSOLE){
                                clu.printString("Out cycle -> WebSocketOptimizedLogsTimerTask");
                            }
                        }
                    }, 0, OPTIMIZED_LOGS_TIMER_PERIOD_CYCLE_TIME);

                    Thread.sleep(OPTIMIZED_LOGS_TIMER_RESTART_CYCLE_TIME);

                    clu.printWithDelimiters("Timer service time ^^ " +
                                            "Last timer did his " + OPTIMIZED_LOGS_TIMER_RESTART_CYCLE_TIME/60000 + " min cycle successfully,yey :)\n" +
                                            "So we will delete it,HAHA! And make a new one ^^.");

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
                        clu.printWithUpperDelimiter("ZKB WebSocket is probably dead again...fe.\n" +
                                                "Trying to restart WebSocket session.");
                        totalKills = totalKills + webSocketSession.getKillsCounter(); //2
                        closeWebSocketConnection();
                        startWebSocketConnection();
                    }
                }
            }
        });
        SocketConnectionsServiceThread.start();
    }

}
