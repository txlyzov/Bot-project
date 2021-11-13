package com.bot.ETRA.utils.check_list;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Component
public class CheckList {
    public static final ArrayList<String> ZKBPostType = new ArrayList<>(Arrays.asList(
            "rebuilt", //rebuilt post with tracked attackers and killers
            "basic")); //as simple ZKB link
    public static final ArrayList<Integer> ZKBColorsPattern= new ArrayList<>(Arrays.asList(
            1,
            2,
            3));
}
