package com.thavelka.feedme;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;


public class Food extends Fragment {

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    Listing[] mListings;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.food,container,false);

        Parcelable[] parcelables = getArguments().getParcelableArray("LISTINGS");
        mListings = Arrays.copyOf(parcelables, parcelables.length, Listing[].class);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.foodRecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ListingAdapter(getActivity(),mListings);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the mViewPagerAdapter to RecyclerView


        return v;
    }
}