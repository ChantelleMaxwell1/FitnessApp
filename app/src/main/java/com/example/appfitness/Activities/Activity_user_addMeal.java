package com.example.appfitness.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Activity_user_addMeal extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Integer total_userCal;

    private ImageButton addImage;

    private EditText addFoodName;
    private EditText addFoodCal;
    private EditText addProtein;
    private EditText addFat;
    private EditText addCarb;
    private Button addMeal;
    private Spinner mealType;

    private Bitmap bitmap = null;
    private Bitmap.CompressFormat format;
    private int quality;
    private Uri imageUri = null;

    private Integer totalCal = 0;

    private static final int PERMISSION_CODE = 1001;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    //private static final  int GALLERY_REQUEST = 1;


    private StorageReference mStorage;
    private DatabaseReference mDBRef;
    private DatabaseReference mDBRef2;
    private FirebaseDatabase mDB;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_meal);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        //mUser = mAuth.getCurrentUser();
        final String userID = mAuth.getCurrentUser().getUid();
        mDB = FirebaseDatabase.getInstance();

        //mDBRef = mDB.getReference().child("User_meal").child(userID);
        // NEW USER MEAL DB STRUCTURE
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        final String DBDate = formatDate.format(currentDate);

        mDBRef = mDB.getReference().child("User_meal").child(userID).child(DBDate);

        mStorage = FirebaseStorage.getInstance().getReference().child("Meal_images");
        mDBRef2 = mDB.getReference().child("User_history").child(userID);

        addImage = (ImageButton) findViewById(R.id.btn_meal_img);
        addFoodName = (EditText) findViewById(R.id.meal_name);
        addFoodCal = (EditText) findViewById(R.id.meal_cal);
        addProtein = (EditText) findViewById(R.id.meal_protein);
        addFat = (EditText) findViewById(R.id.meal_fat);
        addCarb = (EditText) findViewById(R.id.meal_carb);
        addMeal = (Button) findViewById(R.id.btn_add_meal);

        // meal type
        mealType = (Spinner) findViewById(R.id.spin_MealType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.meal_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealType.setAdapter(adapter);
        mealType.setOnItemSelectedListener(this);


        // take photo
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    // request permission
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    // show popup
                    requestPermissions(permission, PERMISSION_CODE);
                } else {
                    // permission already granted
                    openCamera();
                }

                // pervious code
                /*
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                */

            }
        });

        mDBRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                String DBDate = formatDate.format(currentDate);

                String userDBcal = snapshot.child(DBDate).child("User_calories").getValue().toString();
                total_userCal = Integer.parseInt(userDBcal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mealAdded();
            }
        });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
        //startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    // handle permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission from popup granted
                    openCamera();
                }
                else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // new code
        if(resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE){
            //addImage.setImageURI(imageUri);

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                addImage.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        /*if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            //addImage.setImageURI(imageUri);

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                addImage.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }*/

    }


    private void mealAdded() {
        mProgress.setMessage("Meal Added");
        mProgress.show();

        final String foodName = addFoodName.getText().toString().trim();
        final String foodCal = addFoodCal.getText().toString().trim();
        //Integer cal = Integer.parseInt(foodCal);
        final String foodProtein = addProtein.getText().toString().trim();
        //Integer protein = Integer.parseInt(foodProtein);
        final String foodFat = addFat.getText().toString().trim();
        //Integer fat = Integer.parseInt(foodFat);
        final String foodCarb = addCarb.getText().toString().trim();
        //Integer carb = Integer.parseInt(foodCarb);
        final String foodType = mealType.getSelectedItem().toString();



        if(!TextUtils.isEmpty(foodName) && !TextUtils.isEmpty(foodCal)
                && !TextUtils.isEmpty(foodProtein) && !TextUtils.isEmpty(foodFat) && !TextUtils.isEmpty(foodCarb) && imageUri != null){

           StorageReference filepath = mStorage.child("Meal_images").child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri downloadUri = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    //Task<Uri> url = taskSnapshot.getStorage().getDownloadUrl();
                    //Uri url = Uri.parse(taskSnapshot.getStorage().getDownloadUrl().toString());
                    //Uri downloadurl = taskSnapshot.getStorage().getDownloadUrl();

                    //Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    //String downloadUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                    //Uri downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                    String downloadUrl = imageUri.toString();

                    DatabaseReference newMeal = mDBRef.push();

                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("itemName", foodName);
                    dataToSave.put("mealType", foodType);
                    //dataToSave.put("image", downloadUrl.toString());
                    //dataToSave.put("image", String.valueOf(downloadUrl));
                    //dataToSave.put("image", downloadUrl);
                    dataToSave.put("image", downloadUrl);
                    dataToSave.put("calories", foodCal);
                    dataToSave.put("protein", foodProtein);
                    dataToSave.put("fat", foodFat);
                    dataToSave.put("carbs", foodCarb);
                    dataToSave.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));

                    newMeal.setValue(dataToSave);

                    totalCal = Integer.parseInt(foodCal);

                    //Add calories
                    String userID = mAuth.getCurrentUser().getUid();
                    DatabaseReference currentUserDB = mDBRef2;
                    Integer cal = Integer.parseInt(foodCal);

                    Date currentDate = Calendar.getInstance().getTime();
                    SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                    String DBDate = formatDate.format(currentDate);

                    Integer totalCalToAdd = (cal + total_userCal);

                    currentUserDB.child(DBDate).child("User_calories").setValue(totalCalToAdd);


                    mProgress.dismiss();

                    Intent intent = new Intent(Activity_user_addMeal.this , Activity_user_foodDiary.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String text = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}