package com.cooksnet.post;

import java.io.IOException;
import java.util.Locale;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

public class AboutActivity extends BaseActivity {

//	private Handler handler;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);

		setTitle(R.string.menu_more_about);

//		handler = new Handler();

		WebView webView = ((WebView) findViewById(R.id.webViewAbout));
		webView.setBackgroundColor(0);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//		webView.setVerticalScrollbarOverlay(true);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		// TODO 1.5未対応
		// webView.setScrollbarFadingEnabled(false);
		webView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		});
		try {

			if ("zh".equals(Locale.getDefault().getLanguage())) {
				if ("CN".equals(Locale.getDefault().getCountry()) ||
						"SG".equals(Locale.getDefault().getCountry())) {
					getAssets().open("zh-CN/About.html");
					webView.loadUrl("file:///android_asset/zh-CN/About.html");
				} else {
					getAssets().open("zh-TW/About.html");
					webView.loadUrl("file:///android_asset/zh-TW/About.html");
				}
			} else {
				getAssets().open(Locale.getDefault().getLanguage() + "/About.html");
				webView.loadUrl("file:///android_asset/" + Locale.getDefault().getLanguage() + "/About.html");
			}
		} catch (Resources.NotFoundException rnfe) {
			webView.loadUrl("file:///android_asset/en/About.html");
		} catch (IOException ioe) {
			webView.loadUrl("file:///android_asset/en/About.html");
		}
		webView.setFocusableInTouchMode(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return false;
	}

}
