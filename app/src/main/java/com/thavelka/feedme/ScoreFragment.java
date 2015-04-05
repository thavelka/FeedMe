package com.thavelka.feedme;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreFragment extends Fragment {

    public static final String TAG = ScoreFragment.class.getSimpleName();

    List<String> mScores;
    ParseUser mCurrentUser = ParseUser.getCurrentUser();
    ListView mListView;

    TextView mLocationTextView;
    String mLocation;

    public static ScoreFragment newInstance() {
        return new ScoreFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_score, container, false);

        getActivity().setTitle(getString(R.string.topContributors));

        mListView = (ListView) root.findViewById(R.id.scoreList);
        mLocationTextView = (TextView) root.findViewById(R.id.locationTextView);
        mScores = new ArrayList<>();

        new getTopScores().execute();

        return root;
    }

    public class getTopScores extends AsyncTask<Void, Void, List<ParseUser>> {

        @Override
        protected List<ParseUser> doInBackground(Void... params) {

            ParseObject LOCATION = mCurrentUser.getParseObject("location");

            try {
                mLocation = LOCATION.fetchIfNeeded().getString("city") + ", "
                        + LOCATION.fetchIfNeeded().getString("state");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("location", LOCATION);
            query.whereEqualTo("show", true);
            query.whereGreaterThan("score", 0);
            query.orderByDescending("score");
            query.setLimit(20);
            List<ParseUser> users = null;
            try {
                users = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return users;
        }

        @Override
        protected void onPostExecute(List<ParseUser> users) {
            super.onPostExecute(users);
            mLocationTextView.setText(mLocation);
            UserAdapter adapter = new UserAdapter(getActivity(), R.layout.user_list_item, users);
            mListView.setAdapter(adapter);


        }
    }


}
