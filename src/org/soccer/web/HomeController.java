package org.soccer.web;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

	final static Logger logger = Logger.getLogger(HomeController.class);
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
		if(de.size()!=0){
			ArrayList<DocEntity> flatClusterDE = clusteringService.getFlatClusterResult(de);
			ArrayList<DocEntity> singleClusterDE = clusteringService.getSingleClusterResult(de);
			ArrayList<DocEntity> completeClusterDE = clusteringService.getCompleteClusterResult(de);
			model.addAttribute("flatClusterDE",flatClusterDE);
			model.addAttribute("singleClusterDE",singleClusterDE);
			model.addAttribute("completeClusterDE",completeClusterDE);
		}else{
			model.addAttribute("flatClusterDE",new ArrayList<DocEntity>());
			model.addAttribute("singleClusterDE",new ArrayList<DocEntity>());
			model.addAttribute("completeClusterDE",new ArrayList<DocEntity>());
		}

		String expandQuery = queryExpansionService.getExpandedQuery(query);

		ArrayList<DocEntity> expandDE = homeService.getDocEntites(expandQuery);
		String eq = new String("Expanded Query: " + expandQuery);
		
		model.addAttribute("query", query);
		model.addAttribute("DocEntities", de);
		model.addAttribute("googleDE",googleDE);
		model.addAttribute("bingDE",bingDE);

		model.addAttribute("expandQuery",eq);
		model.addAttribute("expandDE", expandDE);
		
		logger.debug("search executed!");
		
		return "index";
	}




}