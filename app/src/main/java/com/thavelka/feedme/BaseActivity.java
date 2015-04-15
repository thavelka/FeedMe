package com.thavelka.feedme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;


public class BaseActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = BaseActivity.class.getSimpleName();

    Toolbar mToolbar;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // GET CURRENT USER
        ParseAnalytics.trackAppOpened(getIntent());
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // GET SCORE (number of approved posts by user)
        ParseRelation<ParseObject> relation = currentUser.getRelation("posts");
        ParseQuery<ParseObject> query = relation.getQuery();
        query.whereEqualTo("isApproved", true);
        query.setLimit(1000);

        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                // i = count of approved posts returned by query
                Log.d(TAG, "Found " + i + " posts by user");
                if (i < 0) { // Parse count returns -1 if no results, set score to 0
                    currentUser.put("score", 0);
                } else { // More than zero posts returns correct number
                    currentUser.put("score", i);
                }
                currentUser.saveInBackground();
            }
        });




        // SETTING UP TOOLBAR
        // Creating toolbar to be used as the activity's actionbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // SETTING UP NAV DRAWER
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // SET INITIAL FRAGMENT
        swapFrag(MainFragment.newInstance(), "MAIN", false);


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // starts fragment corresponding to position of touched item
        Fragment fragment;
        switch (position) {
            case 0:
                // on header click
                break;
            case 1:
                swapFrag(MainFragment.newInstance(), "MAIN", false);
                break;
            case 2:
                swapFrag(FavoritesFragment.newInstance(), "FAVORITES", true);
                break;
            case 3:
                swapFrag(ScoreFragment.newInstance(), "SCORES", true);
                break;
            case 4:
                swapFrag(SettingsFragment.newInstance(), "SETTINGS", true);
                break;
            case 5:
                sendReport();
                break;
            default:

        }


    }

    @SuppressWarnings("deprecation")
    public void restoreActionBar() {
        // what to show in the bar when the drawer is open
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // show different actionbar items if drawer open
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.base, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // signs user out, sends them to the login page
            ParseUser.logOut();
            Intent intent = new Intent(this, InitialActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendReport(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Suggestions");
        alert.setMessage("Is a restaurant missing? Did you find a bug? Share it here.");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 48;
        params.rightMargin = 48;
        params.topMargin = 48;
        params.bottomMargin = 16;
        input.setLayoutParams(params);
        container.addView(input);
        alert.setView(container);


        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (value.length() > 5) {
                    ParseObject report = new ParseObject("Report");
                    report.put("description", value);
                    report.saveInBackground();
                    Toast.makeText(BaseActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BaseActivity.this, "A description is needed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(BaseActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        alert.show();
    }

    private void swapFrag(Fragment fragment, String tag, boolean addToStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (addToStack) {
            fragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container, fragment, tag)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .commit();
        }
    }

}
