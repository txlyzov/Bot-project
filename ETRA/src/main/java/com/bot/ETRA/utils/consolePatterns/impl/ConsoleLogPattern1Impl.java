package com.bot.ETRA.utils.consolePatterns.impl;

import com.bot.ETRA.utils.consolePatterns.ConsoleLogPattern;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ConsoleLogPattern1Impl implements ConsoleLogPattern {

    public ConsoleLogPattern1Impl(){}

    private static final String CONSOLE_DATE_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss"; // 2021/09/20 13:37:00
    private static final String CONSOLE_DELIMITER = "-------------------------" + // 5x25
                                                    "-------------------------" +
                                                    "-------------------------" +
                                                    "-------------------------" +
                                                    "-------------------------";

    @Override
    public void printDelimiter(){
        System.out.println(ConsoleLogPattern1Impl.CONSOLE_DELIMITER);
    }

    @Override
    public String getStringDate(LocalDateTime date){
        return date.format(DateTimeFormatter.ofPattern(CONSOLE_DATE_FORMAT_PATTERN));
    }
    @Override
    public String getStringDateWithBrackets (LocalDateTime date){
        return "[" + date.format(DateTimeFormatter.ofPattern(CONSOLE_DATE_FORMAT_PATTERN)) + "]";
    }

    @Override
    public void printWithUpperDelimiter(String content){
        printDelimiter();
        printString(content);
    }

    @Override
    public void printWithDelimiters(String content){
        printDelimiter();
        printString(content);
        printDelimiter();
    }

    @Override
    public void printWithBottomDelimiter(String content){
        printString(content);
        printDelimiter();
    }

    @Override
    public void printString(String content){
        System.out.println(getStringDateWithBrackets(LocalDateTime.now())
                           + " " + content);
    }

    @Override
    public void printZKBWebSocketResults(LocalDateTime launchingTime, int sessionReconnects, int totalKills){
        printDelimiter();
        printString("Ooookay, stopping it! Program is out. Little results report:" +
                    "\nLaunching date: " + getStringDate(launchingTime) + " " +
                    "\nTotal session reconnects: " + sessionReconnects +
                    "\nTotal kills: " +
                    totalKills +
                    "\nHoping this code was useful to you. Bye bye ^^/");
        printDelimiter();
    }
}
