package com.example.appfitness.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appfitness.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity_userProgress extends AppCompatActivity {

    private BarChart barChart;
    private LineChart lineChart;
    private BarChart waterChart;
    //private TextView test;

    //private ArrayList<BarEntry> dataVals = new ArrayList<BarEntry>();
    private Integer userCal = 0;
    private Integer userWeight = 0;
    private Integer userWater = 0;

    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;
    private DatabaseReference mDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_progress);

        barChart = findViewById(R.id.barchart_cal);
        lineChart = findViewById(R.id.linechart_weight);
        waterChart = findViewById(R.id.barchart_water);
        //test = findViewById(R.id.testData);

        /*BarDataSet barDataSet1 = new BarDataSet(dataValues1(), "DataSet 1");
        barDataSet1.setColor(Color.parseColor("#80B12C"));

        BarData barData = new BarData();
        barData.addDataSet(barDataSet1);

        barChart.setData(barData);
        barChart.invalidate();*/

        mDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        mDBRef = mDB.getReference().child("User_history").child(userID);
        
        retrieveData();
    }

    private void retrieveData() {
        mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> dataVals = new ArrayList<BarEntry>();
                ArrayList<Entry> lineVals = new ArrayList<Entry>();
                ArrayList<BarEntry> waterVals = new ArrayList<BarEntry>();

                // NEW CODE
                int counter = 0;
                /*DataSnapshot dataSnap = (DataSnapshot) snapshot.getChildren();
                for(int i = 0; i < snapshot.getChildrenCount(); ++i){
                    String uCal = dataSnap.child("User_calories").getValue().toString();
                    userCal = Integer.parseInt(uCal);
                    dataVals.add(new BarEntry(counter, userCal));
                    ++counter;
                }*/
                for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
                    int count2 = (int) entrySnapshot.getChildrenCount();
                    String uCal = entrySnapshot.child("User_calories").getValue().toString();
                    String uWeight = entrySnapshot.child("Weight").getValue().toString();
                    String uWater = entrySnapshot.child("User_water").getValue().toString();
                    userCal = Integer.parseInt(uCal);
                    userWeight = Integer.parseInt(uWeight);
                    userWater = Integer.parseInt(uWater);
                    dataVals.add(new BarEntry(counter, userCal));
                    lineVals.add(new Entry(counter, userWeight));
                    waterVals.add(new BarEntry(counter, userWater));
                    ++counter;
                }

                // CALORIES BAR CHART CODE
                BarDataSet barDataSet1 = new BarDataSet(dataVals, "Total Calorie intake");
                barDataSet1.setColor(Color.parseColor("#80B12C"));

                BarData barData = new BarData();
                barData.addDataSet(barDataSet1);
                barChart.setData(barData);
                YAxis rightYAxis = barChart.getAxisRight();
                barChart.getXAxis().setEnabled(false);
                rightYAxis.setEnabled(false);
                Description desc = barChart.getDescription();
                desc.setEnabled(false);
                barChart.setVisibleXRangeMaximum(5);
                //barChart.clear();
                barChart.invalidate();

                // LINE CHART CODE
                LineDataSet lineDataSet1 = new LineDataSet(lineVals, "Weight changes");
                lineDataSet1.setColor(Color.parseColor("#80B12C"));
                lineDataSet1.setCircleColor(Color.parseColor("#80B12C"));
                ArrayList<ILineDataSet> lineDataSet = new ArrayList<>();
                lineDataSet.add(lineDataSet1);
                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);
                lineChart.getXAxis().setEnabled(false);
                lineChart.getAxisRight().setEnabled(false);
                lineChart.setVisibleXRangeMaximum(5);
                lineChart.invalidate();

                // WATER BAR CHART
                BarDataSet barDataSet2 = new BarDataSet(waterVals, "Total Glasses of water");
                barDataSet2.setColor(Color.parseColor("#1eb2db"));

                BarData barData2 = new BarData();
                barData2.addDataSet(barDataSet2);
                waterChart.setData(barData2);
                YAxis rightYAxis2 = waterChart.getAxisRight();
                waterChart.getXAxis().setEnabled(false);
                rightYAxis2.setEnabled(false);
                Description desc2 = waterChart.getDescription();
                desc2.setEnabled(false);
                YAxis leftYAxis2 = waterChart.getAxisLeft();
                leftYAxis2.setAxisMaximum(12);
                leftYAxis2.setAxisMinimum(0);
                waterChart.setVisibleXRangeMaximum(5);
                //chart.moveViewToX(10); // set the left edge of the chart to x-index 10
                waterChart.invalidate();
                //test.setText(dataVals.toString());
                /*
                if(snapshot.hasChildren()){
                    Map<String, Object> td = (HashMap<String, Object>) snapshot.getValue();
                    //List<Object> values = new Arraylist<>(td.values());
                    //String userCal = snapshot.child().child("User_calories").getValue();
                    for (int i = 0; i < snapshot.getChildrenCount(); i++){
                        //dataVals.add(new BarEntry(i, snapshot.))
                    }
                }*/
           }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*private ArrayList<BarEntry> dataValues1(){
        final ArrayList<BarEntry> dataVals = new ArrayList<>();

        mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
                    for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                        //Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                        String uCal = entrySnapshot.child("User_calories").getValue().toString();
                        userCal = Integer.parseInt(uCal);
                        dataVals.add(new BarEntry(i, userCal));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dataVals.add(new BarEntry(0, 3));
        dataVals.add(new BarEntry(1, 5));
        dataVals.add(new BarEntry(2, 16));
        dataVals.add(new BarEntry(3, 6));
        dataVals.add(new BarEntry(4, 11));

        return dataVals;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                startActivity(new Intent(Activity_userProgress.this, Activity_home.class));
                return true;
            case R.id.menu_logout:
                mAuth.signOut();
                startActivity(new Intent(Activity_userProgress.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}