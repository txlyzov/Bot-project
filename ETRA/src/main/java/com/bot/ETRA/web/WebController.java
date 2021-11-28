package com.bot.ETRA.web;

import com.bot.ETRA.discord.DiscordApiValue;
import com.bot.ETRA.models.DatabaseService;
import com.bot.ETRA.models.active_commands.ActiveCommand;
import com.bot.ETRA.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class WebController implements ErrorController { //? "implements ErrorController" probably need to move
    @Autowired
    private DiscordApiValue discordApiValue;
    @Autowired
    private DatabaseService databaseService;



    @GetMapping
    public String main(Model model){
        return "main.html";
    }

    @RequestMapping(value = "/error")
    public String handleError() {
        return "404";
    }


    @RequestMapping(value = "/{serverId:\\d{18}}", method = RequestMethod.GET)
    public String getServerActiveCommandsPage(@PathVariable long serverId, ArrayList<ActiveCommand> activeCommands, Model model) {
        try {
            //getting servers commands for displaying
            Server server = databaseService.findByServerId(serverId);
            server.getActiveCommandIdsList().forEach((commandId) -> {
                activeCommands.add(databaseService.findActiveCommandById(commandId));
            });

            model.addAttribute("server", discordApiValue.getApi().getServerById(serverId).get().getName() + " (id" + serverId + ")");
            model.addAttribute("postingChannel",discordApiValue.getApi().getChannelById(server.getChannelId()).get().toString());
            model.addAttribute("activeCommands",activeCommands);
            model.addAttribute("activeCommandsSize", activeCommands.size());
            //in future commands could have limit per server
            model.addAttribute("activeCommandsMaxSize", "Unlimited");
            model.addAttribute("lastUpdateTime", LocalDateTime.now());
            return "ServerActiveCommandsPage";
        } catch (Exception e) {
            return "404";
        }

    }

}
