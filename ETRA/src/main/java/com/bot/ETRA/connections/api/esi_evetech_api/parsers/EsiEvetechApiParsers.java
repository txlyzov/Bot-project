package com.bot.ETRA.connections.api.esi_evetech_api.parsers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class EsiEvetechApiParsers {
    public String characterNameParser(String jsonString){
        try {
            return new JSONObject(jsonString).getString("name");
        } catch (Exception e){
            return "objectNotFoundException";
        }
    }

    public String corporationNameParser(String jsonString){
        try {
            return new JSONObject(jsonString).getString("name");
        } catch (Exception e){
            return "objectNotFoundException";
        }
    }

    public String allianceNameParser(String jsonString){
        try {
            return new JSONObject(jsonString).getString("name");
        } catch (Exception e){
            return "objectNotFoundException";
        }
    }

    public JSONArray NPSCorporationsIdsParser(String jsonString){
        try {
            return new JSONArray(jsonString);
        } catch (Exception e){
            return null;
        }
    }
}
