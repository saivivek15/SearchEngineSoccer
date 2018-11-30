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
	
	public ArrayList<DocEntity> getSingleClusterResult(ArrayList<DocEntity> docResult) throws Exception {
		return HeirarClustering.getSingleLinkageCluster(docResult);
	}
	
	public ArrayList<DocEntity> getCompleteClusterResult(ArrayList<DocEntity> docResult) throws Exception {
		return HeirarClustering.getCompleteLinkageCluster(docResult);
	}
	
	public ArrayList<DocEntity> getWeightedClusterResult(ArrayList<DocEntity> docResult) throws Exception {
		return HeirarClustering.getWeightedLinkageCluster(docResult);
	}
	
	public ArrayList<DocEntity> getAvgClusterResult(ArrayList<DocEntity> docResult) throws Exception {
		return HeirarClustering.getAverageLinkageCluster(docResult);
	}
}
