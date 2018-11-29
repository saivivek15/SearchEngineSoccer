package org.soccer.indexing;

public class DocEntity {

	private String contents;
	private String url;
	private String filename;
	private float hitScore;
	private int clusterId;
	private float rankScore;
	public String getContents() {
		return contents;
	}
	public String getUrl() {
		return url;
	}
	public String getFilename() {
		return filename;
	}
	public float getHitScore() {
		return hitScore;
	}
	public int getClusterId() {
		return clusterId;
	}
	public float getRankScore() {
		return rankScore;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public void setHitScore(float hitScore) {
		this.hitScore = hitScore;
	}
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}
	public void setRankScore(float rankScore) {
		this.rankScore = rankScore;
	}
	
	
}