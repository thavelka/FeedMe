package com.thavelka.feedme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class NewListingActivity extends ActionBarActivity {

    public static final String TAG = NewListingActivity.class.getSimpleName();

    @InjectView(R.id.restaurantField)
    EditText mRestaurantField;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing);
        ButterKnife.inject(this);

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
                Intent intent = new Intent(NewListingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_listing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void uploadNewListing(final String name, final ArrayList<Integer> days, final String description, final boolean isFood) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
        query.whereStartsWith("name", name);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d(TAG, "Failed to find restaurant.");
                } else {
                    Log.d(TAG, "Retrieved the restaurant.");
                    Listing listing = new Listing();
                    String objId = object.getObjectId();
                    listing.put("restaurant", ParseObject.createWithoutData("Restaurant", objId));
                    listing.put("days", days);
                    listing.put("description", description);
                    listing.put("isFood", isFood);
                    listing.saveInBackground();

                }
            }
        });


    }
}
