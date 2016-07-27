package com.juyo.us.T_omega_itter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TweetListItemViewArrayAdapter extends ArrayAdapter<Status> {
	
	Resources mRes = null;
	Context mContext = null;
	List<Status> mList = null;
	ResponseList<Status> timelineResponse = null;
	Status selectedTweet = null;
	OnClickListener statusClickListener = null;
	int resID = -1;
	Button btRetweet = null;
	Button btFavorite = null;
	TwitterApplication mApp = null;
	
	public TweetListItemViewArrayAdapter(Context context, int resource, List<Status> list) {
		super(context, resource, list);
		mContext = context;
		mList = list;
		mRes = context.getResources();
		resID = resource;
		mApp = (TwitterApplication) context;
		
	}
	
	public TweetListItemViewArrayAdapter(Context context, int resource, ResponseList<Status> timelineTweets) {
		super(context, resource,timelineTweets);
		mContext = context;
		timelineResponse = timelineTweets;
		mRes = context.getResources();
		resID = resource;
		mApp = (TwitterApplication) context;
	}
	
	public TweetListItemViewArrayAdapter(Context context, int resource, ResponseList<Status> timelineTweets, TwitterApplication app) {
		super(context, resource,timelineTweets);
		mContext = context;
		timelineResponse = timelineTweets;
		mRes = context.getResources();
		resID = resource;
		mApp = (TwitterApplication) context;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout timelineItem;
		
		
		ViewHolder holder = null;
		View row = convertView;
		
		if(timelineResponse != null) {
			selectedTweet = timelineResponse.get(position);
		}
		String selectedUserName = selectedTweet.getUser().getName().toString();
		String selectedScreenName = selectedTweet.getUser().getScreenName().toString();
		
		if ( row == null ) {
			//timelineItem = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(inflater);
	        row = layoutInflater.inflate(resID, parent, false);
			
			//holder for this tweet item
			holder = new ViewHolder();
			
			holder.itemScreenname = (TextView) 	row.findViewById(R.id.tweet_list_item_screenname);
			holder.itemUsername = (TextView) 	row.findViewById(R.id.tweet_list_item_username);
			holder.itemText = (TextView) 		row.findViewById(R.id.tweet_list_item_status_text);
			holder.profileIcon = (ImageView) 	row.findViewById(R.id.tweet_list_item_icon);
			holder.protectedIcon = (ImageView) 	row.findViewById(R.id.tweet_list_item_protected_icon);
			holder.retweetButton = (Button) 	row.findViewById(R.id.tweet_button_retweet);
			holder.favoriteButton = (Button)	row.findViewById(R.id.tweet_button_favorite);
			
			
			//set the holder for this tweet as a tag in the view so we don't have to recycle it
			row.setTag(holder);
			
		}
		else {
			//timelineItem = (LinearLayout)row;
			holder = (ViewHolder) row.getTag();
		}
		
		
	/*	
		//TODO Using an AsyncTask to load the slow images in a background thread
		new AsyncTask<ViewHolder, Void, Bitmap>() {
		    private ViewHolder v;

		    @Override
		    protected Bitmap doInBackground(ViewHolder... params) {
		        v = params[0];
		        return mFakeImageLoader.getImage();
		    }

		    @Override
		    protected void onPostExecute(Bitmap result) {
		        super.onPostExecute(result);
		        if (v.position == position) {
		            // If this item hasn't been recycled already, hide the
		            // progress and set and show the image
		            v.progress.setVisibility(View.GONE);
		            v.icon.setVisibility(View.VISIBLE);
		            v.icon.setImageBitmap(result);
		        }
		    }
		}.execute(holder);
		*/
			//onClickListener for items
		this.statusClickListener = new StatusButtonListener(selectedTweet);
		
		// inflate the username and screenname text views
		// these are swapped because the User method .getScreenName returns the username, which .getName() returns the screenname
		
			if (holder.itemScreenname != null) {
				holder.itemScreenname.setText(selectedUserName);
				holder.itemScreenname.setClickable(true);
				holder.favoriteButton.setOnClickListener(this.statusClickListener);
			}
		
			if (holder.itemUsername != null) {
				holder.itemUsername.setText("@" + selectedScreenName);
				holder.itemUsername.setClickable(true);
				holder.favoriteButton.setOnClickListener(this.statusClickListener);
			}
		
			
//				itemText.setText("Sample Tweet Text");
			if(holder.itemText != null) {
				holder.itemText.setText(selectedTweet.getText().toString());
				holder.itemText.setTextColor(mRes.getColor(R.color.clientColor));
			}
			
		
		// inflate the image view of the relative layout
		
			if(holder.profileIcon != null) {
				String userIconURL = selectedTweet.getUser().getBiggerProfileImageURL();
				Bitmap userIconBitmap = null;
				
				try {
					userIconBitmap = getAsBitmap(userIconURL);
				} catch (IOException e) {
					userIconBitmap = null;
				}
				
				holder.profileIcon.setImageBitmap(userIconBitmap);
				holder.profileIcon.setClickable(true);
				holder.favoriteButton.setOnClickListener(this.statusClickListener);
			}
			
			if(holder.favoriteButton != null) {
				holder.favoriteButton.setOnClickListener(this.statusClickListener);
				holder.favoriteButton.setClickable(true);
			}
			
			if(holder.retweetButton != null) {
				holder.retweetButton.setOnClickListener(this.statusClickListener);
				holder.retweetButton.setClickable(true);
			}
			
		Boolean isProtected = selectedTweet.getUser().isProtected();
			if(isProtected) {
				if(holder.protectedIcon != null) {
					holder.protectedIcon.setVisibility(View.VISIBLE);
				}
				if(holder.retweetButton != null) {
					holder.retweetButton.setClickable(false);
				} 
			}
		return row;
	}
	
	protected Bitmap getAsBitmap(String bitmapURL) throws IOException {
		Bitmap bitIcon;

	    HttpURLConnection connection = (HttpURLConnection) new URL(bitmapURL).openConnection();
	    connection.connect();
	    InputStream input = connection.getInputStream();

	    bitIcon = BitmapFactory.decodeStream(input);
	    bitIcon = Bitmap.createScaledBitmap(bitIcon, 60, 60, false);
	    return bitIcon; //new BitmapDrawable(bitIcon);
	}
	
	protected class StatusButtonListener implements View.OnClickListener {
		
		Status thisStatus = null;
		
		public StatusButtonListener(Status status) {
			this.thisStatus = status;
		}

		@Override
		public void onClick(View v) {
			
			Log.v("onClick", "Entered onClick");
			Twitter twitter = new TwitterFactory().getInstance();
			switch(v.getId()) {
				case R.id.tweet_list_item_icon:
				case R.id.tweet_list_item_screenname:
				case R.id.tweet_list_item_username:
					Intent intent = null;
					intent = new Intent(mApp, AccountActivity.class);
						try {
							Log.v("onClick", "Starting AccountActivity");
							mApp.setUserToQuery(this.thisStatus.getUser());
							mApp.startActivity(intent);
							Log.v("onClick", "AccountActivity started");
						} catch(Exception e) {
							e.printStackTrace();
							Log.v("onClick", "AccountActivity failed to start");
						}
					
					break;
			}
			if(v.getId() == R.id.tweet_button_favorite) {
				if(mApp.favoriteTweet(this.thisStatus) == true) {
					Log.v("onClick", "Clicked on Fav button");
					Toast.makeText(mApp, "Favorited!", Toast.LENGTH_SHORT).show();
				}
			}
			if(v.getId() == R.id.tweet_button_retweet) {
				if(mApp.retweetTweet(this.thisStatus) == true) {
					Log.v("onClick", "Clicked RT button");
					Toast.makeText(mApp, "Retweeted!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	static class ViewHolder {
		TextView itemScreenname;
		TextView itemUsername;
		TextView itemText;
		ImageView profileIcon;
		ImageView protectedIcon;
		Button retweetButton;
		Button favoriteButton;
		int position;
		}

	
}
