package com.juyo.us.T_omega_itter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class LoginActivity extends Activity implements OnSeekBarChangeListener {
	
	TwitterApplication mApp = null;
	Twitter twitter = null;
	User user = null;
	SeekBar mLoginSlider = null;
	SharedPreferences mSharedPrefs = null;
	
	public static String LOGIN_SUCCESSFUL = "was_login_activity_successful";
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mApp = (TwitterApplication) this.getApplication();
		twitter = mApp.getTwitter();
				
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		mLoginSlider = (SeekBar) findViewById(R.id.loginSlider);
			mLoginSlider.setOnSeekBarChangeListener(this);
		
		/* Enabling strict mode */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
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
		
	}
	
	/**
	 * Saving user information, after user is authenticated for the first time.
	 * You don't need to show user to login, until user has a valid access token
	 */
	private void saveTwitterInfo(AccessToken accessToken) {
		
		long userID = accessToken.getUserId();
		
		User user;

		mApp.setAccessToken(accessToken);
		try {
			user = this.twitter.showUser(userID);
			mApp.setAppUser(user);
		
			String username = user.getName();
			

			/* Storing oAuth tokens to shared preferences */
			
			SharedPreferences.Editor mSharedPrefEditor = this.mSharedPrefs.edit();
				mSharedPrefEditor.putString(TwitterApplication.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
				mSharedPrefEditor.putString(TwitterApplication.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
				mSharedPrefEditor.putBoolean(TwitterApplication.PREF_KEY_TWITTER_LOGIN, true);
				mSharedPrefEditor.putString(TwitterApplication.PREF_USER_NAME, username);
				mSharedPrefEditor.putInt(TwitterApplication.PREF_KEY_TEST_TWEET_NUMBER, 1);
				//mSharedPrefEditor.putString(TwitterApplication.PREF_KEY_USER_ICON, iconReference);
				mSharedPrefEditor.commit();
//			setUserIcon(user.getOriginalProfileImageURL());
				
		} catch (TwitterException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			String verifier = data.getExtras().getString(mApp.getOAuthVerifier());
			try {
				AccessToken accessToken = twitter.getOAuthAccessToken(mApp.getRequestToken(), verifier);
				saveTwitterInfo(accessToken);
			} catch (Exception e) {
				Log.e("Twitter Login Failed", e.getMessage());
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra(LoginActivity.LOGIN_SUCCESSFUL, true);
		setResult(RESULT_OK, resultIntent);
		
		finish();
	}
	
	private void loginToTwitter() {
		boolean isLoggedIn = mSharedPrefs.getBoolean(TwitterApplication.PREF_KEY_TWITTER_LOGIN, false);
		
		//if (!isLoggedIn) {
		if(true) {
			final ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(mApp.getConsumerKey());
			builder.setOAuthConsumerSecret(mApp.getConsumerSecret());

			final Configuration configuration = builder.build();
			final TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();
			mApp.setTwitter(twitter);

			try {
				mApp.setRequestToken(twitter.getOAuthRequestToken(mApp.getCallbackUrl()));

				/**
				 *  Loading twitter login page on webview for authorization 
				 *  Once authorized, results are received at onActivityResult
				 *  */
				final Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra(WebViewActivity.EXTRA_URL, mApp.getRequestToken().getAuthenticationURL());
				startActivityForResult(intent, TwitterApplication.WEBVIEW_REQUEST_CODE);
				
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			
			Toast.makeText(LoginActivity.this, "Already logged into Twitter", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		loginToTwitter();
	}

}
