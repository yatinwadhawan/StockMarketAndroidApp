package com.example.stockmarket;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class WebBrowserActivity extends Activity {

	private WebView webview;
	private ProgressBar progressBar;
	private FrameLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.web);

		layout = (FrameLayout) findViewById(R.id.framelayout);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		webview = (WebView) findViewById(R.id.webView1);
		String url = getIntent().getExtras().getString("url");
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				layout.removeView(progressBar);
			}
		});

		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl(url);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}
}
