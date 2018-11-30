package org.soccer.crawling;
/**
 * @author vivek
 */
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;




public class Controller {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 10;
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxPagesToFetch(105000);
        config.setResumableCrawling(true);
        config.setPolitenessDelay(25);
        config.setIncludeHttpsPages(true);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);


        controller.addSeed("https://en.wikipedia.org/wiki/Association_football");
        controller.addSeed("http://www.espn.com/");
        controller.addSeed("https://www.soccer.com/");
        controller.addSeed("https://www.cbssports.com/soccer/");
        controller.addSeed("https://en.wiktionary.org/wiki/soccer");
        //controller.addSeed("https://www.msn.com/en-us/sports/soccer");
        controller.addSeed("https://www.forbes.com/sites/chrissmith/2018/11/14/mls-most-valuable-teams-2018/#3866dff32ee9");
        //controller.addSeed("https://www.theguardian.com/football");
        //controller.addSeed("https://www.fifa.com/");
        controller.addSeed("https://www.britannica.com/sports/World-Cup-football");
        controller.addSeed("https://en.wikipedia.org/wiki/FIFA_World_Cup");
       // controller.addSeed("https://www.foxsports.com/soccer");
        controller.addSeed("https://en.wikipedia.org/wiki/Cristiano_Ronaldo");
        controller.addSeed("https://en.wikipedia.org/wiki/FC_Barcelona");
        controller.addSeed("https://en.wikipedia.org/wiki/Manchester_United_F.C.");
        controller.addSeed("https://en.wikipedia.org/wiki/Chelsea_F.C.");
        controller.addSeed("https://en.wikipedia.org/wiki/Manchester_City_F.C.");
        controller.addSeed("https://en.wikipedia.org/wiki/Real_Madrid_C.F.");
        controller.addSeed("https://en.wikipedia.org/wiki/Liverpool_F.C.");
        controller.addSeed("https://www.fotmob.com/");

        controller.addSeed("https://en.wikibooks.org/wiki/Football_(Soccer)");
		controller.addSeed("https://en.wikibooks.org/wiki/Football_(Soccer)/The_Leagues_and_Teams#FIFA_Divisions");
		controller.addSeed("https://en.wikipedia.org/wiki/List_of_top-division_football_clubs_in_UEFA_countries");
		controller.addSeed("https://en.wikipedia.org/wiki/FIFA_World_Player_of_the_Year");
		controller.addSeed("https://en.wikipedia.org/wiki/FIFA_100");
		controller.addSeed("https://en.wikipedia.org/wiki/List_of_men's_national_association_football_teams");
		controller.addSeed("https://en.wikipedia.org/wiki/Association_football_club_names");
		controller.addSeed("https://en.wikipedia.org/wiki/La_Liga");
		controller.addSeed("https://en.wikipedia.org/wiki/Premier_League");
		controller.addSeed("http://www.oceaniafootball.com/ofc/");
		controller.addSeed("http://www.laliga.es/en");
		controller.addSeed("http://www.concacaf.com/");
		controller.addSeed("http://www.espnfc.us/");
		controller.addSeed("http://www.goal.com/en-us/");
		controller.addSeed("http://www.uefa.com/");
		controller.addSeed("http://www.tribalfootball.com/");
		controller.addSeed("http://www.foxsports.com/soccer/news");
		controller.addSeed("http://www.soccernews.com/");
		controller.addSeed("https://sports.yahoo.com/soccer/");
		controller.addSeed("http://bleacherreport.com/world-football");
		controller.addSeed("http://sportslens.com/");
		controller.addSeed("http://www.soccerbase.com/");
		controller.addSeed("http://www.101greatgoals.com/");
		controller.addSeed("http://www.football365.com/");
		controller.addSeed("https://www.msn.com/en-us/sports/soccer");
		controller.addSeed("https://www.theguardian.com/football");
		controller.addSeed("http://edition.cnn.com/sport/football");
		controller.addSeed("http://www.offthepost.info/");
		controller.addSeed("http://www.vitalfootball.co.uk/");
		controller.addSeed("http://www.fifa.com/");
		controller.addSeed("http://www.conmebol.com/en");
		controller.addSeed("http://www.the-afc.com/");
		controller.addSeed("http://www.cafonline.com/");

        controller.start(Crawler.class, numberOfCrawlers);
    }
}