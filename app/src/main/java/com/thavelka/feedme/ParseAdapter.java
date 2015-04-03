package com.thavelka.feedme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

// Custom ParseObject Adapter for Recycler View
// Customized for Listing subclass
public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ParseViewHolder> {

    public static final String TAG = ParseAdapter.class.getSimpleName();
    List<Listing> mObjects = Collections.emptyList();
    Context mContext;
    boolean mIsFavorite;

    public ParseAdapter(Context context, List<Listing> objects, boolean isFavorite) throws ParseException {

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
        @InjectView(R.id.favoriteButton)
        Button mFavoriteButton;
        @InjectView(R.id.shareButton)
        Button mShareButton;
        @InjectView(R.id.directionsButton)
        Button mDirectionsButton;
        @InjectView(R.id.reportButton)
        Button mReportButton;


        public ParseViewHolder(final View itemView) {
            super(itemView);

            // Define views to be filled
            ButterKnife.inject(this, itemView);
            mCompressedLayout.setOnClickListener(this);

        }

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

            // 1dp/ms
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

            // 1dp/ms
            a.setDuration(10);
            Log.d(TAG, "collapsing");
            v.startAnimation(a);
        }

        public void bindObject(final Listing listing) {
            // Grabs name and address from object's restaurant pointer
            // Sets TextView with values
            Picasso.with(itemView.getContext()).load(listing.getImageUrl()).into(mImageCompressed);
            mNameCompressed.setText(listing.getName());
            mAddressCompressed.setText(listing.getAddress());
            mDescriptionCompressed.setText(listing.getDescription());
            // Enters data for detail pane, though it won't be visible yet
            mNameExpanded.setText(listing.getName());
            mAddressExpanded.setText(listing.getAddress());
            mDescriptionExpanded.setText(listing.getDescription());
            if (mIsFavorite) {
                mFavoriteButton.setText("Unfavorite");
            }
            Picasso.with(itemView.getContext()).load(listing.getImageUrl()).into(mImageExpanded);
            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ParseUser user = ParseUser.getCurrentUser();
                    ParseRelation<ParseObject> relation = user.getRelation("favorites");

                    if (mFavoriteButton.getText().toString().equals("Unfavorite")) {
                        relation.remove(listing);
                        Log.d(TAG, "Removed listing from favorites");
                        Toast.makeText(mContext, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        mObjects.remove(getPosition());
                        notifyItemRemoved(getPosition());
                        notifyItemRangeChanged(getPosition(), mObjects.size());


                    } else {
                        relation.add(listing);
                        Log.d(TAG, "Added listing to favorites");
                        Toast.makeText(mContext, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }

                    user.saveInBackground();
                }
            });

            mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toSend = "Join me for " + listing.getDescription() + " at " + listing.getName() + " today!";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, toSend);
                    sendIntent.setType("text/plain");
                    mContext.startActivity(Intent.createChooser(sendIntent, mContext.getResources().getText(R.string.send_to)));
                }
            });

            mDirectionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String address = listing.getAddress();
                    String label = " (" + listing.getName() + ")";
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address + label);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(mContext.getPackageManager()) != null && isNetworkAvailable()) {
                        mContext.startActivity(mapIntent);
                    } else {
                        Toast.makeText(mContext, "Unable to load maps application", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mReportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                    alert.setTitle("Report Listing");
                    alert.setMessage("What's wrong with this listing? Please be specific.");

// Set an EditText view to get user input
                    final EditText input = new EditText(mContext);
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            ParseObject report = new ParseObject("Report");
                            report.put("listing", ParseObject.createWithoutData("Listing", listing.getObjectId()));
                            report.put("description", value);
                            report.saveInBackground();
                            Toast.makeText(mContext, "Thank you!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(mContext, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

                    alert.show();
                }
            });


        }


        @Override
        public void onClick(final View v) {
            if (mExpandedLayout.getVisibility() == View.GONE) {
                expand(mExpandedLayout);
            } else if (mExpandedLayout.getVisibility() == View.VISIBLE) {
                collapse(mExpandedLayout);
            }
        }


    }
}

