package com.juyo.us.T_omega_itter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class TimelineSimpleActivity extends Activity {
	
	TwitterApplication mApp = null;
	MainActivity mAct = null;
	Twitter twitter = null;
	User user = null;
	Resources mRes = null;
	Paging page = null;
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_timeline_simple);
		
		mApp = (TwitterApplication) this.getApplication();
		mAct = (MainActivity) this.getParent();
		twitter = mApp.getTwitter();
		user = mApp.getAppUser();
		mRes = this.getResources();
		page = new Paging(1, 50);
		mApp.setPaging(page);
		
		
		//Theme
				ActionBar actionBar = getActionBar();
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				actionBar.setDisplayShowTitleEnabled(true);
				actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
				
				int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
				if (actionBarTitleId > 0) {
				    TextView title = (TextView) findViewById(actionBarTitleId);
				    if (title != null) {
				        title.setTextColor(mRes.getColor(R.color.clientColor));
				    }
				}
				//Theme end
		
		
		
		ListView lv = (ListView) this.findViewById(R.id.timeline_simple_button_listview);
		lv.setBackgroundColor(mRes.getColor(R.color.clientColorBlack1));
		
		ResponseList<Status> timelineTweets = null ;
		
		try {
			timelineTweets = twitter.getHomeTimeline(mApp.getPage());
		} catch (TwitterException e) {
			Toast.makeText(mApp, "Couldn't getHomeTimeline()", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		
		TweetListItemViewArrayAdapter adapter = new TweetListItemViewArrayAdapter(mApp, R.layout.tweet_list_item_layout, timelineTweets);
		
/*
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mApp, R.layout.tweet_list_item_layout_textonly);
		
		
		if(timelineTweets != null) {
			for(Status tweet: timelineTweets) {
				adapter.add(tweet.getText());
			}
		}

		
		
		String[] adapter = mRes.getStringArray(R.array.mock_tweets);
*/
		lv.setAdapter(adapter);
		
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			mAct.logOutOfTwitter();
		}
		if (id == R.id.action_test_tweet) {
			int testNumber =mAct.mSharedPrefs.getInt(TwitterApplication.PREF_KEY_TEST_TWEET_NUMBER, -1);
			final String status = ("indev Android Twitter client test tweet #".toString() + Integer.toString(testNumber)).toString();
			
			if (status.trim().length() > 0) {
				//new updateTwitterStatus().execute(status);
			} else {
				Toast.makeText(this, "Message is empty!!", Toast.LENGTH_SHORT).show();
			}
			SharedPreferences.Editor editor = mAct.mSharedPrefs.edit();
			editor.putInt(TwitterApplication.PREF_KEY_TEST_TWEET_NUMBER, ++testNumber);
			editor.commit();
		}
		
		return super.onOptionsItemSelected(item);
	}


}
