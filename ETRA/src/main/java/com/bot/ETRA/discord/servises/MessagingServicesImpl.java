package com.bot.ETRA.discord.servises;

import lombok.SneakyThrows;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class MessagingServicesImpl implements MessagingServises {
    @SneakyThrows
    @Override
    public void sendDiscordEmbedMessage(String title, String description, String footer, String thumbnail, Color color, TextChannel textChannel) {
        new MessageBuilder().setEmbed(new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setFooter(footer)
                .setThumbnail(thumbnail)
                .setColor(color))
                .send(textChannel);
    }

    @SneakyThrows
    @Override
    public void sendBasicDiscordMessage(String messageContent,TextChannel textChannel){
        new MessageBuilder().setContent(messageContent).send(textChannel);
    }
}
