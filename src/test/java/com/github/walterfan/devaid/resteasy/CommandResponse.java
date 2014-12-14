package com.github.walterfan.devaid.resteasy;

public class CommandResponse {

	public static final int OK = 200;
	
	private int responseCode;
	private String description;
	
	

	public CommandResponse(int responseCode, String description) {
		super();
		this.responseCode = responseCode;
		this.description = description;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
