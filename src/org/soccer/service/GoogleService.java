package org.soccer.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soccer.indexing.DocEntity;
import org.springframework.stereotype.Service;

@Service
public class GoogleService {
	
	public ArrayList<DocEntity> getGoogleAPIResults(String searchText) throws IOException, JSONException {
		ArrayList<DocEntity> googleDE = new ArrayList<>();
		
		String key = "AIzaSyD138V19Q2043f37KsjJoxZtmDVMhBUQ3E";
		String cx = "003337752057909681064:9yt6jiiy32s"; //
		
		URL url = new URL("https://www.googleapis.com/customsearch/v1?key="+ key + "&cx=" + cx + "&q="+ URLEncoder.encode(searchText, "UTF-8") + "&alt=json");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		StringBuilder response = new StringBuilder();
		while ((line = br.readLine()) != null) {
			response.append(line);
		}
		if (response != null) {

			JSONObject obj = new JSONObject(response.toString());
			JSONArray items = obj.getJSONArray("items");
			
			for (int i = 0; i < items.length(); i++) {
				DocEntity d = new DocEntity();
				d.setUrl(items.getJSONObject(i).getString("link"));
				d.setContents(items.getJSONObject(i).getString("snippet"));
				googleDE.add(d);
			}
		}
		return googleDE;
	}
}
