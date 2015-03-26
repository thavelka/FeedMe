package com.thavelka.feedme;

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

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.List;


public class Drinks extends Fragment {

    public static final String TAG = Drinks.class.getSimpleName();

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar mProgressBar;
    List<Listing> mListings;

    @Override
    public void onResume() {
        super.onResume();
        //getListings(getDay());
        new ShowListings().execute(getDay());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.drinks, container, false);

        mProgressBar = (ProgressBar) v.findViewById(R.id.drinksProgress);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.drinksRefresher);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProgressBar.setEnabled(false);
                new ShowListings().execute(getDay());
            }
        });

        mRecyclerView = (RecyclerView) v.findViewById(R.id.drinksRecyclerView);
        mRecyclerView.addItemDecoration
                (new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        new ShowListings().execute(getDay());

        return v;
    }

    // Returns day of week as an int (Sun = 1, Mon = 2 ... Sat = 7)
    public Integer getDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    private class ShowListings extends AsyncTask<Integer, Void, List<Listing>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressBar.isEnabled()) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Listing> doInBackground(Integer... params) {
            ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
            query.whereEqualTo("isFood", false); // Set constraints for query
            query.whereEqualTo("days", params[0]);
            query.include("restaurant");
            try {
                mListings = query.find();
                Log.d(TAG, "got objects");
                mAdapter = new ParseAdapter(getActivity(), mListings);
                return mListings;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Listing> listings) {
            super.onPostExecute(listings);
            mRecyclerView.setAdapter(mAdapter);
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