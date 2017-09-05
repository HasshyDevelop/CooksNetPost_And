package com.cooksnet.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cooksnet.obj.Recipe;
import com.cooksnet.obj.ResultItem;

public class RecipeCache {

	public final static int MAX_HISTORY = 10;

	private static HashMap<String, Recipe> cache = new HashMap<String, Recipe>();
	private static List<ResultItem> history = new ArrayList<ResultItem>();
	private static List<ResultItem> favorite = new ArrayList<ResultItem>();

	public static Recipe getRecipe(String key) {
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		return null;
	}

	public static List<ResultItem> getHistory() {
		return history;
	}

	public static List<ResultItem> getFavorite() {
		return favorite;
	}

	public static void lookedRecipe(Recipe recipe) {
		if (recipe.isFavorite) {
			return;
		}
		ResultItem item = new ResultItem();
		item.id = recipe.id;
		item.title = recipe.title;
		item.description = recipe.description;
		item.photoUrl = recipe.photoUrl;
		item.photo = recipe.photo;
		item.profileName = recipe.profileName;

		for (ResultItem i : history) {
			if (i.id.equals(item.id)) {
				history.remove(i);
				break;
			}
		}
		history.add(item);
		if (MAX_HISTORY < history.size()) {
			history.remove(0);
		}
	}

	public static void putRecipe(String key, Recipe recipe) {
		cache.put(key, recipe);
	}

	public static void removeRecipe(String key) {
		for (ResultItem i : history) {
			if (i.id.equals(key)) {
				history.remove(i);
				break;
			}
		}
		cache.remove(key);
	}

	public static void putFavoriteRecipe(String key, Recipe recipe) {
		if (key.equals(containsFavoriteRecipe(recipe.id))) {
			return;
		}
		cache.put(key, recipe);
		ResultItem item = new ResultItem();
		item.id = key;
		item.title = recipe.title;
		item.description = recipe.description;
		item.photoUrl = recipe.photoUrl;
		item.photo = recipe.photo;
		item.profileName = recipe.profileName;
		favorite.add(item);
	}

	public static void removeFavoriteRecipe(String key) {
		for (ResultItem i : favorite) {
			if (i.id.equals(key)) {
				favorite.remove(i);
				break;
			}
		}
		cache.remove(key);
	}

	public static String containsFavoriteRecipe(String recipeId) {
		for (ResultItem i : favorite) {
			Recipe recipe = cache.get(i.id);
			if (recipe.id.equals(recipeId)) {
				return i.id;
			}
		}
		return null;
	}

	public static void clear() {
		history.clear();
		favorite.clear();
		cache.clear();
	}
}