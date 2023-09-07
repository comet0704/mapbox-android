package com.mapbox.mapboxcore.location;

import android.app.PendingIntent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import static com.mapbox.mapboxcore.location.Utils.isBetterLocation;

/**
 * Mapbox replacement for Google Play Services Fused Location Client
 * <p>
 * Note: fusion will not work in background mode.
 */
class MapboxFusedLocationEngineImpl extends AndroidLocationEngineImpl {
  private static final String TAG = "MapboxLocationEngine";

  MapboxFusedLocationEngineImpl(@NonNull Context context) {
    super(context);
  }

  @NonNull
  @Override
  public LocationListener createListener(LocationEngineCallback<com.mapbox.mapboxcore.location.LocationEngineResult> callback) {
    return new MapboxLocationEngineCallbackTransport(callback);
  }

  @Override
  public void getLastLocation(@NonNull LocationEngineCallback<com.mapbox.mapboxcore.location.LocationEngineResult> callback) throws SecurityException {
    Location bestLastLocation = getBestLastLocation();
    if (bestLastLocation != null) {
      callback.onSuccess(com.mapbox.mapboxcore.location.LocationEngineResult.create(bestLastLocation));
    } else {
      callback.onFailure(new Exception("Last location unavailable"));
    }
  }

  @Override
  public void requestLocationUpdates(@NonNull com.mapbox.mapboxcore.location.LocationEngineRequest request,
                                     @NonNull LocationListener listener,
                                     @Nullable Looper looper) throws SecurityException {
    super.requestLocationUpdates(request, listener, looper);

    // Start network provider along with gps
    if (shouldStartNetworkProvider(request.getPriority())) {
      try {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
          request.getInterval(), request.getDisplacemnt(),
          listener, looper);
      } catch (IllegalArgumentException iae) {
        iae.printStackTrace();
      }
    }
  }

  @Override
  public void requestLocationUpdates(@NonNull com.mapbox.mapboxcore.location.LocationEngineRequest request,
                                     @NonNull PendingIntent pendingIntent) throws SecurityException {
    super.requestLocationUpdates(request, pendingIntent);

    // Start network provider along with gps
    if (shouldStartNetworkProvider(request.getPriority())) {
      try {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, request.getInterval(),
          request.getDisplacemnt(), pendingIntent);
      } catch (IllegalArgumentException iae) {
        iae.printStackTrace();
      }
    }
  }

  private Location getBestLastLocation() {
    Location bestLastLocation = null;
    for (String provider : locationManager.getAllProviders()) {
      Location location = getLastLocationFor(provider);
      if (location == null) {
        continue;
      }

      if (isBetterLocation(location, bestLastLocation)) {
        bestLastLocation = location;
      }
    }
    return bestLastLocation;
  }

  private boolean shouldStartNetworkProvider(int priority) {
    return (priority == com.mapbox.mapboxcore.location.LocationEngineRequest.PRIORITY_HIGH_ACCURACY
      || priority == LocationEngineRequest.PRIORITY_BALANCED_POWER_ACCURACY)
      && currentProvider.equals(LocationManager.GPS_PROVIDER);
  }

  private static final class MapboxLocationEngineCallbackTransport implements LocationListener {
    private final LocationEngineCallback<com.mapbox.mapboxcore.location.LocationEngineResult> callback;
    private Location currentBestLocation;

    MapboxLocationEngineCallbackTransport(LocationEngineCallback<com.mapbox.mapboxcore.location.LocationEngineResult> callback) {
      this.callback = callback;
    }

    @Override
    public void onLocationChanged(Location location) {

      if (isBetterLocation(location, currentBestLocation)) {
        currentBestLocation = location;
      }

      if (callback != null) {
        callback.onSuccess(LocationEngineResult.create(currentBestLocation));
      }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      Log.d(TAG, "onStatusChanged: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
      Log.d(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
      Log.d(TAG, "onProviderDisabled: " + provider);
    }
  }
}
