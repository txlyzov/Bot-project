package com.bot.Eva.connections.api.zkb_api.parsers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ZKBApiParsers {

    public String solarSystemNameParser(String jsonString) {
        try {
            JSONObject mainObject = new JSONObject(jsonString);
            JSONArray topListsArray = mainObject.getJSONArray("topLists");
            JSONObject topListsObject = topListsArray.getJSONObject(4);
            JSONArray valueArray = topListsObject.getJSONArray("values");
            JSONObject valueObject = valueArray.getJSONObject(0);
            return valueObject.getString("solarSystemName");
        } catch (Exception e) {
            return "objectNotFoundException";
        }
    }

    public String htmlGetShipNameParser(String htmlString) {
        try {
            Pattern pattern = Pattern.compile("\"twitter:title\" content=\".+ \\|");
            Matcher matcher = pattern.matcher(htmlString);
            if (matcher.find()) {
                return matcher.group(0).substring(25).replace(" |","").replace("&#039;","'");
            }
        } catch (Exception e) {
            return "objectNotFoundException";
        }
        return "";
    }
}
