package com.example.DiscordTestBot1.services;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;

public interface MessagingServises {

    void sendMessage(MessageAuthor author, String title, String description, String footer, String thumbnail, TextChannel channel);
}
