package com.thavelka.feedme;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    public final static String TAG = SettingsFragment.class.getSimpleName();
    List<ParseObject> mLocations;
    Spinner mLocationSpinner;
    Button mSaveButton;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        getActivity().setTitle("Settings");
        mLocationSpinner = (Spinner) root.findViewById(R.id.locationSpinner);
        mSaveButton = (Button) root.findViewById(R.id.saveButton);
        new GetLocations().execute();
        setSaveButtonListener();
        return root;
    }

    public void addItemsToSpinner(List<ParseObject> locations) {

        List<String> list = new ArrayList<String>();
        for (ParseObject i : locations) {
            String locationName = i.getString("city") + ", " + i.get("state");
            list.add(locationName);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(dataAdapter);
    }

    public void setSaveButtonListener() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                int spinnerPosition = mLocationSpinner.getSelectedItemPosition();
                ParseObject userLocation = mLocations.get(spinnerPosition);
                user.put("location", ParseObject.createWithoutData("Location", userLocation.getObjectId()));
                user.saveInBackground();
                Toast.makeText(getActivity(), "Settings saved", Toast.LENGTH_SHORT).show();
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
