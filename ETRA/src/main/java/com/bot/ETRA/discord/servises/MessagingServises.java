package com.bot.ETRA.discord.servises;

import org.javacord.api.entity.channel.TextChannel;

import java.awt.*;

public interface MessagingServises {

    void sendDiscordEmbedMessage(String title, String description, String footer, String thumbnail, Color color, TextChannel channel);
    void sendBasicDiscordMessage(String messageContent,TextChannel textChannel);
}
