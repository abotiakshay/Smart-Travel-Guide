package com.akshay.stg;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlaceRecyclerAdapter.ViewHolder> implements Filterable {    // place recycler

    List<placedata> places;
    Context context;
    List<placedata> backup;


    public PlaceRecyclerAdapter(List<placedata> places, Context context) {
        this.places = places;
        this.context = context;
        backup = new ArrayList<>(places);

        Log.d("TAG", "onQueryTextChange: " + places);
    }


    @NonNull
    @Override
    public PlaceRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_gridview, parent, false);
        ViewHolder place_view = new ViewHolder(v);    // view creation in which data will bind at runtime
        return place_view;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceRecyclerAdapter.ViewHolder holder, int position) {
        holder.place_name.setText(places.get(position).getPlace_name());
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // when click on any place
                Intent i = new Intent(context, PlaceDetails.class);

                i.putExtra("place", places.get(position).getPlace_img());
                i.putExtra("name", places.get(position).getPlace_name());
                i.putExtra("description", places.get(position).getPlace_description());
                i.putExtra("country", places.get(position).getPlace_country());
                i.putExtra("state", places.get(position).getPlace_state());
                i.putExtra("city", places.get(position).getPlace_city());
                i.putExtra("postalcode", places.get(position).getPlace_postalcode());
                i.putExtra("category", places.get(position).getPlace_category());
                context.startActivity(i);
            }
        });
        Picasso.get()
                .load(places.get(position).place_img).
                into(holder.place_image);


    }

    @Override
    public Filter getFilter() {
        return placefilter;
    }    // login for search

    Filter placefilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults result = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                result.values = backup;
                result.count = backup.size();
            } else {
                ArrayList<placedata> temp = new ArrayList<>();
                for (placedata p : backup) {

                    if ((p.getPlace_name().toString().toLowerCase().contains(constraint.toString().toLowerCase())) || (p.getPlace_state().toString().toLowerCase().contains(constraint.toString().toLowerCase())) || (p.getPlace_country().toString().toLowerCase().contains(constraint.toString().toLowerCase())) || (p.getPlace_category().toString().toLowerCase().contains(constraint.toString().toLowerCase())) || (p.getPlace_postalcode().toString().toLowerCase().contains(constraint.toString().toLowerCase())) || (p.getPlace_city().toString().toLowerCase().contains(constraint.toString().toLowerCase()))) {
                        temp.add(p);
                    }
                }
                result.count = temp.size();
                result.values = temp;


            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            places = (ArrayList<placedata>) results.values;
            notifyDataSetChanged();

        }
    };

    @Override
    public int getItemCount() {
        return places.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView place_image;
        public TextView place_name;
        public LinearLayout linear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            place_image = itemView.findViewById(R.id.img);
            place_name = itemView.findViewById(R.id.txt);            // assign where to bind
            linear = itemView.findViewById(R.id.linear_layout);

        }

    }
}
