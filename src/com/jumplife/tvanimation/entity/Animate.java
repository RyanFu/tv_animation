package com.jumplife.tvanimation.entity;

public class Animate{
	private int id;
	private String name;
	private String season;
	private String posterUrl;
	private String introduction;
	private String epsNumStr;
	private int views;
	private int typeId;
	
	public Animate() {
		this(-1, "", "", "", "", "", 0, 0);
	}
	
	public Animate (int id, String name, String season, String posterUrl, String introduction, 
			String epsNumStr, int views, int typeId) {
		this.id = id;
		this.name = name;
		this.season = season;
		this.posterUrl = posterUrl;
		this.introduction = introduction;
		this.epsNumStr = epsNumStr;
		this.views = views;
		this.typeId = typeId;
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
	public String getPosterUrl(){
		return posterUrl;
	}
	public void setPosterUrl(String posterUrl){
		this.posterUrl = posterUrl;
	}
	public String getIntroduction(){
		return introduction;
	}
	public void setIntroduction(String introduction){
		this.introduction = introduction;
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
	public int getTypeId(){
		return typeId;
	}
	public void setTypeId(int typeId){
		this.typeId = typeId;
	}
}
