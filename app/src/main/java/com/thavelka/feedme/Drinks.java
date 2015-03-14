package com.thavelka.feedme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thavelka.feedme.R;


public class Drinks extends Fragment {

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View


    //MAKE RESTAURANTS
    public Restaurant rest1 = new Restaurant("Buffalo Wild Wings", "903 University Dr.");
    public Restaurant rest2 = new Restaurant("Domino's Pizza", "409 University Dr.");
    public int[] days1 = {2};
    public int[] days2 = {1,2,3,4,5};
    public Listing listing1 = new Listing(rest1, days1, "Wing Tuesday: $0.65 traditional wings all day. Minimum 6 pieces", true);
    public Listing listing2 = new Listing(rest2, days2, "$7.99 3-topping large", true);
    public Listing[] mListings = {listing1, listing2};





    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.drinks,container,false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.drinksRecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

        mAdapter = new ListingAdapter(getActivity(),mListings);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the mViewPagerAdapter to RecyclerView





        return v;
    }
}