package com.juyo.us.T_omega_itter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class ComposeActivity extends Activity implements TextWatcher {
	
	TwitterApplication mApp = null;
	Twitter twitter = null;
	User user = null;
	SeekBar mLoginSlider = null;
	SharedPreferences mSharedPrefs = null;
	String mTitle = null;
	ProgressDialog pDialog = null;
	EditText etCompose = null;
	MenuItem charCount = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		
		mApp = (TwitterApplication) this.getApplication();
		twitter = mApp.getTwitter();
		
		mTitle = "Compose";
		
		charCount = (MenuItem) findViewById(R.id.tweet_character_count);
		etCompose = (EditText) findViewById(R.id.compose_edittext);
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		
		/* Enabling strict mode */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true); //provides up stack navigation
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
		
		etCompose.setFocusable(true);
		etCompose.addTextChangedListener(this);
//		etCompose.addTextChangedListener(new TextWatcher() {
//
//	          public void afterTextChanged(Editable s) {
//	        	  String length = null;
//	        	  int chars = s.length();
//	        	  if(charCount != null && chars != 0) {
//	        		  length = ((Integer)((140) - chars)).toString();
//	        	  }
//	        	  if(length == null) {
//	        		  length = "";
//	        	  }
//	        	  getActionBar().setTitle(length);
//	          }
//
//	          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//	        	  String length = null;
//	        	  int chars = s.length();
//	        	  if(charCount != null && chars != 0) {
//	        		  length = ((Integer)((140) - chars)).toString();
//	        	  }
//	        	  if(length == null) {
//	        		  length = "";
//	        	  }
//	        	  getActionBar().setTitle(length);
//	          }
//
//	          public void onTextChanged(CharSequence s, int start, int before, int count) {}
//	       });
		
	}
	
	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		
		int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		if (actionBarTitleId > 0) {
		    TextView title = (TextView) findViewById(actionBarTitleId);
		    if (title != null) {
		        title.setTextColor(Color.parseColor("#00CCDD"));
		    }
		}
		
		ColorDrawable cd = new ColorDrawable(getResources().getColor(android.R.color.transparent)); 
			cd.setBounds(0,0,0,0); 
		actionBar.setIcon(cd);
		
		if(((mSharedPrefs.getString(TwitterApplication.PREF_USER_NAME, "Welcome")).toString()).equals("Welcome".toString()) == false) {
			actionBar.setTitle(mSharedPrefs.getString(TwitterApplication.PREF_USER_NAME, "Welcome").toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.compose, menu);
			restoreActionBar();
			return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_send) {
			sendTweet();
			return true;
		} else if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void sendTweet() {
		String status;
		
		if(etCompose != null) {
			status = etCompose.getText().toString();
		} else {
			status = "";
		}
		
		if (status.trim().length() > 0) {
			new updateTwitterStatus().execute(status);
			NavUtils.navigateUpFromSameTask(this);
		} else {
			Toast.makeText(this, "Message is empty!!", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	class updateTwitterStatus extends AsyncTask<String, String, Void> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pDialog = new ProgressDialog(ComposeActivity.this);
			pDialog.setMessage("Posting to twitter...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected Void doInBackground(String... args) {
			mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String status = args[0];
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(mApp.getConsumerKey());
				builder.setOAuthConsumerSecret(mApp.getConsumerSecret());
				
				// Access Token
				String access_token = mSharedPrefs.getString(TwitterApplication.PREF_KEY_OAUTH_TOKEN, null);
				// Access Token Secret
				String access_token_secret = mSharedPrefs.getString(TwitterApplication.PREF_KEY_OAUTH_SECRET, null);

				AccessToken accessToken = new AccessToken(access_token, access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

				// Update status
				StatusUpdate statusUpdate = new StatusUpdate(status);
				//InputStream is = getResources().openRawResource(R.drawable.ic_launcher);
				//statusUpdate.setMedia("test.jpg", is);
				
				twitter4j.Status response = twitter.updateStatus(statusUpdate);

				Log.d("Status", response.getText());
				
			} catch (TwitterException e) {
				Log.d("Failed to post!", e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			/* Dismiss the progress dialog after sharing */
			pDialog.dismiss();
			Toast.makeText(ComposeActivity.this, "Sent to Twitter!", Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		//Toast.makeText(mApp, "onTextChanged", Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		//Toast.makeText(mApp, "afterTextChanged", Toast.LENGTH_LONG).show();
	}
	
}
