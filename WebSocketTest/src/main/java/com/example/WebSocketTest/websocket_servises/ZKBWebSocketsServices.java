package com.example.WebSocketTest.websocket_servises;


import com.example.WebSocketTest.websocket_servises.websockets.ZKBWebSocket;

public interface ZKBWebSocketsServices {
    ZKBWebSocket getWebSocketSession();
    int getTotalKills(); //2
    int getSessionReconnects(); //3
    void startWebSocketConnection();
    void closeWebSocketConnection();
    void webSocketConnectionsService();
    void logsTimerService();

}
