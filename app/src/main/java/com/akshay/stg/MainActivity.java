package com.akshay.stg;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private TextInputLayout usernamet;
    private TextInputLayout passwordt;                       // member variable
    private EditText username;
    String googlename, googleemain, googlephoto;
    private EditText password;
    private Button signin;
    private TextView signup;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "loginuser";
    String title;
    String url = Params.url + "/stg/sign.php";
    private GoogleSignInClient mGoogleSignInClient;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {  // if continue with google
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) { // google sign data comes
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                this.googlename = personName;
                this.googleemain = personEmail;
                this.googlephoto = personPhoto.toString();

                storegooglesignindata();
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("Username", personEmail);

                editor.apply();
                editor.commit();


                Intent i = new Intent(MainActivity.this, Home.class);  // go to home activity
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();


                Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();


                Log.e("mmm", personEmail);
            }


            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("signin", "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void signIn() {  // google sign in
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googlesign()  // if google sign in button cliked
    {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }


    private void storegooglesignindata() {  // store google sign data to database

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = Params.url + "/stg/signinwithgoogle.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("name", googlename);
                parms.put("email", googleemain);
                parms.put("image", googlephoto);  // pass google data


                return parms;
            }
        };

        requestQueue.add(request);


    }


    public void fetchUserData() {   // fetch user data
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String login = null;
                String username = null;
                String password = null;
                String name = null;
                String gender = null;
                String mobile = null;
                String marriages1 = null;
                String dob = null;
                String image = null;
                try {
                    login = obj.getString("message");
                    username = obj.getString("username");
                    password = obj.getString("password");
                    name = obj.getString("name");
                    gender = obj.getString("gender");
                    mobile = obj.getString("mobile");
                    dob = obj.getString("dob");
                    marriages1 = obj.getString("marital_status");
                    image = obj.getString("image");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (login.contains("success")) {  // if new user then set local data (sharedpreferences)

                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("Username", username);

                    editor.apply();
                    editor.commit();


                    Intent i = new Intent(MainActivity.this, Home.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();


                    Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                String user = username.getText().toString();
                String pass = password.getText().toString();
                parms.put("user", user);

                parms.put("pass", pass);   // pass userid and password


                return parms;
            }
        };

        requestQueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, MainActivity.MODE_PRIVATE);

        if (sharedpreferences.getString("Username", null) != null) {  // if aldready login then , directly goto home
            Intent i = new Intent(MainActivity.this, Home.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        googlesign();  // google sign

        signup = (TextView) findViewById(R.id.signup2);
        usernamet = findViewById(R.id.textInputLayout);
        passwordt = findViewById(R.id.textInputLayout1);
        username = (EditText) findViewById(R.id.username);                            // linking resources to variable
        password = (EditText) findViewById(R.id.password);
        signin = (Button) findViewById(R.id.signin);

        signin.setOnClickListener(new View.OnClickListener() {     //sign in on click
            public void onClick(View v) {
                Log.d("LOL","clicked");

                if (!validateUser() | !validatepass())  // logic error if empty
                {
                    return;
                } else {

                    fetchUserData();


                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {   // onclick sign up
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, signup.class);
                startActivity(i);
            }
        });

    }

    private boolean validateUser()        // function for empty user or not
    {
        String user = usernamet.getEditText().getText().toString().trim();
        if (user.isEmpty()) {
            usernamet.setError("Field can't be empty");
            return false;
        } else {
            usernamet.setError(null);
            return true;
        }
    }

    private boolean validatepass()     // function for empty password or not
    {
        String pass = passwordt.getEditText().getText().toString().trim();
        if (pass.isEmpty()) {
            passwordt.setError("Field can't be empty");
            return false;
        } else {
            passwordt.setError(null);
            return true;
        }
    }
}