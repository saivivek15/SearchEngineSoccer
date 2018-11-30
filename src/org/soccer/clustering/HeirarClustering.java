package org.soccer.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
import com.apporiented.algorithm.clustering.WeightedLinkageStrategy;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;;


/**
 * @author Satya A C Obellaneni
 *
 */

public class HeirarClustering {
	static String[] inputArray;
	static double[][] pdist;
	static HashMap<String, Integer> inputMap = new HashMap<>();
	public static ArrayList<ArrayList<String>> docWordsList = new ArrayList<ArrayList<String>>();
	public static ArrayList<String> totalWordsInAllDocs = new ArrayList<String>();
	public static HashSet<String> totalWordsSet = new HashSet<String>();

	public static void main(String[] args) throws FileNotFoundException {
		getAllExamCluster("/Users/sobellan/Desktop/searchEngine/data1");
	}

	public static void getAllExamCluster(String ipDocs) throws FileNotFoundException {
		File[] files = new File(ipDocs).listFiles();
		ArrayList<String> global = new ArrayList<String>();
		ArrayList<String[]> docs = new ArrayList<String[]>();
		Set<String> stopwordsSet = new HashSet<String>();
		ArrayList<String> fileNames = new ArrayList<String>();

		Scanner stopwordsFile = new Scanner(new File("/Users/sobellan/Desktop/searchEngine/stopwords.txt"));
		while (stopwordsFile.hasNext())
			stopwordsSet.add(stopwordsFile.next());
		stopwordsFile.close();

		BufferedReader in;
		File folder = new File("/Users/sobellan/Desktop/searchEngine/data1");

		for (File file : folder.listFiles()) {
			ArrayList<String> docWords = new ArrayList<String>();
			if (file.isHidden()){
				continue;
			}
			fileNames.add(file.getName());
			in = new BufferedReader(new FileReader(file));
			String line = null;
			try { 
				StringBuilder sb = new StringBuilder();
				while ((line = in.readLine()) != null) {
					sb.append(line + " ");
				}
				String lineString = sb.toString();
				String processedFile = processText(lineString);
				String delimiter = " ";
				String[] tokens = processedFile.split(delimiter);
				docWords = removeStopWords(tokens, stopwordsSet);
				totalWordsSet.addAll(docWords);
			} catch (IOException e) {
				e.printStackTrace();
			}
			docWordsList.add(docWords);
			//			System.out.println("File: " + file);
			//			System.out.println("words: " + docWords);
			System.out.println("File: " + file.getName());
		}

		totalWordsInAllDocs.addAll(totalWordsSet);
		System.out.println(totalWordsInAllDocs.size());
		int abc =0;
		ArrayList<double[]> docVectors = new ArrayList<double[]>();
		for (ArrayList<String> documentWord : docWordsList) {
			System.out.println(abc++);
			double[] docVector = new double[totalWordsInAllDocs.size()];
			for (int i = 0; i < totalWordsInAllDocs.size(); i++)
				docVector[i] = tf(documentWord, totalWordsInAllDocs.get(i)) * idf(i);
			//			System.out.println("doc vector values");
			//			for (double d : docVector) {
			//				System.out.print(d + " ");
			//			}
			//			System.out.println("\n");
			docVectors.add(docVector);
		}
		System.out.println("finished doc vector");
		System.out.println("doc vectors size: " + docVectors.size());
		ArrayList<Double> cosineSimilarities = new ArrayList<Double>();
		for (int i = 0; i < docVectors.size(); i++) {
			for (int j = i + 1; j < docVectors.size(); j++) {
				double[] first = docVectors.get(i);
				double[] second = docVectors.get(j);
				cosineSimilarities.add(cosineSim(first, second));
			}
		}

		System.out.println("Cosine similarities size: " + cosineSimilarities.size());

		pdist = new double[1][cosineSimilarities.size()];
		for (int i = 0; i < cosineSimilarities.size(); i++) {
			pdist[0][i] = cosineSimilarities.get(i);
			//			System.out.println(pdist[0][i]);
		}

		System.out.println("pdist size: " + pdist[0].length);
		String[] names = fileNames.toArray(new String[0]);
		System.out.println("filenames size: "+names.length);
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering(pdist, names, new AverageLinkageStrategy());

		cluster.toConsole(0);
		//		System.out.println(cluster.getTotalDistance());
		DendrogramPanel dp = new DendrogramPanel();
		dp.setModel(cluster);

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

	static double tf(ArrayList<String> docWords, String term) {
		double count = 0;
		for (String s : docWords)
			if (s.equalsIgnoreCase(term))
				count++;
		return count / docWords.size();
	}

	static double idf(int term) {
		double count = 0;
		for (ArrayList<String> x : docWordsList)
			for (String s : x)
				if (s.equalsIgnoreCase(totalWordsInAllDocs.get(term))) {
					count++;
					break;
				}
		return Math.log(docWordsList.size() / count);
	}

	public static ArrayList<DocEntity> getAverageLinkageCluster(ArrayList<DocEntity> inputResults) {
		ArrayList<DocEntity> clusterResult = new ArrayList<>();
		ArrayList<String[]> docs = new ArrayList<String[]>();
		ArrayList<String> global = new ArrayList<String>();
		ArrayList<String> inputs = new ArrayList<String>();

		for (int i = 0; i < inputResults.size() && i < 20; i++) {
			StringBuffer sb = new StringBuffer();
			DocEntity document = new DocEntity();
			document = inputResults.get(i);

			String url = document.getUrl();
			inputMap.put(url, i);
			inputs.add(url + ", " + document.getContents());
			sb.append(document.getContents());

			// input cleaning regex
			String[] d = sb.toString().toLowerCase().replaceAll("[\\W&&[^\\s]]", "").replaceAll("[^a-zA-Z\\s]", "")
					.replaceAll("\\s+", " ").split("\\W+");
			for (String u : d)
				if (!global.contains(u))
					global.add(u);
			docs.add(d);
		}
		//

		// compute tf-idf and create document vectors (double[])
		ArrayList<double[]> vecspace = new ArrayList<double[]>();
		for (String[] s : docs) {
			double[] d = new double[global.size()];
			for (int i = 0; i < global.size(); i++)
				d[i] = tf(s, global.get(i)) * idf(docs, global.get(i));
			vecspace.add(d);
		}

		ArrayList<Double> cosineSimilarities = new ArrayList<Double>();
		for (int i = 0; i < vecspace.size(); i++) {
			for (int j = i + 1; j < vecspace.size(); j++) {
				double[] first = vecspace.get(i);
				double[] second = vecspace.get(j);
				cosineSimilarities.add(cosineSim(first, second));
			}
		}
		inputArray = new String[inputs.size()];
		for (int i = 0; i < inputs.size(); i++) {
			inputArray[i] = inputs.get(i);
		}
		pdist = new double[1][cosineSimilarities.size()];
		for (int i = 0; i < cosineSimilarities.size(); i++) {
			pdist[0][i] = cosineSimilarities.get(i);
		}
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering((double[][]) pdist, inputArray, new AverageLinkageStrategy());
		System.out.println("Heirrach......==========");
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

	public static ArrayList<DocEntity> getSingleLinkageCluster(ArrayList<DocEntity> inputResults) {
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering((double[][]) pdist, inputArray, new SingleLinkageStrategy());
		ArrayList<DocEntity> clusterResult = new ArrayList<>();

		System.out.println("Single linkage......==========");
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

	public static ArrayList<DocEntity> getWeightedLinkageCluster(ArrayList<DocEntity> inputResults) {
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering((double[][]) pdist, inputArray, new WeightedLinkageStrategy());
		ArrayList<DocEntity> clusterResult = new ArrayList<>();

		System.out.println("Weighted linkage......==========");
		System.out.println(cluster.getLeafNames());
		List<String> result = cluster.getLeafNames();
		int id = 0;

		for (String res : result) {
			id = inputMap.get(res.split(", ")[0]);

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

	static double cosineSim(double[] docVector1, double[] docVector2) {
		double dProduct = 0, magOfA = 0, magOfB = 0;
		for (int i = 0; i < docVector1.length; i++) {
			dProduct += docVector1[i] * docVector2[i];
			magOfA += Math.pow(docVector1[i], 2);
			magOfB += Math.pow(docVector2[i], 2);
		}
		magOfA = Math.sqrt(magOfA);
		magOfB = Math.sqrt(magOfB);
		if ((magOfA != 0) || (magOfB != 0))
			return dProduct / (magOfA * magOfB);
		else
			return 0.0;
	}

	static double tf(String[] doc, String term) {
		double n = 0;
		for (String s : doc)
			if (s.equalsIgnoreCase(term))
				n++;
		return n / doc.length;
	}

	static double idf(ArrayList<String[]> docs, String term) {
		double n = 0;
		for (String[] x : docs)
			for (String s : x)
				if (s.equalsIgnoreCase(term)) {
					n++;
					break;
				}
		return Math.log(docs.size() / n);
	}

}
