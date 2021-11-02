package com.bot.Eva.discord.servises;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;

import java.awt.*;

public interface MessagingServises {

    void sendDiscordEmbedMessage(String title, String description, String footer, String thumbnail, Color color, TextChannel channel);
    void sendBasicDiscordMessage(String messageContent,TextChannel textChannel);
}
