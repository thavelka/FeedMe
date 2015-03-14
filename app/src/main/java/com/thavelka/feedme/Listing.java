package com.thavelka.feedme;

/**
 * Created by tim on 3/13/15.
 */
public class Listing {

    protected Restaurant mRestaurant;
    protected int[] mDays;
    protected String mDescription;
    protected boolean mIsFood;

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

}
