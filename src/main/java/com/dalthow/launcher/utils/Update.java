package com.dalthow.launcher.utils;

public class Update {
	private boolean latest;
	private boolean resetRequired;
	private String version;
	private String branch;
	private String updateLink;
	private String md5;
	private String game;
	
	public Update(String game, String version, String branch, String updateLink, String md5, boolean latest, boolean resetRequired){
		this.game=game;
		this.version=version;
		this.branch=branch;
		this.updateLink=updateLink;
		this.md5=md5;
		this.latest=latest;
		this.resetRequired=resetRequired;
	}
	
	public Update(){
		
	}
	
	public boolean isLatest() {
		return latest;
	}
	public void setLatest(boolean latest) {
		this.latest = latest;
	}
	public boolean isResetRequired() {
		return resetRequired;
	}
	public void setResetRequired(boolean resetRequired) {
		this.resetRequired = resetRequired;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getUpdateLink() {
		return updateLink;
	}
	public void setUpdateLink(String updateLink) {
		this.updateLink = updateLink;
	}
	public void setMD5(String md5){
		this.md5=md5;
	}
	public String getMD5(){
		return md5;
	}
	public String getGameName(){
		return game;
	}
	
}
