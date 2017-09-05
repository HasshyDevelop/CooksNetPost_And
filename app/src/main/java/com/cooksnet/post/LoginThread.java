package com.cooksnet.post;

import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.cooksnet.obj.Profile;
import com.cooksnet.util.CooksNetWebAccess;

public class LoginThread extends AsyncTask<String, Void, Boolean> {

	private LoginEventListener listener;
	private String nickname;
	private String password;

	public LoginThread(LoginEventListener listener, String nickname, String password) {
		this.listener = listener;
		this.nickname = nickname;
		this.password = password;
	}

	protected Boolean doInBackground(String... s) {
		Profile profile = null;
		try {
			profile = new CooksNetWebAccess().login(nickname, password);
			if (Profile.LOGIN_SUCCESS.equals(profile.loginState)) {
				ExtrasData.profile = profile;
			}
		} catch (IOException ioe) {
			if (ioe.getMessage().startsWith(CooksNetWebAccess.SERVER_ERROR)) {
				listener.loginFailed(new LoginEvent(this, nickname, password, profile, false, ioe.getMessage()
						.replaceAll(CooksNetWebAccess.SERVER_ERROR, "")));
			} else {
				listener.loginFailed(new LoginEvent(this, nickname, password, profile, false, ""));
			}
			return new Boolean(false);
		}
		listener.doAdditionalTask();
		if (!Profile.LOGIN_SUCCESS.equals(profile.loginState)) {
			listener.loginFailed(new LoginEvent(this, nickname, password, profile, true, ""));
			return new Boolean(false);
		} else {
			ExtrasData.profile = profile;
			listener.loginSuccessed(new LoginEvent(this, nickname, password, profile, true, ""));
			return new Boolean(true);
		}
	}
}
