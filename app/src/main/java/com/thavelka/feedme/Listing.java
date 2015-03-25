package com.thavelka.feedme;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("Listing")
public class Listing extends ParseObject {

    protected String mName;
    protected String mAddress;
    protected String mImageUrl;

    public String getName() {
        try {
            mName = getParseObject("restaurant").fetchIfNeeded().getString("name");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mName;
    }

    public String getAddress() {
        try {
            mAddress = getParseObject("restaurant").fetchIfNeeded().getString("address");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mAddress;
    }

    public String getImageUrl() {
        try {
            mImageUrl = getParseObject("restaurant").fetchIfNeeded().getString("imageUrl");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mImageUrl;
    }

    public String getDescription() {
        return getString("description");
    }

}