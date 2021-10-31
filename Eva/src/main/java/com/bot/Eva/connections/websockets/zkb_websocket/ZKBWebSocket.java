package com.bot.Eva.connections.websockets.zkb_websocket;


import com.bot.Eva.connections.websockets.zkb_websocket.parsers.ZKBWebsocketParser;
import com.bot.Eva.discord.DiscordApiValue;
import com.bot.Eva.models.DatabaseService;
import com.bot.Eva.models.active_commands.ActiveCommand;
import com.bot.Eva.models.servers.Server;
import com.bot.Eva.utils.debugs.ConsoleDebugs;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.javacord.api.entity.message.MessageBuilder;
import org.json.JSONArray;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ZKBWebSocket extends TextWebSocketHandler {

    //----------------------------------------------------------------------------------------------
    //Websocket body & main functions
    //----------------------------------------------------------------------------------------------

    private static final String URI_LINK_VALUE = "wss://zkillboard.com/websocket/";
    private static final boolean CAPSULES_REPORTS_AVAILABILITY = false;

    private ConsoleDebugs CD;// = new ConsoleDebugs();
    private DiscordApiValue discordApiValue;
    private ZKBWebsocketParser zkbWebsocketParser;
    private DatabaseService databaseService;


    @Getter
    private WebSocketSession webSocketSession;
    @Getter @Setter
    private int killsCounter;
    @Getter
    private LocalDateTime lastMessageTime;
    @Getter
    private LocalDateTime connectionLostDate;
    @Getter
    private String lastServerChannelMessage;
    @SneakyThrows
    public ZKBWebSocket(int killsCounter,ConsoleDebugs CD,DiscordApiValue discordApiValue, ZKBWebsocketParser zkbWebsocketParser, DatabaseService databaseService) {
        this.killsCounter=killsCounter;
        this.CD = CD;
        this.discordApiValue = discordApiValue;
        this.zkbWebsocketParser = zkbWebsocketParser;
        this.databaseService = databaseService;
        this.connectionLostDate = null;
        this.lastMessageTime = LocalDateTime.now();

        this.webSocketSession = new StandardWebSocketClient().doHandshake(this, new WebSocketHttpHeaders(), URI.create(URI_LINK_VALUE)).get();
        this.webSocketSession.sendMessage(new TextMessage("{\"action\":\"sub\",\"channel\":\"killstream\"}"));
        this.webSocketSession.sendMessage(new TextMessage("{\"action\":\"sub\",\"channel\":\"public\"}"));

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        CD.ZKBWebSocketConsoleDebugHandleTextMessage21();
        lastMessageTime = LocalDateTime.now();
        String messageContent = message.getPayload();

        Matcher serverMatcher = Pattern.compile("^\\{\"action\":").matcher(messageContent);
        while (serverMatcher.find()){
            ifServerMatcherFound(messageContent);
        }

        Matcher killstreamMatcher = Pattern.compile("^\\{\"attackers\":").matcher(messageContent);
        while (killstreamMatcher.find()){
            ifKillstreamMatcherFound(messageContent);
        }
    }

    @SneakyThrows
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        super.afterConnectionEstablished(session);
        CD.ZKBWebSocketConsoleDebugAfterConnectionEstablished11();
    }

    @Override
    @SneakyThrows
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        super.afterConnectionClosed(session, status);
        CD.ZKBWebSocketConsoleDebugAfterConnectionClosed11();
        connectionLostDate = LocalDateTime.now();
    }

    @SneakyThrows
    public void disconnect() {
        if (webSocketSession != null) {
           CD.ZKBWebSocketConsoleDebugDisconnect11();
            webSocketSession.close();
        }
    }



    //----------------------------------------------------------------------------------------------
    //Separated functions (for nice body code view)
    //----------------------------------------------------------------------------------------------



    private void ifServerMatcherFound(String messageContent){
        if(lastServerChannelMessage!=null){
            if((zkbWebsocketParser.serverServerStatusParser(messageContent).equals("OFFLINE"))
                    &&(zkbWebsocketParser.serverServerStatusParser(lastServerChannelMessage).equals("ONLINE"))){
                for (Server server : databaseService.getAllServers()) {
                    new MessageBuilder().setContent("SERVER OFFLINE").send(discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));
                }
            } else
            if((zkbWebsocketParser.serverServerStatusParser(messageContent).equals("ONLINE"))
                    &&(zkbWebsocketParser.serverServerStatusParser(lastServerChannelMessage).equals("OFFLINE"))){
                for (Server server : databaseService.getAllServers()) {
                    new MessageBuilder().setContent("SERVER ONLINE").send(discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));
                }
            } /*else
                    for (Server server : databaseService.getAllServers()) {
                        new MessageBuilder().setContent("test alert").send(discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));
                    }*/
        }
        lastServerChannelMessage = messageContent;
    }


    private void ifKillstreamMatcherFound(String messageContent){
        killsCounter++;
        CD.ZKBWebSocketConsoleDebugHandleTextMessage11(messageContent);

        //capsule reports availability
        if (!CAPSULES_REPORTS_AVAILABILITY){
            //victim shipTypeId parser
            if (zkbWebsocketParser.killmailCharacterShipTypeIdParser(
                    //victim character parser
                    zkbWebsocketParser.killmailVictimObjectParser(messageContent).toString())
                    //compare capsule id and victim player's ship id
                    .equals("670")) {
                return;
            }
        }


        ArrayList<String> commandsIds = new ArrayList<>();
        JSONArray attackersArray = zkbWebsocketParser.killmailAttackersArrayParser(messageContent);

        //for every active command in database check
        for (ActiveCommand activeCommand : databaseService.getAllActiveCommands()) {

            //check every attacker character
            //for(int characterIndex : attackersArray.length()){
            for(Object characterIndex : attackersArray){

                //command's tracking type equals
                if(activeCommand.getCommandType().equals("Tracking character")){
                    //command's tracking id equals
                    if(activeCommand.getNumericalValue().equals(
                            //attacker characterId parser
                            zkbWebsocketParser.killmailCharacterIdParser(
                                    //parameter
                                    characterIndex.toString()))){
                        //if Id's equal => add to list of matching commands
                        commandsIds.add(activeCommand.getId());
                    }
                }

                //command's tracking type equals
                if(activeCommand.getCommandType().equals("Tracking corporation")){
                    //command's tracking id equals
                    if(activeCommand.getNumericalValue().equals(
                            //attacker corporationId parser
                            zkbWebsocketParser.killmailCharacterCorporationIdParser(
                                    //parameter
                                    characterIndex.toString()))){
                        //if Id equal and not already in list
                        if((commandsIds.size()==0)||(!commandsIds.get(commandsIds.size()-1).equals(activeCommand.getId()))){
                            //add to list of matching commands
                            commandsIds.add(activeCommand.getId());
                        }

                    }
                }

                //command's tracking type equals
                if(activeCommand.getCommandType().equals("Tracking alliance")){
                    //command's tracking id equals
                    if(activeCommand.getNumericalValue().equals(
                            //attacker allianceId parser
                            zkbWebsocketParser.killmailCharacterAllianceIdParser(
                                    //parameter
                                    characterIndex.toString()))){
                        //if Id equal and not already in list
                         if((commandsIds.size()==0)||(!commandsIds.get(commandsIds.size()-1).equals(activeCommand.getId()))){
                            //add to list of matching commands
                            commandsIds.add(activeCommand.getId());
                        }

                    }
                }

            }



            //command's tracking type equals
            if(activeCommand.getCommandType().equals("Tracking character")){
                //command's tracking id equals
                if(activeCommand.getNumericalValue().equals(
                        //victim characterId parser
                        zkbWebsocketParser.killmailCharacterIdParser(
                                //victim parser
                                zkbWebsocketParser.killmailVictimObjectParser(
                                //parameter
                                messageContent).toString()))){
                    //if Id equal and not already in list
                     if((commandsIds.size()==0)||(!commandsIds.get(commandsIds.size()-1).equals(activeCommand.getId()))){
                        //add to list of matching commands
                        commandsIds.add(activeCommand.getId());
                    }

                }
            }

            //command's tracking type equals
            if(activeCommand.getCommandType().equals("Tracking corporation")){
                //command's tracking id equals
                if(activeCommand.getNumericalValue().equals(
                        //victim corporationId parser
                        zkbWebsocketParser.killmailCharacterCorporationIdParser(
                                //victim parser
                                zkbWebsocketParser.killmailVictimObjectParser(
                                        //parameter
                                        messageContent).toString()))){
                    //if Id equal and not already in list
                     if((commandsIds.size()==0)||(!commandsIds.get(commandsIds.size()-1).equals(activeCommand.getId()))){
                        //add to list of matching commands
                        commandsIds.add(activeCommand.getId());
                    }

                }
            }

            //command's tracking type equals
            if(activeCommand.getCommandType().equals("Tracking alliance")){
                //command's tracking id equals
                if(activeCommand.getNumericalValue().equals(
                        //victim allianceId parser
                        zkbWebsocketParser.killmailCharacterAllianceIdParser(
                                //victim parser
                                zkbWebsocketParser.killmailVictimObjectParser(
                                        //parameter
                                        messageContent).toString()))){
                    //if Id equal and not already in list
                     if((commandsIds.size()==0)||(!commandsIds.get(commandsIds.size()-1).equals(activeCommand.getId()))){
                        //add to list of matching commands
                        commandsIds.add(activeCommand.getId());
                    }

                }
            }

            //command's tracking type equals
            if(activeCommand.getCommandType().equals("Tracking solar system")){
                //command's tracking id equals
                if(activeCommand.getNumericalValue().equals(
                        //battle solarSystemId parser
                        zkbWebsocketParser.killmailSolarSystemIdParser(
                                //parameter
                                messageContent))){
                    //if Id equal and not already in list
                     if((commandsIds.size()==0)||(!commandsIds.get(commandsIds.size()-1).equals(activeCommand.getId()))){
                        //add to list of matching commands
                        commandsIds.add(activeCommand.getId());
                    }

                }
            }



        }

        //if any command found do it,else just skip
        if (commandsIds.size()!=0){
            //check all servers for follows
            for (Server server : databaseService.getAllServers()) {
                //get server's commands list
                skipAndGoForNextServer: for (String command : server.getActiveCommandIdsList()){
                    //check all Ids in the list
                    for (String commandId : commandsIds){
                        //post link if Id equal
                        if (command.equals(commandId)){
                            new MessageBuilder().setContent("https://zkillboard.com/kill/" +
                                    zkbWebsocketParser.killmailKillmailIdParser(messageContent) + "/").send(
                                    discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));
                            break skipAndGoForNextServer;
                        }
                    }
                }
            }
        }


    }

}
