package com.akshay.stg;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private SearchView main_searchbar;
    List<placedata> list = new ArrayList<placedata>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        RecyclerView recycler = findViewById(R.id.place_recycler2);   // initiatiate recyclerview
        main_searchbar = findViewById(R.id.main_searchbar2);   // seach bar

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(staggeredGridLayoutManager);    // set layout to recycler view

        Intent i = getIntent();
        list = (List<placedata>) i.getSerializableExtra("list");


        PlaceRecyclerAdapter place_adapter = new PlaceRecyclerAdapter(list, SearchActivity.this);
        recycler.setAdapter(place_adapter);  // set adapter to recycler view
        main_searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                place_adapter.getFilter().filter(newText);
                return true;
            }
        });

    }
}