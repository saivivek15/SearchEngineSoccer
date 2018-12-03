package org.soccer.service;

import java.util.ArrayList;

import org.soccer.clustering.FlatClustering;
import org.soccer.clustering.HeirarClustering;
import org.soccer.indexing.DocEntity;
import org.springframework.stereotype.Service;

@Service
public class ClusteringService {
	
	public ArrayList<DocEntity> getFlatClusterResult(ArrayList<DocEntity> docResult) throws Exception {
		return FlatClustering.getFlatCluster(docResult);
	}
	
	public ArrayList<DocEntity> getSingleClusterResult(ArrayList<DocEntity> docResult1) throws Exception {
		return HeirarClustering.getSingleLinkageCluster(docResult1);
	}
	
	public ArrayList<DocEntity> getCompleteClusterResult(ArrayList<DocEntity> docResult2) throws Exception {
		return HeirarClustering.getCompleteLinkageCluster(docResult2);
	}
}
