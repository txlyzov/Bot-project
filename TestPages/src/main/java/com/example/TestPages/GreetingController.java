package com.example.TestPages;

import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GreetingController {
    //private String serverID = "2359044564";

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name",required = false,defaultValue = "World") String name,Model model){
        model.addAttribute("name",name);
        return "greeting";
    }

    @GetMapping
    public String main(Model model){
        model.addAttribute("someinfo","Someinfo here");
        return "main.html";
    }

//    @GetMapping("/${serverID}")
//    public String serverIDPage(@RequestParam(name = "id",required = false) String id, Model model){
//        model.addAttribute("serverId",id);
//        return "serverIDPage";
//    }

//    @SneakyThrows
//    @RequestMapping(value = "/${serverID}",method = RequestMethod.GET)
//    public String serverIDPage(@PathVariable String serverId, Model model) {
//        model.addAttribute("serverId", serverId);
//        return "serverIDPage";
//    }

    //@RequestMapping(value = "/{serverId}", method = RequestMethod.GET)\d{5,10}
    @RequestMapping(value = "/{serverId:\\d{18}}", method = RequestMethod.GET)
    public String getSourceDetails(@PathVariable String serverId, ArrayList<testClassForList> testList, Model model) {
        try {

            testList.add(new testClassForList("qwe","wer","ert"));
            testList.add(new testClassForList("asd","sdf","dfg"));
            testList.add(new testClassForList("zxc","xcv","cvb"));
            testList.add(new testClassForList("zxc","xcv","cvb"));
            testList.add(new testClassForList("zxc","xcv","cvb"));
            testList.add(new testClassForList("zxc","xcv","cvb"));

            model.addAttribute("serverId", serverId);
            model.addAttribute("testList", testList);
            return "serverIdPage";
            // it print like this  {john}
        } catch (Exception e) {
            testList.add(new testClassForList("qwe","wer","ert"));
            testList.add(new testClassForList("asd","sdf","dfg"));
            testList.add(new testClassForList("zxc","xcv","cvb"));
            model.addAttribute("serverId", "serverId not exist");
            model.addAttribute("testList", testList);

            return "serverIdPage";
        }

    }
}
