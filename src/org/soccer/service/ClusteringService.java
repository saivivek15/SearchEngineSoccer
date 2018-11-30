package org.soccer.service;

import java.util.ArrayList;

import org.soccer.clustering.FlatClustering;
import org.soccer.indexing.DocEntity;
import org.springframework.stereotype.Service;

@Service
public class ClusteringService {
	
	public ArrayList<DocEntity> getFlatClusterResult(ArrayList<DocEntity> docResult) throws Exception {
		return FlatClustering.getFlatCluster(docResult);
	}
}
