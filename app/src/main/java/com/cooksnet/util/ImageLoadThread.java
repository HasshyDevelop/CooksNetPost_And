package com.cooksnet.util;

import java.io.IOException;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cooksnet.util.ImageLoadCache.OnImageLoadingDoneListener;

public class ImageLoadThread extends AsyncTask<String, Void, String> implements OnImageLoadingDoneListener {
	public static int displayWidth = 0;
	private boolean successNetwork = true;
	private String errCode;
	private Bitmap bitmap;

	private Handler handler;
	private ArrayAdapter adapter;
	private ImageView view;
	private boolean gone;
	private boolean square;
	private boolean deform;
	private float per;
	private int margin;

	public ImageLoadThread(Handler handler, ImageView view, boolean gone) {
		this.handler = handler;
		this.view = view;
		this.gone = gone;
	}

	public ImageLoadThread(Handler handler, ImageView view, boolean gone, boolean square) {
		this(handler, view, gone);
		this.square = square;
	}

	public ImageLoadThread(Handler handler, ArrayAdapter adapter, ImageView view, boolean gone, boolean square) {
		this(handler, view, gone);
		this.adapter = adapter;
		this.square = square;
	}

	public ImageLoadThread(Handler handler, ImageView view, boolean gone, float per, int margin) {
		this(handler, view, gone);
		this.deform = true;
		this.per = per;
		this.margin = margin;
	}

	public String getErrCode() {
		return errCode;
	}

	public boolean isSuccessNetwork() {
		return successNetwork;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	protected void onPreExecute() {
	}

	protected String doInBackground(String... url) {
		try {
			Bitmap b = ImageLoadCache.getImage(url[0]);
			if (null == b) {
				if (ImageLoadCache.canStartLoading(url[0])) {
					bitmap = new CooksNetWebAccess().getBitmapFromURL(url[0]);
					if (square && null != bitmap) {
						int sa = (bitmap.getWidth() - bitmap.getHeight()) / 2;
						bitmap = Bitmap.createBitmap(bitmap, sa, 0, bitmap.getWidth() - sa - sa, bitmap.getHeight());
					}
					ImageLoadCache.putImage(url[0], bitmap);
				} else {
					ImageLoadCache.addListener(this, url[0]);
				}
			} else {
				bitmap = b;
			}
			return "success";
		} catch (IOException ioe) {
			successNetwork = false;
			if (ioe.getMessage().startsWith(CooksNetWebAccess.SERVER_ERROR)) {
				return ioe.getMessage().replaceAll(CooksNetWebAccess.SERVER_ERROR, "");
			}
			return "";
		}
	}

	protected void onPostExecute(final String res) {
		handler.post(new Runnable() {
			public void run() {
				if (successNetwork) {
					if (null != bitmap) {
						if (null != adapter) {
							adapter.notifyDataSetChanged();
						} else {
							view.setImageBitmap(bitmap);
							view.setVisibility(View.VISIBLE);
						}
						if (deform) {
							int width = (int) (displayWidth * per) - margin;
							int height = bitmap.getHeight() * width / bitmap.getWidth();
							view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
							view.invalidate();
						}
					}
				}
				if (!successNetwork) {
					errCode = res;
				}
			}
		});
	}

	public void onLoadingDone(final String key) {
		handler.post(new Runnable() {
			public void run() {
				bitmap = ImageLoadCache.getImage(key);
				if (null != bitmap) {
					if (null != adapter) {
						adapter.notifyDataSetChanged();
					} else {
						view.setImageBitmap(bitmap);
						view.setVisibility(View.VISIBLE);
					}
					if (deform) {
						int width = (int) (displayWidth * per) - margin;
						int height = bitmap.getHeight() * width / bitmap.getWidth();
						view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
						view.invalidate();
					}
				} else {
					if (gone) {
						view.setVisibility(View.GONE);
					}
				}
			}
		});
	}

}
