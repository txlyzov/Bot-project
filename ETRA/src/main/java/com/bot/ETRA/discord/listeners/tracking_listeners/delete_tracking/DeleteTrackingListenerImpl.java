package com.bot.ETRA.discord.listeners.tracking_listeners.delete_tracking;

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
public class DeleteTrackingListenerImpl implements DeleteTrackingListener {

                                                                                                  /*
    //----------------------------------------------------------------------------------------------
    //Someday necessary to fix
    //----------------------------------------------------------------------------------------------
    - server settings,permission for anyone\for admins
                                                                                                 */



    @Autowired
    private DatabaseService databaseService;
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
        Matcher correctMatcher = Pattern.compile("^`untrack \\d{7,12}$").matcher(messageContent);
        while (correctMatcher.find()) {

            //check user permission
            if ((databaseService.findByServerId(messageCreateEvent.getServer().get().getId()).getServerSettings()
                    .getEveryoneCommandsPermissions().isDeleteTrackingCommandPermission())
                    ||(messageCreateEvent.getMessageAuthor().isServerAdmin())){
                ifCorrectMatcherFound(messageCreateEvent,messageContent.substring(correctMatcher.start(), correctMatcher.end()).substring(9));
                isCorrectCommand = true;

                //regular users can't do it,warning
            } else warningMessage(messageCreateEvent);

        }

        //Incorrect command prompt
        if(!isCorrectCommand){
            Matcher probablyMatcher = Pattern.compile("^`untrack(\\s.{0,20})?$").matcher(messageContent);
            while (probablyMatcher.find()) {

                //check user permission
                if ((databaseService.findByServerId(messageCreateEvent.getServer().get().getId()).getServerSettings()
                        .getEveryoneCommandsPermissions().isDeleteTrackingCommandPermission())
                        ||(messageCreateEvent.getMessageAuthor().isServerAdmin())){
                    messagingServises.sendBasicDiscordMessage("Command looks like \\`untrack [entity id (7-12 digits)]. " +
                                    "If you trying for it - check your input." +
                                    "\nExample - \\`untrack 1234567"
                            ,messageCreateEvent.getChannel());

                    //regular users can't do it,warning
                } else warningMessage(messageCreateEvent);

            }
        }
    }



    //----------------------------------------------------------------------------------------------
    //Main function modules
    //----------------------------------------------------------------------------------------------



    //Warning for regular (without admin permissions) users
    private void warningMessage(MessageCreateEvent messageCreateEvent){
        messagingServises.sendBasicDiscordMessage("Hey! Local administrators said I can't trust you.. (??? ???)\n" +
                "Only server admins use this command..talk with them about it..",messageCreateEvent.getChannel());
    }

    private void ifCorrectMatcherFound(MessageCreateEvent messageCreateEvent,String messageValue){
        long serverId = messageCreateEvent.getServer().get().getId();

        //server for updating
        Server serverToUpdate = databaseService.findByServerId(serverId);

        //list for updating
        ArrayList<String> commandsForUpdate = (ArrayList<String>) serverToUpdate.getActiveCommandIdsList();
        ActiveCommand commandForDelete = databaseService.findActiveCommandByNumericalValue(messageValue);

        try{
            commandsForUpdate.remove(commandForDelete.getId());
            databaseService.saveServer(serverToUpdate);

            //other servers to find commands that are still be in use
            ArrayList<Server> otherServers = (ArrayList<Server>) databaseService.getAllServers();
            otherServers.remove(serverToUpdate);
            boolean deleteDecision = true;

            serverSearch: for(Server server : otherServers){
                for (String commandsInUse : server.getActiveCommandIdsList()){

                    //if command still in use - stop search and change delete decision
                    if(commandForDelete.equals(commandsInUse)){
                        deleteDecision = false;
                        break serverSearch;
                    }
                }
            }

            messagingServises.sendBasicDiscordMessage("No posts about "
                    + commandForDelete.getLiteralValue() + " anymore! Yey! (?????????)",messageCreateEvent.getChannel());

            //delete command if it became useless
            if(deleteDecision){
                databaseService.deleteActiveCommand(commandForDelete);
            }

        } catch (Exception e){
            messagingServises.sendBasicDiscordMessage("I'm already know nothing about id " + messageValue + "!",messageCreateEvent.getChannel());
        }
    }
}
