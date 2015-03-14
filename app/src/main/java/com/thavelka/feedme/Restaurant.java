package com.thavelka.feedme;

/**
 * Created by tim on 3/13/15.
 */
public class Restaurant {

    protected String mName;
    protected String mAddress;

    public Restaurant (String name, String address) {
        mName = name;
        mAddress = address;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }
}
