package com.bot.ETRA.utils.debugs;

import com.bot.ETRA.connections.websockets.zkb_websocket.ZKBWebSocket;
import com.bot.ETRA.connections.websockets.zkb_websocket.ZKBWebSocketsServices;

import java.time.LocalDateTime;

public interface ConsoleDebugs {
    //----------------------------------------------------------------------------------------------
    //Eva Application section
    //----------------------------------------------------------------------------------------------
    void evaApplicationConsoleDebug11();
    void evaApplicationConsoleDebug12();
    void evaApplicationConsoleDebugFinish11(LocalDateTime launchingTime, ZKBWebSocketsServices webSocketsServices);
    //----------------------------------------------------------------------------------------------
    //ZKB WebSocket services section
    //----------------------------------------------------------------------------------------------
    void ZKBWebSocketServicesLogsTimerService11(ZKBWebSocket webSocketSession);
    void ZKBWebSocketServicesLogsTimerService12(int seconds);
    void ZKBWebSocketServicesConnectionsService11();
    void ZKBWebSocketServicesConnection11(LocalDateTime connectionLostDate);
    void ZKBWebSocketServicesConnection12();
    void ZKBWebSocketServicesConnection13();
    void ZKBWebSocketServicesLogsTimerService21();
    void ZKBWebSocketServicesLogsTimerService22();
    //----------------------------------------------------------------------------------------------
    //ZKB WebSocket section
    //----------------------------------------------------------------------------------------------
    void ZKBWebSocketConsoleDebugHandleTextMessage11(String message);
    void ZKBWebSocketConsoleDebugHandleTextMessage21();
    void ZKBWebSocketConsoleDebugAfterConnectionEstablished11();
    void ZKBWebSocketConsoleDebugAfterConnectionClosed11();
    void ZKBWebSocketConsoleDebugDisconnect11();
}
