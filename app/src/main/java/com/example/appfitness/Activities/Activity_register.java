package com.example.appfitness.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appfitness.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_register extends AppCompatActivity {
    private EditText fname;
    private EditText lname;

   // private TextView mDisplayDate;
    //private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "Activity_register";
    //private RadioGroup radioGroup;
    //private RadioButton btn_gender;
    //private String gender;

    private EditText email;
    private EditText password;
    private TextView reg_date;

    private Button register;

    private DatabaseReference mDBRef;
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDB = FirebaseDatabase.getInstance();
        mDBRef = mDB.getReference().child("User");

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);

        fname = (EditText) findViewById(R.id.reg_fname);
        lname = (EditText) findViewById(R.id.reg_Lname);
        //mDisplayDate = (TextView) findViewById(R.id.DOB);
        //radioGroup = (RadioGroup) findViewById(R.id.btnGroup_gender);
        email = (EditText) findViewById(R.id.reg_email);
        password = (EditText) findViewById(R.id.reg_password);
        reg_date = (TextView) findViewById(R.id.reg_date);
        register = (Button) findViewById(R.id.btn_register);

        // DATE OF BIRTH CODE
       /* mDisplayDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Activity_register.this,
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
                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);

            }
        };*/


        // GENDER CODE
        /*radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        });*/

        // REGISTER CODE
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = fname.getText().toString().trim();
                final String surname = lname.getText().toString().trim();

                //final String uDOB = mDisplayDate.getText().toString();
                // get checked id
                //final String ugender = gender;

                final String mail = email.getText().toString().trim();
                final String pwd = password.getText().toString().trim();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) && !TextUtils.isEmpty(mail) && !TextUtils.isEmpty(pwd)
                        /*&& !TextUtils.isEmpty(uDOB) && !TextUtils.isEmpty(ugender)*/){
                    mProgressDialog.setMessage("Creating account...");
                    mProgressDialog.show();

                    mAuth.createUserWithEmailAndPassword(mail,pwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if(authResult != null){
                                String userID = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDB = mDBRef.child(userID);
                                currentUserDB.child("Email").setValue(mail);
                                currentUserDB.child("Password").setValue(pwd);
                                currentUserDB.child("Firstname").setValue(name);
                                currentUserDB.child("Lastname").setValue(surname);
                                //currentUserDB.child("DOB").setValue(uDOB);
                                //currentUserDB.child("Gender").setValue(ugender);
                                currentUserDB.child("image").setValue("none");

                                mProgressDialog.dismiss();

                                //Send users to Goals activity
                                Intent intent = new Intent(Activity_register.this, Activity_userInfo.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                        }
                    });
                }
            }
        });



    }
}