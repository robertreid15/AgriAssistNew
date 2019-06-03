package com.robertreid.farm.system.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.robertreid.farm.system.R;
import com.robertreid.farm.system.barcodes.ui.ItemsActivity;
import com.robertreid.farm.system.findusers.FindUserActivity;
import com.robertreid.farm.system.login.LoginActivity;
import com.robertreid.farm.system.placesOfInterest.LocalPlaces;
import com.robertreid.farm.system.postWork.FindPostActivity;
import com.robertreid.farm.system.weather.ui.WeatherActivity;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener  {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private DrawerLayout drawer;

    private FusedLocationProviderClient mFusedLocationClient;
    private SupportMapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private Switch workSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initDrawer(toolbar);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show())
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();



        registerSubscription();

        workSwitch = findViewById(R.id.workingSwitch);
        //SSwitch like in labourer activity
        workSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkLocationPermission();
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
                sendNotification();
            } else {
                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference("available").child(userId).removeValue();
                mMap.setMyLocationEnabled(false);

            }
        });
    }

    private void sendNotification() {
        String pushId = FirebaseDatabase.getInstance().getReference().push().getKey();
        FirebaseDatabase.getInstance().getReference().child("notifications").child(pushId).child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).setValue(true);

    }


    private void registerSubscription() {
        FirebaseMessaging.getInstance().subscribeToTopic("available");
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(status -> {
            if (status.isSuccess()) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initDrawer(Toolbar toolbar) {

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getUserData(navigationView);

    }

    private void getUserData(NavigationView navigationView) {
        View headerLayout = navigationView.getHeaderView(0);
        CircleImageView userpic = headerLayout.findViewById(R.id.image_userpic);
        TextView username = headerLayout.findViewById(R.id.text_user_name);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        username.setText(currentUser.getDisplayName());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(userpic);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(100000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getAvailableUsers();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                //mMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermission();
            }
        } else {
            //mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            //mMap.setMyLocationEnabled(true);
        }
    }

    private void getAvailableUsers() {

        FirebaseDatabase.getInstance().getReference().child("available").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Get location of users
                    if (ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        continue;
                    double lat = Double.parseDouble(ds.child("latitude").getValue().toString());
                    double lon = Double.parseDouble(ds.child("longitude").getValue().toString());

                    LatLng labourLoc = new LatLng(lat, lon);
                    FirebaseDatabase.getInstance().getReference().child("all_users")
                            .child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String name = dataSnapshot.child("name").getValue().toString();

                                String image = dataSnapshot.child("userpic").getValue().toString();
                                //Load image to marker
                                Glide.with(getApplicationContext())
                                        .asBitmap()
                                        .load(image)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                mMap.addMarker(new MarkerOptions().position(labourLoc).title(name).icon(BitmapDescriptorFactory.fromBitmap(getCroppedBitmap(resource))));

                                            }
                                        });


                            } else
                                mMap.addMarker(new MarkerOptions().position(labourLoc).title("Labour"));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLastLocation = location;
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(2));

                    //Put our location to db
                    Map<String, String> locData = new HashMap<>();
                    locData.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                    locData.put("longitude", String.valueOf(mLastLocation.getLongitude()));
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        FirebaseDatabase.getInstance().getReference().child("available").child(FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()).setValue(locData);

                    }
                }
            }
        }
    };


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this).setTitle("give permission").setMessage("give permission")
                        .setPositiveButton("OK", (dialogInterface, i) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1))
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    public void findUsers() {
        Intent i = new Intent(MainActivity.this, FindUserActivity.class);
        startActivity(i);
    }

    public void findPosts() {
        Intent i = new Intent(MainActivity.this, FindPostActivity.class);
        startActivity(i);
    }

    public void findPlaces() {
        Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a place of interest");

        String[] animals = {"Vets", "Farm Machinery", "Agricultural Feeds", "Welders", "Agricultural Consultants"};
        int checkedItem = 1; // Farm Machinery
        builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item
                Intent i = new Intent(MainActivity.this, LocalPlaces.class);
                i.putExtra("key",which);
                startActivity(i);
            }
        });

        /*builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Passing to new intent " + which, Toast.LENGTH_LONG).show();


            }
        });*/
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_exit:
                logOut();
                break;
            case R.id.nav_find_users:
                findUsers();
                break;
            case R.id.nav_feed:
                findPosts();
                break;
            case R.id.nav_places:
                findPlaces();
                break;
            case R.id.nav_weather:
                fetchWeather();
                break;
            case R.id.nav_items:
                toItems();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchWeather() {
        startActivity(new Intent(this, WeatherActivity.class));
    }

    private void toItems() {
        startActivity(new Intent(this, ItemsActivity.class));
    }

    //Method that return circle image
    private Bitmap getCroppedBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }



}
