package org.soccer.service;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.soccer.indexing.DocEntity;
import org.soccer.indexing.IndexCreator;
import org.soccer.indexing.QueryExecution;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

	final static Logger logger = Logger.getLogger(HomeService.class);
	public ArrayList<DocEntity> getDocEntites(String query) throws Exception {

		try {
			File file = new File(IndexCreator.indexLocation);
			if (!file.exists())
				throw new Exception();
		} catch (Exception e) {
			IndexCreator index = new IndexCreator(IndexCreator.indexLocation);
			index.readFiles();
			logger.debug("lucene index is created!");
		}
		
		
		String[] texts = query.trim().split(" ");
		String q = "";
		for (int i = 0; i < texts.length - 1; i++) {
			q += texts[i] + " AND ";
		}
		q += texts[texts.length - 1];
		logger.debug("query is generated");
		
		ArrayList<DocEntity> dr = QueryExecution.processQuery(q);
		logger.debug("query is processed and results are fetched");
		
//		String[] elements = query.split(" ");
//		StringBuilder newQuery = new StringBuilder();
//		if (elements != null && elements.length > 1) {
//			for (String word : elements) {
//				newQuery.append(word);
//				newQuery.append("%20");
//			}
//			q = newQuery.toString();
//		}

		return dr;
	}

	

}