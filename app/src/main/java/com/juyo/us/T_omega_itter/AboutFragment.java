package com.juyo.us.T_omega_itter;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment  {

	
	public static AboutFragment newInstance() {
		 AboutFragment f = new AboutFragment();
		 
		 Log.v("AboutFragment", "AboutFragment newInstance()");

		 return f;
    }
	 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
	  
		 Log.v("AboutFragment", "AboutFragment onCreateView()");

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
        
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        
        TextView tvAppName = (TextView) v.findViewById(R.id.about_app_name_and_blurb);
        tvAppName.setText( getString(R.string.app_name) + " by Justin Young was developed as the final project for CS 3200 for Fall of 2014. \n It was created using the Twitter4j API library in Eclipse, targeted at Android API level 19.");
        
        TextView tvDisclaimer = (TextView) v.findViewById(R.id.about_disclaimer);
        tvDisclaimer.setText("Twitter is owned by Twitter.com, the name " + getString(R.string.app_name) + " is for educational purposes and does not intend any enfringement on the rights of Twitter.com");
        
        TextView tvReferences = (TextView) v.findViewById(R.id.about_references);
        tvReferences.setText("Twitter: 		https://twitter.com/" +
        		           "\nTwitter4J: 	http://twitter4j.org/" +
        				   "\nEclipse:		https://eclipse.org/ " +
        				   "\nAndroid:   	http://www.android.com/ ");
        
    	return v;
    }
	
	public void onCreate() {
		
		 Log.v("AboutFragment", "AboutFragment onCreate()");

	}
}
