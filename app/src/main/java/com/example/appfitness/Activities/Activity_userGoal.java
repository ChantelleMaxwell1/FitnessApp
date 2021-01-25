package com.example.appfitness.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appfitness.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_userGoal extends AppCompatActivity {

    private TextView units;
    private TextView gweight;
    private TextView gheight;
    private TextView goalWeight;
    private TextView gactive;

    private TextView wUnits;
    private TextView hUnits;
    private TextView gUnits;

    private TextView BMI;
    private TextView BMIrange;
    private TextView calories;

    private Button gUpdate;
    private Button gBack;

    private DatabaseReference mDBRef;
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_goal);

        units = (TextView) findViewById(R.id.tUnits);
        gweight = (TextView) findViewById(R.id.tWeight);
        gheight = (TextView) findViewById(R.id.tHeight);
        goalWeight = (TextView) findViewById(R.id.tGoalWeight);
        gactive = (TextView) findViewById(R.id.tActive);

        wUnits = (TextView) findViewById(R.id.weightUnits);
        hUnits = (TextView) findViewById(R.id.heightUnits);
        gUnits = (TextView) findViewById(R.id.goalUnits);

        BMI = (TextView) findViewById(R.id.BMI);
        BMIrange = (TextView) findViewById(R.id.BMIrange);
        calories = (TextView) findViewById(R.id.calories);

        gUpdate = (Button) findViewById(R.id.btn_updateGoal);
        gBack = (Button) findViewById(R.id.btn_tBack);

        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        mDB = FirebaseDatabase.getInstance();
        mDBRef = mDB.getReference().child("User_info").child(userID);


        mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String SystemType = snapshot.child("SystemType").getValue().toString();
                String weight = snapshot.child("Weight").getValue().toString();
                String height = snapshot.child("Height").getValue().toString();
                String goal = snapshot.child("Goal weight").getValue().toString();
                String active = snapshot.child("Active").getValue().toString();
                String userBMI = snapshot.child("BMI").getValue().toString();
                String userCal = snapshot.child("Goal cal").getValue().toString();

                units.setText(SystemType);

                if(SystemType.equals("metric")){
                    wUnits.setText("kg");
                    hUnits.setText("cm");
                    gUnits.setText("kg");

                } else {
                    wUnits.setText("lbs");
                    hUnits.setText("inches");
                    gUnits.setText("lbs");
                }

                gweight.setText(weight);
                gheight.setText(height);
                goalWeight.setText(goal);
                gactive.setText(active);
                BMI.setText(userBMI);
                calories.setText(userCal);

                Double numBMI = Double.parseDouble(userBMI);

                if(numBMI < 18.5){
                    BMIrange.setText("Underweight");
                } else if (numBMI < 24.9){
                    BMIrange.setText("Healthy weight");
                } else if (numBMI < 29.9){
                    BMIrange.setText("Overweight");
                } else {
                    BMIrange.setText("Obese");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        gBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_userGoal.this, Activity_home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //startActivity(new Intent(Activity_userGoal.this, Activity_home.class));
            }
        });

        gUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_userGoal.this, Activity_weightChanges.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //startActivity(new Intent(Activity_userGoal.this, Activity_weightChanges.class));
            }
        });

    }

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
                startActivity(new Intent(Activity_userGoal.this, Activity_home.class));
                return true;
            case R.id.menu_logout:
                mAuth.signOut();
                startActivity(new Intent(Activity_userGoal.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}