package com.thavelka.feedme;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.software.shell.fab.ActionButton;

import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    public static final String TAG = MainFragment.class.getName();
    protected ViewPager mViewPager;
    protected ViewPagerAdapter mViewPagerAdapter;
    protected SlidingTabLayout mTabs;
    protected String dayOfWeek;

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        if (isFirstTime()) {
            //show alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("FeedMe relies on the community to stay up-to-date. If you enjoy the app, be sure to add any new deals you find, and report inaccurate ones.")
                    .setTitle("Welcome!").setPositiveButton("OK", null).show();

        }

        // Getting name of day of week to use as title
        Calendar mCalendar = Calendar.getInstance();
        dayOfWeek = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        getActivity().setTitle(dayOfWeek);

        //SETTING UP VIEW PAGER + SLIDING TABS
        // Creating The ViewPagerAdapter and Passing Fragment Manager, mTabTitles fot the Tabs and Number Of Tabs.
        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        // Assigning ViewPager View and setting the mViewPagerAdapter
        mViewPager = (ViewPager) root.findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        // Assigning the Sliding Tab Layout View
        mTabs = (SlidingTabLayout) root.findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the mTabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tab_scroll_color);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mViewPager);

        // SETTING UP FLOATING ACTION BUTTON
        // Create button using ActionButton library
        ActionButton actionButton = (ActionButton) root.findViewById(R.id.action_button);
        actionButton.setButtonColor(getResources().getColor(R.color.fab_material_purple_500));
        actionButton.setButtonColorPressed(getResources().getColor(R.color.fab_material_purple_900));
        actionButton.setImageResource(R.drawable.fab_plus_icon);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewListingActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
        }
        return !ranBefore;
    }


}
