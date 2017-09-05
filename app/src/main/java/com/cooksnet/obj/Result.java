package com.cooksnet.obj;

import java.util.ArrayList;
import java.util.List;


public class Result {

	
	public int result = 0;

	// for search
	public int from = 0;
	public int size = 0;

	// for myrecipe
	public int page = 0;
	public int pages = 0;

	public List<ResultItem> items = new ArrayList<ResultItem>();

	public Result() {
	}

}