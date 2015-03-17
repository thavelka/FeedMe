package com.thavelka.feedme;

import android.media.tv.TvContract;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.software.shell.fab.ActionButton;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


// Credit to Akash Bangad from android4devs.com for help with the material design layout

public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    //Declaring titles and icons for testing in drawer layout

    String TITLES[] = {"Home","Favorites","Notifications","Settings"};
    int ICONS[] = {R.mipmap.ic_home_grey600_24dp,R.mipmap.ic_favorite_grey600_24dp,
            R.mipmap.ic_notifications_grey600_24dp,R.mipmap.ic_settings_grey600_24dp};

    //String and int resources for user data for testing drawer layout

    String NAME = "Tim Havelka";
    String EMAIL = "tim.havelka@gmail.com";
    int PROFILE = R.drawable.tim;

    protected ProgressBar mProgressBar;
    protected RecyclerView mRecyclerView;                           // Declaring RecyclerView
    protected RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    protected RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    protected DrawerLayout mDrawerLayout;                                  // Declaring DrawerLayout
    protected ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar mDrawerLayout Toggle
    protected ViewPager mViewPager;
    protected ViewPagerAdapter mViewPagerAdapter;
    protected SlidingTabLayout mTabs;
    protected CharSequence mTabTitles[]={"Food","Drinks"};
    protected int mTabCount =2;
    protected String dayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);


        // Creating toolbar to be used as the activity's actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Calendar mCalendar = Calendar.getInstance();
        dayOfWeek = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        getSupportActionBar().setTitle(dayOfWeek);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Runs when drawer opened
                getSupportActionBar().setTitle("FeedMe");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Runs when drawer closes
                Calendar mCalendar = Calendar.getInstance();
                dayOfWeek = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

                getSupportActionBar().setTitle(dayOfWeek);
            }



        }; // mDrawerLayout Toggle Object Made
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();




        // Creating The ViewPagerAdapter and Passing Fragment Manager, mTabTitles fot the Tabs and Number Of Tabs.
        mViewPagerAdapter =  new ViewPagerAdapter(getSupportFragmentManager(), mTabTitles, mTabCount);

        // Assigning ViewPager View and setting the mViewPagerAdapter
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        // Assigning the Sliding Tab Layout View
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
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

        ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
        actionButton.setButtonColor(getResources().getColor(R.color.fab_material_purple_500));
        actionButton.setButtonColorPressed(getResources().getColor(R.color.fab_material_purple_900));
        actionButton.setImageResource(R.drawable.fab_plus_icon);

        mProgressBar.setVisibility(View.INVISIBLE);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}