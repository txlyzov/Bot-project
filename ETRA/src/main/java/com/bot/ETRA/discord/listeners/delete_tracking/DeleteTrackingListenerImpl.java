package com.bot.ETRA.discord.listeners.delete_tracking;

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
            //if (messageCreateEvent.getMessageAuthor().isServerAdmin()){
                ifCorrectMatcherFound(messageCreateEvent,messageContent.substring(correctMatcher.start(), correctMatcher.end()).substring(9));
                isCorrectCommand = true;
            //} else warningMessage(messageCreateEvent);
        }

        //Incorrect command prompt
        if(!isCorrectCommand){
            Matcher probablyMatcher = Pattern.compile("^`untrack(\\s.{0,20})?$").matcher(messageContent);
            while (probablyMatcher.find()) {
                //if (messageCreateEvent.getMessageAuthor().isServerAdmin()){
                    messagingServises.sendBasicDiscordMessage("Command looks like \\`untrack [entity id (7-12 digits)]. " +
                                    "If you trying for it - check your input." +
                                    "\nExample - \\`untrack 1234567"
                            ,messageCreateEvent.getChannel());
                //} else warningMessage(messageCreateEvent);
            }
        }
    }



    //----------------------------------------------------------------------------------------------
    //Main function modules
    //----------------------------------------------------------------------------------------------



    private void warningMessage(MessageCreateEvent messageCreateEvent){
        messagingServises.sendBasicDiscordMessage("Hey! Its a command with GREAT consequences!\n" +
                "Only server admins can do it,i don't trust you.. (￢ ￢)",messageCreateEvent.getChannel());
    }

    private void ifCorrectMatcherFound(MessageCreateEvent messageCreateEvent,String messageValue){
        long serverId = messageCreateEvent.getServer().get().getId();

        //server for updating
        Server serverToUpdate = databaseService.findByServerId(serverId);
        //list for updating
        ArrayList<String> commandsForUpdate = (ArrayList<String>) serverToUpdate.getActiveCommandIdsList();
        ActiveCommand commandForDelete = databaseService.findActiveCommandByNumericalValue(messageValue);
        commandsForUpdate.remove(commandForDelete.getId());
        databaseService.addServer(serverToUpdate);

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
                + commandForDelete.getLiteralValue() + " anymore! Yey! (◕‿◕)",messageCreateEvent.getChannel());
        //delete command if it became useless
        if(deleteDecision){
            databaseService.deleteActiveCommand(commandForDelete);
        }

        /*else messagingServises.sendBasicDiscordMessage("You wrote wrong yours server id. True id is " + serverId + "." +
                        "\nBut be sure what are you doing,all server settings will be gone!.."
                ,messageCreateEvent.getChannel());*/
    }
}
