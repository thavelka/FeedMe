package com.thavelka.feedme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.List;


public class Food extends Fragment {

    public static final String TAG = Food.class.getSimpleName();

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    @Override
    public void onResume() {
        super.onResume();
        getListings(getDay());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.food, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.foodRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration
                (getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getListings(getDay());

        return v;
    }

    // Returns day of week as an int (Sun = 1, Mon = 2 ... Sat = 7)
    public Integer getDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public void getListings(Integer day) {

        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        query.whereEqualTo("isFood", true); // Set constraints for query
        query.whereEqualTo("days", day);
        query.findInBackground(new FindCallback<Listing>() {
            public void done(List<Listing> listings, ParseException e) {

                if (e == null) {
                    // If listings found, create and set adapter
                    mAdapter = new ParseAdapter(getActivity(), listings);
                    mRecyclerView.setAdapter(mAdapter);
                    Log.d(TAG, "got objects");
                } else {
                    Log.d(TAG, e.getMessage());
                }
            }
        });

    }
}