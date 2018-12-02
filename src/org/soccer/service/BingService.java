package org.soccer.service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.soccer.indexing.DocEntity;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class BingService {
	public String subscriptionKey = "64882f4fcabe4ff8a5103b9c948bd683";
	public String host = "https://api.cognitive.microsoft.com";
	public String path = "/bing/v7.0/search";

	public SearchResults SearchWeb (String searchQuery) throws Exception {
	   
		//ArrayList<DocEntity> results = new ArrayList<>();
		
		// Construct the URL.
	    URL url = new URL(host + path + "?q=" +  URLEncoder.encode(searchQuery, "UTF-8"));

	    // Open the connection.
	    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
	    connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

	    // Receive the JSON response body.
	    InputStream stream = connection.getInputStream();
	    String response = new Scanner(stream).useDelimiter("\\A").next();
	    
	    // Construct the result object.
	    SearchResults results = new SearchResults(new HashMap<String, String>(), response);


	    stream.close();
	    return results;
	}
	
	public  ArrayList<DocEntity> getBingAPIResults(String query) {
	    
		// Confirm the subscriptionKey is valid.
		ArrayList<DocEntity> results = new ArrayList<>();


	    try {
		    if (subscriptionKey.length() != 32) {
		        System.out.println("Invalid Bing Search API subscription key!");
		        System.out.println("Please paste yours into the source code.");
		    }
	        SearchResults result = SearchWeb(query);
		    JsonParser parser = new JsonParser();
		    JsonObject json = parser.parse(result.jsonResponse).getAsJsonObject();
		    if(!json.has("webPages"))
		    	return results;
		    JsonArray pages = json.get("webPages").getAsJsonObject().get("value").getAsJsonArray();
		    for(int i=0;i<pages.size();i++){
		    	DocEntity d = new DocEntity();
		    	d.setUrl(pages.get(i).getAsJsonObject().get("url").toString());
		    	d.setContents(pages.get(i).getAsJsonObject().get("snippet").toString());
		    	d.setTitle(pages.get(i).getAsJsonObject().get("name").toString());
		    	results.add(d);
		    }
		    
	    }
	    catch (Exception e) {
	        e.printStackTrace(System.out);
	    }
	    
	   return results;
	}
}
