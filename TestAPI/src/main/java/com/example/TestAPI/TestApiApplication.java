package com.example.TestAPI;

import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class TestApiApplication {
	private static HttpURLConnection httpURLConnection;

	public static void main(String[] args) {
		SpringApplication.run(TestApiApplication.class, args);
	}

	/*
	//Старый вариант
	@Bean
	@SneakyThrows
	public void app1(){
		BufferedReader reader;
		String line;
		StringBuffer responseContent = new StringBuffer();
		//URL url = new URL("https://jsonplaceholder.typicode.com/albums");
		//URL url = new URL("https://esi.evetech.net/ui/?version=dev#/Character");
		URL url = new URL("https://esi.evetech.net/latest/characters/2114533427/");
		httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setRequestMethod("GET");
		httpURLConnection.setConnectTimeout(5000);
		httpURLConnection.setReadTimeout(5000);


		int status = httpURLConnection.getResponseCode();
		System.out.println(status);

		if(status>299){
			reader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
			while ((line = reader.readLine())!= null){
				responseContent.append(line);
			}
			reader.close();
		} else {
			reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			while ((line = reader.readLine())!= null){
				responseContent.append(line);
			}
			reader.close();
		}
		System.out.println(responseContent.toString());
	}*/


	//Новый вариант
	@Bean
	@SneakyThrows
	public void app2(){
		String ID = "/30001894/";
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://zkillboard.com/ship/26888/")).build();

		//HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://zkillboard.com/api/stats/solarSystemID" + ID)).build();
		//HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://jsonplaceholder.typicode.com/albums")).build();
		//HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://zkillboard.com/api/stats/solarSystemID/30001894/")).build();
		//HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://esi.evetech.net/latest/characters/2114533427/")).build();
		httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body).thenAccept(System.out::println).join();//.thenApply(TestApiApplication::parse2).join();//.thenAccept(System.out::println).join();
	}


	public static String parse(String responseBody){
		JSONArray jsonArray = new JSONArray(responseBody);
		for (int i = 0; i<jsonArray.length();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			int id = jsonObject.getInt("id");
			int userId = jsonObject.getInt("userId");
			String title = jsonObject.getString("title");
			System.out.println(id + " " + title + " " + userId);
		}
		return null;
	}

	public static String parse2(String responseBody){
		JSONObject jsonObject = new JSONObject(responseBody);
		JSONArray topListsArray = jsonObject.getJSONArray("topLists");
		JSONObject topListsObject = topListsArray.getJSONObject(4);
		JSONArray valueArray = topListsObject.getJSONArray("values");
		JSONObject valueObject = valueArray.getJSONObject(0);
		String solarSystemName = valueObject.getString("solarSystemName");
		System.out.println(solarSystemName);
		return null;
	}

	public static String parse3(String responseBody){
		JSONObject jsonObject = new JSONObject(responseBody);
		String title = jsonObject.getString("name");
		System.out.println("name:  " + title);
		return null;
	}
}
