package com.circularrangebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.circularrangebar.CircularRangeBar.CircularRangeBar;

public class Main2Activity extends AppCompatActivity {

    CircularRangeBar circularRangeBar;
    boolean hideProgress = false;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        circularRangeBar = (CircularRangeBar) findViewById(R.id.circularRangeBar);


        button = (Button) findViewById(R.id.addView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circularRangeBar.addCurrentAppointment();
            }
        });
    }

    public void clear(View v) {
        circularRangeBar.cleanAppointments();
    }

    public void add(View v) {
        EditText hour = (EditText) findViewById(R.id.hour);
        EditText min = (EditText) findViewById(R.id.minutes);
        int h = Integer.valueOf(hour.getText().toString());
        int m = Integer.valueOf(min.getText().toString());
        circularRangeBar.addAppointment(h, 15, m, 30);
    }

    public void hide(View v) {
        circularRangeBar.setLeftThumbAngle(0);
        circularRangeBar.setRightThumbAngle(0);
        circularRangeBar.hideCurrentProgress();
    }
}
