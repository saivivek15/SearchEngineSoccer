package org.soccer.indexing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class QueryExecution {

	public static ArrayList<DocEntity> processQuery(String s) throws Exception {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(IndexCreator.indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);

		Query q = new QueryParser(Version.LUCENE_42, "content", IndexCreator.analyzer).parse(s);
		TopDocs topDocs = searcher.search(q, 100);

		ScoreDoc[] hits = topDocs.scoreDocs;

		System.out.println("Found " + hits.length + " hits.");
		ArrayList<DocEntity> drList = new ArrayList<DocEntity>();
		HashSet<String> urlMap = new HashSet<>();

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			if (urlMap.contains(d.get("url")))
				continue;
			urlMap.add(d.get("url"));
			System.out.println(d.get("filename") + " - " + (i + 1) + ". " + " score=" + hits[i].score);

			String content = d.get("content");

			String[] strs = content.split(" ");
			StringBuilder build = new StringBuilder();
			s = s.replaceAll("AND", "");

			for (int j = 0; j < strs.length-4; j++) {
				for (String term : s.split(" ")) {
					if (term.equalsIgnoreCase(strs[j])) {
						if (j > 4 && j < strs.length - 4) {
							build.append("" + strs[j - 4] + " " + strs[j - 3] + " " + strs[j - 2] + " " + strs[j - 1]
									+ " " + strs[j] + " ");
							build.append(strs[j + 1] + " " + strs[j + 2] + " " + strs[j + 3] + " " + strs[j + 4] + " ");
							break;
						} else if (j < 4) {
							build.append(" " + strs[j] + " ");
							build.append(strs[j + 1] + " " + strs[j + 2] + " " + strs[j + 3] + " " + strs[j + 4] + " ");
							break;
						} else if (j > strs.length - 4) {
							build.append(" " + strs[j - 4] + " " + strs[j - 3] + " " + strs[j - 2] + " " + strs[j - 1]
									+ " " + strs[j] + " ");
							break;
						}
					}
				}
				if (build.length() > 100) {
					break;
				}
			}

			DocEntity dr = new DocEntity();
			dr.setContents(build.toString());
			dr.setTitle(d.get("title"));
			dr.setUrl(d.get("url"));
			dr.setHitScore(hits[i].score);
			drList.add(dr);
		}
		return drList;
	}

}