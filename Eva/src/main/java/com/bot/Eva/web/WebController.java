package com.bot.Eva.web;

import com.bot.Eva.discord.DiscordApiValue;
import com.bot.Eva.models.DatabaseService;
import com.bot.Eva.models.active_commands.ActiveCommand;
import com.bot.Eva.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class WebController {
    @Autowired
    private DiscordApiValue discordApiValue;
    @Autowired
    private DatabaseService databaseService;



    @GetMapping
    public String main(Model model){
        model.addAttribute("someinfo","Someinfo here");
        return "main.html";
    }


    @RequestMapping(value = "/{serverId:\\d{18}}", method = RequestMethod.GET)
    public String getServerActiveCommandsPage(@PathVariable long serverId, ArrayList<ActiveCommand> activeCommands, Model model) {
        try {
            Server server = databaseService.findByServerId(serverId);
            //ArrayList<String> commandsIds = (ArrayList<String>) server.getActiveCommandIdsList();
            server.getActiveCommandIdsList().forEach((commandId) -> {
                databaseService.findActiveCommandById(commandId).ifPresent(activeCommands::add);
            });

            model.addAttribute("server", discordApiValue.getApi().getServerById(serverId).get().getName() + " (id" + serverId + ")");
            model.addAttribute("postingChannel",discordApiValue.getApi().getChannelById(server.getChannelId()).get().toString());
            model.addAttribute("activeCommands",activeCommands);
            model.addAttribute("activeCommandsSize", activeCommands.size());
            model.addAttribute("activeCommandsMaxSize", "Unlimited");
            model.addAttribute("lastUpdateTime", LocalDateTime.now());
            return "ServerActiveCommandsPage";
        } catch (Exception e) {
//            testList.add(new testClassForList("qwe","wer","ert"));
//            testList.add(new testClassForList("asd","sdf","dfg"));
//            testList.add(new testClassForList("zxc","xcv","cvb"));
            model.addAttribute("server", "serverId not exist");
            model.addAttribute("activeCommands", activeCommands);
            model.addAttribute("activeCommandsSize", activeCommands.size());
            model.addAttribute("activeCommandsMaxSize", "Unlimited");
            model.addAttribute("lastUpdateTime", LocalDateTime.now());

            return "ServerActiveCommandsPage";
        }

    }


    /*@RequestMapping(value = "/4040707UwU", method = RequestMethod.GET)//_(×﹏×)
    public String getServersPage(Model model) {
        ArrayList<Server> servers = (ArrayList<Server>) databaseService.getAllServers();

        ArrayList<String> serversNames = new ArrayList<>();
        for (Server server : servers){
            serversNames.add(discordApiValue.getApi().getServerById(server.getServerId()).get().getName());
        }

        class Counter{
            private int counter;
            public Counter(){
                this.counter=0;
            }
            public int upCounter(){
                counter++;
                return counter;
            }
        }

        model.addAttribute("servers", servers);
        model.addAttribute("serversNames", serversNames);
        model.addAttribute("counter",new Counter());
        return "serversPage";
    }*/

}
