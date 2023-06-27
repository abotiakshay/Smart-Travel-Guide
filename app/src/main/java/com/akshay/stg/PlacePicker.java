package com.akshay.stg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class PlacePicker extends FragmentActivity implements SearchView.OnQueryTextListener, OnMapReadyCallback {
    FusedLocationProviderClient client;
    private GoogleMap mMap;   // google map variable declaration
    SearchView searchplace;
    String aaddress, city, state, postalCode, knownName, country, place;
    ImageButton imgbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()   // supports map fragment
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);  // get the map into fragment
        imgbtn = findViewById(R.id.imgbtn);

        imgbtn.setOnClickListener(new View.OnClickListener() {   // for current location
            @Override
            public void onClick(View v) {
                FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(PlacePicker.this);
                if (ActivityCompat.checkSelfPermission(PlacePicker.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PlacePicker.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Task<Location> task = client.getLastLocation();
                task.addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Your are here.."));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                });
            }
        });
        searchplace = findViewById(R.id.seachplaceonmap);    //fetch search bar
        searchplace.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {   // search
        mMap.clear();
        String location;
        location = searchplace.getQuery().toString();
        List<Address> addressList = null;
        if (location != null || location != "") {
            Geocoder geocoder = new Geocoder(PlacePicker.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);  // fetch lattitude and logitude from name
                if (addressList.get(0) != null) {

                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));  // add marker on map of place entered in seachbar
                    // zoom to that place
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    aaddress = addressList.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    city = addressList.get(0).getLocality();
                    state = addressList.get(0).getAdminArea();
                    country = addressList.get(0).getCountryName();        // address by searched name
                    postalCode = addressList.get(0).getPostalCode();
                    knownName = addressList.get(0).getFeatureName();
                }
            } catch (Exception e) {
                Toast.makeText(this, "No result found", Toast.LENGTH_SHORT);
            }


        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {  // called to attach map to fragment


        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {  // when login click on map
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (mMap != null) {
                    Marker m = mMap.addMarker(new MarkerOptions()  // add marker when long click
                            .position(latLng)
                            .title("you clicked")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {    // when marker click
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                if (getIntent() != null) {
                    if (getIntent().getStringExtra("classname") != null) {
                        if (getIntent().getStringExtra("classname").equals("addreminder")) {
                            Intent i = new Intent(PlacePicker.this, AddReminder.class);  // lattitude and longitude of clicked marker should be sent to Ad place
                            Bundle args = new Bundle();
                            args.putParcelable("from_position", latLng);  // pass latitude and logitude
                            Params.place = args;
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Params.place_value = true;
                            startActivity(i);
                        }
                    } else {
                        Intent i = new Intent(PlacePicker.this, Add_Place.class);  // lattitude and longitude of clicked marker should be sent to Ad place
                        Bundle args = new Bundle();
                        args.putParcelable("from_position", latLng);  // pass latitude and logitude
                        i.putExtra("bundle", args);
                        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                    }

                }
                return true;
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

}



