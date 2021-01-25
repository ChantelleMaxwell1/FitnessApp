package com.example.appfitness.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfitness.Model.Meal;
import com.example.appfitness.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class MealRecyclerAdapter extends RecyclerView.Adapter<MealRecyclerAdapter.ViewHolder>  {
    private Context context;
    private List<Meal> mealList;

    public MealRecyclerAdapter(Context context, List<Meal> mealList) {
        this.context = context;
        this.mealList = mealList;
    }

    public MealRecyclerAdapter(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_row, parent, false);
        return new MealRecyclerAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MealRecyclerAdapter.ViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        //String imageUrl = null;

        holder.meal_name.setText(meal.getItemName());
        holder.meal_type.setText(meal.getMealType());
        holder.meal_cal.setText(meal.getCalories());
        holder.meal_protein.setText(meal.getProtein());
        holder.meal_fat.setText(meal.getFat());
        holder.meal_carb.setText(meal.getCarbs());
        //holder.meal_imageUrl.setText(meal.getImage());

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.parseLong(meal.getTimestamp())).getTime());

        holder.meal_time.setText(formattedDate);

        // OLD String imageUrl = meal.getImage();

        // NEW / TEST
        String imageUrl = meal.getImage();

        // picasso code
       // Picasso.get().load(imageUrl).into(holder.meal_image);



        if(imageUrl.equals(null)){
            //Picasso.get().load(imageUrl).into(holder.meal_image);
            Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile).into(holder.meal_image);
        } else {
            //Picasso.get().load(meal.getImage()).into(holder.meal_image);
            Picasso.get().load(imageUrl).into(holder.meal_image);
        }

        // camera photo code

    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView meal_image;
        public TextView meal_name;
        public TextView meal_type;
        public TextView meal_cal;
        public TextView meal_protein;
        public TextView meal_fat;
        public TextView meal_carb;
        public TextView meal_time;
        //public TextView meal_imageUrl;
        String userID;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);

            context = ctx;

            meal_image = (ImageView) view.findViewById(R.id.meal_image);
            meal_name = (TextView) view.findViewById(R.id.meal_name);
            meal_type = (TextView) view.findViewById(R.id.meal_type);
            meal_cal = (TextView) view.findViewById(R.id.meal_cal);
            meal_protein = (TextView) view.findViewById(R.id.meal_protein);
            meal_fat = (TextView) view.findViewById(R.id.meal_fat);
            meal_carb = (TextView) view.findViewById(R.id.meal_carbs);
            meal_time = (TextView) view.findViewById(R.id.meal_timestamp);
            //meal_imageUrl = (TextView) view.findViewById(R.id.meal_imageUrl);

            userID = null;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // go to next activity
                }
            });
        }
    }
}
