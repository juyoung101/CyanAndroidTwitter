package com.juyo.us.T_omega_itter;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

	public class AccountFragment extends Fragment {
		
		TwitterApplication mApp = null;
		MainActivity mAct = null;
		Twitter twitter = null;
		User user = null;
		User userToQuery = null;
		Resources mRes = null;
		//Paging page = null;
		//ListView lv = null;
		
		 @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);

	    }
	 
	   
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		//setContentView(R.layout.activity_timeline_simple);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View v = super.onCreateView(inflater, container, savedInstanceState);
	    View parent = (View) inflater.inflate(R.layout.fragment_account, container, false);
//		    lv = (ListView) parent.findViewById(R.id.timeline_fragment_list);
	    
	    
	    mApp = (TwitterApplication) getActivity().getApplication();
		mAct = (MainActivity) this.getActivity();
		twitter = mApp.getTwitter();
		user = mApp.getAppUser();
		mRes = this.getResources();
		
		if(mApp.getUserToQuery() != null) {
			userToQuery = mApp.getUserToQuery();
		} else {
			userToQuery = this.user;
		}
		
		TextView tvScreenname = (TextView) parent.findViewById(R.id.account_screenname);
			tvScreenname.setText(user.getName());
		TextView tvUsername = (TextView) parent.findViewById(R.id.account_username);
			tvUsername.setText("@" + user.getScreenName());
			
		ImageView ivProfileIcon = (ImageView) parent.findViewById(R.id.account_icon);
			ivProfileIcon.setImageBitmap(getAccountBitmap(user));
			
		LinearLayout llBannerHolder = (LinearLayout) parent.findViewById(R.id.account_banner_holder);
			llBannerHolder.setBackground(getAccountBanner(user));
			
		Button btnBlock 	= (Button) parent.findViewById(R.id.account_button_block);
		Button btnFollow	= (Button) parent.findViewById(R.id.account_button_follow);
		
		if(userToQuery == user) {
			btnBlock.setClickable(false);
			btnBlock.setVisibility(View.INVISIBLE);
			
			btnFollow.setClickable(false);
			btnFollow.setVisibility(View.INVISIBLE);
		}
		
		TextView tvBio 		= (TextView) parent.findViewById(R.id.account_bio);
			tvBio.setText(user.getDescription().toString());
		TextView tvLocation = (TextView) parent.findViewById(R.id.account_location);
			tvLocation.setText(user.getLocation());
		TextView tvWebsite 	= (TextView) parent.findViewById(R.id.account_website);
			tvWebsite.setText(user.getURL());
		
		TextView tvFollowing = (TextView) parent.findViewById(R.id.account_following_count);
			tvFollowing.setText("Following\n" + user.getFriendsCount());
		TextView tvFollowers = (TextView) parent.findViewById(R.id.account_followers_count);
			tvFollowers.setText("Followers\n" + user.getFollowersCount());
		TextView tvTweetCount= (TextView) parent.findViewById(R.id.account_tweet_count);
			tvTweetCount.setText("Tweets\n" + user.getStatusesCount());
		
				
	    
	    return parent;
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
