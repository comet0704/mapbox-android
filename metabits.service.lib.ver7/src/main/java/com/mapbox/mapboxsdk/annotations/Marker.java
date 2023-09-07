package com.mapbox.mapboxsdk.annotations;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;


// TODO Mapbox 변경
import kr.co.geosoft.metabits.R;
import android.view.MotionEvent;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.mapbox.mapboxsdk.MarkerType;


/**
 * Marker is an annotation that shows an icon image at a geographical location. The default marker
 * uses a provided icon. This icon can be customized using {@link IconFactory} to generate an
 * {@link Icon} using a provided image. Markers are added to the map by first giving a
 * {@link LatLng} and using {@link MapboxMap#addMarker(MarkerOptions)}. The marker icon will be
 * centered at this position so it is common to add padding to the icon image before usage.
 * <p>
 * Markers are designed to be interactive. They receive click events by default, and are often used
 * with event listeners to bring up info windows. An {@link InfoWindow} is displayed by default when
 * either a title or snippet is provided.
 * </p>
 * @deprecated As of 7.0.0,
 * use <a href="https://github.com/mapbox/mapbox-plugins-android/tree/master/plugin-annotation">
 *   Mapbox Annotation Plugin</a> instead
 */
@Deprecated
public class Marker extends Annotation {

  @Keep
  private LatLng position;
  private String snippet;
  @Nullable
  private Icon icon;
  //Redundantly stored for JNI access
  @Nullable
  @Keep
  private String iconId;
  private String title;

  @Nullable
  private InfoWindow infoWindow;
  private boolean infoWindowShown;

  private int topOffsetPixels;
  private int rightOffsetPixels;


    // TODO Mapbox 변경 - 멤버 변수 추가
    private int type = -1;
    private int index = -1;
    private String name;
    private String address;
    private int ekispertCode = 0;



  /**
   * Constructor
   */
  Marker() {
    super();
  }

  /**
   * Creates a instance of {@link Marker} using the builder of Marker.
   *
   * @param baseMarkerOptions The builder used to construct the Marker.
   */
  public Marker(BaseMarkerOptions baseMarkerOptions) {
    this(baseMarkerOptions.position, baseMarkerOptions.icon, baseMarkerOptions.title, baseMarkerOptions.snippet);
  }

  Marker(LatLng position, Icon icon, String title, String snippet) {
    this.position = position;
    this.title = title;
    this.snippet = snippet;
    setIcon(icon);
  }


    // ----- TODO Mapbox 변경 - 생성자 추가 -----
    Marker(LatLng position, Icon icon, String title, String snippet, int type, int index, String name, String address) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        setIcon(icon);
        //
        this.type = type;
        this.index = index;
        this.name = name;
        this.address = address;
    }

    Marker(LatLng position, Icon icon, String title, String snippet, int type, int index, String name, String address, int ekispertCode) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        setIcon(icon);
        //
        this.type = type;
        this.index = index;
        this.name = name;
        this.address = address;
        this.ekispertCode = ekispertCode;
    }
    // ---------------------------------------------------


  /**
   * Returns the position of the marker.
   *
   * @return A {@link LatLng} object specifying the marker's current position.
   */
  public LatLng getPosition() {
    return position;
  }

  /**
   * Gets the snippet of the marker.
   *
   * @return A string containing the marker's snippet.
   */
  public String getSnippet() {
    return snippet;
  }

  /**
   * Gets the snippet of the marker.
   *
   * @return A string containing the marker's snippet.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Do not use this method, used internally by the SDK.
   */
  public void hideInfoWindow() {
    if (infoWindow != null) {
      infoWindow.close();
    }
    infoWindowShown = false;
  }

  /**
   * Do not use this method, used internally by the SDK.
   *
   * @return true if the infoWindow is shown
   */
  public boolean isInfoWindowShown() {
    return infoWindowShown;
  }

  /**
   * Sets the location of the marker.
   *
   * @param position A {@link LatLng} defining the marker position.
   */
  public void setPosition(LatLng position) {
    this.position = position;
    MapboxMap map = getMapboxMap();
    if (map != null) {
      map.updateMarker(this);
    }
  }

  /**
   * Sets the snippet of the marker.
   *
   * @param snippet A String used in the marker info window. If {@code null}, the snippet is
   *                cleared.
   */
  public void setSnippet(String snippet) {
    this.snippet = snippet;
    refreshInfoWindowContent();
  }

  /**
   * Sets the icon of the marker.
   *
   * @param icon The {@link Icon} to be used as Marker image
   */
  public void setIcon(@Nullable Icon icon) {
    this.icon = icon;
    this.iconId = icon != null ? icon.getId() : null;
    MapboxMap map = getMapboxMap();
    if (map != null) {
      map.updateMarker(this);
    }
  }

  /**
   * Gets the {@link Icon} currently used for the marker. If no Icon was set for the marker, the
   * default icon will be returned.
   *
   * @return The {@link Icon} the marker is using.
   */
  @Nullable
  public Icon getIcon() {
    return icon;
  }

  /**
   * Sets the title of the marker.
   *
   * @param title A String used in the marker info window. If {@code null}, the title is
   *              cleared.
   */
  public void setTitle(String title) {
    this.title = title;
    refreshInfoWindowContent();
  }

  /**
   * Gets the {@link InfoWindow} the marker is using. If the marker hasn't had an info window
   * defined, this will return {@code null}.
   *
   * @return The info window the marker is using.
   */
  @Nullable
  public InfoWindow getInfoWindow() {
    return infoWindow;
  }

  /**
   * Update only for default Marker's InfoWindow content for Title and Snippet
   */
  private void refreshInfoWindowContent() {
    if (isInfoWindowShown() && mapView != null && mapboxMap != null && mapboxMap.getInfoWindowAdapter() == null) {
      InfoWindow infoWindow = getInfoWindow(mapView);
      if (mapView.getContext() != null) {
        infoWindow.adaptDefaultMarker(this, mapboxMap, mapView);
      }
      MapboxMap map = getMapboxMap();
      if (map != null) {
        map.updateMarker(this);
      }
      infoWindow.onContentUpdate();
    }
  }

  /**
   * Do not use this method, used internally by the SDK. Use {@link MapboxMap#selectMarker(Marker)}
   * if you want to programmatically display the markers info window.
   *
   * @param mapboxMap The hosting mapbox map.
   * @param mapView   The hosting map view.
   * @return The info window that was shown.
   */
  @Nullable
  public InfoWindow showInfoWindow(@NonNull MapboxMap mapboxMap, @NonNull MapView mapView) {
    setMapboxMap(mapboxMap);
    setMapView(mapView);
    MapboxMap.InfoWindowAdapter infoWindowAdapter = getMapboxMap().getInfoWindowAdapter();
    if (infoWindowAdapter != null) {
      // end developer is using a custom InfoWindowAdapter
      View content = infoWindowAdapter.getInfoWindow(this);
      if (content != null) {
        infoWindow = new InfoWindow(content, mapboxMap);
        showInfoWindow(infoWindow, mapView);
        return infoWindow;
      }
    }

    InfoWindow infoWindow = getInfoWindow(mapView);
    if (mapView.getContext() != null) {
      infoWindow.adaptDefaultMarker(this, mapboxMap, mapView);
    }
    return showInfoWindow(infoWindow, mapView);
  }

  @NonNull
  private InfoWindow showInfoWindow(InfoWindow iw, MapView mapView) {
    iw.open(mapView, this, getPosition(), rightOffsetPixels, topOffsetPixels);
    infoWindowShown = true;
    return iw;
  }


  // ----- TODO Mapbox 변경 - 기존 getInfoWindow 함수는 주석처리하고 아래 함수로 변경 -----------------------------------
  @Nullable
  /*private InfoWindow getInfoWindow(@NonNull MapView mapView) {
    if (infoWindow == null && mapView.getContext() != null) {
      infoWindow = new InfoWindow(mapView, R.layout.mapbox_infowindow_content, getMapboxMap());
    }
    return infoWindow;
  }*/
    private InfoWindow getInfoWindow(@NonNull final MapView mapView) {
        if (infoWindow == null && mapView.getContext() != null) {
            if (getType() == MarkerType.MARKER_VICS) {
                infoWindow = new InfoWindow(mapView, R.layout.mapbox_infowindow_content_web, getMapboxMap());
                final WebView webView = (WebView) infoWindow.getView().findViewById(R.id.infowindow_description);
                webView.setWebViewClient(new WebViewClient() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return processUrl(url) || super.shouldOverrideUrlLoading(view, url);
                    }

                    @TargetApi(Build.VERSION_CODES.N)
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        return processUrl(request.getUrl().toString()) || super.shouldOverrideUrlLoading(view, request);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                resizeInfoWindow(infoWindow);
                            }
                        }, 200);
                    }

                    /**
                     * a 링크의 URL 처리
                     *
                     * @param url
                     * @return
                     */
                    private boolean processUrl(String url) {
                        boolean isProcessed = false;
                        String outBrowserProtocolStr = "___target=_blank"; //외부 웹브라우저에서 url을 표시하기 위한 protocol 문자열
                        if (url.contains(outBrowserProtocolStr)) {
                            //protocol 문자열은 제거하고 보낸다.
                            url = url.replaceAll(outBrowserProtocolStr, "");
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            //mapView.getActivity().startActivity(intent);
                            isProcessed = true;
                        }

                        return isProcessed;
                    }
                });
                webView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            infoWindow.closeInfoWindow();
                        }
                        return false;
                    }
                });
            } else {
                infoWindow = new InfoWindow(mapView, R.layout.mapbox_infowindow_content, getMapboxMap());
            }
        }
        return infoWindow;
    }
    // ---------------------------------------------------------------------------------------


  /**
   * Do not use this method, used internally by the SDK.
   *
   * @param topOffsetPixels the top offset pixels.
   */
  public void setTopOffsetPixels(int topOffsetPixels) {
    this.topOffsetPixels = topOffsetPixels;
  }

  /**
   * Do not use this method, used internally by the SDK.
   *
   * @param rightOffsetPixels the right offset pixels.
   */
  public void setRightOffsetPixels(int rightOffsetPixels) {
    this.rightOffsetPixels = rightOffsetPixels;
  }

  /**
   * Returns a String with the marker position.
   *
   * @return A String with the marker position.
   */
  @Override
  public String toString() {
    return "Marker [position[" + getPosition() + "]]";
  }


    // ----- TODO Mapbox 변경 - 함수 추가 ----------------------------------------
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getEkispertCode() {
        return ekispertCode;
    }

    public void setEkispertCode(int ekispertCode) {
        this.ekispertCode = ekispertCode;
    }

    private InfoWindow resizeInfoWindow(InfoWindow iw) {
        iw.resizeAndVisible(getPosition(), rightOffsetPixels, topOffsetPixels);
        return iw;
    }
    // --------------------------------------------------------------------------------


}
