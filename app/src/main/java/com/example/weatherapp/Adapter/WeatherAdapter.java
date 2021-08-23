package com.example.weatherapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.Model.WeatherModel;
import com.example.weatherapp.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
private Context context;
private ArrayList<WeatherModel> weatherModels;

    public WeatherAdapter(Context context, ArrayList<WeatherModel> weatherModels) {
        this.context = context;
        this.weatherModels = weatherModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherModel modal = weatherModels.get(position);
        holder.temperature_tv.setText(modal.getTemperature()+"°C");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.condition_imgv);
        holder.wind_tv.setText(modal.getWindSpeed()+ "Km/hr");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh-mm");
        SimpleDateFormat output= new SimpleDateFormat("hh-mm aa");
        try{
            Date t = input.parse(modal.getTime());
            holder.time_tv.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
         private TextView wind_tv,temperature_tv,time_tv;
         private ImageView condition_imgv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wind_tv=itemView.findViewById(R.id.windSpeed_tv);
            temperature_tv=itemView.findViewById(R.id.temperature_tv);
            wind_tv=itemView.findViewById(R.id.windSpeed_tv);
            condition_imgv=itemView.findViewById(R.id.condition_imgv);
        }
    }
}
