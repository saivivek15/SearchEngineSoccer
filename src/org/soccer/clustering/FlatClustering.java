package org.soccer.clustering;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.soccer.indexing.DocEntity;;

/**
 * @author Satya A C Obellaneni
 *
 */
public class FlatClustering {

	public static ArrayList<DocEntity> getFlatCluster(ArrayList<DocEntity> inputResults) {
		ArrayList<DocEntity> clusterResult = new ArrayList<>();
		ArrayList<ArrayList<String>> docs = new ArrayList<>();
		ArrayList<String> filenames = new ArrayList<String>();
		ArrayList<String> global = new ArrayList<String>();
		HashMap<String, Integer> inputMap = new HashMap<>();
	//	HashMap<String,Float> rankMap = new HashMap<>();
		for (int i = 0; i < inputResults.size(); i++) {
			StringBuffer sb = new StringBuffer();
			DocEntity document = new DocEntity();
			document = inputResults.get(i);
			inputMap.put(document.getUrl(), i);
		//	rankMap.put(document.getUrl(), document.getHitScore());
			sb.append(document.getContents());
			// input cleaning regex
			String[] d = sb.toString().toLowerCase().replaceAll("[\\W&&[^\\s]]", "").replaceAll("[^a-zA-Z\\s]", "")
					.replaceAll("\\s+", " ").trim().split("\\W+");
			for (String u : d)
				if (!global.contains(u))
					global.add(u);
			
			docs.add(new ArrayList<String>(Arrays.asList(d)));
			
			if (document.getUrl() != null) {
				filenames.add(document.getUrl());
			}
		}

		ArrayList<ArrayList<Double>> vecspace = new ArrayList<ArrayList<Double>>();
		for (ArrayList<String> doc : docs) {
			ArrayList<Double> docVector = new ArrayList<Double>();
			for (String token:global)
				docVector.add(tf(doc, token) * idf(docs, token));
			vecspace.add(docVector);
		}

		// iterate k-means
		HashMap<ArrayList<Double>, TreeSet<Integer>> clusters = new HashMap<ArrayList<Double>, TreeSet<Integer>>();
		HashMap<ArrayList<Double>, TreeSet<Integer>> tmpClusters = new HashMap<ArrayList<Double>, TreeSet<Integer>>();
		HashSet<Integer> kcenters = new HashSet<Integer>();
		TreeMap<Double, HashMap<ArrayList<Double>, TreeSet<Integer>>> errorSumMap = new TreeMap<Double, HashMap<ArrayList<Double>, TreeSet<Integer>>>();
		int k = 4;
		int maxIterations = 10;
		
		for (int loopCnt = 0; loopCnt < 2; loopCnt++) {

			// randomly initialize cluster centers
			while (kcenters.size() < k)
				kcenters.add((int) (Math.random() * vecspace.size()));

			for (int center : kcenters) {
				tmpClusters.put(vecspace.get(center), new TreeSet<Integer>());
			}

			int iteations = 0;
			while (true) {
				clusters = new HashMap<ArrayList<Double>, TreeSet<Integer>>(tmpClusters);

				// assign clusters to the documents
				for (int i = 0; i < vecspace.size(); i++) {
					ArrayList<Double> centroid = null;
					double similarity = 0;
					for (ArrayList<Double> vector : clusters.keySet()) {
						double cosineSimilarity = computeCosineSimilarity(vecspace.get(i), vector);
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
						ArrayList<Double> docVector = vecspace.get(docId);
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
					similaritySum += computeCosineSimilarity(centerVector, vecspace.get(docId));
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
					DocEntity dc = inputResults.get(inputMap.get(filenames.get(pts)));
					dc.setClusterId(clusterNo);
					clusterResult.add(dc);
					
				}

			}
			clusterNo++;
			System.out.print("\b\b], ");
		}
		System.out.println("\b\b}");
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

	public static ArrayList<DocEntity> getKMeansCluster(String query) {

		StockRestConnection conn = new StockRestConnection();
		ArrayList<DocEntity> clusterResult = null;
		System.out.println("Sending query to clustering"+query);
		query = query.replaceAll(" ", "%20");
		String url = "company" + "/" + query;
		String outputString = "";
		InputStream in = conn.getConnection(url, "GET");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			outputString = sb.toString();
			System.out.println("Inside k means======");
			clusterResult = new ArrayList<>();
			JSONArray jsonArray = JSONArray.fromObject(outputString);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				DocEntity dr = new DocEntity();
				dr.setClusterId((int) jsonObject.get("clusterId"));
				dr.setUrl(jsonObject.get("url").toString());
				dr.setContents(jsonObject.get("content").toString());
				System.out.println(dr.getClusterId());
				System.out.println(dr.getUrl());
				clusterResult.add(dr);
			}

			in.close();
		} catch (Exception e) {
			System.out.println("Buffer Error" + "Error converting result " + e.toString());
		}
		return clusterResult;

	}

}