package com.juyo.us.T_omega_itter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class WebViewActivity extends Activity {
	
	protected WebViewActivity thisAct;
	protected WebView webView;
	
	public static String EXTRA_URL = "extra_url";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		thisAct = this;
		CookieSyncManager.createInstance(this);
		
		setTitle("Login");
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		
		ColorDrawable cd = new ColorDrawable(getResources().getColor(android.R.color.transparent)); 
			cd.setBounds(0,0,0,0); 
		actionBar.setIcon(cd);
		
		int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		if (actionBarTitleId > 0) {
		    TextView title = (TextView) findViewById(actionBarTitleId);
		    if (title != null) {
		        title.setTextColor(Color.parseColor("#00CCDD"));
		    }
		}

		final String url = this.getIntent().getStringExtra(EXTRA_URL);
		if (url == null) {
			Log.e("Twitter", "URL cannot be null");
			finish();
		}

		webView = null;
		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new MyWebViewClient());
		this.clearThisWebViewsCache();
		
		//TODO indev commented out for faster login
//		CookieManager cookieManager = CookieManager.getInstance();        
//		cookieManager.removeAllCookie();
		 
		webView.loadUrl(url);
	}

	protected void clearThisWebViewsCache() {
		this.webView.clearCache(true);
		this.webView.clearHistory();
		this.webView.clearFormData();
		this.webView.clearCache(false);
	}

	class MyWebViewClient extends WebViewClient {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (url.contains(getResources().getString(R.string.twitter_callback))) {
				Uri uri = Uri.parse(url);
				
				/* Sending results back */
				String verifier = uri.getQueryParameter(getString(R.string.twitter_oauth_verifier));
				Intent resultIntent = new Intent();
				resultIntent.putExtra(getString(R.string.twitter_oauth_verifier), verifier);
				setResult(RESULT_OK, resultIntent);
				
				/* closing webview */
				thisAct.clearThisWebViewsCache();//remove webview cache so users can logout. Also security.
				
				finish();
				return true;
			}
			return false;
		}
	}
}