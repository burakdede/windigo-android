package com.windigo.sample.lastfm;

import java.util.List;

public class Album {

	private String name;
	private String artist;
	private String id;
	private String mbid;
	private String url;
	private List<ImageResponse> image;
	private WikiResponse wiki;
	public Album() {
	
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMbid() {
		return mbid;
	}
	public void setMbid(String mbid) {
		this.mbid = mbid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<ImageResponse> getImage() {
		return image;
	}
	public void setImage(List<ImageResponse> image) {
		this.image = image;
	}
	public WikiResponse getWiki() {
		return wiki;
	}
	public void setWiki(WikiResponse wiki) {
		this.wiki = wiki;
	}
	
	
}
