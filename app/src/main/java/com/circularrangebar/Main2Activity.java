package com.circularrangebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        try {
            EditText shour = (EditText) findViewById(R.id.hour);
            EditText smin = (EditText) findViewById(R.id.minutes);
            EditText eMin = (EditText) findViewById(R.id.endmin);
            EditText eH = (EditText) findViewById(R.id.endh);
            int h = Integer.valueOf(shour.getText().toString()) % 24;
            int m = Integer.valueOf(smin.getText().toString()) % 60;
            int eh = Integer.valueOf(eH.getText().toString()) % 24;
            int emin = Integer.valueOf(eMin.getText().toString()) % 60;
            circularRangeBar.addAppointment(h, m, eh, emin);
        } catch (NumberFormatException e) {
            Toast.makeText(Main2Activity.this, "Insert Valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    public void hide(View v) {
        circularRangeBar.setLeftThumbAngle(0);
        circularRangeBar.setRightThumbAngle(0);
        circularRangeBar.hideCurrentProgress();
    }
}
