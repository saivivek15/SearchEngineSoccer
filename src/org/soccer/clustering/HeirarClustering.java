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
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.soccer.indexing.DocEntity;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.CompleteLinkageStrategy;
import com.apporiented.algorithm.clustering.PDistClusteringAlgorithm;
import com.apporiented.algorithm.clustering.SingleLinkageStrategy;
import com.apporiented.algorithm.clustering.WeightedLinkageStrategy;;


/**
 * @author Satya A C Obellaneni
 * 
 * referred from "https://github.com/lbehnke/hierarchical-clustering-java"
 *  which has Apache open source license
 *
 */

public class HeirarClustering {
	static String[] inputArray;
	static double[][] pdist;
	static HashMap<String, Integer> inputMap = new HashMap<>();
	public static ArrayList<ArrayList<String>> docWordsList = new ArrayList<ArrayList<String>>();
	public static ArrayList<String> totalWordsInAllDocs = new ArrayList<String>();
	public static HashSet<String> totalWordsSet = new HashSet<String>();
	public static ArrayList<DocEntity> docEntityList = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		performClusteringOnRawData();
	}

	public static void performClusteringOnRawData() throws IOException {
		Set<String> stopwordsSet = new HashSet<String>();
		HashMap<String, String> fileUrlMap = new HashMap<String, String>();

		Scanner stopwordsFile = new Scanner(new File("/Users/sobellan/Desktop/searchEngine/stopwords.txt"));
		while (stopwordsFile.hasNext())
			stopwordsSet.add(stopwordsFile.next());
		stopwordsFile.close();

		BufferedReader in;

		File contentFolder = new File("/Users/sobellan/Downloads/data1");
		File urlFilePath = new File("/Users/sobellan/Downloads/urls1.txt");

		BufferedReader urlReader = new BufferedReader(new FileReader(urlFilePath));
		String urlLine = null;
		while ((urlLine = urlReader.readLine()) != null) {
			String[] tmpFields = urlLine.split(" ");
			if (!fileUrlMap.containsKey(tmpFields[0])){
				fileUrlMap.put(tmpFields[0], tmpFields[1]);
			}
		}
		urlReader.close();

		for (File file : contentFolder.listFiles()) {
			DocEntity currentObj = new DocEntity();
			if (file.isHidden()){
				continue;
			}
			in = new BufferedReader(new FileReader(file));
			String line = null;
			String content = "";

			while ((line = in.readLine()) != null) {
				content += line;
			}
			currentObj.setFilename(file.getName());
			currentObj.setContents(content);
			String url = fileUrlMap.get(file.getName().split("\\.")[0]);
			
			if (url != null) {
				currentObj.setUrl(url);
				docEntityList.add(currentObj);
			}

		}
		System.out.println("Doc Entity List Size: " + docEntityList.size());
		System.out.println("\nclustering started\n");
		ArrayList<DocEntity> flatClusteredResult = FlatClustering.getFlatCluster(docEntityList);
//		ArrayList<DocEntity> avgClusteredResult = HeirarClustering.getAverageLinkageCluster(docEntityList);

//		System.out.println("\nFlat clustered result size: " + flatClusteredResult.size());
//		for (DocEntity dr:flatClusteredResult) {
//			System.out.println("File name: " + dr.getFilename());
//			System.out.println("Url: " + dr.getUrl());
//			System.out.println("Cluster name: " + dr.getClusterId());
//		}

//		System.out.println("*******************************");
//		System.out.println("Average Linkage");
//		System.out.println("Avg clustered result size: " + avgClusteredResult.size());
//		for (DocEntity dr:avgClusteredResult) {
//			System.out.println("File name: " + dr.getFilename());
//			System.out.println("Url: " + dr.getUrl());
//			System.out.println("Cluster name: " + dr.getClusterId());
//		}

	}

	public static String processText(String str) {
		str = str.replaceAll("\\<.*?>", " "); // to replace SGML tags with space
		str = str.replaceAll("[&+:;,=?@#|'<>.^$*()%\\!/\"]", ""); //remove special characters
		str = str.replaceAll("\\'s", ""); // remove posessives
		str = str.replaceAll("-", " "); // replace - with space
		str = str.replaceAll("\\s+", " "); // replace multiple spaces with single space
		str = str.trim().toLowerCase();
		return str;
	}

	public static ArrayList<String> removeStopWords(String[] tokens, Set<String> stopwordsSet) {
		ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
		tokensList.removeAll(stopwordsSet);
		return tokensList;
	}

	public static ArrayList<DocEntity> getSingleLinkageCluster(ArrayList<DocEntity> inputResults) {
		ArrayList<DocEntity> clusterResult = new ArrayList<>();
		ArrayList<String[]> docs = new ArrayList<String[]>();
		ArrayList<String> totalTokens = new ArrayList<String>();
		ArrayList<String> inputList = new ArrayList<String>();

		for (int i = 0; i < inputResults.size() && i < 20; i++) {
			StringBuffer sb = new StringBuffer();
			DocEntity document = new DocEntity();
			document = inputResults.get(i);

			String url = document.getUrl();
			inputMap.put(url, i);
			inputList.add(url + ", " + document.getContents());
			sb.append(document.getContents());

			// input cleaning regex
			String[] doc = sb.toString().toLowerCase().replaceAll("[\\W&&[^\\s]]", "").replaceAll("[^a-zA-Z\\s]", "")
					.replaceAll("\\s+", " ").split("\\W+");
			for (String u : doc)
				if (!totalTokens.contains(u))
					totalTokens.add(u);
			docs.add(doc);
		}
		//

		// compute tf-idf and create document vectors (double[])
		ArrayList<double[]> docVectors = new ArrayList<double[]>();
		for (String[] s : docs) {
			double[] d = new double[totalTokens.size()];
			for (int i = 0; i < totalTokens.size(); i++)
				d[i] = tf(s, totalTokens.get(i)) * idf(docs, totalTokens.get(i));
			docVectors.add(d);
		}

		ArrayList<Double> cosineSimilarities = new ArrayList<Double>();
		for (int i = 0; i < docVectors.size(); i++) {
			for (int j = i + 1; j < docVectors.size(); j++) {
				double[] first = docVectors.get(i);
				double[] second = docVectors.get(j);
				cosineSimilarities.add(cosineSim(first, second));
			}
		}
		inputArray = new String[inputList.size()];
		for (int i = 0; i < inputList.size(); i++) {
			inputArray[i] = inputList.get(i);
		}
		pdist = new double[1][cosineSimilarities.size()];
		for (int i = 0; i < cosineSimilarities.size(); i++) {
			pdist[0][i] = cosineSimilarities.get(i);
		}
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering((double[][]) pdist, inputArray, new SingleLinkageStrategy());
		System.out.println("Single Linkage......==========");
		System.out.println(cluster.getLeafNames());
		List<String> result = cluster.getLeafNames();
		int id = 0;
		for (String res : result) {
			id = inputMap.get(res.split(", ")[0]);
			System.out.println(id);
			System.out.println(inputResults.get(id).getUrl());
			clusterResult.add(inputResults.get(id));
		}
		return clusterResult;
	}

	public static ArrayList<DocEntity> getCompleteLinkageCluster(ArrayList<DocEntity> inputResults) {
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering((double[][]) pdist, inputArray, new CompleteLinkageStrategy());
		ArrayList<DocEntity> clusterResult = new ArrayList<>();

		System.out.println("Complete linkage......==========");
		System.out.println(cluster.getLeafNames());
		List<String> result = cluster.getLeafNames();
		int id = 0;
		ArrayList<Float> ranks = new ArrayList<>();
		for (int i = 0; i < result.size(); i++) {
			ranks.add((float) Math.random());
		}
		Collections.sort(ranks);
		int i = 0;

		for (String res : result) {
			id = inputMap.get(res.split(", ")[0]);
			// System.out.println(id);
			// System.out.println(inputResults.get(id).getUrlOfDoc());
			DocEntity tempDoc = inputResults.get(id);
			tempDoc.setRankScore(1 - ranks.get(i++));
			clusterResult.add(tempDoc);
		}
		return clusterResult;
	}

	static double cosineSim(double[] a, double[] b) {
		double dotProduct = 0, modA = 0, modB = 0;
		for (int i = 0; i < a.length; i++) {
			dotProduct += a[i] * b[i];
			modA += Math.pow(a[i], 2);
			modB += Math.pow(b[i], 2);
		}
		modA = Math.sqrt(modA);
		modB = Math.sqrt(modB);

		if ((modA != 0) || (modB != 0)) {
			return dotProduct / (modA * modB);
		}
		else
			return 0;
	}

	static double tf(String[] doc, String term) {
		double n = 0;
		for (String token : doc)
			if (token.equalsIgnoreCase(term))
				n++;
		return n / doc.length;
	}

	static double idf(ArrayList<String[]> docs, String term) {
		double n = 0;
		for (String[] tmp : docs)
			for (String token : tmp)
				if (token.equalsIgnoreCase(term)) {
					n++;
					break;
				}
		return Math.log(docs.size() / n);
	}

}
