package com.example.WebSocketTest.utils;

import java.time.LocalDateTime;

public interface ConsoleLogPattern {
    void printDelimiter();
    String getStringDate(LocalDateTime date);
    String getStringDateWithBrackets (LocalDateTime date);
    void printString(String content);
    void printWithUpperDelimiter(String content);
    void printWithDelimiters(String content);
    void printWithBottomDelimiter(String content);
    void printResults(LocalDateTime launchingTime, int sessionReconnects, int totalKills);
}
