package com.bot.ETRA.connections.websockets.zkb_websocket;


import com.bot.ETRA.connections.api.esi_evetech_api.EsiEvetechApi;
import com.bot.ETRA.connections.api.zkb_api.ZKBApi;
import com.bot.ETRA.connections.websockets.zkb_websocket.parsers.ZKBWebsocketParser;
import com.bot.ETRA.discord.DiscordApiValue;
import com.bot.ETRA.discord.servises.MessagingServises;
import com.bot.ETRA.models.DatabaseService;
import com.bot.ETRA.models.active_commands.ActiveCommand;
import com.bot.ETRA.models.servers.Server;
import com.bot.ETRA.utils.debugs.ConsoleDebugs;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.javacord.api.entity.message.MessageBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
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
                                                                                                /*
    //----------------------------------------------------------------------------------------------
    //Someday necessary to fix
    //----------------------------------------------------------------------------------------------
    - optimize number of API requests
    - fix realization of getting Ship name(add normal HTML parser,not regex solution)
    - connect looses
                                                                                                */





    //----------------------------------------------------------------------------------------------
    //Websocket body & main functions
    //----------------------------------------------------------------------------------------------

    //Settings
    //Code blocks disabling
    private static final boolean ENABLING_DISCORD_POSTING = true;
    private static final boolean CAPSULES_REPORTS_AVAILABILITY = false;
    //Embed discord message
    //Messages Colors presets
    private record COLOR_PRESET(Color NULL_COLOR, Color POSITIVE_COLOR, Color NEGATIVE_COLOR, Color NEUTRAL_COLOR, Color SOLAR_SYSTEM_COLOR) {}
    private static final COLOR_PRESET COLOR_PRESET_1 = new COLOR_PRESET(
            new Color(0,0,0), //NULL_COLOR
            new Color(32,178,170), //POSITIVE_COLOR
            new Color(220, 20, 60), //NEGATIVE_COLOR
            new Color(255,255,153), //NEUTRAL_COLOR
            new Color(138,43,226)); //SOLAR_SYSTEM_COLOR
    private static final COLOR_PRESET COLOR_PRESET_2 = new COLOR_PRESET(
            new Color(0,0,0), //NULL_COLOR
            new Color(255,255,255), //POSITIVE_COLOR
            new Color(1,1,1), //NEGATIVE_COLOR
            new Color(220, 20, 60), //NEUTRAL_COLOR
            new Color(148, 0, 211)); //SOLAR_SYSTEM_COLOR
    private static final COLOR_PRESET COLOR_PRESET_3 = new COLOR_PRESET(
            new Color(0,0,0), //NULL_COLOR
            new Color(60, 255, 0), //POSITIVE_COLOR
            new Color(213, 0, 255), //NEGATIVE_COLOR
            new Color(255, 153, 0), //NEUTRAL_COLOR
            new Color(0, 81, 255)); //SOLAR_SYSTEM_COLOR

    //URI's
    private static final String ZKB_WEBSOCKET_LINK_URI = "wss://zkillboard.com/websocket/"; //websocket
    private static final String ZKB_KILL_URI = "https://zkillboard.com/kill/";
    private static final String ZKB_SOLAR_SYSTEM_URI = "https://zkillboard.com/system/";
    private static final String ZKB_SHIP_URI = "https://zkillboard.com/ship/";
    private static final String ZKB_CHARACTER_URI = "https://zkillboard.com/character/";
    private static final String ZKB_CORPORATION_URI = "https://zkillboard.com/corporation/";
    private static final String ZKB_ALLIANCE_URI = "https://zkillboard.com/alliance/";
    private static final String SHIP_IMG_STARTING_URI = "https://images.evetech.net/types/"; //ship image starting
    private static final String SHIP_IMG_ENDING_URI = "/render"; //ship image ending
    //Follows message
    private static final String WEBSOCKET_FOLLOW_MESSAGE_KILLSTREAM = "{\"action\":\"sub\",\"channel\":\"killstream\"}";
    private static final String WEBSOCKET_FOLLOW_MESSAGE_PUBLIC = "{\"action\":\"sub\",\"channel\":\"public\"}";

    //Services
    private ConsoleDebugs CD; //console debug
    private ZKBWebsocketParser zkbWebsocketParser; // parsing websocket messages
    private EsiEvetechApi esiEvetechApi;
    private ZKBApi zkbApi;
    private DatabaseService databaseService;
    private DiscordApiValue discordApiValue;
    private MessagingServises messagingServises; //messages for discord

    //Values for console output
    @Getter @Setter
    private int killsCounter;
    @Getter
    private LocalDateTime lastMessageTime;
    @Getter
    private LocalDateTime connectionLostDate;
    @Getter
    private String lastServerChannelMessage; //save server channel message for ZKBWebSocketService antispam output

    //Session
    @Getter
    private WebSocketSession webSocketSession;


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

        this.webSocketSession = new StandardWebSocketClient().doHandshake(this, new WebSocketHttpHeaders(),
                URI.create(ZKB_WEBSOCKET_LINK_URI)).get();
        this.webSocketSession.sendMessage(new TextMessage(WEBSOCKET_FOLLOW_MESSAGE_KILLSTREAM));
        this.webSocketSession.sendMessage(new TextMessage(WEBSOCKET_FOLLOW_MESSAGE_PUBLIC));

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
    @SneakyThrows
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
            }
        }
        lastServerChannelMessage = messageContent;
    }


    //----------------------------------------------------------------------------------------------
    // 2) Killstream channel handler
    //----------------------------------------------------------------------------------------------

    private record CommandIdsListPattern(ActiveCommand activeCommand, String note){}


    private void ifKillstreamMatcherFound(String messageContent){
        killsCounter++;
        CD.ZKBWebSocketConsoleDebugHandleTextMessage11(messageContent);


        //posts into discords availability
        if(ENABLING_DISCORD_POSTING){
            String killedShip = zkbWebsocketParser.killmailCharacterShipTypeIdParser(
                    zkbWebsocketParser.killmailVictimObjectParser(messageContent).toString());

            if (!CAPSULES_REPORTS_AVAILABILITY){
                if ((killedShip.equals("33328"))||(killedShip.equals("670"))){
                    return;
                }
            }

            //Specific array with ActiveCommands and theirs role in current kill
            ArrayList<CommandIdsListPattern> commands = new ArrayList<>();

            JSONArray attackersArray = zkbWebsocketParser.killmailAttackersArrayParser(messageContent);
            JSONObject victimPlayer = zkbWebsocketParser.killmailVictimObjectParser(messageContent);

            //for every active command in database check
            for (ActiveCommand activeCommand : databaseService.getAllActiveCommands()) {
                String typeAC = activeCommand.getCommandType();
                String idAC = activeCommand.getNumericalValue();

                for(Object characterObject : attackersArray){
                    checkActiveCommand(commands, activeCommand, "Tracking character",
                            typeAC, idAC,
                            "attacker",
                            zkbWebsocketParser.killmailCharacterIdParser(characterObject.toString()));
                    checkActiveCommand(commands, activeCommand, "Tracking corporation",
                            typeAC, idAC,"attacker",
                            zkbWebsocketParser.killmailCharacterCorporationIdParser(characterObject.toString()));
                    checkActiveCommand(commands, activeCommand, "Tracking alliance",
                            typeAC, idAC,"attacker",
                            zkbWebsocketParser.killmailCharacterAllianceIdParser(characterObject.toString()));

                    /*//if command's tracking type equals
                    if(activeCommand.getCommandType().equals("Tracking character")){
                        //command's tracking id equals
                        if(activeCommand.getNumericalValue().equals(
                                //attacker characterId parser
                                zkbWebsocketParser.killmailCharacterIdParser(
                                        //parameter
                                        characterObject.toString()))){
                            //if Id's equal => add to list of matching commands with notes
                            commands.add(new CommandIdsListPattern(activeCommand, "attacker"));
                        }
                    }

                    //if command's tracking type equals
                    if(activeCommand.getCommandType().equals("Tracking corporation")){
                        //command's tracking id equals
                        if(activeCommand.getNumericalValue().equals(
                                //attacker corporationId parser
                                zkbWebsocketParser.killmailCharacterCorporationIdParser(
                                        //parameter
                                        characterObject.toString()))){
                            //if Id equal and not already in list
                            //if((commands.size()==0)||(!commands.get(commands.size()-1).activeCommand().equals(activeCommand.getId()))){
                                //add to list of matching commands with notes
                                commands.add(new CommandIdsListPattern(activeCommand, "attacker"));
                            //}

                        }
                    }

                    //if command's tracking type equals
                    if(activeCommand.getCommandType().equals("Tracking alliance")){
                        //command's tracking id equals
                        if(activeCommand.getNumericalValue().equals(
                                //attacker allianceId parser
                                zkbWebsocketParser.killmailCharacterAllianceIdParser(
                                        //parameter
                                        characterObject.toString()))){
                            //if Id equal and not already in list
                            //if((commands.size()==0)||(!commands.get(commands.size()-1).activeCommand().equals(activeCommand.getId()))){
                                //add to list of matching commands with notes
                                commands.add(new CommandIdsListPattern(activeCommand, "attacker"));
                            //}

                        }
                    }*/

                }

                //check victim character
                {

                    checkActiveCommand(commands, activeCommand, "Tracking character",
                            typeAC, idAC,
                            "killed",
                            zkbWebsocketParser.killmailCharacterIdParser(victimPlayer.toString()));
                    checkActiveCommand(commands, activeCommand, "Tracking corporation",
                            typeAC, idAC,"killed",
                            zkbWebsocketParser.killmailCharacterCorporationIdParser(victimPlayer.toString()));
                    checkActiveCommand(commands, activeCommand, "Tracking alliance",
                            typeAC, idAC,"killed",
                            zkbWebsocketParser.killmailCharacterAllianceIdParser(victimPlayer.toString()));

                    /*//if command's tracking type equals
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
                            //if((commands.size()==0)||(!commands.get(commands.size()-1).activeCommand().equals(activeCommand.getId()))){
                                //add to list of matching commands with notes
                                commands.add(new CommandIdsListPattern(activeCommand, "killed"));
                            //}

                        }
                    }

                    //if command's tracking type equals
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
                            //if((commands.size()==0)||(!commands.get(commands.size()-1).activeCommand().equals(activeCommand.getId()))){
                                //add to list of matching commands with notes
                                commands.add(new CommandIdsListPattern(activeCommand, "killed"));
                            //}
                        }
                    }

                    //if command's tracking type equals
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
                            //if((commands.size()==0)||(!commands.get(commands.size()-1).activeCommand().equals(activeCommand.getId()))){
                                //add to list of matching commands with notes
                                commands.add(new CommandIdsListPattern(activeCommand, "killed"));
                            //}

                        }
                    }*/
                }


                checkActiveCommand(commands, activeCommand, "Tracking solar system",
                        typeAC, idAC,
                        "solar",
                        zkbWebsocketParser.killmailSolarSystemIdParser(messageContent));

                /*//if command's tracking type equals
                if(activeCommand.getCommandType().equals("Tracking solar system")){
                    //command's tracking id equals
                    if(activeCommand.getNumericalValue().equals(
                            //battle solarSystemId parser
                            zkbWebsocketParser.killmailSolarSystemIdParser(
                                    //parameter
                                    messageContent))){
                        //if Id equal and not already in list
                        //if((commands.size()==0)||(!commands.get(commands.size()-1).activeCommand().equals(activeCommand.getId()))){
                            //add to list of matching commands with notes
                            commands.add(new CommandIdsListPattern(activeCommand, "solar"));
                        //}

                    }
                }*/


            }

            //work with commands
            if (commands.size()!=0){
                for (Server server : databaseService.getAllServers()) {
                    if(server.getServerSettings().getPostsSettings().isShouldBePosted()){
                        //posts pattern for server by its settings
                        switch (server.getServerSettings().getPostsSettings().getPostsType()) {
                            case "rebuilt" -> rebuiltPost(messageContent, server, commands, killedShip);
                            case "simple" -> simplePost(messageContent, server, commands);
                            default -> rebuiltPost(messageContent, server, commands, killedShip);
                        }
                    }
                }
            }

        }
    }

    //check active command and addition to input list if true
    private ArrayList<CommandIdsListPattern> checkActiveCommand(ArrayList<CommandIdsListPattern> list,
                                                                ActiveCommand activeCommand,
                                                                String typeToCompare,String type, String id, String note, String parserResult){
        if(type.equals(typeToCompare)){
            if(id.equals(parserResult)){
                list.add(new CommandIdsListPattern(activeCommand, note));
                return list;
            }
        }
        return list;
    }

    //simple link posting
    private void simplePost(String messageContent,Server server,ArrayList<CommandIdsListPattern> commands){
        skipAndGoForNextServer: for (String commandId : server.getActiveCommandIdsList()){
                        for (CommandIdsListPattern command : commands){
                            if (commandId.equals(command.activeCommand.getId())){
                                messagingServises.sendBasicDiscordMessage("https://zkillboard.com/kill/" +
                                        zkbWebsocketParser.killmailKillmailIdParser(messageContent) + "/",discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));
                                //if 1 follow found - no need to continue
                                break skipAndGoForNextServer;
                            }
                        }
                    }
            }


    //rebuilt bot posts with follow enumeration
    private void rebuiltPost(String messageContent, Server server, ArrayList<CommandIdsListPattern> commands, String killedShip){

        //selecting color preset for posts by server setting
        COLOR_PRESET colorPreset;
        switch (server.getServerSettings().getPostsSettings().getPostsColorPattern()) {
            case 1 -> colorPreset = COLOR_PRESET_1;
            case 2 -> colorPreset = COLOR_PRESET_2;
            case 3 -> colorPreset = COLOR_PRESET_3;
            default -> colorPreset = COLOR_PRESET_1;
        }
        //black rgb 0-0-0 breaks in discord,so its just temp value
        Color color = colorPreset.NULL_COLOR;
        //arrays for sorted attackers output
        ArrayList<ActiveCommand> trackedAttackingAlliances = new ArrayList<>();
        ArrayList<ActiveCommand> trackedAttackingCorporations = new ArrayList<>();
        ArrayList<ActiveCommand> trackedAttackingCharacters = new ArrayList<>();

        //variables for sorted killed player info output
        ActiveCommand trackedKilledAlliance = null;
        ActiveCommand trackedKilledCorporation = null;
        ActiveCommand trackedKilledCharacter = null;
        ActiveCommand solarSystem = null;

        //goal - find one at least record for server
        boolean matched = false;


        for (String commandId : server.getActiveCommandIdsList()){
            //check commands with notes and choose color for message
            for (CommandIdsListPattern command : commands){
                if (commandId.equals(command.activeCommand.getId())){
                    switch (command.note) {
                        case "attacker" -> {
                            if (colorPreset.NULL_COLOR.equals(color) || colorPreset.SOLAR_SYSTEM_COLOR.equals(color)) {
                                color = colorPreset.POSITIVE_COLOR;
                            } else
                                if (colorPreset.NEGATIVE_COLOR.equals(color)) {
                                color = colorPreset.NEUTRAL_COLOR;
                            }
                            switch (command.activeCommand.getCommandType().substring(9)){
                                case "alliance" -> trackedAttackingAlliances.add(command.activeCommand);
                                case "corporation" -> trackedAttackingCorporations.add(command.activeCommand);
                                case "character" -> trackedAttackingCharacters.add(command.activeCommand);

                            }
                        }
                        case "killed" -> {
                            if (colorPreset.NULL_COLOR.equals(color) || colorPreset.SOLAR_SYSTEM_COLOR.equals(color)) {
                                color = colorPreset.NEGATIVE_COLOR;
                            } else
                                if (colorPreset.POSITIVE_COLOR.equals(color)) {
                                color = colorPreset.NEUTRAL_COLOR;
                            }
                            switch (command.activeCommand.getCommandType().substring(9)){
                                case "alliance" -> trackedKilledAlliance = command.activeCommand;
                                case "corporation" -> trackedKilledCorporation = command.activeCommand;
                                case "character" -> trackedKilledCharacter = command.activeCommand;

                            }
                        }
                        case "solar" -> {
                            solarSystem = command.activeCommand;
                            if (colorPreset.NULL_COLOR.equals(color)) {
                                color = colorPreset.SOLAR_SYSTEM_COLOR;
                            }
                        }
                    }
                    matched=true;
                }

            }
        }



        if(matched){
            checkAndRemoveDuplicates(trackedAttackingAlliances);
            checkAndRemoveDuplicates(trackedAttackingCorporations);
            checkAndRemoveDuplicates(trackedAttackingCharacters);

            /*//removing repetitions in attack arrays
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
            //removing repetitions in attack arrays
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
            //removing repetitions in attack arrays
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
            }*/

            String title = titleForPostPattern(messageContent);
            String description = descriptionForPostPattern(messageContent,solarSystem,
                    trackedAttackingAlliances,trackedAttackingCorporations,trackedAttackingCharacters,
                    trackedKilledAlliance,trackedKilledCorporation,trackedKilledCharacter);

            //send embed Discord message
            messagingServises.sendDiscordEmbedMessage(title,description,
                    "waiting for others events..",
                    SHIP_IMG_STARTING_URI + killedShip + SHIP_IMG_ENDING_URI,color,
                    discordApiValue.getApi().getTextChannelById(server.getChannelId()).orElseThrow(IllegalStateException::new));

        }
    }

    //cleaning final post from duplicates
    private ArrayList<ActiveCommand> checkAndRemoveDuplicates(ArrayList<ActiveCommand> list){
        int arrayOriginalSize = list.size();
        if(list.size()>1){
            for (int record = arrayOriginalSize-1;record>=1;record--){
                for (int recordForCompare = list.size()-2;recordForCompare>=0;recordForCompare--){
                    if(list.get(record).getNumericalValue().equals(
                            list.get(recordForCompare).getNumericalValue())){
                        list.remove(record);
                        break;
                    }
                }
            }
        }
        return list;
    }

    //Title pattern of Embed discord message
    private String titleForPostPattern(String messageContent){
        String characterId = zkbWebsocketParser.killmailCharacterIdParser(
                zkbWebsocketParser.killmailVictimObjectParser(messageContent).toString());
        String characterName = esiEvetechApi.getCharacterName(characterId);
        if(characterName.equals("objectNotFoundException"))
            return "Energy anomaly detected!\n" +
                    "Unknown source." ;
        else
            return "Energy anomaly detected!\n" +
                    "Seems source is the object of "
                    + characterName
                    + " (" + ZKB_KILL_URI + zkbWebsocketParser.killmailKillmailIdParser(messageContent) + "/)";
    }

    //Description pattern of Embed discord message
    private String descriptionForPostPattern(String messageContent, ActiveCommand solarSystem,
                                             ArrayList<ActiveCommand> trackedAttackingAlliances, ArrayList<ActiveCommand> trackedAttackingCorporations, ArrayList<ActiveCommand> trackedAttackingCharacters,
                                             ActiveCommand trackedKilledAlliance, ActiveCommand trackedKilledCorporation, ActiveCommand trackedKilledCharacter){

        //general info block (Solar system name,Ship name,Registered time)
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



        //attackers info block (followed Alliances,followed Corporations,followed Characters)
        if((trackedAttackingAlliances.size()!=0)
                ||(trackedAttackingCorporations.size()!=0)
                ||(trackedAttackingCharacters.size()!=0)){
            description +="\n";
            description+="Observed attackers:";
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



        //killed player info block (followed Alliance,followed Corporation,followed Character)
        if((trackedKilledAlliance!=null)
                ||(trackedKilledCorporation!=null)
                ||(trackedKilledCharacter!=null)){
            description +="\n";
            description +="Observed destroyed:";
            if(trackedKilledAlliance != null)
                description += "\n" + descriptionEntity(trackedKilledAlliance);
            if(trackedKilledCorporation != null)
                description += "\n" + descriptionEntity(trackedKilledCorporation);
            if(trackedKilledCharacter != null)
                description += "\n" + descriptionEntity(trackedKilledCharacter);
            description +="\n";
            description +="--------------------------------------------";
        }

        return description;
    }

    //create list element for Description pattern of Embed discord message
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




    /*private boolean compareAndAddAC(int listSize,String type,String id,String parserResult){
        if(type.equals("Tracking solar system")){
            //command's tracking id equals
            if(id.equals(parserResult)){
                //if Id equal and not already in list
                if((listSize==0)||(!list.get(commands.size()-1).activeCommand().equals(activeCommand.getId()))){
                    //add to list of matching commands with notes
                    list.add(new CommandIdsListPattern(activeCommand, "solar"));
                    return true;
                }

            }
        }
        return false;
    }*/

    /*if(activeCommand.getCommandType().equals("Tracking corporation")){
        //command's tracking id equals
        if(activeCommand.getNumericalValue().equals(
                //victim corporationId parser
                zkbWebsocketParser.killmailCharacterCorporationIdParser(
                        //victim parser
                        zkbWebsocketParser.killmailVictimObjectParser(
                                //parameter
                                messageContent).toString()))){
            //if Id equal and not already in list
            if((commands.size()==0)||(!commands.get(commands.size()-1).activeCommand().equals(activeCommand.getId()))){
                //add to list of matching commands with notes
                commands.add(new CommandIdsListPattern(activeCommand, "killed"));
            }
        }
    }*/


}
