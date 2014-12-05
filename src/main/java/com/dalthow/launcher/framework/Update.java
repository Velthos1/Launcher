package com.dalthow.launcher.framework;

public class Update {
	private boolean latest;
	private boolean resetRequired;
	private String version;
	private String branch;
	private String updateLink;
	private String changelogLink;
	
	public Update(String version, String branch, String updateLink, String changelogLink, boolean latest, boolean resetRequired){
		this.version=version;
		this.branch=branch;
		this.updateLink=updateLink;
		this.changelogLink=changelogLink;
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
	public String getChangelogLink() {
		return changelogLink;
	}
	public void setChangelogLink(String changelogLink) {
		this.changelogLink = changelogLink;
	}
	
}
