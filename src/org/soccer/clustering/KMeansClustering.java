package org.soccer.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class KMeansClustering {
	
	public ArrayList<String> fileNames = new ArrayList<>();
	public ArrayList<String> totalTokens = new ArrayList<>();
	public HashSet<String> tmpTotalTokens = new HashSet<>();
	public ArrayList<ArrayList<String>> totalDocTokensList = new ArrayList<>();
	public HashMap<String, Integer> outputMap = new HashMap<String, Integer>();
	public ArrayList<ArrayList<Double>> docVectors = new ArrayList<>();
	
	public HashMap<String, Integer> performKMeansClustering(HashMap<String, String> inputMap) {
		
		fileNames.addAll(inputMap.keySet());
		System.out.println("No of files: "+fileNames.size());
		for(String fileName:fileNames) {
		String[] tokens = inputMap.get(fileName).split(" ");
		ArrayList<String> finalTokens = new ArrayList<String>(Arrays.asList(tokens));
		tmpTotalTokens.addAll(finalTokens);
		totalDocTokensList.add(finalTokens);
		}
		totalTokens.addAll(tmpTotalTokens);
		Collections.sort(totalTokens);

		System.out.println("Total Unique words size: "+totalTokens.size());
		
		computeTfIdf();
		TreeMap<Double, HashMap<ArrayList<Double>, TreeSet<Integer>>> errorSumMap = startKMeans();
		
		int clusterNo = 1;
		for (ArrayList<Double> cent : errorSumMap.get(errorSumMap.lastKey()).keySet()) {
			for (int pts : errorSumMap.get(errorSumMap.lastKey()).get(cent)) {
				if (pts < fileNames.size()) {
					outputMap.put(fileNames.get(pts), clusterNo);
				}

			}
			clusterNo++;
		}
		System.out.println("Output Map: "+outputMap);
		return outputMap;
	}
	public void computeTfIdf() {		
		for (ArrayList<String> tokenList : totalDocTokensList) {
			ArrayList<Double> docVector = new ArrayList<Double>();
			for (String token:totalTokens)
				docVector.add(tf(tokenList, token) * idf(totalDocTokensList, token));
			docVectors.add(docVector);
		}
	}

	public TreeMap<Double, HashMap<ArrayList<Double>, TreeSet<Integer>>> startKMeans() {
		HashMap<ArrayList<Double>, TreeSet<Integer>> clusters = new HashMap<ArrayList<Double>, TreeSet<Integer>>();
		HashMap<ArrayList<Double>, TreeSet<Integer>> tmpClusters = new HashMap<ArrayList<Double>, TreeSet<Integer>>();
		HashSet<Integer> kcenters = new HashSet<Integer>();
		TreeMap<Double, HashMap<ArrayList<Double>, TreeSet<Integer>>> errorSumMap = new TreeMap<Double, HashMap<ArrayList<Double>, TreeSet<Integer>>>();
		int k = 3;
		int maxIterations = 500;
		for (int loopCnt = 0; loopCnt < 200; loopCnt++) {

			// randomly initialize cluster centers
			while (kcenters.size() < k)
				kcenters.add((int) (Math.random() * docVectors.size()));

			for (int center : kcenters) {
				tmpClusters.put(docVectors.get(center), new TreeSet<Integer>());
			}

			int iteations = 0;
			while (true) {
				clusters = new HashMap<ArrayList<Double>, TreeSet<Integer>>(tmpClusters);

				// assign clusters to the documents
				for (int i = 0; i < docVectors.size(); i++) {
					ArrayList<Double> centroid = null;
					double similarity = 0;
					for (ArrayList<Double> vector : clusters.keySet()) {
						double cosineSimilarity = computeCosineSimilarity(docVectors.get(i), vector);
						if (cosineSimilarity >= similarity) {
							similarity = cosineSimilarity;
							centroid = vector;
						}
					}
					if (centroid != null)
						clusters.get(centroid).add(i);
				}

				// update clusters centroid
				tmpClusters.clear();
				
				for (ArrayList<Double> centroid : clusters.keySet()) {
					double[] newCentroid = new double[centroid.size()];
					ArrayList<Double> tmp = new ArrayList<Double>();
					for (int docId : clusters.get(centroid)) {
						ArrayList<Double> docVector = docVectors.get(docId);
						for (int i = 0; i < newCentroid.length; i++)
							newCentroid[i] += docVector.get(i);
					}
					for (int i = 0; i < newCentroid.length; i++)
						newCentroid[i] /= clusters.get(centroid).size();
					
					for (double d:newCentroid)
						tmp.add(d);
					tmpClusters.put(tmp, new TreeSet<Integer>());
				}

				// check end conditions and stop clustering
				iteations++;
				
				ArrayList<Double> oldCentroidsList = new ArrayList<Double>();
				ArrayList<Double> newCentroidsList = new ArrayList<Double>();
				for (ArrayList<Double> x : clusters.keySet())
					oldCentroidsList.addAll(x);

				for (ArrayList<Double> x : tmpClusters.keySet())
					newCentroidsList.addAll(x);

				if (oldCentroidsList.equals(newCentroidsList))
					break;

				if (iteations > maxIterations)
					break;
			} // end of while

//			System.out.println(clusters.values());
//			System.out.println(clusters.toString().replaceAll("\\[[\\w@]+=", ""));
//			if (iteations < maxIterations)
//				System.out.println("Converged in " + iteations + " steps.");
//			else
//				System.out.println("Stopped after " + maxIterations + " iterations.");
//			System.out.println("");

			// calculate similarity sum and map it to the clustering
			double similaritySum = 0;
			for (ArrayList<Double> centerVector : clusters.keySet()) {
				TreeSet<Integer> docs = clusters.get(centerVector);
				for (int docId : docs) {
					similaritySum += computeCosineSimilarity(centerVector, docVectors.get(docId));
				}
			}
			errorSumMap.put(similaritySum, new HashMap<ArrayList<Double>, TreeSet<Integer>>(clusters));

			// clear everything for next iteration
			clusters.clear();
			tmpClusters.clear();
			kcenters.clear();
		} // end of main for loop
//		HashMap<ArrayList<Double>, TreeSet<Integer>> d = errorSumMap.lastEntry().getValue();
//		for (ArrayList<Double> dd:d.keySet()){
//			System.out.println("__________________");
//			for (double ddd:d.get(dd))
//				System.out.println(ddd+1);
//		}
		return errorSumMap;
	}

	public double tf(ArrayList<String> doc, String term) {
		double n = 0;
		for (String s : doc)
			if (s.equalsIgnoreCase(term))
				n++;
		return n / doc.size();
	}

	public double idf(ArrayList<ArrayList<String>> docs, String term) {
		double n = 0;
		for (ArrayList<String> x : docs)
			for (String s : x)
				if (s.equalsIgnoreCase(term)) {
					n++;
					break;
				}
		return Math.log(docs.size() / n);
	}

	public double computeCosineSimilarity(ArrayList<Double> a, ArrayList<Double> b) {
		double dotProduct = 0, modA = 0, modB = 0;
		for (int i = 0; i < a.size(); i++) {
			dotProduct += a.get(i) * b.get(i);
			modA += Math.pow(a.get(i), 2);
			modB += Math.pow(b.get(i), 2);
		}
		modA = Math.sqrt(modA);
		modB = Math.sqrt(modB);

		if ((modA != 0) || (modB != 0)) {
			return dotProduct / (modA * modB);
		}
		else
			return 0;
	}


}
