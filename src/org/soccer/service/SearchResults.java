package org.soccer.service;

import java.util.HashMap;

public class SearchResults{
    HashMap<String, String> relevantHeaders;
    String jsonResponse;
    public SearchResults(HashMap<String, String> headers, String json) {
        relevantHeaders = headers;
        jsonResponse = json;
    }
}