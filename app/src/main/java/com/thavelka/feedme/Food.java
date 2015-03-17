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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;


public class Food extends Fragment {

    public static final String TAG = Food.class.getSimpleName();

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.food,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.foodRecyclerView); // Assigning the RecyclerView Object to the xml View
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


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Listing");
        query.whereEqualTo("isFood", true);
        //query.whereEqualTo("days", day);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    mAdapter = new ParseAdapter(getActivity(), objects);

                    mRecyclerView.setAdapter(mAdapter);
                    Log.d(TAG, "got objects");
                } else {
                    Log.d(TAG, e.getMessage());
                }
            }
        });

    }
}