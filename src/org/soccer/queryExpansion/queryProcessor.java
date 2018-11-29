package org.soccer.queryExpansion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class queryProcessor {

    LinkedHashMap<String, Map<Integer, Integer>> tokenMap;
    List<HashMap<String,Integer>>                docMap;
    HashSet<String>                            StopWords;
    public static int                            idx;

    public queryProcessor() throws IOException{
        this.tokenMap = new LinkedHashMap<>();
        this.docMap = new ArrayList<>();
        this.StopWords = new HashSet<>();

        getStopWords();
    }

    public void getStopWords() throws IOException {
        String current = new java.io.File( "." ).getCanonicalPath();
        String StopWordFile = current+"/stopwords";
        try (BufferedReader br = new BufferedReader(new FileReader(StopWordFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                StopWords.add(line.trim());
            }
        }
    }

    public void parseDocs(List<String> DocList) {
        idx = 0;
        for(String docString: DocList) {
            parseDocHelper(docString);
        }
    }

    public void parseDocHelper(String docString) {

        if (docString == null || docString.length() == 0) {
            return;
        }

        // initialize an entry in docMap
        HashMap<String, Integer> docMapObj = new HashMap<>();
        this.docMap.add(docMapObj);

        idx++;

        int wordCount = 0;

        // process text
        String processedDocString = this.processText(docString);


        for (String word: processedDocString.split(" ")){

            wordCount++;

            if (word == null || word.length() <= 2) {
                continue;
            }

            if (this.StopWords.contains(word)) {
                continue;
            }

            // add this word to token map
            if (!this.tokenMap.containsKey(word)) {
                this.tokenMap.put(word, new HashMap<Integer, Integer>());
            }

            this.tokenMap.get(word).put(idx, wordCount);
            docMapObj.put(word, docMapObj.getOrDefault(word, 0) + 1);
        }
    }

    // processText method to process a given line of text
    public String processText(String line){

        // remove SGML Tags
        line = line.replaceAll("<.*>", "");

        // remove possessives
        line = line.replaceAll("'s", "");

        // remove special chars
        line = line.replaceAll("[+^:,*?;#&~=%@`'$!_)/(}{\\.]", "");

        // replace "-" with space
        line = line.replaceAll("-", " ");

        // remove extra space.
        line = line.replaceAll("\\s+"," ");

        // lowercase text
        line = line.trim().toLowerCase();

        return line;
    }

}

