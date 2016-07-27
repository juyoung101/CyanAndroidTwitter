package com.juyo.us.T_omega_itter;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterApplication extends Application {

	protected static String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	protected static String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	protected static String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
	protected static String PREF_KEY_ARE_PREFS_INITED = "are_prefs_initialized";
	protected static String PREF_USER_NAME = "twitter_user_name";
	protected static String PREF_KEY_TEST_TWEET_NUMBER = "number_of_test_tweets";
	protected static String PREF_KEY_USER_ICON = "ref_of_user_icon";
	protected static String ACCESS_TOKEN_ID = "bundle_id_of_access_token";

	/* Any number for uniquely distinguish your request */
	public static final int WEBVIEW_REQUEST_CODE = 100;
	public static final int LOGIN_REQUEST_CODE = 200;
	
	protected Twitter twitter = null;
	protected RequestToken requestToken = null;
	protected AccessToken accessToken = null;
	protected static SharedPreferences mSharedPreferences = null;
	protected User user = null;
	protected User userToQuery = null;
	protected Paging page = null;
	
	protected int timelineType = -1;
	
	protected String consumerKey = null;
	protected String consumerSecret = null;
	protected String callbackUrl = null;
	protected String oAuthVerifier = null;
	
//	protected String getPREF_NAME() {return this.PREF_NAME;}
//	protected String getPREF_KEY_OAUTH_TOKEN() {return this.PREF_KEY_OAUTH_TOKEN;}
//	protected String getPREF_KEY_OAUTH_SECRET() {return this.PREF_KEY_OAUTH_SECRET;}
//	protected String getPREF_KEY_TWITTER_LOGIN() {return this.PREF_KEY_TWITTER_LOGIN;}
//	protected String getPREF_USER_NAME() {return this.PREF_USER_NAME;}
	
	protected String getConsumerKey() {return this.consumerKey;}
	protected String getConsumerSecret() {return this.consumerSecret;}
	protected String getCallbackUrl() {return this.callbackUrl;}
	protected String getOAuthVerifier() {return this.oAuthVerifier;}
	protected Twitter getTwitter() {return this.twitter;}
	protected RequestToken getRequestToken() {return this.requestToken;}
	protected AccessToken getAccessToken() {return this.accessToken;}
	protected User getAppUser() {return this.user;}
	protected User getUserToQuery() {return this.userToQuery;}
	protected Paging getPage() {return this.page;}
	protected int getTimelineType() {return this.timelineType;}
	
	protected boolean setConsumerKey(String x) {
		this.consumerKey = x;
		return (TextUtils.isEmpty(this.consumerKey)? false : true);
	}
	protected boolean setConsumerSecret(String x) {
		this.consumerSecret = x;
		return (TextUtils.isEmpty(this.consumerSecret)? false : true);
	}
	protected boolean setCallbackUrl(String x) {
		this.callbackUrl = x;
		return (TextUtils.isEmpty(this.callbackUrl)? false : true);
	}
	protected boolean setOAuthVerifier(String x) {
		this.oAuthVerifier = x;
		return (TextUtils.isEmpty(this.oAuthVerifier)? false : true);
	}
	protected boolean setTwitter(Twitter x) {
		this.twitter = x;
		return ((this.twitter == null)? false : true);
	}
	protected boolean setRequestToken(RequestToken x) {
		this.requestToken = x;
		return ((this.requestToken == null)? false : true);
	}
	protected boolean setAccessToken(AccessToken x) {
		this.accessToken = x;
		return ((this.accessToken == null)? false : true);
	}
	protected boolean setAppUser(User x) {
		this.user = x;
		return ((this.user == null)? false : true);
	}
	protected boolean setUserToQuery(User x) {
		this.userToQuery = x;
		return ((this.userToQuery == null)? false : true);
	}
	protected boolean setPaging(Paging x) {
		this.page = x;
		return ((this.user == null)? false : true);
	}
	protected boolean setTimelineType(int x) {
		this.timelineType = x;
		return ((this.timelineType == -1)? false : true);
	}
	
	
	protected Boolean favoriteTweet(Status x) {
		try {
			this.twitter.createFavorite(x.getId());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	protected Boolean retweetTweet(Status x) {
		try {
			this.twitter.retweetStatus(x.getId());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	protected Paging prevPage(Paging x) {
		if(x.getPage() > 1) {
			return x;
		}
		x.setPage(x.getPage()-1);
		return x;
	}
	protected Paging nextPage(Paging x) {
		x.setPage(x.getPage()+1);
		return x;
	}
	
	
}
