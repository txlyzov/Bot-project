package com.bot.Eva.discord.listeners.delete_settings;

import com.bot.Eva.discord.servises.MessagingServises;
import com.bot.Eva.models.DatabaseService;
import com.bot.Eva.models.active_commands.ActiveCommand;
import com.bot.Eva.models.servers.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResetServerSettingsListenerImpl implements ResetServerSettingsListener {
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
            Matcher correctMatcher = Pattern.compile("^`reset \\d{18}$").matcher(messageContent);
            while (correctMatcher.find()) {
                //check user admin permission
                if (messageCreateEvent.getMessageAuthor().isServerAdmin()){
                    ifCorrectMatcherFound(messageCreateEvent,messageContent.substring(correctMatcher.start(), correctMatcher.end()).substring(7));
                    isCorrectCommand = true;
                    //regular users can't do it,warning
                } else warningMessage(messageCreateEvent);
            }

            //Incorrect command prompt
            if(!isCorrectCommand){
                Matcher probablyMatcher = Pattern.compile("^`reset(\\s.{0,25})?$").matcher(messageContent);
                while (probablyMatcher.find()) {
                    //check user admin permission
                    if (messageCreateEvent.getMessageAuthor().isServerAdmin()){
                        messagingServises.sendBasicDiscordMessage("Command looks like \\`reset [serverId (18 digits)]. " +
                                        "If you trying for it - check your input." +
                                        "\nBut be sure about what are you doing,all server settings will be gone!.." +
                                        "\nYours server id is " + messageCreateEvent.getServer().get().getId()
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
        messagingServises.sendBasicDiscordMessage("Hey! Its a command with GREAT consequences!\n" +
                "Only server admins can do it,i don't trust you.. (￢ ￢)",messageCreateEvent.getChannel());
    }

    private void ifCorrectMatcherFound(MessageCreateEvent messageCreateEvent,String messageValue){
        long serverId = messageCreateEvent.getServer().get().getId();
        //compare server id as confirmation
        if(messageValue.equals(Long.toString(serverId))){
            //delete server from database
            Server serverToDelete = databaseService.findByServerId(serverId);
            databaseService.deleteServer(serverToDelete);

            //deleted server's commands,need delete them too
            ArrayList<String> commandsForDelete = (ArrayList<String>) serverToDelete.getActiveCommandIdsList();
            //other servers to find commands that are still be in use
            ArrayList<Server> otherServers = (ArrayList<Server>) databaseService.getAllServers();
            //check uses of all commands for delete
            for(String commandForDelete : commandsForDelete){
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
                //delete command if it became useless
                if(deleteDecision){
                    databaseService.deleteActiveCommand(
                            databaseService.findActiveCommandById(commandForDelete)
                                    .orElseThrow(IllegalStateException::new));
                }
            }
            messagingServises.sendBasicDiscordMessage("Well..i don't know this place anymore.. ╮(︶▽︶)╭",messageCreateEvent.getChannel());
        }//server id comparing failed warning
        else messagingServises.sendBasicDiscordMessage("You wrote wrong yours server id. True id is " + serverId + "." +
                        "\nBut be sure what are you doing,all server settings will be gone!.."
                ,messageCreateEvent.getChannel());
    }


}
