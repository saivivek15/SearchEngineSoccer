package org.soccer.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soccer.indexing.DocEntity;
import org.soccer.indexing.IndexCreator;
import org.soccer.indexing.QueryExecution;
import org.soccer.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

	private final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private final HomeService homeService;

	@Autowired
	public HomeController(HomeService homeService) {
		this.homeService = homeService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Map<String, Object> model) {

		logger.debug("index() is executed!");
		model.put("msg", homeService.getDesc());
		
		return "index";
	}
	
	
	@RequestMapping("/search")
	public String redirectDynamicWelcomePage(ModelMap model, @RequestParam("searchText") String searchText)
			throws Exception {
		try {
			File file = new File(IndexCreator.indexLocation);
			if (!file.exists())
				throw new Exception();
		} catch (Exception e) {
			IndexCreator index = new IndexCreator(IndexCreator.indexLocation);
			index.readFiles();
		}
		String[] texts = searchText.trim().split(" ");
		String query = "";
		for (int i = 0; i < texts.length - 1; i++) {
			query += texts[i] + " AND ";
		}
		query += texts[texts.length - 1];
		ArrayList<DocEntity> dr = QueryExecution.processQuery(query);

		String[] elements = searchText.split(" ");
		StringBuilder newQuery = new StringBuilder();
		if (elements != null && elements.length > 1) {
			for (String word : elements) {
				newQuery.append(word);
				newQuery.append("%20");
			}
			query = newQuery.toString();
		}


		model.addAttribute("searchText", searchText);
		model.addAttribute("records", dr);

		return "index";
	}




}