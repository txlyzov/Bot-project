package com.bot.Eva.discord.servises;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class MessagingServicesImpl implements MessagingServises {
    @Override
    public void sendDiscordEmbedMessage(String title, String description, String footer, String thumbnail, Color color, TextChannel textChannel) {
        int red = (int) Math.floor(Math.random() * 255);
        int green = (int) Math.floor(Math.random() * 255);
        int blue = (int) Math.floor(Math.random() * 255);
        new MessageBuilder().setEmbed(new EmbedBuilder()
                        //.setAuthor(author)

                //.addField("Field title", title)
                .setTitle(title)
                .setDescription(description)
                .setFooter(footer)
                .setThumbnail(thumbnail)
                .setColor(color))
                //.setColor(new Color(red,green,blue)))
                .send(textChannel);
    }

    public void sendBasicDiscordMessage(String messageContent,TextChannel textChannel){
        new MessageBuilder().setContent(messageContent).send(textChannel);
    }
}
