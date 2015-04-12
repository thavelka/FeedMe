package com.thavelka.feedme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.CountCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;


public class BaseActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = BaseActivity.class.getSimpleName();

    Toolbar mToolbar;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // GET CURRENT USER
        ParseAnalytics.trackAppOpened(getIntent());
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // GET SCORE (number of approved posts by user)
        ParseRelation<ParseObject> relation = currentUser.getRelation("posts");
        ParseQuery<ParseObject> query = relation.getQuery();
        query.whereEqualTo("isApproved", true);
        query.setLimit(1000);

        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                // i = count of approved posts returned by query
                Log.d(TAG, "Found " + i + " posts by user");
                if (i < 0) { // Parse count returns -1 if no results, set score to 0
                    currentUser.put("score", 0);
                } else { // More than zero posts returns correct number
                    currentUser.put("score", i);
                }
                currentUser.saveInBackground();
            }
        });




        // SETTING UP TOOLBAR
        // Creating toolbar to be used as the activity's actionbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // SETTING UP NAV DRAWER
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // starts fragment corresponding to position of touched item
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = MainFragment.newInstance();
                break;
            case 1:
                fragment = MainFragment.newInstance();
                break;
            case 2:
                fragment = FavoritesFragment.newInstance();
                break;
            case 3:
                fragment = ScoreFragment.newInstance();
                break;
            case 4:
                fragment = SettingsFragment.newInstance();
                break;
            default:
                fragment = MainFragment.newInstance();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @SuppressWarnings("deprecation")
    public void restoreActionBar() {
        // what to show in the bar when the drawer is open
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // show different actionbar items if drawer open
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.base, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // signs user out, sends them to the login page
            ParseUser.logOut();
            ParseLoginBuilder builder = new ParseLoginBuilder(this);
            startActivityForResult(builder.build(), 0);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
