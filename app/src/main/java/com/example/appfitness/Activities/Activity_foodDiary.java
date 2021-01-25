package com.example.appfitness.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class Activity_foodDiary extends AppCompatActivity {

    // calories
    private TextView goal_cal;
    private TextView user_cal;
    private ProgressBar progressBar;
    private int totalUserCal = 0;

    // Breakfast
    private TextView Bcals;
    private Button breakfast;

    // Lunch
    private TextView Lcals;

    // Dinner
    private TextView Dcals;

    // Snacks
    private TextView Scals;

    // Water
    private TextView water;
    private Button add_glass;
    private Button minus_glass;
    private int totalWater = 0;

    // Database
    private DatabaseReference mDBRef;
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary);

        //Calories
        user_cal = (TextView) findViewById(R.id.user_cal);
        goal_cal = (TextView) findViewById(R.id.goal_cal);

        //Breakfast
        breakfast = (Button) findViewById(R.id.btn_breakfast);

        // Water
        water = (TextView) findViewById(R.id.txt_water);
        add_glass = (Button) findViewById(R.id.btn_AddWater);
        minus_glass = (Button) findViewById(R.id.btn_MinusWater);


        // Database
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        mDB = FirebaseDatabase.getInstance();
        mDBRef = mDB.getReference().child("User_info").child(userID);



        mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uGoalCal = snapshot.child("Goal cal").getValue().toString();

                goal_cal.setText(uGoalCal);
                //water.setText(totalWater);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // BREAKFAST CODE
        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_foodDiary.this, ActivityMeal_Breakfast.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // WATER CODE
        add_glass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalWater += 1;
                String updateWater = Integer.toString(totalWater);
                water.setText(updateWater);
            }
        });

        minus_glass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalWater == 0){
                    totalWater = 0;
                    String updateWater = Integer.toString(totalWater);
                    water.setText(updateWater);

                } else {
                    totalWater -= 1;
                    String updateWater = Integer.toString(totalWater);
                    water.setText(updateWater);
                }
            }
        });
    }
}