package com.circularrangebar;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.circularrangebar.CircularRangeBar.AppointmentView;
import com.circularrangebar.CircularRangeBar.CircularRangeBar;
import com.circularrangebar.Views.SeekBar;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private final int CHART_ENTRY_VALUE = 15;
    CircularRangeBar circularRangeBar;
    PieChart mChart;
    Button button;
    List<AppointmentView> appointmentViewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circularRangeBar = (CircularRangeBar) findViewById(R.id.circularRangeBar);
        mChart = (PieChart) findViewById(R.id.piechart);
        mChart.setMaxHighlightDistance(0);
        mChart.setData(generatePieData());
        mChart.setHoleRadius(dpToPx(30));
        mChart.setTransparentCircleRadius(0);
        mChart.setRotationEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setDrawEntryLabels(false);
        Description description = new Description();
        description.setText("");
        mChart.setDescription(description);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mChart.highlightValues(null);
                PieEntry pieEntry = (PieEntry) e;
                circularRangeBar.setVisibility(View.VISIBLE);
                int indexOfEntry = Integer.valueOf(pieEntry.getLabel());
                int startAngel = (indexOfEntry * CHART_ENTRY_VALUE + (int) circularRangeBar.getStartAngle()) % 360;
                int startProgress = CHART_ENTRY_VALUE;
                circularRangeBar.setLeftThumbAngle(startAngel);
                circularRangeBar.setProgress(startProgress);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        button = (Button) findViewById(R.id.addView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circularRangeBar.addCurrentAppointment();
            }
        });
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    protected PieData generatePieData() {

        int count = 24;

        ArrayList<PieEntry> entries1 = new ArrayList<PieEntry>();

        for (int i = 0; i < count; i++) {
            entries1.add(new PieEntry((float) (30), String.valueOf(i)));
        }

        PieDataSet ds1 = new PieDataSet(entries1, "");
        ds1.setHighlightEnabled(false);
        ds1.setDrawValues(false);
        ds1.setColors(Color.parseColor("#d3d3d3"));
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.TRANSPARENT);
        ds1.setValueTextSize(12f);

        PieData d = new PieData(ds1);

        return d;
    }


}
