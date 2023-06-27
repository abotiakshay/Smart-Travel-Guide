package com.akshay.stg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class Favourite extends Fragment {

    private RecyclerView recycler;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    Context context;
    View home_view;
    PlaceRecyclerAdapter place_adapter;

    private List<placedata> places=new ArrayList<>();
    public String user;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        home_view=inflater.inflate(R.layout.fragment_favourite,container,false);
        androidx.appcompat.widget.Toolbar tt = getActivity().findViewById(R.id.tthome);
        tt.setTitle("Favourite");
        SharedPreferences sh= getActivity().getSharedPreferences("loginuser",MODE_PRIVATE); // get logged in user

        user=sh.getString("Username",null);

        Log.e("usermain", user);



        recycler=home_view.findViewById(R.id.liked_places_view);
        placesData();  // get user liked place

        return  home_view;
    }



    public void placesData(){
        display_places();
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(staggeredGridLayoutManager);    // set layout to recycler view


    }

    public void display_places(){  // fetch data(of user liked place) from database

        // recycler view for show items on the screen
        String url= Params.url+"/stg/getlikedplacedata.php"; // php string to get liked place
        RequestQueue queue= Volley.newRequestQueue(context);
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    Log.e("rrr", response );

                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray array=jsonObject.getJSONArray("data");
                    for (int i=0; i<array.length(); i++ ){
                        JSONObject ob=array.getJSONObject(i);

                        placedata listData=new placedata(ob.getString("name")
                                ,ob.getString("description")
                                ,ob.getString("category")
                                ,ob.getString("city")
                                ,ob.getString("state")
                                ,ob.getString("country")
                                ,ob.getString("postalcode")
                                , Params.url+"/stg/images/"+ ob.getString("image"));
                        places.add(listData);
                        Log.e("placemain", listData.getPlace_img());
                    }
                    place_adapter = new PlaceRecyclerAdapter(places, context);
                    recycler.setAdapter(place_adapter);




                }catch (Exception e)
                {
                    e.printStackTrace();
                }



            }
        }, error -> Toast.makeText(context,error.getMessage(),Toast.LENGTH_SHORT).show()) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String,String>parms=new HashMap<String, String>();
                parms.put("user",user);
                return  parms;
            }
        }  ;
        queue.add(request);









    }
}