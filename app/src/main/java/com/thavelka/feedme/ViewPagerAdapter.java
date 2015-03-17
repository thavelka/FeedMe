package com.thavelka.feedme;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Calendar;

// Holds info needed to set up pager and sliding tabs
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // Stores names of Tabs
    int tabCount; // Number of Tabs

    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mTabCountSum) {
        super(fm);
        this.Titles = mTitles;
        this.tabCount = mTabCountSum;
    }

    @Override
    public Fragment getItem(int position) {

        // Returns a fragment based on current position in pager
        if(position == 0) {
            return new Food(); // Creates and returns Food fragment
        } else {
            return new Drinks(); // Creates and return Drinks fragment
        }
    }

    @Override
    // Returns title of tab at current position
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    // Return number of tabs
    public int getCount() {
        return tabCount;
    }
}
