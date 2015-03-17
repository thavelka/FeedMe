package com.thavelka.feedme;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("Listing")
public class Listing extends ParseObject {

    protected String mName;
    protected String mAddress;

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

    public String getDescription() {
        return getString("description");
    }

}