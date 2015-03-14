package com.thavelka.feedme;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by tim on 3/13/15.
 */
public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    private Listing[] mListings;

    public ListingAdapter(FragmentActivity activity, Listing[] listings) {
        mListings = listings;
    }


    @Override
    public ListingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        ListingViewHolder viewHolder = new ListingViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListingViewHolder listingViewHolder, int i) {
        listingViewHolder.bindListing(mListings[i]);
    }

    @Override
    public int getItemCount() {
        return mListings.length;
    }

    public class ListingViewHolder extends RecyclerView.ViewHolder {


        public TextView mNameLabel;
        public TextView mAddressLabel;
        public TextView mDescriptionLabel;


        public ListingViewHolder(View itemView) {
            super(itemView);

            mNameLabel = (TextView) itemView.findViewById(R.id.name);
            mAddressLabel = (TextView) itemView.findViewById(R.id.address);
            mDescriptionLabel = (TextView) itemView.findViewById(R.id.description);
        }

        public void bindListing (Listing listing) {
            mNameLabel.setText(listing.getRestaurantName());
            mAddressLabel.setText(listing.getRestaurantAddress());
            mDescriptionLabel.setText(listing.getDescription());
        }

    }


}
