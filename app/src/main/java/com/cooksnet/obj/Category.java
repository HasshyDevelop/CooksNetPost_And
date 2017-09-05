package com.cooksnet.obj;

import java.util.ArrayList;
import java.util.List;

public class Category extends ImageTextItem {

	public final static Category MORE = new Category();
	public final static String MORE_ID = "moremoremore";
	public String id = "";
	public String name = "";
	public boolean hidden = false;
	public int childCount = 0;
	public int recipeCount = 0;
	public int page = 0;
	public int pages = 0;
	public List<Category> children = new ArrayList<Category>();
	public List<ResultItem> recipes = new ArrayList<ResultItem>();

	static {
		MORE.id = MORE_ID;
	}

	public Category() {
		super();
	}

	public Integer getImageResource() {
		return imageResource;
	}

	public void setImageResource(Integer imageResource) {
		this.imageResource = imageResource;
	}

	public String getText() {
		return name;
	}

	public void setText(String text) {
		this.name = text;
	}

}