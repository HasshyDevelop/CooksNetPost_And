package com.cooksnet.obj;

import java.util.Locale;

public class Profile {

	public static final String LOGIN_SUCCESS = "login=succeeded";
	public static final String LOGIN_NOT_PROFILED = "login=notprofiled";
	public static final String LOGIN_FAILED = "login=failed";

	public String loginState = "";

	public String nickname = "";
	public String password = "";
	public String id = "";
	public String name = "";
	public String lang = "";
	public Locale locale = Locale.getDefault();

	public Profile() {
	}

}