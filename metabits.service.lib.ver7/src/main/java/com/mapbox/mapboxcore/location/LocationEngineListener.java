package com.mapbox.mapboxcore.location;


import android.location.Location;

/**
 * Callback used in LocationEngine
 */

public interface LocationEngineListener {

    void onConnected();

    void onLocationChanged(Location location);

    void onReroute();

}
