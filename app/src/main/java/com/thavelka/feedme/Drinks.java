package com.thavelka.feedme;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class Drinks extends Fragment {

    public static final String TAG = Drinks.class.getSimpleName();

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.drinks,container,false);


        mRecyclerView = (RecyclerView) v.findViewById(R.id.drinksRecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        getListings(getDay());

        return v;
    }

    public Integer getDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public void getListings(Integer day) {

        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        //query.whereEqualTo("isFood", true);
        //query.whereEqualTo("days", day);
        query.findInBackground(new FindCallback<Listing>() {
            public void done(List<Listing> listings, ParseException e) {

                if (e == null) {

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