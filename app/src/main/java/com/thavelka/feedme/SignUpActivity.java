package com.thavelka.feedme;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SignUpActivity extends ActionBarActivity {

    public static final String TAG = SignUpActivity.class.getSimpleName();

    @InjectView(R.id.usernameField)
    EditText mUserNameField;
    @InjectView(R.id.fullNameField)
    EditText mFullNameField;
    @InjectView(R.id.passwordField)
    EditText mPasswordField;
    @InjectView(R.id.emailField)
    EditText mEmailField;
    @InjectView(R.id.locationSpinner)
    Spinner mLocationSpinner;
    @InjectView(R.id.signUpButton)
    Button mSignUpButton;


    List<ParseObject> mLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.inject(this);

        new GetLocations().execute();

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUserNameField.getText().toString();
                String fullname = mFullNameField.getText().toString();
                String password = mPasswordField.getText().toString();
                String email = mEmailField.getText().toString();
                int spinnerPosition = mLocationSpinner.getSelectedItemPosition();
                ParseObject userLocation = mLocations.get(spinnerPosition);
                signUpUser(username, fullname, password, email, userLocation);
            }
        });

    }

    public void addItemsToSpinner(List<ParseObject> locations) {

        List<String> list = new ArrayList<>();
        for (ParseObject i : locations) {
            String locationName = i.getString("city") + ", " + i.get("state");
            list.add(locationName);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(dataAdapter);
    }

    protected void signUpUser(String username, String fullname, String password, String email, ParseObject userLocation) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("name", fullname);
        user.put("location", ParseObject.createWithoutData("Location", userLocation.getObjectId()));

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(SignUpActivity.this, "User created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
