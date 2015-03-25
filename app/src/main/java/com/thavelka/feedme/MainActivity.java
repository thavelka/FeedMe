package com.thavelka.feedme;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    // View and member variable declarations

    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected ViewPager mViewPager;
    protected ViewPagerAdapter mViewPagerAdapter;
    protected SlidingTabLayout mTabs;
    protected CharSequence mTabTitles[] = {"Food", "Drinks"}; // Set tab names
    protected int mTabCount = 2; // Set number of tabs
    protected String dayOfWeek;

    // Test values for nav drawer
    String TITLES[] = {"Home", "Favorites", "Notifications", "Settings"};
    int ICONS[] = {R.mipmap.ic_home_grey600_24dp, R.mipmap.ic_favorite_grey600_24dp,
            R.mipmap.ic_notifications_grey600_24dp, R.mipmap.ic_settings_grey600_24dp};
    String NAME = "Tim Havelka";
    String EMAIL = "tim.havelka@gmail.com";
    int PROFILE = R.drawable.tim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseAnalytics.trackAppOpened(getIntent());

        String consumerKey = "XZD_PVM-IOn85-1-_Qq0gg";
        String consumerSecret = "Tx71U5GScIAUgs_DBWUkSDTlG08";
        String token = "tO1R_J3s2LdVMEbYfjUzuVS5DBmqoKcu";
        String tokenSecret = "62WQJQAEigy1lkGyRZRlamq4inI";


        // SETTING UP TOOLBAR
        // Creating toolbar to be used as the activity's actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Getting name of day of week to use as title
        Calendar mCalendar = Calendar.getInstance();
        dayOfWeek = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        getSupportActionBar().setTitle(dayOfWeek);

        // SETTING UP NAV DRAWER
        // Set up RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set Drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

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

        //SETTING UP VIEW PAGER + SLIDING TABS
        // Creating The ViewPagerAdapter and Passing Fragment Manager, mTabTitles fot the Tabs and Number Of Tabs.
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mTabTitles, mTabCount);

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

        // SETTING UP FLOATING ACTION BUTTON
        // Create button using ActionButton library
        ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
        actionButton.setButtonColor(getResources().getColor(R.color.fab_material_purple_500));
        actionButton.setButtonColorPressed(getResources().getColor(R.color.fab_material_purple_900));
        actionButton.setImageResource(R.drawable.fab_plus_icon);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewListingActivity.class);
                startActivity(intent);
            }
        });


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

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else {
            Toast.makeText(this, getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    private boolean isLocationAvailable() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            Toast.makeText(this, "Location Unavailable", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}