package org.soccer.queryExpansion;

import java.io.File;
import java.io.IOException;
import java.util.*;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

public class stemHelper {

    Map<String, Set<String>> stemMap;
    Set<String> tokensWithoutStemmingSet;
    WordnetStemmer     stemmer;

    public stemHelper(String query) throws IOException {
        this.stemMap = new HashMap<>();

        String current = new java.io.File( "." ).getCanonicalPath();
        String wordNet = current+"/dict";
        final Dictionary dict = new Dictionary(new File(wordNet));
        dict.open();
        this.stemmer = new WordnetStemmer(dict);

        this.tokensWithoutStemmingSet = new HashSet<>();
        this.tokensWithoutStemmingSet.addAll(Arrays.asList(query.split(" ")));
    }

    public void executeStemming(HashMap<String, Map<Integer, Integer>> tokenMap) {
        Set<String> set = new HashSet<>();
        Set<String> keySet = tokenMap.keySet();
        set.addAll(keySet);

        boolean flag;
        Set<String> Stems = new HashSet<>();
        POS[] values = new POS[] {POS.NOUN, POS.VERB, POS.ADJECTIVE, POS.ADVERB};
        for (String w1: set) {
            flag = false;

            for(String w: this.tokensWithoutStemmingSet) {
                if (w.contains(w1) || w1.contains(w)) {
                    flag = true;
                    break;
                }
            }

            if (flag) {
                continue;
            }

            for(POS pos: values) {
                Stems.addAll(this.stemmer.findStems(w1, pos));
            }
        }

        List<String> stemsFound;
        for (String w1: keySet) {
            flag = false;
            for(POS pos: values) {
                stemsFound = this.stemmer.findStems(w1, pos);
                for (String w: stemsFound){
                    if (Stems.contains(w)){
                        flag = true;
                        this.stemMap.putIfAbsent(w, new HashSet<>());
                        this.stemMap.get(w).add(w1);
                    }
                }
            }

            if (!flag) {
                this.stemMap.putIfAbsent(w1, new HashSet<>());
                this.stemMap.get(w1).add(w1);
            }
        }

        Set<String> tokensSet;
        Stems = new HashSet<>();
        Stems.addAll(this.stemMap.keySet());
        for (String w1: Stems) {
            tokensSet = this.stemMap.get(w1);
            flag = false;
            if (tokensSet != null && tokensSet.size() > 1) {
                for (String w2: tokensSet) {
                    if (tokenMap.containsKey(w2)) {
                        flag = true;
                    } else {
                        this.stemMap.remove(w2);
                    }
                }

                for (int i = w1.length() - 1; i > 0; i--){
                    String subStr = w1.substring(0, i);
                    if (this.stemMap.containsKey(subStr)) {
                        Set<String> common = new HashSet<>();
                        common.addAll(this.stemMap.get(w1));
                        common.removeAll(this.stemMap.get(subStr));

                        if (common.size() != this.stemMap.get(w1).size()) {
                            this.stemMap.remove(subStr);
                        }
                    }
                }
            } else if (tokensSet != null && tokensSet.size() == 1) {

                for (String w2: tokensSet){
                    if (!tokenMap.containsKey(w2)) {
                        this.stemMap.remove(w2);
                    } else {
                        flag = true;
                    }
                }
            }

            if (!flag || w1.length() < 3) {
                this.stemMap.remove(w1);
            }
        }
    }
}
