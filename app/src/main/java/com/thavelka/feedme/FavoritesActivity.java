package com.thavelka.feedme;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class FavoritesActivity extends ActionBarActivity {

    public static final String TAG = FavoritesActivity.class.getSimpleName();
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected LinearLayout mHomeRow;
    protected LinearLayout mFavoritesRow;
    protected LinearLayout mNotificationsRow;
    protected LinearLayout mSettingsRow;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    List<Listing> mListings;
    TextView mEmptyText;
    String dayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // SETTING UP NAV DRAWER
        mHomeRow = (LinearLayout) findViewById(R.id.homeRow);
        mFavoritesRow = (LinearLayout) findViewById(R.id.favoritesRow);
        mNotificationsRow = (LinearLayout) findViewById(R.id.notificationsRow);
        mSettingsRow = (LinearLayout) findViewById(R.id.settingsRow);
        mHomeRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawers();
            }
        });
        mFavoritesRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });

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

        mListings = Collections.emptyList();
        mEmptyText = (TextView) findViewById(R.id.emptyFavoritesText);
        mRecyclerView = (RecyclerView) findViewById(R.id.favoritesRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration
                (this, DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getFavorites();
        try {
            mAdapter = new ParseAdapter(this, mListings, true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mRecyclerView.setAdapter(mAdapter);

    }


    private void getFavorites() {
        if (isNetworkAvailable()) {
            new ShowListings().execute();
        } else {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else {
            isAvailable = false;
        }
        return isAvailable;
    }

    private class ShowListings extends AsyncTask<Void, Void, List<Listing>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mEmptyText.setVisibility(View.GONE);
        }

        @Override
        protected List<Listing> doInBackground(Void... params) {
            ParseUser user = ParseUser.getCurrentUser();
            ParseRelation<Listing> relation = user.getRelation("favorites");
            ParseQuery<Listing> query = relation.getQuery();
            query.include("listing");
            try {
                mListings = query.find();
                Log.d(TAG, "got " + mListings.size() + " objects");
                mAdapter = new ParseAdapter(FavoritesActivity.this, mListings, true);
                return mListings;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<Listing> listings) {
            super.onPostExecute(listings);
            mRecyclerView.setAdapter(mAdapter);
            if (listings.size() == 0) {
                mEmptyText.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }

}
