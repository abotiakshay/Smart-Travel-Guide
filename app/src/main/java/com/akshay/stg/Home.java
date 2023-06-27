package com.akshay.stg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private androidx.appcompat.widget.Toolbar tt;
    private DrawerLayout dl;
    private NavigationView nv;                                   // component declaration
    private BottomNavigationView bottomnavigation;
    Home_fragment hh = new Home_fragment();
    String url = Params.url + "/stg/getuserdata.php";
    String login = null;
    String username = null;
    String password = null;
    String name = null;                                         // variable declaration for to store user data
    String gender = null;
    String mobile = null;
    String marriages1 = null;
    String dob = null;
    String image = null;
    String usernamemain;
    private GoogleSignInClient mGoogleSignInClient;            // google sign in variable declaration

    private void fetchuserdata()                // function to fetch user data into application
    {
        SharedPreferences sh = getSharedPreferences("loginuser", MODE_PRIVATE);  // get logged in user
        usernamemain = sh.getString("Username", null);
        RequestQueue requestQueue = Volley.newRequestQueue(Home.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, (Response.Listener<String>) response ->
        {    // here user data will come from database
            Log.e("dataa", response);
            try {
                JSONObject obj = new JSONObject(response);
                login = obj.getString("message");
                username = obj.getString("username");
                password = obj.getString("password");
                name = obj.getString("name");
                gender = obj.getString("gender");
                mobile = obj.getString("mobile");
                dob = obj.getString("dob");
                marriages1 = obj.getString("marital_status");
                image = obj.getString("image");

                View headerView = nv.getHeaderView(0);
                TextView navUsername = (TextView) headerView.findViewById(R.id.p_email);
                Log.e("useruser", username + name);
                navUsername.setText(username);
                TextView navName = headerView.findViewById(R.id.p_namee);
                navName.setText(name);
                ImageView navImage = headerView.findViewById(R.id.p_profile);
                String url = Params.url + "/stg/images/" + image;
                Picasso.get()
                        .load(url).
                        into(navImage);

                Log.e("nnn", obj.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("zxcvbnm", "onErrorResponse: " + url);
                Toast.makeText(Home.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("user", usernamemain);  // pass logged in user to database
                return parms;
            }
        };
        requestQueue.add(request);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {             // oncreate method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        Log.d("Zzz", "On Create () called");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //login for google sign in
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        fetchuserdata();   // function called for fetch user data
        tt = findViewById(R.id.tthome);  // toolbar initialization
        dl = findViewById(R.id.navigation_layout);                       // logic for drawer layout
        nv = findViewById(R.id.navigation_v);      // navigation view initialization
        nv.setNavigationItemSelectedListener(this);      // Listener for (if naviagation item click)
        bottomnavigation = findViewById(R.id.navigation_view);  // bottom navigation view initialization
        bottomnavigation.setOnNavigationItemSelectedListener(navigationselected); // listener for (if bottom naviagation item selected )
        setSupportActionBar(tt);   // action bar
        ActionBar actionBar = getSupportActionBar();                    // menu button (left menu button) on toolbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        Intent abc = getIntent();




        if (abc.getStringExtra("name") != null) {
            if (abc.getStringExtra("name").equals("AddReminder")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Reminder()).commit();
                bottomnavigation.setSelectedItemId(R.id.reminder_nav);
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Home_fragment()).commit(); // call default fragment when first time activity created
                bottomnavigation.setSelectedItemId(R.id.home_b_nav);
            }

        }
       else if(abc.getStringExtra("like")!=null)
        {
            if (abc.getStringExtra("like").equals("liked")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Favourite()).commit();
                bottomnavigation.setSelectedItemId(R.id.favourite_nav);
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Home_fragment()).commit(); // call default fragment when first time activity created
                bottomnavigation.setSelectedItemId(R.id.home_b_nav);
            }
        }

        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Home_fragment()).commit(); // call default fragment when first time activity created

        }
    }
    // class for (when bottom naviagation item selected)
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationselected = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment f = null;
            switch (item.getItemId()) {
                case R.id.home_b_nav:   // home fragment called
                    f = new Home_fragment();
                    break;
                case R.id.favourite_nav: // favourite fragment called
                    f = new Favourite();
                    break;
                case R.id.reminder_nav: // favourite fragment called
                    f = new Reminder();
                    break;
                case R.id.diary_nav:  // profile fragment called
                    f = new Diary();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, f).commit();
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();                     // toolbar menu
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {  // when toolbar item clicked
        switch (item.getItemId()) {
            case android.R.id.home: {
                dl.openDrawer(GravityCompat.START);   // opens drawer window
                return true;
            }
            case R.id.notification_button:         // opens current location activity
                Intent i = new Intent(Home.this, Your_Location.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {   // when back press if drawer window open then close drawer window
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {  // when naviagation item selected
        Intent i = null;
        switch (item.getItemId()) {
            case R.id.home_nav:   // called home fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Home_fragment()).commit();
                dl.closeDrawer(GravityCompat.START);
                break;
            case R.id.your_location:  // called current location activity
                i = new Intent(Home.this, MapsActivity.class);
                startActivity(i);
                break;
            case R.id.add_place:  // called place picker for add place (map)
                i = new Intent(Home.this, PlacePicker.class);
                startActivity(i);
                break;
            case R.id.visited_place:  // called place picker for (Map)
                i = new Intent(Home.this, PlacePicker.class);
                startActivity(i);
                break;
            case R.id.online_booking:  // called actiity for online booking
                i = new Intent(Home.this, OnlineBooking.class);
                startActivity(i);
                break;
            case R.id.show_time_table:
                i = new Intent(Home.this, ShowTimeTable.class);
                startActivity(i);
                break;
            case R.id.share:    // login for share the app
                i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.akshay.stg");
                i.putExtra(Intent.EXTRA_SUBJECT, "Smart Travel Guide");
                i.putExtra(Intent.EXTRA_TITLE, "Share Smart Travel Guide with your Friends");
                startActivity(Intent.createChooser(i, "Share Via"));
                break;
            case R.id.rate_us:  // login for rate us
                Toast.makeText(Home.this, "Redirected app on PlayStore", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.akshay.stg")));
                break;
            case R.id.help:  // help avitvity called
                i = new Intent(Home.this, Help.class);
                startActivity(i);
                break;
            case R.id.contactus:
                String to = "aboti.akshay1125@gmail.com";
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("message/rfc822");
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                break;
            case R.id.Logout:  // login for logout
                SharedPreferences sh = getSharedPreferences("loginuser", MODE_PRIVATE);
                SharedPreferences.Editor editor = sh.edit();
                editor.putString("Username", null);
                editor.apply();
                editor.commit();
                signOut();  // google sign out called
                Toast.makeText(Home.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Home.this, MainActivity.class));    // switch to main activity when logout
                break;
        }
        return true;
    }

    private void signOut() {    // login for google sign out
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    // for testing perpose.........
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Zzz", "On Start () called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Zzz", "On Resume () called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Zzz", "On pause () called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("Zzz", "On ReStart () called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Zzz", "On Stop () called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Zzz", "On Destroy () called");
    }
}