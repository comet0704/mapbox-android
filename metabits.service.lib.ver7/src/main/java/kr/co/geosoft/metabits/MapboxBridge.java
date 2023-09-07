package kr.co.geosoft.metabits;

import android.content.Context;
import android.graphics.PointF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

/**
 * Created by geosoft on 2017-05-31.
 */

public class MapboxBridge extends com.mapbox.mapboxsdk.maps.MapView {

    public MapboxBridge(@NonNull Context context) {
        super(context);
    }

    public MapboxBridge(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MapboxBridge(@NonNull Context context, @NonNull MapboxMapOptions geoMapOptions) {
        super(context, geoMapOptions);
    }

    public LatLng getLatLng() {
        return mapboxMap.getLatLng();
    }

    public void setLatLng(final LatLng latLng) {
        mapboxMap.setLatLng(latLng);
    }

    protected void setLatLng(final LatLng latLng, double cx, double cy) {
        mapboxMap.setLatLng(latLng, cx, cy);
    }

    public void resetNorth() {
        mapboxMap.resetNorth();
    }

    protected double getDirection() {
        return mapboxMap == null ? 0.0 : mapboxMap.getBearing();
    }

    protected void setDirection(final double azimuth) {
        mapboxMap.setBearing(azimuth);
    }

    public double getZoom() {
        return mapboxMap.getZoom();
    }

    public double getTilt() {
        return mapboxMap.getTilt();
    }

    public LatLng fromScreenLocation(@NonNull PointF point) {
        return mapboxMap.getProjection().fromScreenLocation(point);
    }

    public PointF toScreenLocation(@NonNull LatLng location) {
        return mapboxMap.getProjection().toScreenLocation(location);
    }

}
