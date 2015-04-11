package com.thavelka.feedme;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.ui.ParseLoginDispatchActivity;

import butterknife.InjectView;


public class InitialActivity extends ParseLoginDispatchActivity {

    @InjectView(R.id.tutorialImage1)
    ImageView mTutorial1;
    @InjectView(R.id.nextButton)
    Button mButton;

    int mCount;

    @Override
    protected Class<?> getTargetClass() {
        return BaseActivity.class;
    }

    @Override
    protected Intent getParseLoginIntent() {
        return super.getParseLoginIntent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_initial);
    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_initial);
//        ButterKnife.inject(this);
//        // CHECK USER LOGGED IN
//        ParseAnalytics.trackAppOpened(getIntent());
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        if (currentUser != null) {
//            // Allow user to continue, send user's info to header in drawer
//            Intent intent = new Intent(InitialActivity.this, BaseActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        } else {
//            if (isFirstTime()) {
//                mCount = 0;
//                Picasso.with(InitialActivity.this).load(R.drawable.tutorial1).into(mTutorial1, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        mButton.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
//                mButton.setVisibility(View.VISIBLE);
//                mButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Send user to signup
//                        Intent intent = new Intent(InitialActivity.this, SignUpActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    }
//                });
//            } else {
//                // Send user to login
//                ParseLoginBuilder builder = new ParseLoginBuilder(this);
//                startActivityForResult(builder.build(), 0);
//            }
//
//
//        }
//    }
//
//    /**
//     * Checks that application runs first time and write flag at SharedPreferences
//     *
//     * @return true if 1st time
//     */
//    private boolean isFirstTime() {
//        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
//        boolean ranBefore = preferences.getBoolean("RanBefore", false);
//        if (!ranBefore) {
//            // first time
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putBoolean("RanBefore", true);
//            editor.apply();
//
//        }
//        return !ranBefore;
//
//    }


}
