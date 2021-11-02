package com.bot.Eva.connections.websockets.zkb_websocket.parsers;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class ZKBWebsocketParser {

    //----------------------------------------------------------------------------------------------
    //Server channel parsers
    //----------------------------------------------------------------------------------------------


    public String serverServerStatusParser(String serverMessage){ return new JSONObject(serverMessage).getString("tqStatus"); }

    public String serverOnlineValueParser(String serverMessage){
        return new JSONObject(serverMessage).getString("tqCount");
    }



    //----------------------------------------------------------------------------------------------
    //Killstream channel parsers
    //----------------------------------------------------------------------------------------------



    public JSONArray killmailAttackersArrayParser(String killstreamMessage){
        return new JSONObject(killstreamMessage).getJSONArray("attackers");
    }

    public JSONObject killmailVictimObjectParser(String killstreamMessage){
        return new JSONObject(killstreamMessage).getJSONObject("victim");
    }

    public JSONObject killmailCharacterParser(String characterArray,int index){
        return new JSONArray(characterArray).getJSONObject(index);
    }

    public String killmailCharacterIdParser(String character){
        try {
            return Integer.toString(new JSONObject(character).getInt("character_id"));
        } catch (Exception e){
            return "objectNotFoundException";
        }
    }

    public String killmailCharacterCorporationIdParser(String character){
        try {
            return Integer.toString(new JSONObject(character).getInt("corporation_id"));
        } catch (Exception e){
            return "objectNotFoundException";
        }
    }

    public String killmailCharacterAllianceIdParser(String character){
        try {
            return Integer.toString(new JSONObject(character).getInt("alliance_id"));
        } catch (Exception e){
            return "objectNotFoundException";
        }
    }

    public String killmailCharacterShipTypeIdParser(String character){
        try {
            return Integer.toString(new JSONObject(character).getInt("ship_type_id"));
        } catch (Exception e){
            return "objectNotFoundException";
        }
    }

    public String killmailKillmailIdParser(String killstreamMessage){
        return Integer.toString(new JSONObject(killstreamMessage).getInt("killmail_id"));
    }

    public String killmailKillmailTimeParser(String killstreamMessage){
        return new JSONObject(killstreamMessage).getString("killmail_time");
    }

    public String killmailSolarSystemIdParser(String killstreamMessage){
        return Integer.toString(new JSONObject(killstreamMessage).getInt("solar_system_id"));
    }

}
