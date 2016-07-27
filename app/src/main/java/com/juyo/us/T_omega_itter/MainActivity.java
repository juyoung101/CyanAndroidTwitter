package com.juyo.us.T_omega_itter;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	
	TwitterApplication mApp = null;
	ProgressDialog pDialog = null;
	Twitter twitter = null;
	User user = null;
	SharedPreferences mSharedPrefs = null;
	Resources mRes = null;
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	
	private NavigationDrawerFragment mNavigationDrawerFragment;
//	mNavigationDrawerFragment.setOnClickListener

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	static String LOGTAG = "com.juyo.us.addeltle";
	
	
	@SuppressLint("NewApi") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mApp = (TwitterApplication) this.getApplication();
		mRes = this.getResources();

		if(savedInstanceState != null) {
			if(savedInstanceState.getSerializable(TwitterApplication.ACCESS_TOKEN_ID) != null) {
				mApp.setAccessToken((AccessToken) savedInstanceState.getSerializable(TwitterApplication.ACCESS_TOKEN_ID));
			}
		}
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			if(mSharedPrefs.getBoolean(TwitterApplication.PREF_KEY_ARE_PREFS_INITED, false) == false) {
				initSharedPreferences();
			}
				
		/* Enabling strict mode */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		initTwitterConfigs();
		
		/* Check if required twitter keys are set */
		if (TextUtils.isEmpty(mApp.getConsumerKey()) || TextUtils.isEmpty(mApp.getConsumerSecret())) {
			Toast.makeText(this, "Twitter key and secret not configured",
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		//mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		// Set drawer onClickListener
//		mNavigationDrawerFragment.onC
		
//		boolean isLoggedIn = mSharedPrefs.getBoolean(TwitterApplication.PREF_KEY_TWITTER_LOGIN, false);
		
		if(mApp.getAccessToken() == null) {
			launchLoginActivity();
		} else {
			//reinitTwitterObject();
			welcomeBackFunction();
			//launchLoginActivity();
		}
		
		restoreActionBar();
		
	}
	
	
	protected void reinitTwitterObject() {
		final ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(mApp.getConsumerKey());
		builder.setOAuthConsumerSecret(mApp.getConsumerSecret());

		final Configuration configuration = builder.build();
		final TwitterFactory factory = new TwitterFactory(configuration);
		twitter = factory.getInstance();
		mApp.setTwitter(twitter);
	}
	
	protected void welcomeBackFunction() {
		mTitle = mSharedPrefs.getString(TwitterApplication.PREF_USER_NAME, "Welcome");
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
//		actionBar.setIcon(mApp.getUserIcon());
//		actionBar.setIcon(userIcon);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			boolean wasSuccessful = data.getBooleanExtra(LoginActivity.LOGIN_SUCCESSFUL, false);
			
			if(wasSuccessful == true) {
					welcomeBackFunction();
			} else {
				launchLoginActivity();
			}
		}

		//super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void launchLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, TwitterApplication.LOGIN_REQUEST_CODE);
	}
	
	public void launchComposeActivity() {
		Intent intent = null;
		intent = new Intent(this, ComposeActivity.class);
		
			try {
				startActivity(intent);
			} catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	protected void logOutOfTwitter() {
		initSharedPreferences();
		initTwitterConfigs();
		launchLoginActivity();
	}
	
	private void initSharedPreferences() {
		SharedPreferences.Editor mSharedPrefEditor = this.mSharedPrefs.edit();
			mSharedPrefEditor.putString(TwitterApplication.PREF_KEY_OAUTH_TOKEN, null);
			mSharedPrefEditor.putString(TwitterApplication.PREF_KEY_OAUTH_SECRET, null);
			mSharedPrefEditor.putBoolean(TwitterApplication.PREF_KEY_TWITTER_LOGIN, false);
			mSharedPrefEditor.putString(TwitterApplication.PREF_USER_NAME, null);
			mSharedPrefEditor.putBoolean(TwitterApplication.PREF_KEY_ARE_PREFS_INITED, true);
			mSharedPrefEditor.putInt(TwitterApplication.PREF_KEY_TEST_TWEET_NUMBER, 1);
			mSharedPrefEditor.commit();
	}
	
	/* Reading twitter essential configuration parameters from strings.xml */
	private void initTwitterConfigs() {
		mApp.setConsumerKey(getString(R.string.twitter_consumer_key));
		mApp.setConsumerSecret(getString(R.string.twitter_consumer_secret));
		mApp.setCallbackUrl(getString(R.string.twitter_callback));
		mApp.setOAuthVerifier(getString(R.string.twitter_oauth_verifier));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		
		if(position == 0) { //Home
			fragmentManager.beginTransaction().replace(R.id.container, new HomeScreenFragment()).commit();
		}
		if(position == 1) { //Timeline
			mApp.setTimelineType(TimelineSimpleFragment.TIMELINE);
			fragmentManager.beginTransaction().replace(R.id.container, new TimelineSimpleFragment()).commit();
		}
		if(position == 2) { //Mentions
			mApp.setTimelineType(TimelineSimpleFragment.MENTIONS);
			fragmentManager.beginTransaction().replace(R.id.container, new TimelineSimpleFragment()).commit();
		}
		if(position == 3) { //My Account
			mApp.setUserToQuery(mApp.getAppUser());
			fragmentManager.beginTransaction().replace(R.id.container, new AccountFragment()).commit();
		}
		if(position == 4) { //Trends
			
		}
		if(position == 5 ) { //About
			fragmentManager.beginTransaction().replace(R.id.container,  new AboutFragment()).commit();
		}
		
		//fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
			case 1:
				mTitle = getString(R.string.title_section1);
				//Toast.makeText(getApplicationContext(), "onSectionAttached(1)", Toast.LENGTH_LONG).show();
				//launchTimelineActivity();
				break;
			case 2:
				mTitle = getString(R.string.title_section2);
				//Toast.makeText(getApplicationContext(), "onSectionAttached(2)", Toast.LENGTH_LONG).show();
				break;
			case 3:
				mTitle = getString(R.string.title_section3);
				//Toast.makeText(getApplicationContext(), "onSectionAttached(3)", Toast.LENGTH_LONG).show();
				break;
			case 4:
				mTitle = getString(R.string.title_section4);
				break;
			case 5: 
				mTitle = getString(R.string.title_section5);
		}
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
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			logOutOfTwitter();
		}
		if (id == R.id.action_test_tweet) {
			int testNumber = mSharedPrefs.getInt(TwitterApplication.PREF_KEY_TEST_TWEET_NUMBER, -1);
			final String status = ("indev Android Twitter client test tweet #".toString() + Integer.toString(testNumber)).toString();
			
			if (status.trim().length() > 0) {
				new updateTwitterStatus().execute(status);
			} else {
				Toast.makeText(this, "Message is empty!!", Toast.LENGTH_SHORT).show();
			}
			SharedPreferences.Editor editor = mSharedPrefs.edit();
			editor.putInt(TwitterApplication.PREF_KEY_TEST_TWEET_NUMBER, ++testNumber);
			editor.commit();
		}
		if (id == R.id.action_compose) {
			launchComposeActivity();
		}
		return super.onOptionsItemSelected(item);
	}

	
	class updateTwitterStatus extends AsyncTask<String, String, Void> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pDialog = new ProgressDialog(MainActivity.this);
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
			Toast.makeText(MainActivity.this, "Sent to Twitter!", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String	ARG_SECTION_NUMBER	= "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
		
		public void onItemClickListener () {
			
		}
	}
	
	
	//TODO activity lifecycle
	public void onPause() {
		super.onPause();
		Bundle bundle = new Bundle();
		bundle.putSerializable(TwitterApplication.ACCESS_TOKEN_ID, (Serializable) mApp.getAccessToken());
		onSaveInstanceState(bundle);
	}
	public void onDestroy() {
		super.onDestroy();
		Bundle bundle = new Bundle();
		bundle.putSerializable(TwitterApplication.ACCESS_TOKEN_ID, (Serializable) mApp.getAccessToken());
		//onSaveInstanceState(bundle);
	}
	public void onDestroy(Bundle bundle) {
		if(bundle == null) {bundle = new Bundle();}
		
		if(bundle != null) {
			bundle.putSerializable(TwitterApplication.ACCESS_TOKEN_ID, (Serializable) mApp.getAccessToken());
		}
	}
	
	@SuppressWarnings("unused")
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	    }
	}

	

}
