package com.akshay.stg;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

// This activity is for Profile..................
public class Diary extends Fragment {


    Context context;
    View home_view;
    Button edit, save;
    RadioButton male, female, married, unmarried;  // component declaration
    EditText name, email, mobile, dob;
    RadioGroup gender, marriegestatus;
    String profileimage123;
    ImageView profilephoto;
    Home abc;
    String url = Params.url + "/stg/updateprofile.php";  // php file to update profile
    String[] maritical = {"Married", "Unmarried"};

    private Bitmap bitmap;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        abc = (Home) getActivity();  // get instanse of home activity
    }

    // when open file chooser , and select image (to update profile)
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri filepath = data.getData();
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(filepath);

                bitmap = BitmapFactory.decodeStream(inputStream); // converts into bitmap
                profilephoto.setImageBitmap(bitmap);
                imageStore(bitmap); // to make encoded image
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    private void imageStore(Bitmap bitmap) {   // makes encoded image


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        profileimage123 = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // login to store that image into database (update profile)
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, Params.url + "/stg/changeprofile.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("errorrr", response);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (obj.getString("done").equals("1")) {

                        Intent i=new Intent(getContext(), Home.class);

                        startActivity(i);
                        getActivity().finish();
                        Toast.makeText(context, "Profile Photo Updated Successfull", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "Can't Update Profile Profile", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("user", abc.username); // pass username
                parms.put("upload", profileimage123);  // pass the profile image fetched from file manager


                return parms;
            }
        };

        requestQueue.add(request);


    }


    public void getprofilepic() {


        Dexter.withContext(context)  // take storage permission
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");  // if granted then open filechooser
                        startActivityForResult(Intent.createChooser(intent, "Select image"), 1);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();


    }


    public void changeprofilephoto()  // fetched profile image into app , or to change profile photo
    {

        TextView change = home_view.findViewById(R.id.changeprofile);
        profilephoto = home_view.findViewById(R.id.profilephotoimage);
        String url = Params.url + "/stg/images/" + abc.image;
        Picasso.get()
                .load(url).
                into(profilephoto);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getprofilepic();
            }
        });
    }


    private void assignId() {

        edit = home_view.findViewById(R.id.button2);
        save = home_view.findViewById(R.id.button);
        name = (EditText) home_view.findViewById(R.id.pname);
        email = (EditText) home_view.findViewById(R.id.pemail);
        male = home_view.findViewById(R.id.male);
        female = home_view.findViewById(R.id.ffemale);
        mobile = home_view.findViewById(R.id.editTextPhone);   // assignment of id to compenents
        dob = home_view.findViewById(R.id.editTextDate);
        gender = home_view.findViewById(R.id.female);
        married = home_view.findViewById(R.id.married);
        unmarried = home_view.findViewById(R.id.unmarried);
        marriegestatus = home_view.findViewById(R.id.maritical);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        home_view = inflater.inflate(R.layout.fragment_diary, container, false);
        androidx.appcompat.widget.Toolbar tt = getActivity().findViewById(R.id.tthome);
        tt.setTitle("Profile");
        assignId();  // called to assign id
        changeprofilephoto(); // called to fetch profile photo or to change profile


        if(abc.name != null) {
            String name1 = abc.name;
//        Log.e("akea", name1);
            String email1 = abc.username;       // fill user data , whatever comming from database (from home activity)
//        Log.e("akea", email1);
            name.setText(name1);
            email.setText(email1);
            String s = abc.gender.toUpperCase();
            if (s.equals("MALE") || s.equals("Male")) {
                male.setChecked(true);
            } else {
                female.setChecked(true);
            }
            String sS = abc.marriages1.toUpperCase();

            if (sS.equals("MARRIED")) {
                married.setChecked(true);
            } else if (sS.equals("UNMARRIED")) {
                unmarried.setChecked(true);
            }
        }

        mobile.setText(abc.mobile);
        dob.setText(abc.dob);


        edit.setOnClickListener(new View.OnClickListener() {   // set visible compent when click on edit
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                male.setEnabled(true);
                female.setEnabled(true);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {  // when click on save profile
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });


        return home_view;


    }


    private void updateProfile()   // function for update the profile
    {


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("errorrr", response);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (obj.getString("success").equals("1")) {

                        Intent i=new Intent(getContext(), Home.class);

                        startActivity(i);
                        getActivity().finish();



                        Toast.makeText(context, "Profile Updated Successfull", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(context, "Can't Update Profile", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("name", name.getText().toString());
                parms.put("email", email.getText().toString());   // pass edited data
                parms.put("mobile", mobile.getText().toString());
                parms.put("dob", dob.getText().toString());
                String genderfinal;
                if (gender.getCheckedRadioButtonId() == R.id.male) {
                    genderfinal = "Male";
                } else {
                    genderfinal = "Female";
                }


                parms.put("gender", genderfinal);

                String marriege = null;

                if (marriegestatus.getCheckedRadioButtonId() == R.id.married) {
                    marriege = "Married";
                } else if (marriegestatus.getCheckedRadioButtonId() == R.id.unmarried) {
                    marriege = "Unmarried";
                }
                parms.put("marritical", marriege);


                return parms;
            }
        };

        requestQueue.add(request);


    }

}
