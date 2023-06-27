package com.akshay.stg;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Add_Place extends AppCompatActivity {
    Button add_photos, addplace;
    EditText placename, description, city, state, country, postalcode;  // component declaration
    Spinner category;
    String encodeImage;
    ImageView place;
    Bitmap bitmap;
    private static final String url = Params.url + "/stg/placeupload.php";  // php file to add place


    public void assignid() {
        placename = findViewById(R.id.placename);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        country = findViewById(R.id.country);
        postalcode = findViewById(R.id.postalcode);
        description = findViewById(R.id.placedescription);    // assignment id to compenent
        add_photos = (Button) findViewById(R.id.add_photos);
        addplace = findViewById(R.id.addplace);
        place = findViewById(R.id.placeimg);
        category = (Spinner) findViewById(R.id.category);  // locate category spinner
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__place);
        assignid();

        add_photos.setOnClickListener(new View.OnClickListener() {   // when clicked on add_photo button
            @Override
            public void onClick(View v) {


                Dexter.withContext(Add_Place.this)  // get runtime storage permission
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");   // if permission granted
                                startActivityForResult(Intent.createChooser(intent, "Select image"), 1); // opens file chooser

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();  // if deny then continuesly check when reopen the app
                            }
                        }).check();


            }
        });


        String[] place_category = {   // category string
                "Select Category",
                "Agricultural land",
                "Allotment",
                "Amberfield land",
                "Ancient woodland",
                "Areas of outstanding natural beauty",
                "City",
                "Common land",
                "Concealment place",
                "Conservation area",
                "Construction site",
                "Conurbation",
                "County wildlife site",
                "Cul-de-sac",
                "Cultural quarter",
                "Designated area",
                "Entrapment place",
                "Forest",
                "Garden town",
                "Green belt",
                "Green space",
                "Greenfield land",
                "Hamlet",
                "Heritage coast",
                "Land",
                "Local green space",
                "Local site",
                "Megacity",
                "National nature reserves",
                "National park",
                "Park",
                "Plaza",
                "Previously-developed site",
                "Ribbon development",
                "Safeguarded land",
                "Site of nature conservation interest",
                "Sites of special scientific interest",
                "Smart city",
                "Special areas of conservation",
                "Special protection areas",
                "Strategic industrial locations",
                "Suburb",
                "Town",
                "Village",
                "Village greens",
                "Wetlands",
                "Wood",
                "World heritage site",
                "Temple",
                "Historic Site",


        };


//Locate all Edittext to their id


        ArrayAdapter category_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, place_category);   // set values in category spinner
        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(category_adapter);

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        LatLng fromPosition = bundle.getParcelable("from_position");  // get lattitude and longitude from Map
        Geocoder geocoder = new Geocoder(Add_Place.this);

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(fromPosition.latitude, fromPosition.longitude, 1); // get address from lattitude and logitude
            String address = addresses.get(0).getAddressLine(1);            // find address form lattitue and longitude
            String city1 = addresses.get(0).getLocality();
            String state1 = addresses.get(0).getAdminArea();
            String zip = addresses.get(0).getPostalCode();
            String country1 = addresses.get(0).getCountryName();
            String knownName = addresses.get(0).getFeatureName();

            //set the value:
            city.setText(city1);
            state.setText(state1);
            country.setText(country1);
            postalcode.setText(zip);
        } catch (IOException e) {
            e.printStackTrace();
        }


        addplace.setOnClickListener(new View.OnClickListener() {  // when click on add place button
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(Add_Place.this);
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("akak", response);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (obj.getString("done").equals("1"))  // if place data inserted then , toast message come
                            {
                                Toast.makeText(Add_Place.this, "Place Added", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Add_Place.this, Home.class));
                            } else if (obj.getString("done").equals("0")) {
                                Toast.makeText(Add_Place.this, "Failed to add place", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        Toast.makeText(Add_Place.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {  // pass place data to database
                        Map<String, String> parms = new HashMap<String, String>();
                        parms.put("name", placename.getText().toString());
                        parms.put("description", description.getText().toString());
                        parms.put("city", city.getText().toString());
                        parms.put("state", state.getText().toString());
                        parms.put("category", category.getSelectedItem().toString());
                        parms.put("postalcode", postalcode.getText().toString());
                        parms.put("country", country.getText().toString());
                        parms.put("upload", encodeImage);

                        return parms;
                    }
                };

                requestQueue.add(request);


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {  // login to choose image from storage
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);

                bitmap = BitmapFactory.decodeStream(inputStream);  // converts into bitmap
                place.setImageBitmap(bitmap);
                imageStore(bitmap);  // to make encoded image
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    private void imageStore(Bitmap bitmap) {  // make encoded image


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        encodeImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }


}