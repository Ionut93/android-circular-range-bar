package com.circularrangebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.circularrangebar.Views.SeekBar;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBar;
    EditText valueChanger;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setCurrentValue(30);

        valueChanger = (EditText) findViewById(R.id.edittext);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.valueOf(valueChanger.getText().toString());
                seekBar.setValue(currentValue);
            }
        });
    }
}
