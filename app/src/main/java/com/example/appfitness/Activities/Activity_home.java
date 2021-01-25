package com.example.appfitness.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfitness.Adapter.MyAdapter;
import com.example.appfitness.Model.ListItem;
import com.example.appfitness.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Activity_home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;

    private DatabaseReference mDBRef;
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        ListItem item1 = new ListItem("Goals", "Target weight & calorie intake", "View");
        ListItem item2 = new ListItem("Profile", "Your profile information", "Edit");
        ListItem item3 = new ListItem("Food Diary", "Track meals and calorie intake", "Add");
        ListItem item4 = new ListItem("Exercise", "Track steps using step counter", "Add");
        ListItem item5 = new ListItem("Weight", "Update weight changes", "Edit");
        ListItem item6 = new ListItem("Progress", "View your achievements", "View");

        listItems.add(item1);
        listItems.add(item2);
        listItems.add(item3);
        listItems.add(item4);
        listItems.add(item5);
        listItems.add(item6);
        /*for(int i = 0; i < 10; i++){
            ListItem item = new ListItem("Item" + (i+1), "Description");

            listItems.add(item);
        }*/

        adapter = new MyAdapter(this, listItems);
        recyclerView.setAdapter(adapter);
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
                //Intent intent = new Intent();
                //Intent intent = new Intent(Activity_home.this, Activity_home.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(new Intent(Activity_home.this, Activity_home.class));
                //Toast.makeText(this, "Home selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_logout:
                mAuth.signOut();
                startActivity(new Intent(Activity_home.this, MainActivity.class));
                //Toast.makeText(this, "Logout selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}