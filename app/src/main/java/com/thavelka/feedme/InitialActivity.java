package com.thavelka.feedme;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.parse.ui.ParseLoginDispatchActivity;


public class InitialActivity extends ParseLoginDispatchActivity {

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

}
