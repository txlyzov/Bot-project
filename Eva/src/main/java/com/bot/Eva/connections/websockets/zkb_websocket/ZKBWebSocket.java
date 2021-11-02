package com.bot.Eva.connections.websockets.zkb_websocket;


import com.bot.Eva.connections.api.esi_evetech_api.EsiEvetechApi;
import com.bot.Eva.connections.api.zkb_api.ZKBApi;
import com.bot.Eva.connections.websockets.zkb_websocket.parsers.ZKBWebsocketParser;
import com.bot.Eva.discord.DiscordApiValue;
import com.bot.Eva.discord.servises.MessagingServises;
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

import java.awt.*;
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

    private static final boolean ENABLING_DISCORD_POSTING = true;
    private static final boolean CAPSULES_REPORTS_AVAILABILITY = false;
    private static final String SHIP_IMG_STARTING_URI = "https://images.evetech.net/types/";
    private static final String SHIP_IMG_ENDING_URI = "/render";

    private static final String ZKB_KILL_URI = "https://zkillboard.com/kill/";
    private static final String ZKB_SOLAR_SYSTEM_URI = "https://zkillboard.com/system/";
    private static final String ZKB_SHIP_URI = "https://zkillboard.com/ship/";
    private static final String ZKB_CHARACTER_URI = "https://zkillboard.com/character/";
    private static final String ZKB_CORPORATION_URI = "https://zkillboard.com/corporation/";
    private static final String ZKB_ALLIANCE_URI = "https://zkillboard.com/alliance/";

    private ConsoleDebugs CD;// = new ConsoleDebugs();
    private DiscordApiValue discordApiValue;
    private ZKBWebsocketParser zkbWebsocketParser;
    private EsiEvetechApi esiEvetechApi;
    private ZKBApi zkbApi;
    private DatabaseService databaseService;
    private MessagingServises messagingServises;


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
    public ZKBWebSocket(int killsCounter, ConsoleDebugs CD, DiscordApiValue discordApiValue,
                        ZKBWebsocketParser zkbWebsocketParser,
                        EsiEvetechApi esiEvetechApi, ZKBApi zkbApi,
                        DatabaseService databaseService, MessagingServises messagingServises) {
        this.killsCounter=killsCounter;
        this.CD = CD;
        this.discordApiValue = discordApiValue;
        this.zkbWebsocketParser = zkbWebsocketParser;
        this.esiEvetechApi = esiEvetechApi;
        this.zkbApi = zkbApi;
        this.databaseService = databaseService;
        this.messagingServises = messagingServises;
        this.connectionLostDate = null;
        this.lastMessageTime = LocalDateTime.now();

        this.webSocketSession = new StandardWebSocketClient().doHandshake(this, new WebSocketHttpHeaders(), URI.create(URI_LINK_VALUE)).get();
        this.webSocketSession.sendMessage(new TextMessage("{\"action\":\"sub\",\"channel\":\"killstream\"}"));
        this.webSocketSession.sendMessage(new TextMessage("{\"action\":\"sub\",\"channel\":\"public\"}"));

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



    //----------------------------------------------------------------------------------------------
    //Separated functions
    //----------------------------------------------------------------------------------------------



    //----------------------------------------------------------------------------------------------
    // 1) Server channel handler
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


    //----------------------------------------------------------------------------------------------
    // 2) Killstream channel handler
    //----------------------------------------------------------------------------------------------


    private static class CommandIdsListPattern {
        @Getter
        private ActiveCommand activeCommand;
        @Getter
        private String note;

        public CommandIdsListPattern(ActiveCommand activeCommand, String note) {
            this.activeCommand = activeCommand;
            this.note = note;
        }
    }


    private void ifKillstreamMatcherFound(String messageContent){
        killsCounter++;
        CD.ZKBWebSocketConsoleDebugHandleTextMessage11(messageContent);


        //posts into discords
        if(ENABLING_DISCORD_POSTING){

            //victim shipTypeId parser
            String killedShip = zkbWebsocketParser.killmailCharacterShipTypeIdParser(
                    //victim character parser
                    zkbWebsocketParser.killmailVictimObjectParser(messageContent).toString());

            //capsule reports availability
            if (!CAPSULES_REPORTS_AVAILABILITY){
                //compare capsule id and victim player's ship id
                if ((killedShip.equals("33328"))||(killedShip.equals("670"))){
                    return;
                }
            }


            ArrayList<CommandIdsListPattern> commands = new ArrayList<>();
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
                            commands.add(new CommandIdsListPattern(activeCommand, "attacker"));
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
                            if((commands.size()==0)||(!commands.get(commands.size()-1).getActiveCommand().equals(activeCommand.getId()))){
                                //add to list of matching commands
                                commands.add(new CommandIdsListPattern(activeCommand, "attacker"));
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
                            if((commands.size()==0)||(!commands.get(commands.size()-1).getActiveCommand().equals(activeCommand.getId()))){
                                //add to list of matching commands
                                commands.add(new CommandIdsListPattern(activeCommand, "attacker"));
                            }

                        }
                    }

                }

                //check victim character
                {
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
                            if((commands.size()==0)||(!commands.get(commands.size()-1).getActiveCommand().equals(activeCommand.getId()))){
                                //add to list of matching commands
                                commands.add(new CommandIdsListPattern(activeCommand, "killed"));
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
                            if((commands.size()==0)||(!commands.get(commands.size()-1).getActiveCommand().equals(activeCommand.getId()))){
                                //add to list of matching commands
                                commands.add(new CommandIdsListPattern(activeCommand, "killed"));
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
                            if((commands.size()==0)||(!commands.get(commands.size()-1).getActiveCommand().equals(activeCommand.getId()))){
                                //add to list of matching commands
                                commands.add(new CommandIdsListPattern(activeCommand, "killed"));
                            }

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
                        if((commands.size()==0)||(!commands.get(commands.size()-1).getActiveCommand().equals(activeCommand.getId()))){
                            //add to list of matching commands
                            commands.add(new CommandIdsListPattern(activeCommand, "solar"));
                        }

                    }
                }



            }

        /*//if any command found do it,else just skip
        if (commandsIds.size()!=0){
            //check all servers for follows
            for (Server server : databaseService.getAllServers()) {
                //get server's commands list
                skipAndGoForNextServer: for (String command : server.getActiveCommandIdsList()){
                    //check all Ids in the list
                    for (String[] commandId : commandsIds){
                        //post link if Id equal
                        if (command.equals(commandId[0])){
                            new MessageBuilder().setContent("https://zkillboard.com/kill/" +
                                    zkbWebsocketParser.killmailKillmailIdParser(messageContent) + "/").send(
                                    discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));
                            break skipAndGoForNextServer;
                        }
                    }
                }
            }
        }*/

            //if any command found do it,else just skip
            if (commands.size()!=0){
                //check all servers for follows
                for (Server server : databaseService.getAllServers()) {
                    rebuildPost(messageContent,server,commands,killedShip);
                    //get server's commands list
                    /*skipAndGoForNextServer: for (String command : server.getActiveCommandIdsList()){
                        //check all Ids in the list
                        for (CommandIdsListPattern commandId : commands){
                            //post link if Id equal
                            if (command.equals(commandId.activeCommand.getId())){

                                messagingServises.sendMessage("Yey,founded!","[Link](https://zkillboard.com/kill/" +
                                                zkbWebsocketParser.killmailKillmailIdParser(messageContent) + "/)",
                                        "waiting for others events..",
                                        URI_SHIP_IMG_STARTING + killedShip + URI_SHIP_IMG_ENDING,Color.black,
                                        discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));
                            *//*new MessageBuilder().setContent("https://zkillboard.com/kill/" +
                                    zkbWebsocketParser.killmailKillmailIdParser(messageContent) + "/").send(
                                    discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));
                            *//*break skipAndGoForNextServer;
                            }
                        }
                    }*/

                }
            }

        }





    }

    private void simplePost(String messageContent,Server server,ArrayList<CommandIdsListPattern> commands){
        skipAndGoForNextServer: for (String command : server.getActiveCommandIdsList()){
                        //check all Ids in the list
                        for (CommandIdsListPattern commandId : commands){
                            //post link if Id equal
                            if (command.equals(commandId.activeCommand.getId())){
                                messagingServises.sendBasicDiscordMessage("https://zkillboard.com/kill/" +
                                        zkbWebsocketParser.killmailKillmailIdParser(messageContent) + "/",discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));
                                break skipAndGoForNextServer;
                            }
                        }
                    }
            }


    private void rebuildPost(String messageContent, Server server, ArrayList<CommandIdsListPattern> commands, String killedShip){
        for (String commandId : server.getActiveCommandIdsList()){

            Color color = new Color(1,1,1);
            ArrayList<ActiveCommand> trackedAttackingAlliances = new ArrayList<>();
            ArrayList<ActiveCommand> trackedAttackingCorporations = new ArrayList<>();
            ArrayList<ActiveCommand> trackedAttackingCharacters = new ArrayList<>();

            ActiveCommand trackedKilledAlliance = null;
            ActiveCommand trackedKilledCorporation = null;
            ActiveCommand trackedKilledCharacter = null;
            ActiveCommand solarSystem = null;
            boolean matched = false;

            for (CommandIdsListPattern command : commands){
                //post link if Id equal
                if (commandId.equals(command.activeCommand.getId())){
                    switch (command.note) {
                        case "attacker" -> {
                            if (new Color(1, 1, 1).equals(color) || new Color(148, 0, 211).equals(color)) {
                                color = new Color(255, 255, 255);
                            } else if (new Color(0, 0, 0).equals(color)) {
                                color = new Color(220, 20, 60);
                            }
                            switch (command.activeCommand.getCommandType().substring(9)){
                                case "alliance" -> trackedAttackingAlliances.add(command.activeCommand);
                                case "corporation" -> trackedAttackingCorporations.add(command.activeCommand);
                                case "character" -> trackedAttackingCharacters.add(command.activeCommand);

                            }
                        }
                        case "killed" -> {
                            if (new Color(1, 1, 1).equals(color) || new Color(148, 0, 211).equals(color)) {
                                color = new Color(0, 0, 0);
                            } else if (new Color(255, 255, 255).equals(color)) {
                                color = new Color(220, 20, 60);
                            }
                            switch (command.activeCommand.getCommandType().substring(9)){
                                case "alliance" -> trackedKilledAlliance = command.activeCommand;
                                case "corporation" -> trackedKilledCorporation = command.activeCommand;
                                case "character" -> trackedKilledCharacter = command.activeCommand;

                            }
                        }
                        case "solar" -> {
                            solarSystem = command.activeCommand;
                            if (new Color(1, 1, 1).equals(color)) {
                                color = new Color(148, 0, 211);
                            }
                        }
                    }
                    matched=true;
                }

            }

            if(matched){
                if(trackedAttackingAlliances.size()>1){
                    int arrayOriginalListSize = trackedAttackingAlliances.size();
                    for (int record = arrayOriginalListSize-1;record>=1;record--){
                        for (int recordForCompare = trackedAttackingAlliances.size()-2;recordForCompare>=0;recordForCompare--){
                            if(trackedAttackingAlliances.get(record).getNumericalValue().equals(
                                    trackedAttackingAlliances.get(recordForCompare).getNumericalValue())){
                                trackedAttackingAlliances.remove(record);
                                break;
                            }
                        }
                    }
                }
                if(trackedAttackingCorporations.size()>1){
                    int arrayOriginalListSize = trackedAttackingCorporations.size();
                    for (int record = arrayOriginalListSize-1;record>=1;record--){
                        for (int recordForCompare = trackedAttackingCorporations.size()-2;recordForCompare>=0;recordForCompare--){
                            if(trackedAttackingCorporations.get(record).getNumericalValue().equals(
                                    trackedAttackingCorporations.get(recordForCompare).getNumericalValue())){
                                trackedAttackingCorporations.remove(record);
                                break;
                            }
                        }
                    }
                }
                if(trackedAttackingCharacters.size()>1){
                    int arrayOriginalListSize = trackedAttackingCharacters.size();
                    for (int record = arrayOriginalListSize-1;record>=1;record--){
                        for (int recordForCompare = trackedAttackingCharacters.size()-2;recordForCompare>=0;recordForCompare--){
                            if(trackedAttackingCharacters.get(record).getNumericalValue().equals(
                                    trackedAttackingCharacters.get(recordForCompare).getNumericalValue())){
                                trackedAttackingCharacters.remove(record);
                                break;
                            }
                        }
                    }
                }

                String title = titleForPostPattern(messageContent);
                String description = descriptionForPostPattern(messageContent,solarSystem,
                        trackedAttackingAlliances,trackedAttackingCorporations,trackedAttackingCharacters,
                        trackedKilledAlliance,trackedKilledCorporation,trackedKilledCharacter);

                messagingServises.sendDiscordEmbedMessage(title,description,
                        "waiting for others events..",
                        SHIP_IMG_STARTING_URI + killedShip + SHIP_IMG_ENDING_URI,color,
                        discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));
                break;
            }

        }
    }

    private String titleForPostPattern(String messageContent){
        return "Energy anomaly detected!\n" +
                "Seems source is the ship of "
                + esiEvetechApi.getCharacterName(zkbWebsocketParser.killmailCharacterIdParser(
                zkbWebsocketParser.killmailVictimObjectParser(messageContent).toString()))
                + " (" + ZKB_KILL_URI + zkbWebsocketParser.killmailKillmailIdParser(messageContent) + "/)";
    }

    private String descriptionForPostPattern(String messageContent, ActiveCommand solarSystem,
                                             ArrayList<ActiveCommand> trackedAttackingAlliances, ArrayList<ActiveCommand> trackedAttackingCorporations, ArrayList<ActiveCommand> trackedAttackingCharacters,
                                             ActiveCommand trackedKilledAlliance, ActiveCommand trackedKilledCorporation, ActiveCommand trackedKilledCharacter){

        String description ="--------------------------------------------";
        description +="\n";

        description += "Solar system: [";
        if(solarSystem == null){
            String solarSystemId = zkbWebsocketParser.killmailSolarSystemIdParser(messageContent);
            description += zkbApi.getSolarSystemName(solarSystemId) + "]" +
                    "(" + ZKB_SOLAR_SYSTEM_URI + solarSystemId + "/)";
        } else {
            description += solarSystem.getLiteralValue() + " (!)]" +
                    "(" + ZKB_SOLAR_SYSTEM_URI + solarSystem.getNumericalValue() + "/)";
        }
        description +="\n";

        String shipTypeId = zkbWebsocketParser.killmailCharacterShipTypeIdParser(zkbWebsocketParser.killmailVictimObjectParser(messageContent).toString());
        description += "Ship: [" + zkbApi.getShipPage(shipTypeId) + "]" +
                "(" + ZKB_SHIP_URI + shipTypeId + ")";
        description +="\n";


        description += "Registration: " + zkbWebsocketParser.killmailKillmailTimeParser(messageContent)
                .replace("Z","")
                .replace("T"," ");
        description +="\n";
        description +="--------------------------------------------";


        if((trackedAttackingAlliances.size()!=0)
                ||(trackedAttackingCorporations.size()!=0)
                ||(trackedAttackingCharacters.size()!=0)){
            description +="\n";
            description+="Tracked attackers:";
            if(trackedAttackingAlliances.size()!=0)
                for (ActiveCommand attacker : trackedAttackingAlliances){
                    description +="\n";
                    description += descriptionEntity(attacker);
                }

            if(trackedAttackingCorporations.size()!=0)
                for (ActiveCommand attacker : trackedAttackingCorporations){
                    description +="\n";
                    description += descriptionEntity(attacker);
                }

            if(trackedAttackingCharacters.size()!=0)
                for (ActiveCommand attacker : trackedAttackingCharacters){
                    description +="\n";
                    description += descriptionEntity(attacker);
                }
            description +="\n";
            description +="--------------------------------------------";
        }






        if((trackedKilledAlliance!=null)
                ||(trackedKilledCorporation!=null)
                ||(trackedKilledCharacter!=null)){
            description +="\n";
            description +="Tracked destroyed:";
            description +="\n";
            if(trackedKilledAlliance != null)
                description += descriptionEntity(trackedKilledAlliance);
            if(trackedKilledCorporation != null)
                description += descriptionEntity(trackedKilledCorporation);
            if(trackedKilledCharacter != null)
                description += descriptionEntity(trackedKilledCharacter);
            description +="\n";
            description +="--------------------------------------------";
        }

        return description;
    }

    private String descriptionEntity(ActiveCommand activeCommand){
        String description = "- [" + activeCommand.getCommandType().substring(9) + " \\ " + activeCommand.getLiteralValue() + "](";
        switch (activeCommand.getCommandType().substring(9)) {
            case "alliance" -> description += ZKB_ALLIANCE_URI;
            case "corporation" -> description += ZKB_CORPORATION_URI;
            case "character" -> description += ZKB_CHARACTER_URI;
        }
        description += activeCommand.getNumericalValue() + "/)";
        return description;
    }


}
