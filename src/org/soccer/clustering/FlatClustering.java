package org.soccer.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.soccer.indexing.DocEntity;;

/**
 * @author Satya A C Obellaneni
 *
 */
public class FlatClustering {

	public static ArrayList<DocEntity> getFlatCluster(ArrayList<DocEntity> inputList) {
		ArrayList<DocEntity> clusterResult = new ArrayList<>();
		ArrayList<ArrayList<String>> docs = new ArrayList<>();
		ArrayList<String> filenames = new ArrayList<String>();
		ArrayList<String> totalTokens = new ArrayList<String>();
		HashMap<String, Integer> inputMap = new HashMap<>();
		HashMap<String,Float> rankMap = new HashMap<>();
		for (int i = 0; i < inputList.size(); i++) {
			StringBuffer sb = new StringBuffer();
			DocEntity document = new DocEntity();
			document = inputList.get(i);
			inputMap.put(document.getUrl(), i);
			rankMap.put(document.getUrl(), document.getHitScore());
			sb.append(document.getContents());
			// input cleaning regex
			String[] d = sb.toString().toLowerCase().replaceAll("[\\W&&[^\\s]]", "").replaceAll("[^a-zA-Z\\s]", "")
					.replaceAll("\\s+", " ").trim().split("\\W+");
			for (String u : d)
				if (!totalTokens.contains(u))
					totalTokens.add(u);
			
			docs.add(new ArrayList<String>(Arrays.asList(d)));
			
			if (document.getUrl() != null) {
				filenames.add(document.getUrl());
			}
		}

		ArrayList<ArrayList<Double>> docVectors = new ArrayList<ArrayList<Double>>();
		for (ArrayList<String> doc : docs) {
			ArrayList<Double> docVector = new ArrayList<Double>();
			for (String token:totalTokens)
				docVector.add(tf(doc, token) * idf(docs, token));
			docVectors.add(docVector);
		}

		// iterate k-means
		HashMap<ArrayList<Double>, TreeSet<Integer>> clusters = new HashMap<ArrayList<Double>, TreeSet<Integer>>();
		HashMap<ArrayList<Double>, TreeSet<Integer>> tmpClusters = new HashMap<ArrayList<Double>, TreeSet<Integer>>();
		HashSet<Integer> kcenters = new HashSet<Integer>();
		TreeMap<Double, HashMap<ArrayList<Double>, TreeSet<Integer>>> errorSumMap = new TreeMap<Double, HashMap<ArrayList<Double>, TreeSet<Integer>>>();
		int k = 3;
		int maxIterations = 10;
		
		for (int loopCnt = 0; loopCnt < 2; loopCnt++) {

			// randomly initialize cluster centers
			while (kcenters.size() < k)
				kcenters.add((int) (Math.random() * docVectors.size()));

			for (int center : kcenters) {
				tmpClusters.put(docVectors.get(center), new TreeSet<Integer>());
			}

			int iteations = 0;
			int test = 1;
			while (true) {
				System.out.println("In while: "+test++);
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

			System.out.println(clusters.toString().replaceAll("\\[[\\w@]+=", ""));
			if (iteations < maxIterations)
				System.out.println("Converged in " + iteations + " steps.");
			else
				System.out.println("Stopped after " + maxIterations + " iterations.");
			System.out.println("");

			// calculate similarity sum and map it to the clustering
			double similaritySum = 0;
			for (ArrayList<Double> centerVector : clusters.keySet()) {
				TreeSet<Integer> docus = clusters.get(centerVector);
				for (int docId : docus) {
					similaritySum += computeCosineSimilarity(centerVector, docVectors.get(docId));
				}
			}
			errorSumMap.put(similaritySum, new HashMap<ArrayList<Double>, TreeSet<Integer>>(clusters));

			// clear everything for next iteration
			clusters.clear();
			tmpClusters.clear();
			kcenters.clear();
		} // end of main for loop
		HashMap<ArrayList<Double>, TreeSet<Integer>> d = errorSumMap.lastEntry().getValue();
		// pick the clustering with the maximum similarity sum and print the
		// filenames and indices
		System.out.println("Best Convergence:");
		System.out.println(errorSumMap.get(errorSumMap.lastKey()).toString().replaceAll("\\[[\\w@]+=", ""));
		System.out.print("{");
		int clusterNo = 1;
		System.out.println("Filenames size: "+filenames.size());
		for (ArrayList<Double> cent : errorSumMap.get(errorSumMap.lastKey()).keySet()) {
			System.out.print("[");
			for (int pts : errorSumMap.get(errorSumMap.lastKey()).get(cent)) {
				if (pts < filenames.size()) {
					System.out.print(filenames.get(pts).substring(0, filenames.get(pts).length() - 1) + ", ");
					DocEntity dc = inputList.get(inputMap.get(filenames.get(pts)));
					dc.setClusterId(clusterNo);
					clusterResult.add(dc);
					
				}

			}
			clusterNo++;
			System.out.print("\b\b], ");
		}
		System.out.println("\b\b}");
		System.out.println("Cluster Result Size: "+clusterResult.size());
		return clusterResult;

	}

	static double computeCosineSimilarity(ArrayList<Double> a, ArrayList<Double> b) {
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


	static double tf(ArrayList<String> doc, String term) {
		double n = 0;
		for (String s : doc)
			if (s.equalsIgnoreCase(term))
				n++;
		return n / doc.size();
	}

	static double idf(ArrayList<ArrayList<String>> docs, String term) {
		double n = 0;
		for (ArrayList<String> x : docs)
			for (String s : x)
				if (s.equalsIgnoreCase(term)) {
					n++;
					break;
				}
		return Math.log(docs.size() / n);
	}
}