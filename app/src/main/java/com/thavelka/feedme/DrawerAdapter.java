package com.thavelka.feedme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private String mName;        //String Resource for header View Name
    private String mEmail;       //String Resource for header view mEmail
    private int mScore;
    private String mLocation;
    private String mImageUrl;
    private Context mContext;


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    DrawerAdapter(Context context, String Titles[], int Icons[], String Name, String Email, int Score, String location, String imageUrl) { // Constructor takes info to display as params

        mContext = context;
        mNavTitles = Titles;
        mIcons = Icons;
        mName = Name;
        mEmail = Email;
        mScore = Score;
        mLocation = location;
        mImageUrl = imageUrl;

    }

    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false); //Inflating the layout

            return new ViewHolder(v, viewType); // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false); //Inflating the layout

            return new ViewHolder(v, viewType); //returning the object created


        }
        return null;

    }


    //Below first we override the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(DrawerAdapter.ViewHolder holder, int position) {
        if (holder.holderId == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            holder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our mTabTitles
            holder.imageView.setImageResource(mIcons[position - 1]);// Setting the image with array of our icons
        } else {
            Drawable shadow = mContext.getResources().getDrawable(R.drawable.shadow);
            shadow.setAlpha(90);
            holder.Name.setText(mName + " (" + mScore + ')');
            holder.email.setText(mEmail);
            holder.location.setText(mLocation);
            Picasso.with(mContext).load(mImageUrl).into(holder.headerImage);
            holder.shadow.setImageDrawable(shadow);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    // With the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int holderId;

        TextView textView;
        ImageView imageView;
        ImageView headerImage;
        ImageView shadow;
        TextView Name;
        TextView email;
        TextView location;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);


            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
                holderId = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            } else {

                headerImage = (ImageView) itemView.findViewById(R.id.headerImage);
                shadow = (ImageView) itemView.findViewById(R.id.shadow);
                Name = (TextView) itemView.findViewById(R.id.headerName);         // Creating Text View object from header.xml for mName
                email = (TextView) itemView.findViewById(R.id.headerEmail);       // Creating Text View object from header.xml for mEmail
                location = (TextView) itemView.findViewById(R.id.userLocation);
                holderId = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }

        }

    }

}


