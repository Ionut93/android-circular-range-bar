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
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        circularRangeBar = (CircularRangeBar) findViewById(R.id.circularRangeBar);
        circularRangeBar.setOnCircularSeekBarChangeListener(new CircularRangeBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularRangeBar circularSeekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStopTrackingTouch(CircularRangeBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularRangeBar seekBar) {

            }

            @Override
            public void onInsideCircleClicked(CircularRangeBar seekBar) {
                Toast.makeText(Main2Activity.this, "Inside Circle", Toast.LENGTH_SHORT).show();
            }
        });

        button = (Button) findViewById(R.id.addView);

    }

    public void clear(View v) {

    }

    public void add(View v) {

    }

    public void hide(View v) {
        circularRangeBar.setLeftThumbAngle(0);
        circularRangeBar.setRightThumbAngle(0);
        circularRangeBar.hideCurrentProgress();
    }

    public void middle(View v) {
        Toast.makeText(this, "Map clicked", Toast.LENGTH_SHORT).show();
    }
}
