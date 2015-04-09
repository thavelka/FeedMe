package com.thavelka.feedme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class NewListingActivity extends ActionBarActivity {

    public static final String TAG = NewListingActivity.class.getSimpleName();

    @InjectView(R.id.restaurantField)
    AutoCompleteTextView mRestaurantField;
    @InjectView(R.id.descriptionField)
    EditText mDescriptionField;
    @InjectView(R.id.sundayBox)
    CheckBox mSundayBox;
    @InjectView(R.id.mondayBox)
    CheckBox mMondayBox;
    @InjectView(R.id.tuesdayBox)
    CheckBox mTuesdayBox;
    @InjectView(R.id.wednesdayBox)
    CheckBox mWednesdayBox;
    @InjectView(R.id.thursdayBox)
    CheckBox mThursdayBox;
    @InjectView(R.id.fridayBox)
    CheckBox mFridayBox;
    @InjectView(R.id.saturdayBox)
    CheckBox mSaturdayBox;
    @InjectView(R.id.foodRadio)
    RadioButton mFoodRadio;
    @InjectView(R.id.drinkRadio)
    RadioButton mDrinkRadio;
    @InjectView(R.id.submitButton)
    Button mSubmitButton;

    String mName;
    String mDescription;
    ArrayList<Integer> mDays = new ArrayList<>();
    boolean mIsFood;
    List<ParseObject> mLocations;
    ArrayList<String> mRestaurants = new ArrayList<>();
    ArrayAdapter<String> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.inject(this);
        getRestaurants();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = mRestaurantField.getText().toString();
                mDescription = mDescriptionField.getText().toString();

                if (mSundayBox.isChecked()) {
                    mDays.add(1);
                }
                if (mMondayBox.isChecked()) {
                    mDays.add(2);
                }
                if (mTuesdayBox.isChecked()) {
                    mDays.add(3);
                }
                if (mWednesdayBox.isChecked()) {
                    mDays.add(4);
                }
                if (mThursdayBox.isChecked()) {
                    mDays.add(5);
                }
                if (mFridayBox.isChecked()) {
                    mDays.add(6);
                }
                if (mSaturdayBox.isChecked()) {
                    mDays.add(7);
                }
                if (mFoodRadio.isChecked() && !mDrinkRadio.isChecked()) {
                    mIsFood = true;
                }

                uploadNewListing(mName, mDays, mDescription, mIsFood);

            }
        });
    }

    public void uploadNewListing(final String name, final ArrayList<Integer> days, final String description, final boolean isFood) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
        query.whereEqualTo("name", name);
        query.whereEqualTo("location", ParseUser.getCurrentUser().getParseObject("location"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d(TAG, "Failed to find restaurant.");
                    Toast.makeText(NewListingActivity.this, "Unable to find restaurant", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewListingActivity.this, BaseActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "Retrieved the restaurant.");
                    final Listing listing = new Listing();
                    String objId = object.getObjectId();
                    // TODO: VALIDATE PARAMS FOR NEW LISTING BEFORE SUBMISSION
                    listing.put("restaurant", ParseObject.createWithoutData("Restaurant", objId));
                    listing.put("days", days);
                    listing.put("description", description);
                    listing.put("isFood", isFood);
                    listing.put("location", ParseUser.getCurrentUser().getParseObject("location"));
                    listing.put("isApproved", false);
                    listing.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            ParseUser user = ParseUser.getCurrentUser();
                            ParseRelation<ParseObject> relation = user.getRelation("posts");
                            relation.add(listing);
                            user.saveInBackground();
                            Toast.makeText(NewListingActivity.this, getString(R.string.submittedToast), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NewListingActivity.this, BaseActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });


                }
            }
        });


    }

    private void getRestaurants() {
        ParseQuery<Restaurant> query = ParseQuery.getQuery("Restaurant");
        query.whereEqualTo("location", ParseUser.getCurrentUser().getParseObject("location"));
        query.setLimit(1000);
        query.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(List<Restaurant> restaurants, ParseException e) {
                for (Restaurant i : restaurants) {
                    mRestaurants.add(i.getString("name"));
                }
                setUpAdapter(mRestaurants);
            }
        });
    }

    private void setUpAdapter(ArrayList<String> names) {
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, names);
        mRestaurantField.setAdapter(mAdapter);
    }

}
