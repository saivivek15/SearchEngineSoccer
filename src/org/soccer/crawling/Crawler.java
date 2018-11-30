package org.soccer.crawling;
/**
 * @author vivek
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler {
    
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$");

    private final String basePath = "/Users/vivek/Desktop/data";
    private final String graphPath = "/Users/vivek/Desktop/graph.txt";
    private final String urlsPath = "/Users/vivek/Desktop/urls.txt";


     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         return !FILTERS.matcher(href).matches();
     }

     
     @Override
     public void visit(Page page) {
         int docId = page.getWebURL().getDocid();
         int parentDocId = page.getWebURL().getParentDocid();
         String url = String.join(" ", String.valueOf(docId), page.getWebURL().getURL(), System.lineSeparator());

         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String content = new String(" "+ htmlParseData.getTitle()+"::"+htmlParseData.getText());
             String edge = new String(String.valueOf(parentDocId)+" "+ String.valueOf(docId)+ System.lineSeparator());
             try {
                 Files.write(Paths.get(basePath, + docId + ".txt"), content.getBytes());
                 Files.write(Paths.get(graphPath), edge.getBytes(), StandardOpenOption.APPEND);
                 Files.write(Paths.get(urlsPath), url.getBytes(), StandardOpenOption.APPEND);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
}