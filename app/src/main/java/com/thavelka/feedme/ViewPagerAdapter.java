package com.thavelka.feedme;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

// Holds info needed to set up pager and sliding tabs
public class ViewPagerAdapter extends FragmentPagerAdapter {

    protected CharSequence mTabTitles[] = {"Food", "Drinks"}; // Set tab names
    protected int mTabCount = 2; // Set number of tabs

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        // Returns a fragment based on current position in pager
        if (position == 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isFood", true);
            Fragment foodFragment = new ListingsFragment();
            foodFragment.setArguments(bundle);
            return foodFragment;
            //return new Food(); // Creates and returns Food fragment
        } else {
            //return new Drinks(); // Creates and return Drinks fragment
            Bundle bundle = new Bundle();
            bundle.putBoolean("isFood", false);
            Fragment drinksFragment = new ListingsFragment();
            drinksFragment.setArguments(bundle);
            return drinksFragment;
        }
    }

    @Override
    // Returns title of tab at current position
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }

    @Override
    // Return number of tabs
    public int getCount() {
        return mTabCount;
    }
}
