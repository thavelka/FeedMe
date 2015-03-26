package com.thavelka.feedme;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class FavoritesActivity extends ActionBarActivity {

    public static final String TAG = FavoritesActivity.class.getSimpleName();

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar mProgressBar;
    List<Listing> mListings;
    TextView mEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListings = Collections.emptyList();
        mProgressBar = (ProgressBar) findViewById(R.id.favoritesProgress);
        mEmptyText = (TextView) findViewById(R.id.emptyFavoritesText);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.favoritesRefresher);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProgressBar.setEnabled(false);
                getFavorites();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.favoritesRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration
                (this, DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ParseAdapter(this, mListings);
        mRecyclerView.setAdapter(mAdapter);

        getFavorites();

    }

    public Integer getDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    private void getFavorites() {
        if (isNetworkAvailable()) {
            new ShowListings().execute(getDay());
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
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

    private class ShowListings extends AsyncTask<Integer, Void, List<Listing>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mEmptyText.setVisibility(View.GONE);
            if (mProgressBar.isEnabled()) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Listing> doInBackground(Integer... params) {
            ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
            query.include("restaurant");
            try {
                mListings = query.find();
                Log.d(TAG, "got objects");
                mAdapter = new ParseAdapter(FavoritesActivity.this, mListings);
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
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            mProgressBar.setEnabled(true);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }

}
