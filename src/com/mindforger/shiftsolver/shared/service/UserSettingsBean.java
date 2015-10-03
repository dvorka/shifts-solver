package com.mindforger.shiftsolver.shared.service;

import java.io.Serializable;

public class UserSettingsBean implements Serializable {
		
	private String perspective;
	private boolean sendFriendRequestEmails;
	
	public UserSettingsBean() {
	}

	public UserSettingsBean(String perspective, boolean sendFriendRequestEmails) {
		super();
		this.perspective = perspective;
		this.sendFriendRequestEmails = sendFriendRequestEmails;
	}

	public boolean isSendFriendRequestEmails() {
		return sendFriendRequestEmails;
	}

	public void setSendFriendRequestEmails(boolean sendFriendRequestEmails) {
		this.sendFriendRequestEmails = sendFriendRequestEmails;
	}

	public String getPerspective() {
		return perspective;
	}

	public void setPerspective(String perspective) {
		this.perspective = perspective;
	}

	private static final long serialVersionUID = -9116380662640943067L;
}
