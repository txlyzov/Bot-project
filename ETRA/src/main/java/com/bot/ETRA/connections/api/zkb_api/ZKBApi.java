package com.bot.ETRA.connections.api.zkb_api;

import com.bot.ETRA.connections.api.zkb_api.parsers.ZKBApiParsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ZKBApi {
    private static final String MAIN_URI = "https://zkillboard.com/";
    private static final String SOLAR_SYSTEM_ENDING_URI = "api/stats/solarSystemID/";
    private static final String SHIP_PAGE_URI = "ship/";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private HttpRequest request;
    @Autowired
    private ZKBApiParsers zkbApiParsers;

    public String getSolarSystemName(String solarSystemId){
        request = HttpRequest.newBuilder().uri(URI.create(MAIN_URI + SOLAR_SYSTEM_ENDING_URI + solarSystemId + "/")).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).thenApply(zkbApiParsers::solarSystemNameParser).join();
    }

    public String getShipPage(String shipId){
        request = HttpRequest.newBuilder().uri(URI.create(MAIN_URI + SHIP_PAGE_URI + shipId + "/")).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).thenApply(zkbApiParsers::htmlGetShipNameParser).join();
    }
}
