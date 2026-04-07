package com.example.doanappfood.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.PolylineDecoder;
import com.example.doanappfood.databinding.FragmentMapBinding;
import com.example.doanappfood.model.StoreModel;
import com.example.doanappfood.viewmodel.MapViewModel;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private MapViewModel viewModel;

    private LocationManager locationManager;
    private Polyline routePolyline;
    private MyLocationNewOverlay myLocationOverlay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        Configuration.getInstance().setUserAgentValue(getContext().getPackageName());

        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public  void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        setupMap();
        setupBlueDot();
        checkPermissionsAndStartLocation();
    }
    private void setupMap() {
        // Google Satellite
        XYTileSource googleSat = new XYTileSource(
                "Google-Sat",
                0, 19, 256, ".png",
                new String[]{
                        "https://mt0.google.com/vt/lyrs=s&x=",
                        "https://mt1.google.com/vt/lyrs=s&x=",
                        "https://mt2.google.com/vt/lyrs=s&x=",
                        "https://mt3.google.com/vt/lyrs=s&x="
                }
        ) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getX(pMapTileIndex) + "&y="
                        + MapTileIndex.getY(pMapTileIndex) + "&z="
                        + MapTileIndex.getZoom(pMapTileIndex);
            }
        };

        binding.mapView.setTileSource(googleSat);
        binding.mapView.setMultiTouchControls(true);
        binding.mapView.getController().setZoom(14.0);
        binding.mapView.getController().setCenter(new GeoPoint(21.0285, 105.8542));
    }
    private void setupBlueDot() {
        GpsMyLocationProvider provider = new GpsMyLocationProvider(requireContext());

        myLocationOverlay = new MyLocationNewOverlay(provider, binding.mapView);
        myLocationOverlay.enableFollowLocation();
        myLocationOverlay.setEnableAutoStop(true);
        myLocationOverlay.setDrawAccuracyEnabled(true);
        Bitmap raw = BitmapFactory.decodeResource(getResources(), R.drawable.ic_markerpeople);
        Bitmap personIcon = Bitmap.createScaledBitmap(raw, 80, 80, true);

        myLocationOverlay.setPersonIcon(personIcon);
        binding.mapView.getOverlays().add(myLocationOverlay);
        binding.mapView.invalidate();
    }

    private void checkPermissionsAndStartLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            startLocationTracking();
        }
    }
    private void startLocationTracking() {
        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation();
        }
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission")
        Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (last == null) {
            @SuppressLint("MissingPermission")
            Location network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            last = network;
        }

        if (last != null) {
            onLocationReady(last);
        } else {
            Log.e("MAP", "Không lấy được vị trí ban đầu.");
        }
    }

    private void onLocationReady(Location last) {
        GeoPoint userPoint = new GeoPoint(last.getLatitude(), last.getLongitude());
        binding.mapView.getController().animateTo(userPoint);
//        addMarker(userPoint, "Vị trí của bạn", R.drawable.ic_marker);

        loadAllStores();
        findNearestAndRoute(last.getLatitude(), last.getLongitude());
    }
    private void loadAllStores() {
        viewModel.getAllStores().observe(getViewLifecycleOwner(), stores -> {
            if (stores == null) return;
            for (StoreModel store : stores) {
                addMarker(new GeoPoint(store.getLat(), store.getLng()), store.getName(), R.drawable.ic_marker);
            }
        });
    }
    private void findNearestAndRoute(double lat, double lng) {
        viewModel.getNearestStore(lat, lng).observe(getViewLifecycleOwner(), nearest -> {
            if (nearest == null) return;
            loadDirections(lat, lng, nearest.getLat(), nearest.getLng(), nearest.getName());
        });
    }

    private void loadDirections(double fromLat, double fromLng, double toLat, double toLng, String storeName) {
        viewModel.getDirections(fromLat, fromLng, toLat, toLng).observe(getViewLifecycleOwner(), direction -> {
            if (direction == null) return;
            drawRoute(direction.getPolyline());
            showRouteInfo(storeName, direction.getDistanceText(), direction.getDurationText());
        });
    }

    private void drawRoute(String encodedPolyline) {
        if (routePolyline != null) binding.mapView.getOverlays().remove(routePolyline);
        List<GeoPoint> points = PolylineDecoder.decode(encodedPolyline);
        routePolyline = new Polyline();
        routePolyline.setPoints(points);
        routePolyline.getOutlinePaint().setColor(Color.RED);
        routePolyline.getOutlinePaint().setStrokeWidth(10f);
        binding.mapView.getOverlays().add(routePolyline);
        binding.mapView.invalidate();
    }

    private void showRouteInfo(String storeName, String distance, String duration) {
        binding.tvStoreName.setText(storeName);
        binding.tvDistance.setText( distance);
        binding.tvDuration.setText(duration);
        binding.cardRouteInfo.setVisibility(View.VISIBLE);
    }
    private void addMarker(GeoPoint point, String title, int iconRes) {
        Marker marker = new Marker(binding.mapView);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setIcon(ContextCompat.getDrawable(requireContext(), iconRes));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        binding.mapView.getOverlays().add(marker);
        binding.mapView.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationTracking();
        }
    }

    @Override public void onResume() {
        super.onResume();
        binding.mapView.onResume();
        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation();
        }
    }
    @Override public void onPause() {
        super.onPause();
        binding.mapView.onPause();
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        binding.mapView.onDetach();
        binding = null;
    }
}