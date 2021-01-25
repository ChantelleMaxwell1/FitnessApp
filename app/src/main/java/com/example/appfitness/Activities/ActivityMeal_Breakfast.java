package com.example.appfitness.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appfitness.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityMeal_Breakfast extends AppCompatActivity {

    private Button addNewBreakfast;

    private DatabaseReference mDBRef;
    private FirebaseDatabase mDB;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal__breakfast);

        addNewBreakfast = (Button) findViewById(R.id.btn_add_new_breakfast);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDB = FirebaseDatabase.getInstance();
        mDBRef = mDB.getReference().child("Meals").child("Breakfast");


        addNewBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(ActivityMeal_Breakfast.this, Activity_add_breakfast.class));
            }
        });

    }
}