package com.example.sellerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sellerapp.databinding.ActivityAddressPickerBinding;
import com.example.sellerapp.databinding.ActivityLoginBinding;
import com.example.sellerapp.viewmodels.AddressPickerViewmodel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Point2D;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;

public class AddressPickerActivity extends AppCompatActivity {

    private MapMarker chosenAddressMarker;
    private AddressPickerViewmodel viewModel;
    private LocationManager locationManager;
    private FloatingActionButton btnMyAddress;
    private MapViewLite mapSearch;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new AddressPickerViewmodel();
        ActivityAddressPickerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_address_picker);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        btnMyAddress = findViewById(R.id.btnMyAddress);

        MaterialToolbar tbAddressPicker = findViewById(R.id.tbAddressSearch);
        tbAddressPicker.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ListView lvSuggests = findViewById(R.id.lvAddresses);
        lvSuggests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.pickAddress(i);
            }
        });

        viewModel.getHereAddresses().observe(this, addresses -> {
            lvSuggests.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, addresses));
        });

        viewModel.getErrorMsg().observe(this, error -> {
            Snackbar.make(btnMyAddress, error, BaseTransientBottomBar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .setTextColor(Color.WHITE)
                    .show();
        });


        mapSearch = findViewById(R.id.mapClinicAddress);
        mapSearch.onCreate(savedInstanceState);
        mapSearch.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if (errorCode == null) {
                    mapSearch.getCamera().setTarget(new GeoCoordinates(-7.2459717, 112.7378266));
                    mapSearch.getCamera().setZoomLevel(14);
                } else {
                    Snackbar.make(mapSearch, "Terjadi masalah dalam menampilkan peta, mohon periksa kembali jaringan anda", BaseTransientBottomBar.LENGTH_LONG)
                            .setBackgroundTint(Color.RED)
                            .setTextColor(Color.WHITE)
                            .show();
                }
            }
        });
        mapSearch.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(@NonNull Point2D point2D) {
                GeoCoordinates coordinates = mapSearch.getCamera().viewToGeoCoordinates(point2D);
                viewModel.searchAddressWithCoordinates(coordinates.latitude, coordinates.longitude);
                setMapViewMarker(mapSearch, coordinates.latitude, coordinates.longitude);
            }
        });

        viewModel.getKoordinat().observe(AddressPickerActivity.this, koor -> {
            setMapViewMarker(mapSearch, koor.getLat(), koor.getLng());
        });

        TextInputEditText edSearch = findViewById(R.id.edSearchAddress);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.searchAddressHere(editable.toString());
            }
        });

        btnMyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = new String[3];
                permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
                permissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
                requestPermissions(permissions, 23);
                locationManager = (LocationManager) AddressPickerActivity.this.getSystemService(Context.LOCATION_SERVICE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 23){
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setSpeedRequired(false);
                criteria.setCostAllowed(true);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        viewModel.searchAddressWithCoordinates(location.getLatitude(), location.getLongitude());
                        setMapViewMarker(mapSearch, location.getLatitude(), location.getLongitude());
                    }
                };
                assert locationManager != null;
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    locationManager.requestSingleUpdate(criteria, locationListener, null);
                }
                else{
                    Snackbar.make(btnMyAddress, "Lokasi tidak terdeteksi, mohon aktfikan gps anda", BaseTransientBottomBar.LENGTH_LONG)
                            .setBackgroundTint(Color.RED)
                            .setTextColor(Color.WHITE)
                            .show();
                }
            }
            else{
                Snackbar.make(btnMyAddress, "Untuk Menentukan Lokasi, permission lokasi harus diberikan", BaseTransientBottomBar.LENGTH_LONG)
                        .setBackgroundTint(Color.RED)
                        .setTextColor(Color.WHITE)
                        .show();
            }
        }
    }

    public void donePick(View v){
        if(viewModel.getHereChosenAddress() != null){
            Intent resultintent = new Intent();
            resultintent.putExtra("alamat", viewModel.getHereChosenAddress());
            resultintent.putExtra("koordinat", viewModel.getStringCoordinate());
            setResult(12, resultintent);
            finish();
        }
    }

    private void setMapViewMarker(MapViewLite map, double latitude, double longitude){
        if (chosenAddressMarker != null) {
            map.getMapScene().removeMapMarker(chosenAddressMarker);
        }
        chosenAddressMarker = new MapMarker(new GeoCoordinates(latitude, longitude));
        chosenAddressMarker.addImage(MapImageFactory.fromResource(AddressPickerActivity.this.getResources(), R.drawable.placeholder), new MapMarkerImageStyle());
        map.getMapScene().addMapMarker(chosenAddressMarker);
        map.getCamera().setTarget(new GeoCoordinates(latitude, longitude));
    }
}