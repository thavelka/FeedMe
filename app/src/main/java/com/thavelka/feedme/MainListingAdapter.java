package com.thavelka.feedme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/*
* ParseObject adapter for RecyclerView
* Customized for Listing ParseObject subclass
*
* This adapter is intended for use in MainFragment,
* where the list of deals will be shown
*
* A custom ViewHolder for the Listing is contained in this class
 */
public class MainListingAdapter extends RecyclerView.Adapter<MainListingAdapter.ParseViewHolder> {

    public static final String TAG = MainListingAdapter.class.getSimpleName();
    List<Listing> mObjects = Collections.emptyList();
    Context mContext;
    boolean mIsFavorite;
    ParseUser currentUser = ParseUser.getCurrentUser();

    public MainListingAdapter(Context context, List<Listing> objects, boolean isFavorite) throws ParseException {

        // Get list of objects to adapt
        mObjects = objects;
        mContext = context;
        mIsFavorite = isFavorite;

    }

    @Override
    public ParseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        return new ParseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParseViewHolder parseViewHolder, int i) {

        parseViewHolder.bindObject(mObjects.get(i));
    }

    @Override
    public void onViewRecycled(ParseViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.mExpandedLayout.getVisibility() == View.VISIBLE) {
            holder.quickCollapse(holder.mExpandedLayout);
        }
    }


    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable;
        isAvailable = networkInfo != null && networkInfo.isConnected();
        return isAvailable;
    }

    /*
    * Custom ViewHolder class for ParseObject Listing subclass
    *
    * Contains all code for interaction with listings inside bindObject method
    * Also contains code for custom animations for listing (expand, collapse, quickCollapse)
     */

    public class ParseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Declare views to be filled
        @InjectView(R.id.compressed)
        RelativeLayout mCompressedLayout;
        @InjectView(R.id.nameCompressed)
        TextView mNameCompressed;
        @InjectView(R.id.addressCompressed)
        TextView mAddressCompressed;
        @InjectView(R.id.descriptionCompressed)
        TextView mDescriptionCompressed;
        @InjectView(R.id.imageCompressed)
        CircleImageView mImageCompressed;
        @InjectView(R.id.expanded)
        LinearLayout mExpandedLayout;
        @InjectView(R.id.nameExpanded)
        TextView mNameExpanded;
        @InjectView(R.id.addressExpanded)
        TextView mAddressExpanded;
        @InjectView(R.id.descriptionExpanded)
        TextView mDescriptionExpanded;
        @InjectView(R.id.imageExpanded)
        ImageView mImageExpanded;
        @InjectView(R.id.shadow)
        ImageView mShadow;
        @InjectView(R.id.favoriteButton)
        ButtonFlat mFavoriteButton;
        @InjectView(R.id.shareButton)
        ButtonFlat mShareButton;
        @InjectView(R.id.directionsButton)
        ButtonFlat mDirectionsButton;
        @InjectView(R.id.reportButton)
        ButtonFlat mReportButton;


        public ParseViewHolder(final View itemView) {
            super(itemView);

            // Define views to be filled
            ButterKnife.inject(this, itemView);
            mCompressedLayout.setOnClickListener(this);
        }

        // Actions to be carried out when compressed view is clicked
        @Override
        public void onClick(final View v) {
            if (mExpandedLayout.getVisibility() == View.GONE) {
                expand(mExpandedLayout);

            } else if (mExpandedLayout.getVisibility() == View.VISIBLE) {
                collapse(mExpandedLayout);
            }
        }

        public void bindObject(final Listing listing) {

            // Grab name, address, and image from Restaurant pointer, description from Listing
            final String name = listing.getName();
            final String address = listing.getAddress();
            final String imageUrl = listing.getImageUrl();
            final String description = listing.getDescription();

            // Sets up compressed view of listing, which is the normal state of the object
            Picasso.with(itemView.getContext()).load(imageUrl).into(mImageCompressed);
            mNameCompressed.setText(name);
            mAddressCompressed.setText(address);
            mDescriptionCompressed.setText(description);

            // Sets up expanded detail panel, even though it won't be visible yet.
            Picasso.with(itemView.getContext()).load(listing.getImageUrl()).into(mImageExpanded);
            Picasso.with(itemView.getContext()).load(R.drawable.shadow).into(mShadow);
            mNameExpanded.setText(name);
            mAddressExpanded.setText(address);
            mDescriptionExpanded.setText(description);

            // If adapter is called from FavoritesFragment, change button text to "Unfavorite"
            if (mIsFavorite) {
                mFavoriteButton.setText("Unfavorite");
            }

            /*
            * Define actions to be carried out on button press
            *
            * Favorite: Add or remove listing from user's favorites
            * Share: Creates short, shareable message with restaurant name and deal description
            * Directions: Sends intent to Google Maps with restaurant address and name
            * Report: Allows user to report an incorrect or expired deal
             */

            // Define actions for Favorite button
            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ParseUser user = ParseUser.getCurrentUser();
                    ParseRelation<ParseObject> relation = user.getRelation("favorites");

                    // If adapter is called from FavoritesFragment, remove listing from favorites
                    if (mIsFavorite) {
                        relation.remove(listing);
                        Log.d(TAG, "Removed listing from favorites");
                        Toast.makeText
                                (mContext, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        mObjects.remove(getPosition());
                        notifyItemRemoved(getPosition());
                        notifyItemRangeChanged(getPosition(), mObjects.size());

                    // If adapter is called from MainFragment add listing to favorites
                    } else {
                        relation.add(listing);
                        Log.d(TAG, "Added listing to favorites");
                        Toast.makeText(mContext, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }

                    user.saveInBackground();
                }
            });

            // Define actions for Share button
            mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Build simple message for user to share through other app
                    String toSend = "Join me for " + description + " at " + name + " today!";

                    // Create Intent to share message
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);

                    // Attach message
                    sendIntent.putExtra(Intent.EXTRA_TEXT, toSend);
                    sendIntent.setType("text/plain");

                    // Allow user to choose an app and send the message
                    mContext.startActivity(Intent.createChooser(sendIntent, mContext.getResources().getText(R.string.send_to)));
                }
            });

            // Define actions for Directions button
            mDirectionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Create label to pass to Google Maps, will be displayed as location name
                    String label = " (" + name + ")";

                    // Parse parameters as proper URI format, create Intent
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address + label);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                    // Define app to receive intent, make sure it exists
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(mContext.getPackageManager()) != null && isNetworkAvailable()) {
                        // If app exists, send intent
                        mContext.startActivity(mapIntent);
                    } else {
                        // If not, show error toast, do not send
                        Toast.makeText(mContext, "Unable to load maps application",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Define actions for Report Button
            mReportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Create dialog that will allow user to enter a report description and send it
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                    alert.setTitle("Report Listing");
                    alert.setMessage("What's wrong with this listing? Please be specific.");

                    // Set an EditText view to get user input
                    final EditText input = new EditText(mContext);
                    input.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                    // Set up dialog layout using FrameLayout as container for elements
                    FrameLayout container = new FrameLayout(mContext);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 48;
                    params.rightMargin = 48;
                    params.topMargin = 48;
                    params.bottomMargin = 16;
                    input.setLayoutParams(params);
                    container.addView(input);
                    alert.setView(container);

                    // Actions to be carried out on Submit button press
                    alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();

                            if (value.length() > 5) { // Makes sure user has entered a description

                                // Create report with user's name, a description, and listing's ID
                                ParseObject report = new ParseObject("Report");
                                report.put("user", currentUser);
                                report.put("listing", ParseObject
                                        .createWithoutData("Listing", listing.getObjectId()));
                                report.put("description", value);
                                report.saveInBackground();
                                Toast.makeText(mContext, "Thank you!", Toast.LENGTH_SHORT).show();

                            } else { // Send error toast if description is too short
                                Toast.makeText(mContext, "A description is needed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // Close dialog and send toast on Cancel button press
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(mContext, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Present user with the dialog
                    alert.show();


                }
            });


        }

        /*
        * Custom animations to be applied to Listing objects
        *
        * Expand: Details panel slides down from below compressed view
        * Collapse: Details panel slides back up into compressed view
        * QuickCollapse: Near-instantaneous collapse, intended for automatic use, not user input
        *
        * Animation duration value determined by height of panel / display density.
        * Resulting value gives us a speed of about 1ms/dp
        *
         */

        public void expand(final View v) {
            v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final int targetHeight = v.getMeasuredHeight();

            v.getLayoutParams().height = 0;
            v.setVisibility(View.VISIBLE);
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            ? LinearLayout.LayoutParams.WRAP_CONTENT
                            : (int) (targetHeight * interpolatedTime);
                    v.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
            Log.d(TAG, "expanding");
            v.startAnimation(a);
        }

        public void collapse(final View v) {
            final int initialHeight = v.getMeasuredHeight();

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        v.setVisibility(View.GONE);
                    } else {
                        v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // 1dp/ms
            a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
            Log.d(TAG, "collapsing");
            v.startAnimation(a);
        }

        public void quickCollapse(final View v) {
            final int initialHeight = v.getMeasuredHeight();

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        v.setVisibility(View.GONE);
                    } else {
                        v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration(10);
            Log.d(TAG, "quick collapsing");
            v.startAnimation(a);
        }

    }
}

