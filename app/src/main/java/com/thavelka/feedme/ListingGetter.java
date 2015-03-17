package com.thavelka.feedme;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tim on 3/14/15.
 */
public class ListingGetter {

    //MAKE RESTAURANTS
//    public Restaurant bww = new Restaurant("Buffalo Wild Wings", "903 University Drive");
//    public Restaurant dominos = new Restaurant("Domino's Pizza", "409 University Drive");
//    public Restaurant fazolis = new Restaurant("Fazoli's", "400 Harvey Road");
//    public Restaurant gumbys = new Restaurant("Gumby's", "107 Dominik");
//    public Restaurant nakedfish = new Restaurant("Naked Fish Sushi", "1808 Texas Ave. South");
//    public Restaurant jins = new Restaurant("Jin's Asian Cafe", "110 Nagle St");
//    public int[] days1 = {3};
//    public int[] days2 = {2,3,4,5,6};
//    public int[] days3 = {3,5};
//    public int[] days4 = {1,7};
//    public Listing listing1 = new Listing(bww, days1, "Wing Tuesday: $0.65 traditional wings all day. Minimum 6 pieces", true);
//    public Listing listing2 = new Listing(dominos, days2, "$7.99 3-topping large", true);
//    public Listing listing3 = new Listing(fazolis, days3, "$2.99 Trio: Pizza, spaghetti, fettucinni, and unlimited breadsticks", true);
//    public Listing listing4 = new Listing(gumbys, days1, "$0.50 Pizza Rolls", true);
//    public Listing listing5 = new Listing(nakedfish, days4, "$7.99 roll and soup", true);
//    public Listing listing6 = new Listing(jins, days4, "$6.99 entree and soup", true);
//    public Listing[] mListings = {listing1, listing2, listing3, listing4, listing5, listing6};

    public static final String TAG = ListingGetter.class.getSimpleName();

    protected ParseObject mRestaurant;
    protected List<ParseObject> mListings;


    public ArrayList<Listing> getFoodListings(int day) {
        ArrayList<Listing> toSend = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Listing");
        query.whereEqualTo("days", day);
        query.whereEqualTo("isFood", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + objects.size() + " listings");
                    mListings = objects;
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });

        if (mListings !=null) {
            for (ParseObject i : mListings) {
                String name = i.getParseObject("restaurant").getString("name");
                String address = i.getParseObject("restaurant").getString("address");
                Restaurant restaurant = new Restaurant(name, address);
                String description = i.getString("description");
                Listing listing = new Listing(restaurant, description);
                toSend.add(listing);
            }
        }
        return toSend;
    }

    public ArrayList<Listing> getDrinkListings(int day) {
        ArrayList<Listing> toSend = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Listing");
        query.whereEqualTo("days", day);
        query.whereEqualTo("isFood", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + objects.size() + " listings");
                    mListings = objects;
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });

        for (ParseObject i : mListings) {
            String name = i.getParseObject("restaurant").getString("name");
            String address = i.getParseObject("restaurant").getString("address");
            Restaurant restaurant = new Restaurant(name, address);
            String description = i.getString("description");
            Listing listing = new Listing(restaurant, description);
            toSend.add(listing);
        }

        return toSend;
    }

    public ParseObject getRestaurant (String name) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
        query.whereStartsWith("name", name);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d(TAG, "Failed to find restaurant.");
                } else {
                    Log.d(TAG, "Retrieved the restaurant.");
                    mRestaurant = object;
                }
            }
        });

        return mRestaurant;
    }

    public void uploadNewListing(final String name, final int[] days, final String description, final boolean isFood) {

        final ArrayList<Integer> daysList = new ArrayList<Integer>(days.length);
        for (int i : days) {
            daysList.add(i);
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
        query.whereStartsWith("name", name);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d(TAG, "Failed to find restaurant.");
                } else {
                    Log.d(TAG, "Retrieved the restaurant.");
                    ParseObject listing = new ParseObject("Listing");
                    String objId = object.getObjectId();
                    listing.put("restaurant", ParseObject.createWithoutData("Restaurant", objId));
                    listing.put("days", daysList);
                    listing.put("description", description);
                    listing.put("isFood", isFood);
                    listing.saveInBackground();
                }
            }
        });


    }

    public void initUpload() {
        int[] days1 = {3};
        int[] days2 = {2,3,4,5,6};
        int[] days3 = {3,5};
        int[] days4 = {1,7};
        uploadNewListing("Buffalo Wild Wings", days1, "Wing Tuesday: $0.65 traditional wings all day. Minimum 6 pieces", true);
        uploadNewListing("Domino\'s Pizza", days2, "$7.99 3-topping large", true);
        uploadNewListing("Fazoli\'s", days3, "$2.99 Trio: Pizza, spaghetti, fettucinni, and unlimited breadsticks", true);
        uploadNewListing("Gumby\'s Pizza", days1, "$0.50 Pizza Rolls", true);
        uploadNewListing("Naked Fish Sushi", days4, "$7.99 roll and soup", true);
        uploadNewListing("Jin\'s Asian Cafe", days4, "$6.99 entree and soup", true);
        Log.d(TAG, "initial upload complete");
    }

}
