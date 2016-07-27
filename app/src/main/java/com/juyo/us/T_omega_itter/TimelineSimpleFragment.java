package com.juyo.us.T_omega_itter;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class TimelineSimpleFragment extends Fragment {

	TwitterApplication mApp = null;
	MainActivity mAct = null;
	Twitter twitter = null;
	User user = null;
	Resources mRes = null;
	Paging page = null;
	ListView lv = null;
	
	String timelineType = null;
	
	static int TIMELINE = 1;
	static int USER = 2;
	static int TREND = 3;
	static int MENTIONS = 4;
	
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View v = super.onCreateView(inflater, container, savedInstanceState);
	    View parent = (View) inflater.inflate(R.layout.fragment_timeline_simple, container, false);
	    lv = (ListView) parent.findViewById(R.id.timeline_fragment_list);
	    
	    
	    mApp = (TwitterApplication) getActivity().getApplication();
		mAct = (MainActivity) this.getActivity();
		twitter = mApp.getTwitter();
		user = mApp.getAppUser();
		mRes = this.getResources();
		page = new Paging(1, 50);
		mApp.setPaging(page);
		

		lv.setBackgroundColor(mRes.getColor(R.color.clientColorBlack1));
		
		ResponseList<Status> timelineTweets = null ;
		
		int typeOfTimeline = -1;
		typeOfTimeline = mApp.getTimelineType();
		
		switch(typeOfTimeline) {
			case 1:
				try {
					timelineTweets = twitter.getHomeTimeline(mApp.getPage());
				} catch (TwitterException e) {
					Toast.makeText(mApp, "Couldn't getHomeTimeline()", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					timelineTweets = twitter.getUserTimeline(mApp.getUserToQuery().getId(), mApp.getPage());
				} catch (TwitterException e) {
					Toast.makeText(mApp, "Couldn't getUserTimeline()", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					timelineTweets = twitter.getHomeTimeline(mApp.getPage());
					Trends t = (Trends) twitter.trends();
					Trend[] tArray = t.getTrends();
					//tArray[1]
				} catch (TwitterException e) {
					Toast.makeText(mApp, "Couldn't getHomeTimeline()", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				break;
			case 4:
				try {
					timelineTweets = twitter.getMentionsTimeline(mApp.getPage());
				} catch (TwitterException e) {
					Toast.makeText(mApp, "Couldn't getMentionsTimeline()", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				break;
		}
		try {
			timelineTweets = twitter.getHomeTimeline(mApp.getPage());
		} catch (TwitterException e) {
			Toast.makeText(mApp, "Couldn't getHomeTimeline()", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		
		TweetListItemViewArrayAdapter adapter = new TweetListItemViewArrayAdapter(mApp, R.layout.tweet_list_item_layout, timelineTweets);
		
		lv.setAdapter(adapter);
	    
	    
	    return parent;
	}

}
