package com.bot.ETRA.utils.debugs.impl;

import com.bot.ETRA.connections.websockets.zkb_websocket.ZKBWebSocket;
import com.bot.ETRA.connections.websockets.zkb_websocket.ZKBWebSocketsServices;
import com.bot.ETRA.utils.console_patterns.ConsoleLogPattern;
import com.bot.ETRA.utils.debugs.ConsoleDebugs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConsoleDebugs1Impl implements ConsoleDebugs {
    private static final boolean ALL_DEBUG_MESSAGES_INTO_CONSOLE = true;
    private static final boolean ALL_ADDITIONAL_DEBUG_MESSAGES_INTO_CONSOLE = false;
    
    private static final boolean EVAAPPLICATION_DEBUG_MESSAGES_INTO_CONSOLE = false;
    private static final boolean ADDITIONAL_EVAAPPLICATION_DEBUG_MESSAGES_INTO_CONSOLE = false;

    private static final boolean ZKBWEBSOCKET_DEBUG_MESSAGES_INTO_CONSOLE = false;
    private static final boolean ADDITIONAL_ZKBWEBSOCKET_DEBUG_MESSAGES_INTO_CONSOLE = false;

    private static final boolean ZKBWEBSOCKET_SERVICES_DEBUG_MESSAGES_INTO_CONSOLE = false;
    private static final boolean ADDITIONAL_ZKBWEBSOCKET_SERVICES_DEBUG_MESSAGES_INTO_CONSOLE = false;

    @Autowired
    @Qualifier("consoleLogPattern1Impl")
    private ConsoleLogPattern CLP;

    //----------------------------------------------------------------------------------------------
    //Eva Application section
    //----------------------------------------------------------------------------------------------



    public void evaApplicationConsoleDebug11(){
        if(EVAAPPLICATION_DEBUG_MESSAGES_INTO_CONSOLE||
                ALL_DEBUG_MESSAGES_INTO_CONSOLE){
            CLP.printDelimiter();
            CLP.printString("Hey! App is starting.\n" +
                    "Needs to establish connections with services for its full functionality.\n" +
                    "Connecting to ZKB..");
        }
    }

    public void evaApplicationConsoleDebug12(){
        if(EVAAPPLICATION_DEBUG_MESSAGES_INTO_CONSOLE||
                ALL_DEBUG_MESSAGES_INTO_CONSOLE){
            CLP.printString("Application launched successfully." +
                    "\nAll needed connections are established. Have fun!");
            CLP.printDelimiter();
        }
    }

    public void evaApplicationConsoleDebugFinish11(LocalDateTime launchingTime, ZKBWebSocketsServices webSocketsServices){
        if(EVAAPPLICATION_DEBUG_MESSAGES_INTO_CONSOLE||
                ALL_DEBUG_MESSAGES_INTO_CONSOLE){
            CLP.printZKBWebSocketResults(launchingTime,webSocketsServices.getSessionReconnects(),webSocketsServices.getWebSocketSession().getKillsCounter());
        }
    }



    //----------------------------------------------------------------------------------------------
    //ZKB WebSocket services section
    //----------------------------------------------------------------------------------------------



    public void ZKBWebSocketServicesLogsTimerService11(ZKBWebSocket webSocketSession){
        if(ZKBWEBSOCKET_SERVICES_DEBUG_MESSAGES_INTO_CONSOLE||
                ALL_DEBUG_MESSAGES_INTO_CONSOLE) {
            CLP.printString("Server channel: " + webSocketSession.getLastServerChannelMessage());
            CLP.printString("Total tracked kills: " + webSocketSession.getKillsCounter());
        }
    }

    public void ZKBWebSocketServicesLogsTimerService12(int seconds){
        if(ZKBWEBSOCKET_SERVICES_DEBUG_MESSAGES_INTO_CONSOLE||
                ALL_DEBUG_MESSAGES_INTO_CONSOLE) {
            CLP.printWithDelimiters("Timer service time. " +
                    "Last timer did his " + seconds/60000 + " min cycle successfully.\n");
        }
    }

    public void ZKBWebSocketServicesConnectionsService11(){
        if(ZKBWEBSOCKET_SERVICES_DEBUG_MESSAGES_INTO_CONSOLE||
                ALL_DEBUG_MESSAGES_INTO_CONSOLE) {
            CLP.printWithUpperDelimiter("ZKB WebSocket is probably dead again.\n" +
                    "Trying to restart WebSocket session.");
        }
    }

    public void ZKBWebSocketServicesConnection11(LocalDateTime connectionLostDate){
        if(ZKBWEBSOCKET_SERVICES_DEBUG_MESSAGES_INTO_CONSOLE||
                ALL_DEBUG_MESSAGES_INTO_CONSOLE) {
            CLP.printWithBottomDelimiter("Fixed! New connection established." +
                    "\nBut we lost messages between " +
                    CLP.getStringDate(connectionLostDate) + " and " + CLP.getStringDate(LocalDateTime.now()) + ".");
        }
    }

    public void ZKBWebSocketServicesConnection12(){
        if(ZKBWEBSOCKET_SERVICES_DEBUG_MESSAGES_INTO_CONSOLE||
                ALL_DEBUG_MESSAGES_INTO_CONSOLE) {
            CLP.printString("We didn't get ZKB WebSocket connection already." +
                    "\nWe need to wait a little and try again.");
        }
    }

    public void ZKBWebSocketServicesConnection13(){
        if(ZKBWEBSOCKET_SERVICES_DEBUG_MESSAGES_INTO_CONSOLE||
                ALL_DEBUG_MESSAGES_INTO_CONSOLE) {
            CLP.printString("Problem doesn't solved yet." +
                    "\nWe need to wait a little and try again.");
        }
    }

    public void ZKBWebSocketServicesLogsTimerService21(){
        if(ADDITIONAL_ZKBWEBSOCKET_SERVICES_DEBUG_MESSAGES_INTO_CONSOLE
                ||(ALL_DEBUG_MESSAGES_INTO_CONSOLE
                &&ALL_ADDITIONAL_DEBUG_MESSAGES_INTO_CONSOLE)) {
            CLP.printString("In cycle -> WebSocketOptimizedLogsTimerTask");
        }
    }

    public void ZKBWebSocketServicesLogsTimerService22(){
        if(ADDITIONAL_ZKBWEBSOCKET_SERVICES_DEBUG_MESSAGES_INTO_CONSOLE
                ||(ALL_DEBUG_MESSAGES_INTO_CONSOLE
                &&ALL_ADDITIONAL_DEBUG_MESSAGES_INTO_CONSOLE)) {
            CLP.printString("Out cycle -> WebSocketOptimizedLogsTimerTask");
        }
    }



    //----------------------------------------------------------------------------------------------
    //ZKB WebSocket section
    //----------------------------------------------------------------------------------------------



    public void ZKBWebSocketConsoleDebugHandleTextMessage11(String message){
        if(ZKBWEBSOCKET_DEBUG_MESSAGES_INTO_CONSOLE||
                ALL_DEBUG_MESSAGES_INTO_CONSOLE) {
            CLP.printString("Killstream channel: " + message);
        }
    }

    public void ZKBWebSocketConsoleDebugHandleTextMessage21(){
        if(ADDITIONAL_ZKBWEBSOCKET_DEBUG_MESSAGES_INTO_CONSOLE
                ||(ALL_DEBUG_MESSAGES_INTO_CONSOLE
                &&ALL_ADDITIONAL_DEBUG_MESSAGES_INTO_CONSOLE)){
            CLP.printString("New message.");
        }
    }

    public void ZKBWebSocketConsoleDebugAfterConnectionEstablished11(){
        if(ADDITIONAL_ZKBWEBSOCKET_DEBUG_MESSAGES_INTO_CONSOLE
                ||(ALL_DEBUG_MESSAGES_INTO_CONSOLE
                &&ALL_ADDITIONAL_DEBUG_MESSAGES_INTO_CONSOLE)){
            CLP.printString("Connection established(ZKB).");
        }
    }

    public void ZKBWebSocketConsoleDebugAfterConnectionClosed11(){
        if(ADDITIONAL_ZKBWEBSOCKET_DEBUG_MESSAGES_INTO_CONSOLE
                ||(ALL_DEBUG_MESSAGES_INTO_CONSOLE
                &&ALL_ADDITIONAL_DEBUG_MESSAGES_INTO_CONSOLE)){
            CLP.printString("Connection closed(ZKB).");
        }
    }

    public void ZKBWebSocketConsoleDebugDisconnect11(){
        if(ADDITIONAL_ZKBWEBSOCKET_DEBUG_MESSAGES_INTO_CONSOLE
                ||(ALL_DEBUG_MESSAGES_INTO_CONSOLE
                &&ALL_ADDITIONAL_DEBUG_MESSAGES_INTO_CONSOLE)){
            CLP.printString("Closing connection(ZKB)...");
        }
    }
}
