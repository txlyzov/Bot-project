package com.bot.ETRA.connections.api.esi_evetech_api;

import com.bot.ETRA.connections.api.esi_evetech_api.parsers.EsiEvetechApiParsers;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class EsiEvetechApi {
    private static final String MAIN_URI = "https://esi.evetech.net/";
    private static final String CHARACTERS_ENDING_URI = "v5/characters/";
    private static final String CORPORATIONS_ENDING_URI = "v5/corporations/";
    private static final String NPS_CORPORATIONS_ENDING_URI = "v2/corporations/npccorps/";
    private static final String ALLIANCE_ENDING_URI = "v4/alliances/";


    private final HttpClient httpClient = HttpClient.newHttpClient();
    private HttpRequest request;
    @Autowired
    private EsiEvetechApiParsers esiEvetechApiParsers;

    public String getCharacterName(String characterId){
        request = HttpRequest.newBuilder().uri(URI.create(MAIN_URI + CHARACTERS_ENDING_URI + characterId + "/")).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).thenApply(esiEvetechApiParsers::characterNameParser).join();
    }

    public String getCorporationName(String corporationId){
        request = HttpRequest.newBuilder().uri(URI.create(MAIN_URI + CORPORATIONS_ENDING_URI + corporationId + "/")).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).thenApply(esiEvetechApiParsers::corporationNameParser).join();
    }

    public String getAllianceName(String allianceId){
        request = HttpRequest.newBuilder().uri(URI.create(MAIN_URI + ALLIANCE_ENDING_URI + allianceId + "/")).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).thenApply(esiEvetechApiParsers::allianceNameParser).join();
    }

    public JSONArray getNPSCorporationsIds(){
        request = HttpRequest.newBuilder().uri(URI.create(MAIN_URI + NPS_CORPORATIONS_ENDING_URI)).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).thenApply(esiEvetechApiParsers::NPSCorporationsIdsParser).join();
    }

}
