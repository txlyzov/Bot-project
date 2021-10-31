package com.bot.Eva.models.active_commands;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(value = "ActiveCommands")
public class ActiveCommand {
    private static final String DATE_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss"; // 2021/09/20 13:37:00
    @Getter @Setter @Id
    private String id;
    @Getter @Setter
    private String commandType;
    @Getter @Setter
    private String numericalValue;
    @Getter @Setter
    private String literalValue;
    @Getter @Setter
    private String registrationTime;


    public ActiveCommand(String commandType, String numericalValue, String literalValue) {
        this.commandType = commandType;
        this.numericalValue = numericalValue;
        this.literalValue = literalValue;
        this.registrationTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
    }
}
