package com.cooksnet.post;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HtmlWebViewActivity extends BaseActivity {

    private Handler handler;

    private ProgressDialogFragment dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.htmlwebview);

        handler = new Handler();

        setTitle(getIntent().getStringExtra("title"));

        final WebView webView = ((WebView) findViewById(R.id.webViewHtml));
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        webView.setVerticalScrollbarOverlay(true);

        webView.setWebChromeClient(new WebChromeClient() {
            boolean isLoading = false;

            public void onProgressChanged(WebView view, int progress) {
                HtmlWebViewActivity.this.setProgress(progress * 1000);
                if (progress > 0 && !isLoading) {
                    isLoading = true;
                    handler.post(new Runnable() {
                        public void run() {
//							dialog = ProgressDialog.show(HtmlWebViewActivity.this, "",
//									getText(R.string.dialog_progress).toString(), true);
                            dialog = ProgressDialogFragment.newInstance("", getString(R.string.dialog_progress));
                            dialog.show(getSupportFragmentManager(), "CreateThread");
                        }
                    });
                }
                if (isLoading && progress == 100) {
                    handler.post(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

//		webView.setHttpAuthUsernamePassword("www.cooksnet.com", "secret", "admin", "");
//		webView.setWebViewClient(new WebViewClient() {
//			private String loginCookie;
//
//			@Override
//			public void onLoadResource(WebView view, String url) {
//				CookieManager cookieManager = CookieManager.getInstance();
//				loginCookie = cookieManager.getCookie(url);
//			}
//
//			@Override
//			public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
//				webView.setHttpAuthUsernamePassword("www.cooksnet.com", "secret", "admin", "");
//				handler.proceed("admin", "");
//				// String[] up = view.getHttpAuthUsernamePassword(host, realm);
//				// if (up != null && up.length == 2) {
//				// }
//			}
//
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				CookieManager cookieManager = CookieManager.getInstance();
//				cookieManager.setCookie(url, loginCookie);
//			}
//		});

        if (Build.VERSION.RELEASE.startsWith("4") || Build.VERSION.RELEASE.startsWith("3")) {
            Map<String, String> extraHeaders = new HashMap<String, String>();
            extraHeaders.put("Accept-Language", Locale.getDefault().toString());
            webView.loadUrl(getIntent().getStringExtra("url"), extraHeaders);
        } else {
            webView.loadUrl(getIntent().getStringExtra("url"));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return false;
    }

}
