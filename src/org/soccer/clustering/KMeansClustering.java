package org.soccer.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class KMeansClustering {

	public ArrayList<String> fileNames = new ArrayList<>();
	public ArrayList<String> totalTokens = new ArrayList<>();
	public HashSet<String> tmpTotalTokens = new HashSet<>();
	public ArrayList<ArrayList<String>> totalDocTokensList = new ArrayList<>();
	public HashMap<String, Integer> outputMap = new HashMap<String, Integer>();
	public ArrayList<ArrayList<Double>> docVectors = new ArrayList<>();
	public Set<String> stopwordsSet = new HashSet<String>();

	public static void main(String[] args) throws IOException{
		KMeansClustering kmc = new KMeansClustering();
		kmc.performKMeansClustering();
	}

	public HashMap<String, Integer> performKMeansClustering() throws IOException {

		BufferedReader br0 = new BufferedReader(new FileReader("/Users/sobellan/Desktop/searchEngine/stopwords.txt"));
		String stopWordLine = null;
		while ((stopWordLine = br0.readLine()) != null) {
			String[] words = stopWordLine.split(",\\s*");
			this.stopwordsSet.addAll(Arrays.asList(words));
		}

		BufferedReader in;

		File contentFolder = new File("/Users/sobellan/Downloads/data1");

		File[] docFiles = contentFolder.listFiles();
		Arrays.sort(docFiles);
		for (File file : docFiles) {
			if (file.isHidden()){
				continue;
			}
			System.out.println(file.getName());
			fileNames.add(file.getName());
			in = new BufferedReader(new FileReader(file));
			String line = null;
			String content = "";

			while ((line = in.readLine()) != null) {
				content += line;
			}
			String processedFile = processText(content);
			String delimiter = " ";
			String[] tokens = processedFile.split(delimiter);
			ArrayList<String> finalTokens = removeStopWords(tokens);
			tmpTotalTokens.addAll(finalTokens);
			totalDocTokensList.add(finalTokens);

		}
		totalTokens.addAll(tmpTotalTokens);
		Collections.sort(totalTokens);

		System.out.println("Filenames size: "+fileNames.size());
		System.out.println("Total Unique words size: "+totalTokens.size());
		System.out.println(" Clustering started");

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

	public String processText(String str) {
		str = str.replaceAll("\\<.*?>", " "); // to replace SGML tags with space
		str = str.replaceAll("\\'s", ""); // remove posessives
		str = str.replaceAll("[&+:;,=?@#|<>.'^$*()%\\!/\"]", ""); //remove special characters
		str = str.replaceAll("-", " "); // replace - with space
		str = str.replaceAll("\\s+", " "); // replace multiple spaces with single space
		str = str.trim().toLowerCase();
		return str;
	}

	public ArrayList<String> removeStopWords(String[] tokens) {
		ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
		tokensList.removeAll(this.stopwordsSet);
		return tokensList;
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
		int k = 12;
		int maxIterations = 5;
		for (int loopCnt = 0; loopCnt < 2; loopCnt++) {

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
