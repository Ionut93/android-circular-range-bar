package com.circularrangebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.circularrangebar.CircularRangeBar.CircularRangeBar;

public class Main2Activity extends AppCompatActivity {
    CircularRangeBar circularRangeBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        circularRangeBar = (CircularRangeBar) findViewById(R.id.circularRangeBar);
    }
}
