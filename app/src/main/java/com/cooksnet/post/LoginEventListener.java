package com.cooksnet.post;

public interface LoginEventListener {

	public void doAdditionalTask();
	public void loginSuccessed(LoginEvent le);
	public void loginFailed(LoginEvent le);

}
