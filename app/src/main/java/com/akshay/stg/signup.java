package com.akshay.stg;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private TextInputEditText password;
    private TextInputEditText confirmpassword;
    private RadioGroup gender;
    private CheckBox agree;
    private Button signup;

    String url = Params.url + "/stg/signup.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        assignId();


        confirmpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if (password.getText().toString().equals(confirmpassword.getText().toString())) {
                        confirmpassword.setError(null);

                    } else {
                        confirmpassword.setError("Confirm password does't match with Password");
                    }
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((name.getText().toString().isEmpty()) || (email.getText().toString().isEmpty()) || (password.getText().toString().isEmpty()) || (confirmpassword.getText().toString().isEmpty())) {


                    Toast.makeText(signup.this, "Please enter valid data", Toast.LENGTH_SHORT).show();


                } else {


                    userData();


                }


            }
        });


    }

    private void assignId() {
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (TextInputEditText) findViewById(R.id.passwordmain);
        gender = (RadioGroup) findViewById(R.id.gender);
        agree = (CheckBox) findViewById(R.id.checkBox);
        signup = (Button) findViewById(R.id.signup);
        confirmpassword = (TextInputEditText) findViewById(R.id.confirmpassword);
    }

    private void userData() {


        if (agree.isChecked()) {


            RequestQueue requestQueue = Volley.newRequestQueue(signup.this);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (obj.getString("signup").equals("1")) {
                            Toast.makeText(signup.this, "Sign up Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(signup.this, MainActivity.class));
                        } else {
                            Toast.makeText(signup.this, "Sign up failed !!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    Toast.makeText(signup.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parms = new HashMap<String, String>();
                    parms.put("name", name.getText().toString());
                    parms.put("email", email.getText().toString());
                    parms.put("password", password.getText().toString());
                    String genderfinal;
                    if (gender.getCheckedRadioButtonId() == R.id.Male) {
                        genderfinal = "Male";
                    } else {
                        genderfinal = "Female";
                    }


                    parms.put("gender", genderfinal);
                    return parms;
                }
            };

            requestQueue.add(request);

        } else if (!(agree.isChecked())) {
            Toast.makeText(signup.this, R.string.termtoast, Toast.LENGTH_SHORT).show();
            agree.setTextColor(Color.RED);
        }


    }


}
