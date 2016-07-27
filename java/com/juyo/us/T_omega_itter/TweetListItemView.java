package com.juyo.us.T_omega_itter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class TweetListItemView extends RelativeLayout {

	public TweetListItemView(Context context) {
		super(context);
		init();
		
	}
	
	public TweetListItemView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		init();
	}
	
	public TweetListItemView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
	}

}
