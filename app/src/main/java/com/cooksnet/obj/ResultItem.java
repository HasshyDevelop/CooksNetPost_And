package com.cooksnet.obj;

import android.graphics.Bitmap;

public class ResultItem {

	public final static ResultItem MORE = new ResultItem();
	public final static String MORE_ID = "moremoremore";
	public String id = "";
	public String title = "";
	public String profileName = "";
	public String by = "";
	public String description = "";
	public String photoUrl = "";
	public Bitmap photo;

	static {
		MORE.id = MORE_ID;
	}

	public ResultItem() {
	}

}