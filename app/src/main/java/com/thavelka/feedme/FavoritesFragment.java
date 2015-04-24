package com.thavelka.feedme;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    public static final String TAG = FavoritesFragment.class.getSimpleName();
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    List<Listing> mListings = new ArrayList<>();

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);

        getActivity().setTitle("Favorites");
        mListings = Collections.emptyList();
        mRecyclerView = (RecyclerView) root.findViewById(R.id.favoritesRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setPadding(0,0,0,300);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getFavorites();
        try {
            mAdapter = new MainListingAdapter(getActivity(), mListings, true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mRecyclerView.setAdapter(mAdapter);


        return root;
    }

    private void getFavorites() {
        if (isNetworkAvailable()) {
            ParseUser user = ParseUser.getCurrentUser();
            ParseRelation<Listing> relation = user.getRelation("favorites");
            ParseQuery<Listing> query = relation.getQuery();
            query.include("listing");
            query.findInBackground(new FindCallback<Listing>() {
                @Override
                public void done(List<Listing> listings, ParseException e) {
                    showFavorites(listings);
                }
            });

        } else {
            Toast.makeText(getActivity(), "Network unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private void showFavorites(List<Listing> listings) {
        try {
            if (listings != null && listings.size() > 0) {
                mListings = listings;
                mAdapter = new MainListingAdapter(getActivity(), listings, true);
                mRecyclerView.setAdapter(mAdapter);

            }
        } catch (ParseException e) {
            e.printStackTrace();
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




}
