package com.thavelka.feedme;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class FeedMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Restaurant.class);
        ParseObject.registerSubclass(Listing.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.APPLICATION_ID), getString(R.string.CLIENT_KEY));
    }
}
