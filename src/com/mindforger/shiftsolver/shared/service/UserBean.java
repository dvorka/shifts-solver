package com.mindforger.shiftsolver.shared.service;

import java.io.Serializable;

public class UserBean implements Serializable {

	private String userId;
	private String nickname;
	private String description; 
	private String role; 
	private String gravatarMd5;
	
	public UserBean() {
	}
	
	public UserBean(String userId, String nickname, String web, String description, String role, String gravatarMd5) {
		super();
		this.userId = userId;
		this.nickname = nickname;
		this.description = description;
		this.role = role;
		this.gravatarMd5 = gravatarMd5;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGravatarMd5() {
		return gravatarMd5;
	}

	public void setGravatarMd5(String gravatarMd5) {
		this.gravatarMd5 = gravatarMd5;
	}
	
	private static final long serialVersionUID = 8627395180863207013L;
}
