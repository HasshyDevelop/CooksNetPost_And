package com.cooksnet.post;

import java.util.EventObject;

import com.cooksnet.obj.Profile;

public class LoginEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private final String nickname;
	private final String password;
	private final Profile profile;
	private final boolean successNetwork;
	private final String errorCode;

	public LoginEvent(Object source, String nickname, String password, Profile profile, boolean successNetwork,
			String errorCode) {
		super(source);
		this.nickname = nickname;
		this.password = password;
		this.profile = profile;
		this.successNetwork = successNetwork;
		this.errorCode = errorCode;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPassword() {
		return password;
	}

	public Profile getProfile() {
		return profile;
	}

	public boolean isSuccessNetwork() {
		return successNetwork;
	}

	public String getErrorCode() {
		return errorCode;
	}

}
