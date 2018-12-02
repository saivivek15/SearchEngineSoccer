package org.soccer.indexing;
/**
 * @author vivek
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;

public class ComputePageRank {
	
	static String graphLocation = "/Users/vivek/Desktop/graph.txt/";
	static String urlLocation = "/Users/vivek/Desktop/urls.txt/";
	
	public VertexScoringAlgorithm<String, Double> getPageRank( ) throws IOException{
		Pseudograph<String, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
		
		FileReader fr = new FileReader(new File(urlLocation));
    	BufferedReader br = new BufferedReader(fr);
    	String line ="";
    	while ((line = br.readLine()) != null) {
        	g.addVertex(line.split(" ")[0]);
        }
    	
		fr = new FileReader(new File(graphLocation));
    	br = new BufferedReader(fr);
    	line ="";
        while ((line = br.readLine()) != null) {
        	if(!g.vertexSet().contains(line.split(" ")[0]))
        		g.addVertex(line.split(" ")[0]);
        	if(!g.vertexSet().contains(line.split(" ")[1]))
        		g.addVertex(line.split(" ")[1]);
        	g.addEdge(line.split(" ")[0], line.split(" ")[1]);
        }
        br.close();
        
        VertexScoringAlgorithm<String, Double> pr = new org.jgrapht.alg.scoring.PageRank<>(g);
        return pr;
	}
	


}
