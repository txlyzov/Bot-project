package com.bot.Eva.discord.listeners.delete_tracking;

import org.javacord.api.event.message.MessageCreateEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteTrackingListenerImpl implements DeleteTrackingListener {


    //----------------------------------------------------------------------------------------------
    //Main function
    //----------------------------------------------------------------------------------------------

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        /*boolean isCorrectCommand =false;
        String messageContent = messageCreateEvent.getMessageContent();

        //If command correct
        Matcher correctMatcher = Pattern.compile("^`rtr \\d{7,12}$").matcher(messageContent);
        while (correctMatcher.find()) {
          //  isCorrectCommand = ifCorrectMatcherFound(messageCreateEvent,messageContent.substring(correctMatcher.start(), correctMatcher.end()).substring(4));
        }

        //Incorrect command prompt
        if(!isCorrectCommand){
            Matcher probablyMatcher = Pattern.compile("^`rtr(\\s.{0,20})?$").matcher(messageContent);
            while (probablyMatcher.find()) {
               // ifProbablyMatcherFound(messageCreateEvent);//messageCreateEvent.getChannel().sendMessage("Command looks like \\`tr [number with 7-12 digits]. If you trying for it - check your input.\nExample: \\`tr 1234567.");
            }
        }*/
    }
}
