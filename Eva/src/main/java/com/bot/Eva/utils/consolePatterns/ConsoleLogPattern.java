package com.bot.Eva.utils.consolePatterns;

import java.time.LocalDateTime;

public interface ConsoleLogPattern {
    void printDelimiter();
    String getStringDate(LocalDateTime date);
    String getStringDateWithBrackets (LocalDateTime date);
    void printString(String content);
    void printWithUpperDelimiter(String content);
    void printWithDelimiters(String content);
    void printWithBottomDelimiter(String content);
    void printZKBWebSocketResults(LocalDateTime launchingTime, int sessionReconnects, int totalKills);
}
