package com.thavelka.feedme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the mTabTitles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of mTabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        ListingGetter listingGetter = new ListingGetter();
        //listingGetter.initUpload();
        Bundle bundle = new Bundle();


        if(position == 0) // if the position is 0 we are returning the First tab
        {
            Food foodTab = new Food();//
            Calendar mCalendar = Calendar.getInstance();
//            int day = mCalendar.get(Calendar.DAY_OF_WEEK);
//            ArrayList<Listing> foodList = listingGetter.getFoodListings(day);
//            Listing[] foodArray = foodList.toArray(new Listing[foodList.size()]);
//            bundle.putParcelableArray("LISTINGS",foodArray);
//            foodTab.setArguments(bundle);
            return foodTab;
        }
        else             // As we are having 2 mTabs if the position is now 0 it must be 1 so we are returning second tab
        {
            Drinks drinksTab = new Drinks();
            Calendar mCalendar = Calendar.getInstance();
            int day = mCalendar.get(Calendar.DAY_OF_WEEK);
            //ArrayList<Listing> drinksList = listingGetter.getDrinkListings(day);
            //Listing[] drinksArray = drinksList.toArray(new Listing[drinksList.size()]);
            //bundle.putParcelableArray("LISTINGS",drinksArray);
            //drinksTab.setArguments(bundle);
            return drinksTab;
        }


    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of mTabs for the mTabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
