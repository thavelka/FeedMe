package com.thavelka.feedme;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class ListingsFragment extends Fragment {

    public static final String TAG = ListingsFragment.class.getSimpleName();

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar mProgressBar;
    List<Listing> mListings;
    TextView mEmptyText;
    boolean mIsFood;

    @Override
    public void onResume() {
        super.onResume();
        getListings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsFood = getArguments().getBoolean("isFood");
        View v = inflater.inflate(R.layout.fragment_listings, container, false);
        mListings = Collections.emptyList();
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mEmptyText = (TextView) v.findViewById(R.id.emptyText);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresher);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProgressBar.setEnabled(false);
                getListings();
            }
        });
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setPadding(0,0,0,400);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getListings();
        return v;
    }

    // Returns day of week as an int (Sun = 1, Mon = 2 ... Sat = 7)
    public Integer getDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    private void getListings() {
        if (isNetworkAvailable()) {
            new ShowListings().execute(getDay());
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Network unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable;
        isAvailable = networkInfo != null && networkInfo.isConnected();
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
            query.whereEqualTo("isFood", mIsFood); // Set constraints for query
            query.whereEqualTo("days", params[0]);
            if (ParseUser.getCurrentUser() != null) {
                query.whereEqualTo("location", ParseUser.getCurrentUser().getParseObject("location"));
            }
            query.whereEqualTo("isApproved", true);
            query.include("restaurant");
            try {
                mListings = query.find();
                Log.d(TAG, "got " + mListings.size() + " objects");
                mAdapter = new MainListingAdapter(getActivity(), mListings, false);
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