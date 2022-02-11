package my.istts.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.viewmodels.GroomerLocationViewModel;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapviewlite.CameraUpdate;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapPolyline;
import com.here.sdk.mapviewlite.MapPolylineStyle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.Padding;
import com.here.sdk.mapviewlite.PixelFormat;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.CarOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroomerLocationActivity extends AppCompatActivity {

    private MapMarker groomerMarker;
    private MapMarker destMarker;
    private MapPolyline routeMapPolyline;
    private GroomerLocationViewModel viewModel;
    private RoutingEngine routingEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groomer_location);

        viewModel = new GroomerLocationViewModel();
        viewModel.setPesananJanjitemu(getIntent().getStringExtra("id_pj"));

        MaterialToolbar tbGroomLoc = findViewById(R.id.tbGroomerLocation);
        tbGroomLoc.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }

        MapViewLite mapSearch = findViewById(R.id.mapGroomerLocation);
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

        viewModel.getDestLocation().observe(this, destLoc->{
            displayRouteOnMap(mapSearch, destLoc, viewModel.getGroomerLocation().getValue());
        });

        viewModel.getGroomerLocation().observe(this, groomerLoc->{
            displayRouteOnMap(mapSearch, viewModel.getDestLocation().getValue(), groomerLoc);
        });

    }

    private void displayRouteOnMap(MapViewLite mapSearch, String[] dest, String[] groomer){
        Waypoint startWaypoint = null;
        Waypoint destinationWaypoint = null;
        if(dest != null){
            GeoCoordinates destCoor = new GeoCoordinates(Double.parseDouble(dest[0]), Double.parseDouble(dest[1]));
            destinationWaypoint = new Waypoint(destCoor);
            addDestMarker(mapSearch, destCoor);
        }
        if(groomer != null && !groomer[0].equals("0")){
            GeoCoordinates startCoor = new GeoCoordinates(Double.parseDouble(groomer[0]), Double.parseDouble(groomer[1]));
            startWaypoint = new Waypoint(startCoor);
            addGroomerMarker(mapSearch, startCoor);
        }

        if(startWaypoint != null && destinationWaypoint != null){
            List<Waypoint> waypoints =
                    new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

            routingEngine.calculateRoute(
                    waypoints,
                    new CarOptions(),
                    new CalculateRouteCallback() {
                        @Override
                        public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                            if (routingError == null) {
                                assert routes != null;
                                Route route = routes.get(0);

                                addRouteLine(mapSearch, route);

                                CameraUpdate cameraUpdate = mapSearch.getCamera().calculateEnclosingCameraUpdate(
                                        route.getBoundingBox(),
                                        new Padding(10, 10, 10, 10));
                                mapSearch.getCamera().updateCamera(cameraUpdate);
                            } else {
                                Snackbar.make(mapSearch, "Tidak dapat menemukan rute", BaseTransientBottomBar.LENGTH_LONG)
                                        .setBackgroundTint(Color.RED)
                                        .setTextColor(Color.WHITE)
                                        .show();
                            }
                        }
                    });
        }

    }

    private void addRouteLine(MapViewLite map, Route route){
        if(routeMapPolyline != null){
            map.getMapScene().removeMapPolyline(routeMapPolyline);
        }
        GeoPolyline routeGeoPolyline;
        try {
            routeGeoPolyline = new GeoPolyline(route.getPolyline());
        } catch (InstantiationErrorException e) {
            // It should never happen that the route polyline contains less than two vertices.
            return;
        }
        MapPolylineStyle mapPolylineStyle = new MapPolylineStyle();
        mapPolylineStyle.setColor(0x00908AA0, PixelFormat.RGBA_8888);
        mapPolylineStyle.setWidthInPixels(10);
        routeMapPolyline = new MapPolyline(routeGeoPolyline, mapPolylineStyle);
        map.getMapScene().addMapPolyline(routeMapPolyline);
    }

    private void addDestMarker(MapViewLite map, GeoCoordinates coordinates){
        if (destMarker != null) {
            map.getMapScene().removeMapMarker(destMarker);
        }
        destMarker = new MapMarker(coordinates);
        destMarker.addImage(MapImageFactory.fromResource(GroomerLocationActivity.this.getResources(), R.drawable.placeholder), new MapMarkerImageStyle());
        map.getMapScene().addMapMarker(destMarker);
    }

    private void addGroomerMarker(MapViewLite map, GeoCoordinates coordinates){
        if (groomerMarker != null) {
            map.getMapScene().removeMapMarker(groomerMarker);
        }
        groomerMarker = new MapMarker(coordinates);
        groomerMarker.addImage(MapImageFactory.fromResource(GroomerLocationActivity.this.getResources(), R.drawable.groomerplace), new MapMarkerImageStyle());
        map.getMapScene().addMapMarker(groomerMarker);
    }
}