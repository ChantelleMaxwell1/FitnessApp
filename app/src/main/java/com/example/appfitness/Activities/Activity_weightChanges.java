package com.example.appfitness.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Activity_weightChanges extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView units;
    private EditText uWeight;
    private EditText uHeight;
    private EditText uGoal;
    private TextView wUnits;
    private TextView hUnits;
    private TextView gUnits;

    //private static final String TAG = "Activity_weightChanges";
    private TextView userAge;
    private Spinner active_level;

    private Button update;
    private Button back;

    private TextView uCal;
    private TextView bmi;

    // workable variables
    private String SystemType;
    private String userBMI;
    private String gender;
    private String userCal;
    private String weight;
    private String height;
    private String DOB;
    private Double BMI;
    private Double BMR;
    private Integer age;
    private Double targetCal;

    private Integer userWater = 0;
    private Integer userCalories = 0;


    private DatabaseReference mDBRef;
    private DatabaseReference mDBRef2;
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_changes);

        // DATABASE
        mDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        // DATE
        Date currentDate = Calendar.getInstance().getTime();
        final SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        final String DBDate = formatDate.format(currentDate);
        // DB References
        mDBRef = mDB.getReference().child("User_info").child(userID);
        mDBRef2 = mDB.getReference().child("User_history").child(userID).child(DBDate);  // history table


        // inputs
        units = (TextView) findViewById(R.id.txt_units);
        uWeight = (EditText) findViewById(R.id.update_userWeight);
        uHeight = (EditText) findViewById(R.id.update_userHeight);
        uGoal = (EditText) findViewById(R.id.update_goalWeight);
        wUnits = (TextView) findViewById(R.id.txt_unit1);
        hUnits = (TextView) findViewById(R.id.txt_unit2);
        gUnits = (TextView) findViewById(R.id.txt_unit3);

        userAge = (TextView) findViewById(R.id.txt_age);
        uCal = (TextView) findViewById(R.id.txt_calIntake);
        bmi = (TextView) findViewById(R.id.txt_BMI);

        update = (Button) findViewById(R.id.btn_updateChanges);
        back = (Button) findViewById(R.id.btn_backHome);


        // spinner
        active_level = (Spinner) findViewById(R.id.spin_ActiveLevel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.active_level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        active_level.setAdapter(adapter);
        active_level.setOnItemSelectedListener(this);

        mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SystemType = snapshot.child("SystemType").getValue().toString();
                weight = snapshot.child("Weight").getValue().toString();
                height = snapshot.child("Height").getValue().toString();
                String goal = snapshot.child("Goal weight").getValue().toString();
                String active = snapshot.child("Active").getValue().toString();
                userBMI = snapshot.child("BMI").getValue().toString();
                gender = snapshot.child("Gender").getValue().toString();
                userCal = snapshot.child("Goal cal").getValue().toString();
                DOB = snapshot.child("DOB").getValue().toString();

                String userDOB = DOB;
                String birthYear = "";

                if(userDOB.length() > 4){
                    birthYear = userDOB.substring(userDOB.length() - 4);
                }

                Integer DOByear = Integer.parseInt(birthYear);

                Calendar cal = Calendar.getInstance();
                Integer year = cal.get(Calendar.YEAR);

                age = (year - DOByear);
                userAge.setText(age.toString());

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

                uWeight.setText(weight);
                uHeight.setText(height);
                uGoal.setText(goal);
                //gactive.setText(active);
                bmi.setText(userBMI);
                uCal.setText(userCal);

                if(active.equals("Not active")){
                    active_level.setSelection(0);
                } else if (active.equals("Somewhat active")){
                    active_level.setSelection(1);
                } else if (active.equals("Active")){
                    active_level.setSelection(2);
                } else {
                    active_level.setSelection(3);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDBRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String water = snapshot.child("User_water").getValue().toString();
                    userWater = Integer.parseInt(water);

                    String historyCal = snapshot.child("User_calories").getValue().toString();
                    userCalories = Integer.parseInt(historyCal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String userID = mAuth.getCurrentUser().getUid();
                DatabaseReference currentUser = mDBRef;
                DatabaseReference currentHistory = mDBRef2;

                // user input changes
                String newWeight = uWeight.getText().toString().trim();
                Double w = Double.parseDouble(newWeight);
                String newHeight = uHeight.getText().toString().trim();
                Double h = Double.parseDouble(newHeight);
                String newGoal = uGoal.getText().toString().trim();
                Double g = Double.parseDouble(newGoal);
                String active = active_level.getSelectedItem().toString();
                String uAge = userAge.getText().toString();
                Integer AGE = Integer.parseInt(uAge);
                String userBMI = bmi.getText().toString();
                Double saveBMI = Double.parseDouble(userBMI);

                // input changes into DB
                //currentUser.child("Weight").setValue(w);
                //currentUser.child("Height").setValue(h);
                //currentUser.child("Goal weight").setValue(g);
                //currentUser.child("Active").setValue(active);

                // WORK OUT AGE
                /*String userDOB = DOB;
                String birthYear = "";

                if(userDOB.length() > 4){
                    birthYear = userDOB.substring(userDOB.length() - 4);
                }

                Integer DOByear = Integer.parseInt(birthYear);

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);

                //age = (year - DOByear);
                */
                // WORK OUT BMI
                if(SystemType.equals("metric") || SystemType.equals("METRIC")){
                    BMI = ((w/h/h)*10000);
                    Double roundBMI = Math.round(BMI * 100.0) / 100.0;
                    bmi.setText(roundBMI.toString());
                    if(gender.equals("male")){
                        BMR = (66 + (13.8 * w) + (5 * h) - (6.8 * AGE));
                    } else {
                        // female and metric
                        BMR = (655 + (9.6 * w) + (1.8 * h) - (4.7 * AGE));
                    }
                } else {
                    BMI = ((w/h/h) * 703);
                    Double roundBMI = Math.round(BMI * 100.0) / 100.0;
                    bmi.setText(roundBMI.toString());
                    if(gender.equals("male")){
                        BMR = (66 + (6.23 * w) + (12.7 * h) - (6.8 * AGE));
                    } else {
                        // female and imperial
                        BMR = (655 + (4.35 * w) + (4.7 * h) - (4.7 * AGE));
                    }
                }

                //userAge.setText(DOByear);
                //userAge.setText(DOByear);

                // WORK OUT GOAL CALORIE INTAKE
               if(active.equals("Not active")){
                    targetCal = (BMR * 1.2);
                } else if (active.equals("Somewhat active")){
                    targetCal = (BMR * 1.375);
                } else if (active.equals("Active")){
                    targetCal = (BMR * 1.55);
                } else {
                    targetCal = (BMR * 1.725);
                }
                Double roundGoalCal = Math.round(targetCal * 100.0) / 100.0;
                uCal.setText(roundGoalCal.toString());


                // input changes into DB
                currentUser.child("Weight").setValue(w);
                currentUser.child("Height").setValue(h);
                currentUser.child("Goal weight").setValue(g);
                currentUser.child("Active").setValue(active);
                currentUser.child("Goal cal").setValue(roundGoalCal);
                currentUser.child("BMI").setValue(saveBMI);


                currentHistory.child("Goal_calories").setValue(roundGoalCal);
                currentHistory.child("Weight").setValue(w);
                currentHistory.child("User_calories").setValue(userCalories);
                currentHistory.child("User_water").setValue(userWater);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_weightChanges.this, Activity_home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String text = adapterView.getItemAtPosition(position).toString();
        //Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                startActivity(new Intent(Activity_weightChanges.this, Activity_home.class));
                return true;
            case R.id.menu_logout:
                mAuth.signOut();
                startActivity(new Intent(Activity_weightChanges.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}