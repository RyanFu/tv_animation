package com.jumplife.tvanimation.entity;

public class Animate{
	private int id;
	private String name;
	private String season;
	private String coverPosterUrl;
	private String introPosterUrl;
	private String introduction;
	private String actors;
	private String epsNumStr;
	private int views;
	private int areaId;
	
	public Animate() {
		this(-1, "", "", "", "", "", "", "", 0, 0);
	}
	
	public Animate (int id, String name, String season, String coverPosterUrl, String introPosterUrl, String introduction, 
			String actors, String epsNumStr, int views, int areaId) {
		this.id = id;
		this.name = name;
		this.season = season;
		this.coverPosterUrl = coverPosterUrl;
		this.introPosterUrl = introPosterUrl;
		this.introduction = introduction;
		this.actors = actors;
		this.epsNumStr = epsNumStr;
		this.views = views;
		this.areaId = areaId;
	}
	
	
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getSeason(){
		return season;
	}
	public void setSeason(String season){
		this.season = season;
	}
	public String getCoverPosterUrl(){
		return coverPosterUrl;
	}
	public void setCoverPosterUrl(String coverPosterUrl){
		this.coverPosterUrl = coverPosterUrl;
	}
	public String getIntroPosterUrl(){
		return introPosterUrl;
	}
	public void setIntroPosterUrl(String introPosterUrl){
		this.introPosterUrl = introPosterUrl;
	}
	public String getIntroduction(){
		return introduction;
	}
	public void setIntroduction(String introduction){
		this.introduction = introduction;
	}	
	public String getActors(){
		return this.actors;
	}
	public void setActors(String actors){
		this.actors = actors;
	}
	public String getEpsNumStr(){
		return epsNumStr;
	}
	public void setEpsNumStr(String epsNumStr){
		this.epsNumStr = epsNumStr;
	}
	public int getViews(){
		return views;
	}
	public void setViews(int views){
		this.views = views;
	}
	public int getAreId(){
		return areaId;
	}
	public void setAreId(int areaId){
		this.areaId = areaId;
	}
}
