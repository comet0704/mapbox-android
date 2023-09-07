package kr.co.geosoft.metabits;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

/**
 * Created by 성관 on 2016-08-03.
 */
public class MapActivity extends Activity {

    private RelativeLayout mMapContainer;
    private MapView mMapView;

    // 지도의 기본좌표  35.679826,139.689493
    public static double DEFAULT_MAP_LONGITUDE = 139.68650579;
    public static double DEFAULT_MAP_LATITUDE = 35.68267721;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(MapActivity.this, getString(R.string.geo_map_access_token));

        setContentView(R.layout.activity_map);

        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.onCreate(new Bundle());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap geoMap) {
                //
                // geoMap.setStyleUrl(Style.getGeosStreetsUrl(1));
                //
                CameraPosition normalCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(DEFAULT_MAP_LATITUDE, DEFAULT_MAP_LONGITUDE))
                        .zoom(11.19)
                        .bearing(0)
                        .tilt(0)
                        .build();
                geoMap.setCameraPosition(normalCameraPosition);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mMapView.invalidate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
