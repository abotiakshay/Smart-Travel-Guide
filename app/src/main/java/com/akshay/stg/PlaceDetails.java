package com.akshay.stg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// this activity indicates description of place
public class PlaceDetails extends AppCompatActivity {
    private ImageView place;
    private LikeButton like;
    private TextView description, location, category;

    private Toolbar toolbar;
    String url1 = Params.url + "/stg/insertfavourite.php";
    String url2 = Params.url + "/stg/removefavourite.php";

    String url3 = Params.url + "/stg/findlikedplace.php";
    private String user;
    String p_name;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placedetails);
        place = findViewById(R.id.place_image);  //asign id
        description = findViewById(R.id.description);
        location = findViewById(R.id.location);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        category = findViewById(R.id.category);
        like = findViewById(R.id.heart_button);
        SharedPreferences sh = getSharedPreferences("loginuser", MODE_PRIVATE);

        user = sh.getString("Username", null);  // get logged in user

        Intent i = getIntent();
        Bundle extra = i.getExtras();


        p_name = extra.getString("name");  // get place name
        String p_image = extra.getString("place");
        String p_description = extra.getString("description"); // get place description
        String p_category = extra.getString("category");
        String p_location = extra.getString("city") + "," + " " + extra.getString("state") + "," + " " + extra.getString("country") + "," + " " + extra.getString("postalcode") + ".";


        Picasso.get()
                .load(p_image).  // fetch image
                into(place);

        toolbar.setTitle(p_name); // set title to place name
        setSupportActionBar(toolbar);   // action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        }
//        ActionBar actionBar = getSupportActionBar();                    // menu button (left menu button) on toolbar
//        actionBar.setDisplayHomeAsUpEnabled(true);
        description.setText(p_description);
        category.setText(p_category);
        location.setText(p_location);


        findlikedplace(); // if like than , heart should aldready red

        like.setOnLikeListener(new OnLikeListener() {  // when click on likeButton
            @Override
            public void unLiked(LikeButton likeButton) {

                removelikedplace();  // when dislike


            }

            @Override
            public void liked(LikeButton likeButton) {  // when like
                insertlikedplace();


            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findlikedplace() {  // login for (if perticular place is liked or not )

        RequestQueue requestQueue = Volley.newRequestQueue(PlaceDetails.this);
        StringRequest request = new StringRequest(Request.Method.POST, url3, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String r = null;
                try {
                    r = obj.getString("s");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (r.equals("1")) {
                    like.setLiked(true);  // set liked
                } else {
                    like.setLiked(false); // set notliked
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(PlaceDetails.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();


                parms.put("user", user);
                parms.put("place", p_name);  // pass place name and username


                return parms;
            }
        };

        requestQueue.add(request);

    }

    private void removelikedplace() {  // login to remov from like


        RequestQueue requestQueue = Volley.newRequestQueue(PlaceDetails.this);
        StringRequest request = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("dataa", response);

                Intent i=new Intent(PlaceDetails.this, Home.class);
                i.putExtra("like","liked");
                startActivity(i);
                finish();

                Toast.makeText(PlaceDetails.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(PlaceDetails.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("place", p_name);
                return parms;
            }
        };

        requestQueue.add(request);


    }


    private void insertlikedplace() { // login to add like


        RequestQueue requestQueue = Volley.newRequestQueue(PlaceDetails.this);
        StringRequest request = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(PlaceDetails.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(PlaceDetails.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("user", user);
                parms.put("place", p_name);
                return parms;
            }
        };

        requestQueue.add(request);


    }
}