package com.bot.ETRA.discord.listeners.add_tracking;

import com.bot.ETRA.connections.api.esi_evetech_api.EsiEvetechApi;
import com.bot.ETRA.connections.api.zkb_api.ZKBApi;
import com.bot.ETRA.discord.servises.MessagingServises;
import com.bot.ETRA.models.DatabaseService;
import com.bot.ETRA.models.active_commands.ActiveCommand;
import com.bot.ETRA.models.servers.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class AddTrackingListenerImpl implements AddTrackingListener {
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private EsiEvetechApi esiEvetechApi;
    @Autowired
    private ZKBApi zkbApi;
    @Autowired
    private MessagingServises messagingServises;

    //----------------------------------------------------------------------------------------------
    //Main function
    //----------------------------------------------------------------------------------------------

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        boolean isCorrectCommand =false;
        String messageContent = messageCreateEvent.getMessageContent();

        //If command correct
        Matcher correctMatcher = Pattern.compile("^`tr \\d{7,12}$").matcher(messageContent);
        while (correctMatcher.find()) {
            ifCorrectMatcherFound(messageCreateEvent,messageContent.substring(correctMatcher.start(), correctMatcher.end()).substring(4));
            isCorrectCommand = true;
        }

        //Incorrect command prompt
        if(!isCorrectCommand){
            Matcher probablyMatcher = Pattern.compile("^`tr(\\s.{0,20})?$").matcher(messageContent);
            while (probablyMatcher.find()) {
                ifProbablyMatcherFound(messageCreateEvent);
            }
        }
    }



    //----------------------------------------------------------------------------------------------
    //Main function modules
    //----------------------------------------------------------------------------------------------



    //If command correct
    private void ifCorrectMatcherFound(MessageCreateEvent messageCreateEvent,String messageValue){

        long serverId = messageCreateEvent.getServer().get().getId();
        long channelId = messageCreateEvent.getChannel().getId();

        ActiveCommand command = databaseService.findActiveCommandByNumericalValue(messageValue);
        if(command!=null){
            Server server = databaseService.findByServerId(serverId);
            if(server!=null){
                for(String serverCommand : server.getActiveCommandIdsList()){
                    if (command.getId().equals(serverCommand)){
                        //Already tracking
                        messagingServises.sendBasicDiscordMessage("Already tracking (⌐■_■)",messageCreateEvent.getChannel());
                    }
                }
            }
            //Start tracking
            ifNotFoundByNumericalValue(messageCreateEvent,serverId,channelId,messageValue,true);
        } else {
            //Start tracking
            ifNotFoundByNumericalValue(messageCreateEvent,serverId,channelId,messageValue,
                    //Add ActiveCommand record
                    commandTypeDefining(messageValue,messageCreateEvent));
        }
    }

    //Start tracking?
    private void ifNotFoundByNumericalValue(MessageCreateEvent messageCreateEvent,long serverId,
                                            long channelId,String messageValue,boolean startTracking){

        //Starting tracking
        if (startTracking){
            Server server = databaseService.findByServerId(serverId);
            ActiveCommand command = databaseService.findActiveCommandByNumericalValue(messageValue);
            //Server exists in database
            if (server != null) {
                server.addActiveCommand(command.getId());
                databaseService.addServer(server);
                messagingServises.sendBasicDiscordMessage("Starting observe "+ command.getLiteralValue() +
                        " (id" + messageValue + ")! " +
                        "No one/Nothing will be unnoticed (almost). " +
                        "\n(￣^￣)ゞ (⌐■_■)",messageCreateEvent.getChannel());
            }
            //Server not exists in database
            else {
                ArrayList<String> commandsList = new ArrayList<>();
                commandsList.add(command.getId());
                databaseService.addServer(new Server(serverId, channelId, commandsList));
                messagingServises.sendBasicDiscordMessage("Starting observe "+ command.getLiteralValue() +
                        " (id" + messageValue + ")! " +
                        "No one/Nothing will be unnoticed (almost). " +
                        "\n(￣^￣)ゞ (⌐■_■)" +
                        "\nAll bot activity will post in this channel. Use \\*\\`sp\\* to change posting channel.",messageCreateEvent.getChannel());
            }
        }

    }

    //Definition of command type & adding in database
    private boolean commandTypeDefining(String messageValue,MessageCreateEvent messageCreateEvent){
        //Alliance name global search (disable it for antispam and saving resources)
        String allianceName = esiEvetechApi.getAllianceName(messageValue);
        if (!allianceName.equals("objectNotFoundException")){
            databaseService.addActiveCommand(new ActiveCommand("Tracking alliance",messageValue,allianceName));
            return true;
        }

        //Character name global search
        String characterName = esiEvetechApi.getCharacterName(messageValue);
        if (!characterName.equals("objectNotFoundException")){
            databaseService.addActiveCommand(new ActiveCommand("Tracking character",messageValue,characterName));
            return true;
        }
        //Corporation name global search
        String corporationName = esiEvetechApi.getCorporationName(messageValue);
        if (!corporationName.equals("objectNotFoundException")) {
            //NPC corporation check (banned for antispam and saving resources)
            for (Object NPSCorporationId : esiEvetechApi.getNPSCorporationsIds()){
                if (NPSCorporationId.toString().equals(messageValue)){
                    messagingServises.sendBasicDiscordMessage("Tracking NPS Corporation not allowed,don't litter my resources!" +
                            "\n(╬ Ò﹏Ó)",messageCreateEvent.getChannel());
                    return false;
                }
            }
            databaseService.addActiveCommand(new ActiveCommand("Tracking corporation", messageValue, corporationName));
            return true;
        }
        //Solar system name global search
        String solarSystemName = zkbApi.getSolarSystemName(messageValue);
        if(!solarSystemName.equals("objectNotFoundException")){
            databaseService.addActiveCommand(new ActiveCommand("Tracking solar system",messageValue,solarSystemName));
            return true;
        }
        //If global api search failed
        messagingServises.sendBasicDiscordMessage("Api services cant find any info about id"
                + messageValue + "." +
                " If its new created id - wait for few days.\n" +
                "Otherwise you can report this case additionally with (command).",messageCreateEvent.getChannel());
        return false;
    }



    //Incorrect command prompt
    private void ifProbablyMatcherFound(MessageCreateEvent messageCreateEvent){
        messagingServises.sendBasicDiscordMessage("" +
                "Command looks like \\`tr [number with 7-12 digits]. " +
                "If you trying for it - check your input.\nExample: \\`tr 1234567.",
                messageCreateEvent.getChannel());
    }
}
