package com.thavelka.feedme;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tim on 3/13/15.
 */
public class Listing implements Parcelable{

    protected Restaurant mRestaurant;
    protected int[] mDays;
    protected String mDescription;
    protected boolean mIsFood;

    public Listing () {};

    public Listing (Restaurant restaurant, String description) {
        mRestaurant = restaurant;
        mDescription = description;
    }

    public Listing (Restaurant restaurant, int[] days, String description, boolean isFood) {
        mRestaurant = restaurant;
        mDays = days;
        mDescription = description;
        mIsFood = isFood;
    }

    public boolean getIsFood() {
        return mIsFood;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }

    public void setIsFood(boolean isFood) {
        mIsFood = isFood;
    }

    public String getRestaurantName() {
        return mRestaurant.getName();
    }

    public void setRestaurantName(String name) {
        mRestaurant.setName(name);
    }

    public String getRestaurantAddress() {
        return mRestaurant.getAddress();
    }

    public void setRestaurantAddress(String address) {
        mRestaurant.setAddress(address);
    }

    public int[] getDays() {
        return mDays;
    }

    public void setDays(int[] days) {
        mDays = days;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRestaurant.getName());
        dest.writeString(mRestaurant.getAddress());
        dest.writeIntArray(mDays);
        dest.writeString(mDescription);
        dest.writeInt(mIsFood ? 1 : 0);
    }

    private Listing (Parcel in) {
        setRestaurantName(in.readString());
        setRestaurantAddress(in.readString());
        mDays = in.createIntArray();
        mDescription = in.readString();
        mIsFood = in.readInt() != 0;
    }

    public static final Creator<Listing> CREATOR = new Creator<Listing>() {
        @Override
        public Listing createFromParcel(Parcel source) {
            return new Listing(source);
        }

        @Override
        public Listing[] newArray(int size) {
            return new Listing[size];
        }
    };
}
