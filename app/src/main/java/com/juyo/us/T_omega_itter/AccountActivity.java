package com.juyo.us.T_omega_itter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import twitter4j.Twitter;
import twitter4j.User;

public class AccountActivity extends Activity{
		
		TwitterApplication mApp = null;
		MainActivity mAct = null;
		Twitter twitter = null;
		User user = null;
		User userToQuery = null;
		Resources mRes = null;
		String mTitle = null;
		//Paging page = null;
		//ListView lv = null;
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_account);
	    
	    mApp = (TwitterApplication) this.getApplication();
		twitter = mApp.getTwitter();
		user = mApp.getAppUser();
		mRes = this.getResources();
		
		if(mApp.getUserToQuery() != null) {
			userToQuery = mApp.getUserToQuery();
		} else {
			userToQuery = this.user;
		}
		
		mTitle = userToQuery.getName().toString();
		
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
	
		
		TextView tvScreenname = (TextView) this.findViewById(R.id.account_screenname);
			tvScreenname.setText(user.getName());
		TextView tvUsername = (TextView) this.findViewById(R.id.account_username);
			tvUsername.setText("@" + user.getScreenName());
			
		ImageView ivProfileIcon = (ImageView) this.findViewById(R.id.account_icon);
			ivProfileIcon.setImageBitmap(getAccountBitmap(user));
			
		LinearLayout llBannerHolder = (LinearLayout) this.findViewById(R.id.account_banner_holder);
			llBannerHolder.setBackground(getAccountBanner(user));
			
		Button btnBlock 	= (Button) this.findViewById(R.id.account_button_block);
		Button btnFollow	= (Button) this.findViewById(R.id.account_button_follow);
		
		if(userToQuery == user) {
			btnBlock.setClickable(false);
			btnBlock.setVisibility(View.INVISIBLE);
			
			btnFollow.setClickable(false);
			btnFollow.setVisibility(View.INVISIBLE);
		}
		
		TextView tvBio 		= (TextView) this.findViewById(R.id.account_bio);
			tvBio.setText(user.getDescription().toString());
		TextView tvLocation = (TextView) this.findViewById(R.id.account_location);
			tvLocation.setText(user.getLocation());
		TextView tvWebsite 	= (TextView) this.findViewById(R.id.account_website);
			tvWebsite.setText(user.getURL());
		
		TextView tvFollowing = (TextView) this.findViewById(R.id.account_following_count);
			tvFollowing.setText("Following\n" + user.getFriendsCount());
		TextView tvFollowers = (TextView) this.findViewById(R.id.account_followers_count);
			tvFollowers.setText("Followers\n" + user.getFollowersCount());
		TextView tvTweetCount= (TextView) this.findViewById(R.id.account_tweet_count);
			tvTweetCount.setText("Tweets\n" + user.getStatusesCount());
		
	    
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
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.global, menu);
			restoreActionBar();
			return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected Bitmap getAccountBitmap(User inUser) {
		String userIconURL = inUser.getOriginalProfileImageURL();
		Bitmap userIconBitmap = null;
		
		try {
			userIconBitmap = collectAccountBitmap(userIconURL);
		} catch (IOException e) {
			userIconBitmap = null;
		}

	   return userIconBitmap;
	}
	
	protected Bitmap collectAccountBitmap(String userIconURL) throws IOException {
		
		Bitmap bitIcon;
		HttpURLConnection connection = (HttpURLConnection) new URL(userIconURL).openConnection();
	    	connection.connect();
	    InputStream input = connection.getInputStream();

	    bitIcon = BitmapFactory.decodeStream(input);
	    bitIcon = Bitmap.createScaledBitmap(bitIcon, 60, 60, false);
	    return bitIcon; 
	}
	
	protected Drawable getAccountBanner(User inUser) {
		String userBannerURL = inUser.getProfileBannerMobileURL();
		Drawable userBannerBitmap = null;
		
			try {
				userBannerBitmap = collectBannerDrawable(userBannerURL);
			} catch(IOException e) {
				userBannerBitmap = null;
			}
			
			return userBannerBitmap;
	}

	protected Drawable collectBannerDrawable(String userBannerURL) throws IOException{
		Bitmap bitBanner;
		HttpURLConnection connection = (HttpURLConnection) new URL(userBannerURL).openConnection();
			connection.connect();
		InputStream input = connection.getInputStream();
		
		bitBanner = BitmapFactory.decodeStream(input);
		//bitBanner = Bitmap.createScaledBitmap(bitIcon, )
		return new BitmapDrawable(bitBanner);
	}
}
