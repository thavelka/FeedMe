package com.thavelka.feedme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class NewListingActivity extends ActionBarActivity {

    public static final String TAG = NewListingActivity.class.getSimpleName();

    @InjectView(R.id.restaurantField)
    EditText mRestaurantField;
    @InjectView(R.id.descriptionField)
    EditText mDescriptionField;
    @InjectView(R.id.locationSpinner)
    Spinner mLocationSpinner;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.inject(this);

        new GetLocations().execute();

        mSubmitButton.setFocusableInTouchMode(true);

        FrameLayout touchInterceptor = (FrameLayout) findViewById(R.id.touchInterceptor);
        touchInterceptor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mRestaurantField.isFocused()) {
                        Rect outRect = new Rect();
                        mRestaurantField.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            mRestaurantField.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                    if (mDescriptionField.isFocused()) {
                        Rect outRect = new Rect();
                        mDescriptionField.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            mDescriptionField.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });

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

                int spinnerPosition = mLocationSpinner.getSelectedItemPosition();
                ParseObject listingLocation = mLocations.get(spinnerPosition);

                uploadNewListing(mName, mDays, mDescription, mIsFood, listingLocation);

            }
        });
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mDescriptionField.getWindowToken(), 0);
    }

    public void addItemsToSpinner(List<ParseObject> locations) {

        List<String> list = new ArrayList<String>();
        for (ParseObject i : locations) {
            String locationName = i.getString("city") + ", " + i.get("state");
            list.add(locationName);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(dataAdapter);
    }

    public void uploadNewListing(final String name, final ArrayList<Integer> days, final String description, final boolean isFood, final ParseObject listingLocation) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
        query.whereStartsWith("name", name);
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
                    Listing listing = new Listing();
                    String objId = object.getObjectId();
                    listing.put("restaurant", ParseObject.createWithoutData("Restaurant", objId));
                    listing.put("days", days);
                    listing.put("description", description);
                    listing.put("isFood", isFood);
                    listing.put("location", ParseObject.createWithoutData("Location", listingLocation.getObjectId()));
                    listing.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(NewListingActivity.this, "Post added successfully", Toast.LENGTH_SHORT).show();
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

    private class GetLocations extends AsyncTask<Void, Void, List<ParseObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<ParseObject> doInBackground(Void... params) {
            ParseQuery query = ParseQuery.getQuery("Location");
            try {
                mLocations = query.find();
                Log.d(TAG, "got " + mLocations.size() + " objects");
                return mLocations;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<ParseObject> locations) {
            super.onPostExecute(locations);
            addItemsToSpinner(locations);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }
}
