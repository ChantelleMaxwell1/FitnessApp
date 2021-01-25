package com.example.appfitness.Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfitness.Adapter.MealRecyclerAdapter;
import com.example.appfitness.Model.Meal;
import com.example.appfitness.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.graphics.Color.RED;

public class Activity_user_foodDiary extends AppCompatActivity {

    // Meals
    private Button add_meal;
    private RecyclerView mealView;
    private MealRecyclerAdapter mealRecyclerAdapter;
    private List<Meal> mealList;

    // calories
    private TextView goal_cal;
    private TextView user_cal;
    private ProgressBar progressBar;
    private int totalUserCal = 0;
    private double userGoalCalories;
    private Integer userCurrentWeight;

    //private TextView userProgCal;

    // Water
    private TextView water;
    private Button add_glass;
    private Button minus_glass;
    private int totalWater;

    // Database
    private DatabaseReference mDBRef;
    private DatabaseReference mDBRef2;
    private DatabaseReference mDBRef3;
    private FirebaseDatabase mDB;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_food_diary);

        // Meal
        add_meal = (Button) findViewById(R.id.btn_add_new_meal);

        mealList = new ArrayList<>();
        mealView = (RecyclerView) findViewById(R.id.meal_recyclerView);
        mealView.setHasFixedSize(true);
        mealView.setLayoutManager(new LinearLayoutManager(this));

        //Calories
        user_cal = (TextView) findViewById(R.id.user_cal);
        goal_cal = (TextView) findViewById(R.id.goal_cal);

        // progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //userProgCal = (TextView) findViewById(R.id.progCal);

        // Water
        water = (TextView) findViewById(R.id.txt_water);
        add_glass = (Button) findViewById(R.id.btn_add_water);
        minus_glass = (Button) findViewById(R.id.btn_minus_water);


        // Database
        mAuth = FirebaseAuth.getInstance();
        //mUser = mAuth.getCurrentUser();
        String userID = mAuth.getCurrentUser().getUid();
        mDB = FirebaseDatabase.getInstance();
        //mDBRef = mDB.getReference().child("User_meal").child(userID);
        // new
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        final String DBDate = formatDate.format(currentDate);

        mDBRef = mDB.getReference().child("User_meal").child(userID).child(DBDate);
        mDBRef2 = mDB.getReference().child("User_info").child(userID);
        mDBRef3 = mDB.getReference().child("User_history").child(userID);

        mDBRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uGoalCal = snapshot.child("Goal cal").getValue().toString();
                String userWeight = snapshot.child("Weight").getValue().toString();
                userGoalCalories = Double.parseDouble(uGoalCal);
                userCurrentWeight = Integer.parseInt(userWeight);
                goal_cal.setText(uGoalCal);
                //water.setText(totalWater);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       mDBRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                String userID = mAuth.getCurrentUser().getUid();
                final DatabaseReference currentUserDB = mDBRef3;

                Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                final String DBDate = formatDate.format(currentDate);


                //Integer user_calories = 0;
                if(snapshot.child(DBDate).exists()){
                    //Double cal_percent = null;
                    //Integer progressPercent = 0;
                    if(snapshot.child(DBDate).child("User_calories").exists()){
                        String userDBCal = snapshot.child(DBDate).child("User_calories").getValue().toString();
                        //user_calories = Integer.parseInt(userDBCal);
                        totalUserCal = Integer.parseInt(userDBCal);
                        //user_cal.setText(user_calories);
                        user_cal.setText(userDBCal);

                        String currentGoalCal = goal_cal.getText().toString();
                        double currGoalCal = Double.parseDouble(currentGoalCal);
                        int convertGoalCal = (int)Math.round(currGoalCal);

                        /*Integer calPercent = ((user_calories/convertGoalCal) * 100);
                        progressBar.setProgress(calPercent.intValue());
                        userProgCal.setText(calPercent.toString());*/
                        int val = 100;
                        float calPercent = (float)totalUserCal/(float)convertGoalCal*100;
                        int finalCal = Math.round(calPercent);
                        progressBar.setProgress((int)calPercent);
                        //userProgCal.setText(String.valueOf(calPercent));
                        //userProgCal.setText(String.valueOf(finalCal));

                        if(finalCal > val){
                            progressBar.setProgressTintList(ColorStateList.valueOf(RED));
                        }
                    }

                    String userDBWater = "";
                    if(snapshot.child(DBDate).child("User_water").exists()){
                        userDBWater = snapshot.child(DBDate).child("User_water").getValue().toString();
                    }

                    //Integer user_water = Integer.parseInt(userDBWater);
                    totalWater = Integer.parseInt(userDBWater);
                    //water.setText(user_water);
                    water.setText(userDBWater);

                } else {
                    //Integer userGoalCal = Integer.parseInt(goal_cal);
                    Integer newCal = 0;
                    currentUserDB.child(DBDate).child("Goal_calories").setValue(userGoalCalories);
                    //currentUserDB.child(DBDate).child("User_calories").setValue(user_calories);
                    currentUserDB.child(DBDate).child("User_calories").setValue(totalUserCal);
                    currentUserDB.child(DBDate).child("User_water").setValue(newCal);
                    currentUserDB.child(DBDate).child("Weight").setValue(userCurrentWeight);
                    //String newCal = snapshot.child(DBDate).child("User_calories")
                    user_cal.setText(0);
                    water.setText(0);
                }

                // WATER SECTION
                add_glass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        totalWater += 1;
                        String updateWater = Integer.toString(totalWater);
                        water.setText(updateWater);

                        currentUserDB.child(DBDate).child("User_water").setValue(totalWater);
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

                            currentUserDB.child(DBDate).child("User_water").setValue(totalWater);
                        }
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Meal code
        add_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_user_foodDiary.this, Activity_user_addMeal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //startActivity( new Intent(Activity_user_foodDiary.this, Activity_user_addMeal.class));
            }
        });

        /*
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
        });*/
    }

    /*private void updateUserCal() {
        String userID = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDB = mDBRef;
        //mDBRef = mDB.getReference().child("User_meal").child(userID);
        //currentUserDB.child("User_meal").child(userID)
        user_cal.setText();


    }*/

    @Override
    protected void onStart() {
        super.onStart();
        mDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Meal meal = snapshot.getValue(Meal.class);

                mealList.add(meal);

                mealRecyclerAdapter = new MealRecyclerAdapter(Activity_user_foodDiary.this, mealList);
                mealView.setAdapter(mealRecyclerAdapter);
                mealRecyclerAdapter.notifyDataSetChanged();

                //totalUserCal += Integer.parseInt(meal.calories);
                //user_cal.setText(totalUserCal);
                //totalUserCal = (totalUserCal);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                startActivity(new Intent(Activity_user_foodDiary.this, Activity_home.class));
                return true;
            case R.id.menu_logout:
                mAuth.signOut();
                startActivity(new Intent(Activity_user_foodDiary.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}