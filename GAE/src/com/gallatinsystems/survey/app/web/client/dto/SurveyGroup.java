package com.gallatinsystems.survey.app.web.client.dto;

import java.io.Serializable;
import java.util.Date;


public class SurveyGroup implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2235565143615667202L;
	/**
	 * 
	 */
	private Long keyId;
	public Long getKeyId() {
		return keyId;
	}
	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}
	private String description;
	private String code;
	private Date createdDateTime;
	private Date lastUpdateDateTime;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public Date getLastUpdateDateTime() {
		return lastUpdateDateTime;
	}
	public void setLastUpdateDateTime(Date lastUpdateDateTime) {
		this.lastUpdateDateTime = lastUpdateDateTime;
	}
	
}
