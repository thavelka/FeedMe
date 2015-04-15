package com.thavelka.feedme;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    public static final String TAG = FavoritesFragment.class.getSimpleName();
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    List<Listing> mListings;
    TextView mEmptyText;

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
        mEmptyText = (TextView) root.findViewById(R.id.emptyFavoritesText);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.favoritesRecyclerView);

        mRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
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
            new ShowListings().execute();
        } else {
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
                mAdapter = new MainListingAdapter(getActivity(), mListings, true);
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
