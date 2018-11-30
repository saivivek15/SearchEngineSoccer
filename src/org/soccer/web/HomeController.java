package org.soccer.web;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soccer.indexing.DocEntity;
import org.soccer.service.BingService;
import org.soccer.service.ClusteringService;
import org.soccer.service.GoogleService;
import org.soccer.service.HomeService;
import org.soccer.service.QueryExpansionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

	private Logger logger = LoggerFactory.getLogger(HomeController.class);
	private HomeService homeService;
	private GoogleService googleService;
	private BingService bingService;
	private ClusteringService clusteringService;
	private QueryExpansionService queryExpansionService;
	
	@Autowired
	public HomeController(HomeService homeService, GoogleService googleService, BingService bingService, ClusteringService cs, QueryExpansionService queryExpansionService) {
		this.homeService = homeService;
		this.googleService = googleService;
		this.bingService = bingService;
		this.clusteringService = cs;
		this.queryExpansionService = queryExpansionService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Map<String, Object> model) {

		logger.debug("home page!");
		return "index";
	}
	
	
	@RequestMapping("/search")
	public String redirectDynamicWelcomePage(ModelMap model, @RequestParam("query") String query)
			throws Exception {
		
		ArrayList<DocEntity> de = homeService.getDocEntites(query);
		ArrayList<DocEntity> googleDE = googleService.getGoogleAPIResults(query);
		ArrayList<DocEntity> bingDE = bingService.getBingAPIResults(query);
		ArrayList<DocEntity> flatClusterDE = clusteringService.getFlatClusterResult(de);
		ArrayList<DocEntity> singleClusterDE = clusteringService.getSingleClusterResult(de);
		ArrayList<DocEntity> avgClusterDE = clusteringService.getAvgClusterResult(de);
		ArrayList<DocEntity> completeClusterDE = clusteringService.getCompleteClusterResult(de);
		ArrayList<DocEntity> weightedClusterDE = clusteringService.getWeightedClusterResult(de);

		String expandQuery = queryExpansionService.getExpandedQuery(query);
		ArrayList<DocEntity> expandDE = homeService.getDocEntites(expandQuery);
		String eq = new String("Expanded Query: " + expandQuery);
		
		model.addAttribute("query", query);
		model.addAttribute("DocEntities", de);
		model.addAttribute("googleDE",googleDE);
		model.addAttribute("bingDE",bingDE);
		model.addAttribute("flatClusterDE",flatClusterDE);
		model.addAttribute("singleClusterDE",flatClusterDE);
		model.addAttribute("avgClusterDE",flatClusterDE);
		model.addAttribute("completeClusterDE",flatClusterDE);
		model.addAttribute("weightedClusterDE",flatClusterDE);
		model.addAttribute("expandQuery",eq);
		model.addAttribute("expandDE", expandDE);
		
		logger.debug("search executed!");
		
		return "index";
	}




}