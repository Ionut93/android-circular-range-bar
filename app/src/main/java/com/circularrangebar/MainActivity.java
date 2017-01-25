package com.circularrangebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.circularrangebar.CircularRangeBar.CircularRangeBar;
import com.circularrangebar.Views.SeekBar;

public class MainActivity extends AppCompatActivity {

    Button btnLeft, btnRight;
    CircularRangeBar circularRangeBar;
    TextView progressValue, leftAngle, rightAngle;
    int progress;
    float startangle, endangle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLeft = (Button) findViewById(R.id.left);
        btnRight = (Button) findViewById(R.id.right);
        circularRangeBar = (CircularRangeBar) findViewById(R.id.circularRangeBar);
        progressValue = (TextView) findViewById(R.id.progressValue);
        leftAngle = (TextView) findViewById(R.id.leftTAngle);
        rightAngle = (TextView) findViewById(R.id.rigthTangle);
        startangle = circularRangeBar.getStartAngle();
        endangle = circularRangeBar.getEndAngle();
        progress = circularRangeBar.getProgress();
        progressValue.setText(String.valueOf(progress));
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = circularRangeBar.getProgress();
                progress += 50;
                circularRangeBar.setProgress(progress);
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startangle = circularRangeBar.getStartAngle();
                startangle +=50;
                circularRangeBar.setLeftThumbAnglePoint(startangle, 50);
            }
        });
        circularRangeBar.setOnCircularSeekBarChangeListener(new CircularRangeBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularRangeBar circularSeekBar, int progress, boolean fromUser) {
                progressValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStopTrackingTouch(CircularRangeBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularRangeBar seekBar) {

            }
        });
    }
}
