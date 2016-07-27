package com.juyo.us.T_omega_itter;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeScreenFragment extends Fragment  {

	 public static HomeScreenFragment newInstance() {
		 HomeScreenFragment f = new HomeScreenFragment();
		 
		 Log.v("HomeScreenFragment", "HomeScreenFragment newInstance()");

		 return f;
    }
	 
	  @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		  
			 Log.v("HomeScreenFragment", "HomeScreenFragment onCreateView()");

	        if (container == null) {
	            // We have different layouts, and in one of them this
	            // fragment's containing frame doesn't exist.  The fragment
	            // may still be created from its saved state, but there is
	            // no reason to try to create its view hierarchy because it
	            // won't be displayed.  Note this is not needed -- we could
	            // just run the code below, where we would create and return
	            // the view hierarchy; it would just never be used.
	            return null;
	        }
	        
	        View v = inflater.inflate(R.layout.fragment_home_screen, container, false);
	        
	        TextView tv = (TextView) v.findViewById(R.id.home_screen_text_view);
	        
	        
        	return v;
//	        ScrollView scroller = new ScrollView(getActivity());
//	        TextView text = new TextView(getActivity());
//	        int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//	                4, getActivity().getResources().getDisplayMetrics());
//	        text.setPadding(padding, padding, padding, padding);
//	        scroller.addView(text);
//	        text.setText("This is the default HomeScreenFragment view");
//	        return scroller;
	    }
	
	public void onCreate() {
		
		 Log.v("HomeScreenFragment", "HomeScreenFragment onCreate()");

	}
}
