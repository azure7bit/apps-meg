package com.database.meggnify.meggnet_users;

public class Meggnet {
	private int id;
	private String nric;
	private String email;
	private String auth_token;
	
	public int getId(){
		return id;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public String getNRIC(){
		return nric;
	}
	
	public void setNRIC(String nric){
		this.nric = nric;
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getToken(){
		return auth_token;
	}
	
	public void setToken(String auth_token){
		this.auth_token = auth_token;
	}

}
