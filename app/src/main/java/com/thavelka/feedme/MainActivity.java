package com.thavelka.feedme;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseObject;
import com.software.shell.fab.ActionButton;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


// Credit to Akash Bangad from android4devs.com for help with the material design layout

public class MainActivity extends ActionBarActivity {

    //Declaring titles and icons for testing in drawer layout

    String TITLES[] = {"Home","Favorites","Notifications","Settings"};
    int ICONS[] = {R.mipmap.ic_home_grey600_24dp,R.mipmap.ic_favorite_grey600_24dp,
            R.mipmap.ic_notifications_grey600_24dp,R.mipmap.ic_settings_grey600_24dp};

    //String and int resources for user data for testing drawer layout

    String NAME = "Tim Havelka";
    String EMAIL = "tim.havelka@gmail.com";
    int PROFILE = R.drawable.tim;

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout mDrawerLayout;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar mDrawerLayout Toggle


    ViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;
    SlidingTabLayout mTabs;
    CharSequence mTabTitles[]={"Food","Drinks"};
    int mTabCount =2;
    String dayOfWeek;

    ListingGetter mListingGetter;
    public static final String LISTINGS = "LISTINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // Creating toolbar to be used as the activity's actionbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Calendar mCalendar = Calendar.getInstance();
        dayOfWeek = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        getSupportActionBar().setTitle(dayOfWeek);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed siz
        mAdapter = new DrawerAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter
        mRecyclerView.setAdapter(mAdapter);                              // Setting the mViewPagerAdapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);        // mDrawerLayout object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened
                getSupportActionBar().setTitle("FeedMe");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
                Calendar mCalendar = Calendar.getInstance();
                dayOfWeek = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

                getSupportActionBar().setTitle(dayOfWeek);
            }



        }; // mDrawerLayout Toggle Object Made
        mDrawerLayout.setDrawerListener(mDrawerToggle); // mDrawerLayout Listener set to the mDrawerLayout toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State




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