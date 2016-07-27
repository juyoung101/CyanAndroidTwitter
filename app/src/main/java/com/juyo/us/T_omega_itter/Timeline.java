package com.juyo.us.T_omega_itter;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class Timeline extends ListActivity {

	TwitterApplication mApp = null;
	Twitter twitter = null;
	User user = null;
//	MainActivity mMainAct = null;
	ResponseList<Status> timeline = null;
	Paging page = null;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_timeline);
		
		Toast.makeText(getApplicationContext(), "Timeline.onCreate()", Toast.LENGTH_LONG).show();
		
		mApp =  (TwitterApplication) this.getApplication();
		twitter = mApp.getTwitter();
		user = mApp.getAppUser();
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
		        title.setTextColor(Color.parseColor("#00CCDD"));
		    }
		}
		//Theme end
		
		
		try {
			timeline = twitter.getHomeTimeline();
		} catch (TwitterException e) {
			Toast.makeText(getApplicationContext(), "Could not retrieve Timeline", Toast.LENGTH_LONG).show();
		} catch (NullPointerException npe) {
			Toast.makeText(getApplicationContext(), "NullPointerException, Could not retrieve Timeline", Toast.LENGTH_LONG).show();
		}
		
		Toast.makeText(getApplicationContext(), "GotUserTimeline", Toast.LENGTH_LONG).show();
		
		ArrayList<Status> timelineList = (ArrayList<Status>) timeline;
		
//	TweetListItemViewArrayAdapter tweetAdapter = new TweetListItemViewArrayAdapter(this, R.layout.tweet_list_item_layout, timelineList);
		
		
//    	ArrayList<String> texts = new ArrayList<String>();
//    	for(Status tweet : timelineList) {
//    		texts.add(tweet.getText());
//    	}
//    	
//    	ArrayAdapter<String> tweetAdapter = new ArrayAdapter<String>(this, R.layout.tweet_list_item_layout_textonly, texts);
		
    	
		//this.setListAdapter(tweetAdapter);
		
		ListView lv = this.getListView();
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
//		this.getListView().setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View child, int position, long id) {
//				Log.v(LOG_TAG, "Clicked on: " + arrAuthorNames[position]);
//				callNewActivity(position);
//				}
//			});	
		
//		mHostAct.displayBook(mBookSelected);
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		//TODO
	}
	
}
