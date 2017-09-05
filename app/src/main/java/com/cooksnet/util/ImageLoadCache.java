package com.cooksnet.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;

public class ImageLoadCache {
	private static HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();
	private static HashMap<String, Bitmap> special = new HashMap<String, Bitmap>();
	private static List<String> loading = new ArrayList<String>();
	private static Map<String, List<OnImageLoadingDoneListener>> listeners = new HashMap<String, List<OnImageLoadingDoneListener>>();

	public static Bitmap getImage(String key) {
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		if (special.containsKey(key)) {
			return special.get(key);
		}
		return null;
	}

	public static boolean canStartLoading(String key) {
		if (cache.containsKey(key)) {
			return false;
		}
		if (loading.contains(key)) {
			return false;
		} else {
			loading.add(key);
			return true;
		}
	}

	public static void putImage(String key, Bitmap image) {
		if (!cache.containsKey(key)) {
			if (loading.size() > 100) {
				String old = loading.get(0);
				cache.remove(old);
				loading.remove(old);
			}
			cache.put(key, image);
			if (listeners.containsKey(key)) {
				synchronized (listeners) {
					for (OnImageLoadingDoneListener listener : listeners.get(key)) {
						if (null != listener) {
							listener.onLoadingDone(key);
						}
					}
				}
			}
		}
	}

	public static void putSpecialImage(String key, Bitmap image) {
		if (!cache.containsKey(key)) {
			special.put(key, image);
		}
	}

	public static void addListener(OnImageLoadingDoneListener listener, String key) {
		if (cache.containsKey(key)) {
			listener.onLoadingDone(key);
			return;
		}
		if (listeners.containsKey(key)) {
			if (!listeners.get(key).contains(listener)) {
				synchronized (listeners) {
					listeners.get(key).add(listener);
				}
			}
		} else {
			List<OnImageLoadingDoneListener> ls = new ArrayList<OnImageLoadingDoneListener>();
			synchronized (listeners) {
				listeners.put(key, ls);
			}
		}
	}

	public static void clear() {
		cache.clear();
		special.clear();
		loading.clear();
		synchronized (listeners) {
			listeners.clear();
		}
	}

	public interface OnImageLoadingDoneListener {
		abstract void onLoadingDone(String key);
	}
}