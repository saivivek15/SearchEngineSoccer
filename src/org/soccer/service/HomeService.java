package org.soccer.service;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soccer.indexing.DocEntity;
import org.soccer.indexing.IndexCreator;
import org.soccer.indexing.QueryExecution;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

	private static final Logger logger = LoggerFactory.getLogger(HomeService.class);

	public ArrayList<DocEntity> getDocEntites(String searchText) throws Exception {

		try {
			File file = new File(IndexCreator.indexLocation);
			if (!file.exists())
				throw new Exception();
		} catch (Exception e) {
			IndexCreator index = new IndexCreator(IndexCreator.indexLocation);
			index.readFiles();
			logger.debug("lucene index is created!");
		}
		
		
		String[] texts = searchText.trim().split(" ");
		String query = "";
		for (int i = 0; i < texts.length - 1; i++) {
			query += texts[i] + " AND ";
		}
		query += texts[texts.length - 1];
		logger.debug("query is generated");
		
		ArrayList<DocEntity> dr = QueryExecution.processQuery(query);
		logger.debug("query is processed and results are fetched");
		
		String[] elements = searchText.split(" ");
		StringBuilder newQuery = new StringBuilder();
		if (elements != null && elements.length > 1) {
			for (String word : elements) {
				newQuery.append(word);
				newQuery.append("%20");
			}
			query = newQuery.toString();
		}

		return dr;
	}


}