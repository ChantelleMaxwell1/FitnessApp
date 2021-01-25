package com.example.appfitness.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfitness.Model.Breakfast;
import com.example.appfitness.R;

import java.util.Date;
import java.util.List;

public class BreakfastRecyclerAdapter  extends RecyclerView.Adapter<BreakfastRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Breakfast> breakfastList;

    public BreakfastRecyclerAdapter(Context context, List<Breakfast> breakfastList) {
        this.context = context;
        this.breakfastList = breakfastList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Breakfast breakfast = breakfastList.get(position);
        String imageUrl = null;

        holder.breakfast_name.setText(breakfast.getItemName());
        holder.breakfast_cal.setText(breakfast.getCalories());
        holder.breakfast_protein.setText(breakfast.getProtein());
        holder.breakfast_fat.setText(breakfast.getFat());
        holder.breakfast_carb.setText(breakfast.getCarbs());

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(breakfast.getTimestamp())).getTime());

        holder.breakfast_time.setText(formattedDate);

       imageUrl = breakfast.getImage();

       // picasso code

       // camera photo code

    }

    @Override
    public int getItemCount() {
        return breakfastList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView breakfast_image;
        public TextView breakfast_name;
        public TextView breakfast_cal;
        public TextView breakfast_protein;
        public TextView breakfast_fat;
        public TextView breakfast_carb;
        public TextView breakfast_time;
        String userID;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);

            context = ctx;

            /*breakfast_image = (ImageView) view.findViewById(R.id.breakfast_image);
            breakfast_name = (TextView) view.findViewById(R.id.breakfast_name);
            breakfast_cal = (TextView) view.findViewById(R.id.breakfast_cal);
            breakfast_protein = (TextView) view.findViewById(R.id.breakfast_protein);
            breakfast_fat = (TextView) view.findViewById(R.id.breakfast_fat);
            breakfast_carb = (TextView) view.findViewById(R.id.breakfast_carbs);
            breakfast_time = (TextView) view.findViewById(R.id.breakfast_timestamp);
            */

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
