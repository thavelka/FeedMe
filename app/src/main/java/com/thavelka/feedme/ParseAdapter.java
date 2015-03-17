package com.thavelka.feedme;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tim on 3/16/15.
 */

// Custom ParseObject Adapter for Recycler View
// Customized for Listing subclass
public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ParseViewHolder> {

    private List<Listing> mObjects;

    public ParseAdapter(FragmentActivity activity, List<Listing> objects) {
        // Get list of objects to adapt
        mObjects = objects;
    }


    @Override
    public ParseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        ParseViewHolder viewHolder = new ParseViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ParseViewHolder parseViewHolder, int i) {
        parseViewHolder.bindObject(mObjects.get(i));
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public class ParseViewHolder extends RecyclerView.ViewHolder {

        // Declare views to be filled
        public TextView mNameLabel;
        public TextView mAddressLabel;
        public TextView mDescriptionLabel;


        public ParseViewHolder(View itemView) {
            super(itemView);
            // Define views to be filled
            mNameLabel = (TextView) itemView.findViewById(R.id.name);
            mAddressLabel = (TextView) itemView.findViewById(R.id.address);
            mDescriptionLabel = (TextView) itemView.findViewById(R.id.description);
        }

        public void bindObject(Listing listing) {

            // Grabs name and address from object's restaurant pointer
            // Sets TextView with values
            mNameLabel.setText(listing.getName());
            mAddressLabel.setText(listing.getAddress());
            mDescriptionLabel.setText(listing.getDescription());
        }

    }


}
