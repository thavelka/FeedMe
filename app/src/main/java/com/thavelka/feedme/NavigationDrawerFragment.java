package com.thavelka.feedme;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Locale;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    protected String dayOfWeek;
    private String NAME = "NAME";
    private String EMAIL = "EMAIL";
    private int SCORE = 0;
    private String CITY_STATE;
    private String IMAGE_URL;
    private String[] TITLES = {"Home", "Favorites", "Leaderboard", "Settings", "Suggestions"};
    private int[] ICONS = {R.mipmap.ic_home_grey600_24dp, R.mipmap.ic_favorite_grey600_24dp, R.mipmap.ic_star_grey600_24dp,
            R.mipmap.ic_settings_grey600_24dp, R.mipmap.ic_chat_grey600_24dp};
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;
    private RecyclerView.Adapter drawerAdapter;
    private RecyclerView recyclerView;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        //        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Calendar mCalendar = Calendar.getInstance();
        dayOfWeek = mCalendar.getDisplayName
                (Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isDrawerOpen()) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
            } else {
                mDrawerLayout.openDrawer(mFragmentContainerView);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        if (ParseUser.getCurrentUser() != null) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            NAME = currentUser.getUsername();
            EMAIL = currentUser.getEmail();
            SCORE = currentUser.getInt("score");
            ParseObject LOCATION = ParseUser.getCurrentUser().getParseObject("location");
            try {
                CITY_STATE = LOCATION.fetchIfNeeded().getString("city") + ", "
                        + LOCATION.fetchIfNeeded().getString("state");
                IMAGE_URL = LOCATION.fetchIfNeeded().getString("imageUrl");
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        recyclerView = (RecyclerView) root.findViewById(R.id.drawerRecyclerView);

        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        drawerAdapter = new DrawerAdapter
                (getActivity(), TITLES, ICONS, NAME, EMAIL, SCORE, CITY_STATE, IMAGE_URL);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        selectItem(position);
                    }
                })
        );
        recyclerView.setAdapter(drawerAdapter);
        return root;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle
                (getActivity(), mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                getActionBar().setTitle(getActivity().getTitle());

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateDrawer();
                getActionBar().setTitle("FeedMe");
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }


    protected void selectItem(int position) {
        mCurrentSelectedPosition = position;
//        if (mRecyclerView != null) {
//            mRecyclerView.setItemChecked(position, true);
//        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public void updateDrawer() {
        if (ParseUser.getCurrentUser() != null) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            NAME = currentUser.getUsername();
            EMAIL = currentUser.getEmail();
            SCORE = currentUser.getInt("score");
            ParseObject LOCATION = currentUser.getParseObject("location");
            try {
                CITY_STATE = LOCATION.fetchIfNeeded().getString("city") + ", "
                        + LOCATION.fetchIfNeeded().getString("state");
                IMAGE_URL = LOCATION.fetchIfNeeded().getString("imageUrl");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            drawerAdapter = new DrawerAdapter
                    (getActivity(), TITLES, ICONS, NAME, EMAIL, SCORE, CITY_STATE, IMAGE_URL);
            recyclerView.setAdapter(drawerAdapter);
        }


    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
