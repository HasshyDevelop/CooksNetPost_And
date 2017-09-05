package com.cooksnet.obj;

public class ImageTextItem {

	protected Integer imageResource;
	protected String text = "";

	public ImageTextItem() {
	}

	public ImageTextItem(Integer imageResource, String text) {
		this.imageResource = imageResource;
		this.text = text;
	}

	public Integer getImageResource() {
		return imageResource;
	}

	public void setImageResource(Integer imageResource) {
		this.imageResource = imageResource;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
