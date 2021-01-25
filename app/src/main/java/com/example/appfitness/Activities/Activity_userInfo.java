package com.example.appfitness.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appfitness.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Activity_userInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /*private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "Activity_userInfo";
    private RadioGroup radioGroup;
    private RadioButton btn_gender;
    private String gender;*/

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "Activity_userInfo";

    private Switch aSwitch;
    private String systemType;
    private TextView weight;
    private TextView height;
    private TextView goal;
    private EditText input_weight;
    private EditText input_height;
    private EditText input_goal;
    private Spinner active_level;

    private RadioGroup radioGroup;
    private RadioButton btn_gender;
    private String gender;

    private Button save_info;
    private Double BMI;
    private Double BMR;
    private int age;
    private Double goal_cal;

    private DatabaseReference mDBRef;
    private DatabaseReference mDBRef2; // history table
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mDB = FirebaseDatabase.getInstance();
        mDBRef = mDB.getReference().child("User_info");
        mDBRef2 = mDB.getReference().child("User_history");  // history table
        mAuth = FirebaseAuth.getInstance();

        mDisplayDate = (TextView) findViewById(R.id.DOB);

        aSwitch = (Switch) findViewById(R.id.toggle_metric);
        weight = (TextView) findViewById(R.id.txt_weight);
        height = (TextView) findViewById(R.id.txt_height);
        goal = (TextView) findViewById(R.id.txt_goalWeight);
        input_weight = (EditText) findViewById(R.id.user_weight);
        input_height = (EditText) findViewById(R.id.user_height);
        input_goal = (EditText) findViewById(R.id.user_goal);


        active_level = (Spinner) findViewById(R.id.spin_ActiveLevel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.active_level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        active_level.setAdapter(adapter);
        active_level.setOnItemSelectedListener(this);

        radioGroup = (RadioGroup) findViewById(R.id.btnGroup_gender);

        save_info = (Button) findViewById(R.id.btn_start);

        // GET DATE OF BIRTH
        mDisplayDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Activity_userInfo.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDataSet: date: " + month + "/" + day + "/" + year);
                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);

                age = (Calendar.getInstance().get(Calendar.YEAR) - year);

            }
        };

        // ACTIVITY LEVEL CODE
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked == true){
                    // toggle is set to imperial
                    weight.setText("pounds");
                    height.setText("inches");
                    goal.setText("pounds");
                    systemType = "imperial";
                } else {
                    // toggle is set to metric
                    weight.setText("kgs");
                    height.setText("cms");
                    goal.setText("kgs");
                    systemType = "metric";
                }
            }
        });

        // GENDER CODE
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkID) {
                btn_gender = (RadioButton) findViewById(checkID);
                switch (btn_gender.getId()){
                    case R.id.btn_male: {
                        if(btn_gender.isChecked()){
                            gender = "male";
                        }
                    }
                    break;
                    case R.id.btn_female: {
                        if(btn_gender.isChecked()){
                            gender = "female";
                        }
                    }
                    break;
                }
            }
        });

        save_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get users DOB
                String uDOB = mDisplayDate.getText().toString();
                // get checked id
                //String ugender = gender;
                // get metric or imperial
                String usystemType = systemType;
                //get users weight, height and goal
                String uweight = input_weight.getText().toString().trim();
                Double w = Double.parseDouble(uweight);
                String uheight = input_height.getText().toString().trim();
                Double h = Double.parseDouble(uheight);
                String ugoal = input_goal.getText().toString().trim();
                Double g = Double.parseDouble(ugoal);
                String ugender = gender;

                String active = active_level.getSelectedItem().toString();

                if(!TextUtils.isEmpty(uweight) && !TextUtils.isEmpty(uheight) && !TextUtils.isEmpty(ugoal) && !TextUtils.isEmpty(uDOB)
                && !TextUtils.isEmpty(ugender) && !TextUtils.isEmpty(usystemType) && !TextUtils.isEmpty(active)){

                    String userID = mAuth.getCurrentUser().getUid();
                    DatabaseReference currentUserDB = mDBRef.child(userID);

                    // history table
                    DatabaseReference currentUserDBHistory = mDBRef2.child(userID);

                    currentUserDB.child("DOB").setValue(uDOB);
                    currentUserDB.child("SystemType").setValue(usystemType);
                    currentUserDB.child("Weight").setValue(w);
                    currentUserDB.child("Height").setValue(h);
                    currentUserDB.child("Goal weight").setValue(g);
                    currentUserDB.child("Active").setValue(active);
                    currentUserDB.child("Gender").setValue(ugender);

                    // work out BMI
                    if(usystemType.equals("metric")){
                        BMI = ((w/h/h)*10000);
                        if(ugender.equals("male")){
                            BMR = (66 + (13.8 * w) + (5 * h) - (6.8 * age));
                        } else {
                            // female and metric
                            BMR = (655 + (9.6 * w) + (1.8 * h) - (4.7 * age));
                        }
                    } else {
                        BMI = ((w/h/h) * 703);
                        if(ugender.equals("male")){
                            BMR = (66 + (6.23 * w) + (12.7 * h) - (6.8 * age));
                        } else {
                            // female and imperial
                            BMR = (655 + (4.35 * w) + (4.7 * h) - (4.7 * age));
                        }
                    }

                    // WORK OUT GOAL CALORIE INTAKE
                    if(active.equals("Not active")){
                        goal_cal = (BMR * 1.2);
                    } else if (active.equals("Somewhat active")){
                        goal_cal = (BMR * 1.375);
                    } else if (active.equals("Active")){
                        goal_cal = (BMR * 1.55);
                    } else {
                        goal_cal = (BMR * 1.725);
                    }

                    // ADD BMI to DB
                    Double roundBMI = Math.round(BMI * 100.0) / 100.0;
                    currentUserDB.child("BMI").setValue(roundBMI);

                    Double roundGoalCal = Math.round(goal_cal * 100.0) / 100.0;
                    currentUserDB.child("Goal cal").setValue(roundGoalCal);

                    // history table
                    /*Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    month = month + 1;
                    String DBdate = (month + "/" + day + "/" + year);*/

                    Date thisDate = Calendar.getInstance().getTime();
                    SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                    String DBDate = formatDate.format(thisDate);

                    //currentUserDB.child("DOB").setValue(DBDate);

                   /* Date todayDate = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate = formatter.format(todayDate);
                    String stringDate = strDate;
                    */

                    Integer user_cal = 0;
                    Integer user_water = 0;
                    currentUserDBHistory.child(DBDate).child("Weight").setValue(w);
                    currentUserDBHistory.child(DBDate).child("Goal_calories").setValue(roundGoalCal);
                    currentUserDBHistory.child(DBDate).child("User_calories").setValue(user_cal);
                    currentUserDBHistory.child(DBDate).child("User_water").setValue(user_water);

                    Intent intent = new Intent(Activity_userInfo.this, Activity_home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }



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
                startActivity(new Intent(Activity_userInfo.this, Activity_home.class));
                return true;
            case R.id.menu_logout:
                mAuth.signOut();
                startActivity(new Intent(Activity_userInfo.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}