package org.soccer.service;

import org.soccer.queryExpansion.*;
import org.springframework.stereotype.Service;

@Service
public class QueryExpansionService {
	
	public String getExpandedQuery(String query) throws Exception{
		queryExpansion qe = new queryExpansion();
		String expandedQuery = qe.buildQueryExpansionString(query);
		return expandedQuery;
	}
}
