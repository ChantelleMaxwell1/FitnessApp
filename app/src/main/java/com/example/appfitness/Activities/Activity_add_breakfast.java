package com.example.appfitness.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appfitness.Model.Breakfast;
import com.example.appfitness.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Activity_add_breakfast extends AppCompatActivity {

    private ImageButton addImage;
    private EditText addFoodName;
    private EditText addFoodCal;
    private EditText addProtein;
    private EditText addFat;
    private EditText addCarb;
    private Button addBreakfast;

    private Bitmap bitmap = null;
    //private Uri imageUri;
    //private static final int GALLERY_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 1001;

    private StorageReference mStorage;
    private DatabaseReference mDBRef;
    private FirebaseDatabase mDB;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_breakfast);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDBRef = FirebaseDatabase.getInstance().getReference().child("Meals").child("Breakfast");
        //mDBRef = mDB.getReference().child("Meals").child("Breakfast");
        mStorage = FirebaseStorage.getInstance().getReference().child("Meal_images");

        addImage = (ImageButton) findViewById(R.id.breakfast_img_add);
        addFoodName = (EditText) findViewById(R.id.breakfast_name_add);
        addFoodCal = (EditText) findViewById(R.id.breakfast_cal_add);
        addProtein = (EditText) findViewById(R.id.breakfast_protein_add);
        addFat = (EditText) findViewById(R.id.breakfast_fat_add);
        addCarb = (EditText) findViewById(R.id.breakfast_carb_add);
        addBreakfast = (Button) findViewById(R.id.btn_addBreakfast);

        // add image / take image
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                //galleryIntent.setType("image/*");
                //startActivityForResult(galleryIntent, GALLERY_CODE);
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //pictureIntent.setType("image/*");
                startActivityForResult(pictureIntent, CAMERA_REQUEST_CODE);
            }
        });

        // add breakfast
        addBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mealAdded();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            imageUri = data.getData();
            addImage.setImageURI(imageUri);
        }*/
       if(requestCode == CAMERA_REQUEST_CODE){
           if(resultCode == RESULT_OK){
               bitmap = (Bitmap) data.getExtras().get("data");
               addImage.setImageBitmap(bitmap);
           }
       }
       /*if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
           imageUri = data.getData();
           addImage.setImageURI(imageUri);
        }*/
    }

    private void mealAdded() {
        mProgress.setMessage("Meal Added");
        mProgress.show();

        String foodName = addFoodName.getText().toString().trim();
        String foodCal = addFoodCal.getText().toString().trim();
        //Integer cal = Integer.parseInt(foodCal);
        String foodProtein = addProtein.getText().toString().trim();
        //Integer protein = Integer.parseInt(foodProtein);
        String foodFat = addFat.getText().toString().trim();
        //Integer fat = Integer.parseInt(foodFat);
        String foodCarb = addCarb.getText().toString().trim();
        //Integer carb = Integer.parseInt(foodCarb);

        if(!TextUtils.isEmpty(foodName) && !TextUtils.isEmpty(foodCal)
                && !TextUtils.isEmpty(foodProtein) && !TextUtils.isEmpty(foodFat) && !TextUtils.isEmpty(foodCarb) && bitmap != null){

            //StorageReference filepath = mStorage.child("Meal_images").child(bitmap.);
            Breakfast breakfast = new Breakfast("imageUrl", "itemName", "calories", "protein", "fat", "carbs", "timestamp", "userID");

            mDBRef.setValue(breakfast).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Meal Added", Toast.LENGTH_LONG).show();
                    mProgress.dismiss();
                }
            });
        }
    }
}