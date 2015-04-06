package com.thavelka.feedme;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

// Custom ParseObject Adapter for Recycler View
// Customized for Listing subclass
public class AdminListingAdapter extends RecyclerView.Adapter<AdminListingAdapter.ParseViewHolder> {

    public static final String TAG = AdminListingAdapter.class.getSimpleName();
    List<Listing> mObjects = Collections.emptyList();
    Context mContext;
    boolean mIsFavorite;

    public AdminListingAdapter(Context context, List<Listing> objects, boolean isFavorite) throws ParseException {

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
        @InjectView(R.id.favoriteButton) // Hijacking favorite button to use as approve button
                Button mApproveButton;
        @InjectView(R.id.shareButton)  // Hijacking share button to use as delete button
                Button mDeleteButton;
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
            // Hides buttons when in admin mode
            mDirectionsButton.setVisibility(View.GONE);
            mReportButton.setVisibility(View.GONE);
            // Grabs name and address from object's restaurant pointer
            // Sets TextViews with values
            Picasso.with(itemView.getContext()).load(listing.getImageUrl()).into(mImageCompressed);
            mNameCompressed.setText(listing.getName());
            mAddressCompressed.setText(listing.getAddress());
            mDescriptionCompressed.setText(listing.getDescription());
            // Enters data for detail pane, though it won't be visible yet
            mNameExpanded.setText(listing.getName());
            mAddressExpanded.setText(listing.getAddress());
            mDescriptionExpanded.setText(listing.getDescription());
            mApproveButton.setText("Approve");
            mDeleteButton.setText("Delete");

            Picasso.with(itemView.getContext()).load(listing.getImageUrl()).into(mImageExpanded);
            mApproveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listing.put("isApproved", true);
                    listing.saveInBackground();
                    Toast.makeText(mContext, "Listing approved", Toast.LENGTH_SHORT).show();
                    mObjects.remove(getPosition());
                    notifyItemRemoved(getPosition());
                    notifyItemRangeChanged(getPosition(), mObjects.size());
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listing.deleteInBackground();
                    Toast.makeText(mContext, "Listing Deleted", Toast.LENGTH_SHORT).show();
                    mObjects.remove(getPosition());
                    notifyItemRemoved(getPosition());
                    notifyItemRangeChanged(getPosition(), mObjects.size());
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

