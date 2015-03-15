package com.thavelka.feedme;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by tim on 3/14/15.
 */
public class ListingGetter {

    //MAKE RESTAURANTS
    public Restaurant bww = new Restaurant("Buffalo Wild Wings", "903 University Drive");
    public Restaurant dominos = new Restaurant("Domino's Pizza", "409 University Drive");
    public Restaurant fazolis = new Restaurant("Fazoli's", "400 Harvey Road");
    public Restaurant gumbys = new Restaurant("Gumby's", "107 Dominik");
    public Restaurant nakedfish = new Restaurant("Naked Fish Sushi", "1808 Texas Ave. South");
    public Restaurant jins = new Restaurant("Jin's Asian Cafe", "110 Nagle St");
    public int[] days1 = {3};
    public int[] days2 = {2,3,4,5,6};
    public int[] days3 = {3,5};
    public int[] days4 = {1,7};
    public Listing listing1 = new Listing(bww, days1, "Wing Tuesday: $0.65 traditional wings all day. Minimum 6 pieces", true);
    public Listing listing2 = new Listing(dominos, days2, "$7.99 3-topping large", true);
    public Listing listing3 = new Listing(fazolis, days3, "$2.99 Trio: Pizza, spaghetti, fettucinni, and unlimited breadsticks", true);
    public Listing listing4 = new Listing(gumbys, days1, "$0.50 Pizza Rolls", true);
    public Listing listing5 = new Listing(nakedfish, days4, "$7.99 roll and soup", true);
    public Listing listing6 = new Listing(jins, days4, "$6.99 entree and soup", true);
    public Listing[] mListings = {listing1, listing2, listing3, listing4, listing5, listing6};



    public ArrayList<Listing> getFoodListings(int day) {
        ArrayList<Listing> toSend = new ArrayList<>();
        for (Listing i : mListings) {
            if (ArrayUtils.contains(i.getDays(), day) && i.getIsFood()) {
                toSend.add(i);
            }
        }
        return toSend;
    }

    public ArrayList<Listing> getDrinkListings(int day) {
        ArrayList<Listing> toSend = new ArrayList<>();
        for (Listing i : mListings) {
            if (ArrayUtils.contains(i.getDays(), day) && !(i.getIsFood())) {
                toSend.add(i);
            }
        }
        return toSend;
    }

}
