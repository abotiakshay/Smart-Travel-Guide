package com.akshay.stg;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Home_fragment extends Fragment {
    public RecyclerView recycler;
    Context context;
    View home_view;
    ProgressDialog pp;
    private EditText seachViewofMain;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    PlaceRecyclerAdapter place_adapter;
    public List<placedata> places = new ArrayList<placedata>();

    @Nullable
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        Log.e("akak", "On attach2 () called");

    }

    private void assignId() {
        Toolbar tt = getActivity().findViewById(R.id.tthome);
        tt.setTitle(R.string.app_name);
        recycler = home_view.findViewById(R.id.place_recycler);
        seachViewofMain = (EditText) home_view.findViewById(R.id.main_searchbar);


    }

    public void placesData() {
        display_places();
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(staggeredGridLayoutManager);    // set layout to recycler view


    }

    @Override


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        home_view = inflater.inflate(R.layout.fragment_home, container, false);
        pp = new ProgressDialog(context);
        pp.show();
        Log.e("akak", "On createview2 () called");

        assignId();


        seachViewofMain.setOnClickListener(new View.OnClickListener() {     // click on seach bar in home page;
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SearchActivity.class);
                i.putExtra("list", (Serializable) places);
                startActivity(i);
            }
        });


        placesData();

        return home_view;

    }


    public void display_places() {  // fetch place data from database

        // recycler view for show items on the screen
        String url = Params.url + "/stg/getplacedata.php";  // php string to fetch data
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pp.dismiss();
                try {


                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject ob = array.getJSONObject(i);

                        placedata listData = new placedata(ob.getString("name")
                                , ob.getString("description")
                                , ob.getString("category")
                                , ob.getString("city")
                                , ob.getString("state")
                                , ob.getString("country")
                                , ob.getString("postalcode")
                                , Params.url + "/stg/images/" + ob.getString("image"));
                        places.add(listData);

                    }
                    place_adapter = new PlaceRecyclerAdapter(places, context);
                    recycler.setAdapter(place_adapter);  // set adapter to recycler view


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pp.dismiss();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }


    // for testing perpose..........................


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("akak", "On create2 () called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("akak", "On Start 2() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("akak", "On Stop 2() called");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("akak", "On Destroy2() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("akak", "On Resume2 () called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("akak", "On Pause2 () called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("akak", "On Dettach2 () called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("akak", "On Destroy view2 () called");
    }
}
