package com.example.appfitness.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appfitness.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Activity_Profile extends AppCompatActivity {

    private ImageButton profile_pic;
    private TextView fname;
    private TextView lname;
    private TextView gender;
    private TextView birthDate;
    private TextView email;
    //private EditText pwd;

    private final static int GALLERY_CODE = 1;
    //private Uri mImageUri;
    private Uri resultUri = null;
    private StorageReference mStorage;

    private Button btn_pBack;
    private Button btn_pUpdate;
    private static final String TAG = "Activity_Profile";

    private DatabaseReference mDBRef;
    private DatabaseReference mDBRef2;
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__profile);

        profile_pic = (ImageButton) findViewById(R.id.profile_pic);
        fname = (TextView) findViewById(R.id.profile_fname);
        lname = (TextView) findViewById(R.id.profile_lname);
        gender = (TextView) findViewById(R.id.profile_gender);
        birthDate = (TextView) findViewById(R.id.profile_DOB);
        email = (TextView) findViewById(R.id.profile_email);
        //pwd = (EditText) findViewById(R.id.profile_pwd);

        btn_pBack = (Button) findViewById(R.id.btn_pBack);
        btn_pUpdate = (Button) findViewById(R.id.btn_pUpdate);

        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        mStorage = FirebaseStorage.getInstance().getReference().child("User_profilePic");
        //mUser = mAuth.getCurrentUser();
        mDB = FirebaseDatabase.getInstance();
        mDBRef = mDB.getReference().child("User").child(userID);
        mDBRef2 = mDB.getReference().child("User_info").child(userID);
        //mDBRef.keepSynced(true);



        mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Firstname = snapshot.child("Firstname").getValue().toString();
                String Lastname = snapshot.child("Lastname").getValue().toString();
                //String Gender = snapshot.child("Gender").getValue().toString();
                //String DOB = snapshot.child("DOB").getValue().toString();
                String Email = snapshot.child("Email").getValue().toString();
                //String Password = snapshot.child("Password").getValue().toString();
                String profilePicUrl = snapshot.child("image").getValue().toString();

                fname.setText(Firstname);
                lname.setText(Lastname);
                //gender.setText(Gender);
                //birthDate.setText(DOB);
                email.setText(Email);
                //pwd.setText(Password);
                //profile_pic.setImageURI();

                if(!profilePicUrl.equals("none")){
                    Picasso.get().load(profilePicUrl).resize(140, 140).into(profile_pic);
                } else {
                    Picasso.get().load(resultUri).placeholder(R.drawable.default_profile).into(profile_pic);
                }

                Log.d(TAG, "Testing");


                //fname.addTextChangedListener(textWatcher);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDBRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Gender = snapshot.child("Gender").getValue().toString();
                String DOB = snapshot.child("DOB").getValue().toString();

                gender.setText(Gender);
                birthDate.setText(DOB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });


        btn_pBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Profile.this, Activity_home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //startActivity(new Intent(Activity_Profile.this, Activity_home.class));
            }
        });

        btn_pUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StorageReference imagePath = mStorage.child("User_profilePic").child(resultUri.getLastPathSegment());

                imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String userID = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDB = mDBRef;
                        //mDBRef = mDB.getReference().child("User").child(userID);
                        currentUserDB.child("image").setValue(resultUri.toString());

                        Intent intent = new Intent(Activity_Profile.this, Activity_home.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            resultUri = data.getData();
            //profile_pic.setImageURI(mImageUri);
            CropImage.activity(resultUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                profile_pic.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    /*TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };*/

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && requestCode == RESULT_OK){

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //User user = snapshot.getValue(User.class);
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
    protected void onStop() {
        super.onStop();
    }

/*
    @Override
    protected void onStart() {
        super.onStart();
        mDBRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                String fname = snapshot.child("Firstname").getValue().toString();
                String lname = snapshot.child("Lastname").getValue().toString();
                String email = snapshot.child("Email").getValue().toString();
                String pwd = snapshot.child("Password").getValue().toString();

                profile_fname.setText(fname);
                profile_lname.setText(lname);
                profile_email.setText(email);
                profile_pwd.setText(pwd);

            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        mDBRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                String gender = snapshot.child("Gender").getValue().toString();
                String DOB = snapshot.child("DOB").getValue().toString();

                profile_gender.setText(gender);
                profile_DOB.setText(DOB);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

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
                startActivity(new Intent(Activity_Profile.this, Activity_home.class));
                return true;
            case R.id.menu_logout:
                mAuth.signOut();
                startActivity(new Intent(Activity_Profile.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}