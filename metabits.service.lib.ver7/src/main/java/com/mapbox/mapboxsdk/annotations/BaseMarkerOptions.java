package com.mapbox.mapboxsdk.annotations;

import android.os.Parcelable;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Abstract builder class for composing custom Marker objects.
 * <p>
 * Extending this class requires implementing Parceable interface.
 *
 * @param <U> Type of the marker to be composed
 * @param <T> Type of the builder to be used for composing a custom Marker
 * @deprecated As of 7.0.0,
 * use <a href="https://github.com/mapbox/mapbox-plugins-android/tree/master/plugin-annotation">
 *   Mapbox Annotation Plugin</a> instead
 */
@Deprecated
public abstract class BaseMarkerOptions<U extends Marker, T extends BaseMarkerOptions<U, T>> implements Parcelable {

  protected LatLng position;
  protected String snippet;
  protected String title;
  protected Icon icon;


  // TODO Mapbox 변경 - 멤버 변수들 추가
  protected int type;
  protected int index;
  protected String name;
  protected String address;
  protected int ekispertCode;



  /**
   * Set the geographical location of the Marker.
   *
   * @param position the location to position the {@link Marker}.
   * @return the object for which the method was called.
   */
  public T position(LatLng position) {
    this.position = position;
    return getThis();
  }

  /**
   * Set the snippet of the Marker.
   *
   * @param snippet the snippet of the {@link Marker}.
   * @return the object for which the method was called.
   */
  public T snippet(String snippet) {
    this.snippet = snippet;
    return getThis();
  }

  /**
   * Set the title of the Marker.
   *
   * @param title the title of the {@link Marker}.
   * @return the object for which the method was called.
   */
  public T title(String title) {
    this.title = title;
    return getThis();
  }

  /**
   * Set the icon of the Marker.
   *
   * @param icon the icon of the {@link Marker}.
   * @return the object for which the method was called.
   */
  public T icon(Icon icon) {
    this.icon = icon;
    return getThis();
  }

  /**
   * Set the icon of the Marker.
   *
   * @param icon the icon of the {@link Marker}.
   * @return the object for which the method was called.
   */
  public T setIcon(Icon icon) {
    return icon(icon);
  }

  /**
   * Set the geographical location of the Marker.
   *
   * @param position the location to position the {@link Marker}.
   * @return the object for which the method was called.
   */
  public T setPosition(LatLng position) {
    return position(position);
  }

  /**
   * Set the snippet of the Marker.
   *
   * @param snippet the snippet of the {@link Marker}.
   * @return the object for which the method was called.
   */
  public T setSnippet(String snippet) {
    return snippet(snippet);
  }

  /**
   * Set the title of the Marker.
   *
   * @param title the title of the {@link Marker}.
   * @return the object for which the method was called.
   */
  public T setTitle(String title) {
    return title(title);
  }

  /**
   * Get the instance of the object for which this method was called.
   *
   * @return the object for which the this method was called.
   */
  public abstract T getThis();

  /**
   * Get the Marker.
   *
   * @return the Marker created from this builder.
   */
  public abstract U getMarker();



  // ----- TODO Mapbox 변경 - 추가된 멤버변수 관련 추가 -----
  public T type(int type) {
      this.type = type;
      return getThis();
  }

  public T index(int index) {
      this.index = index;
      return getThis();
  }

  public T name(String name) {
      this.name = name;
      return getThis();
  }

  public T address(String address) {
      this.address = address;
      return getThis();
  }

  public T ekispertCode(int ekispertCode) {
      this.ekispertCode = ekispertCode;
      return getThis();
  }
  // ---------------------------------------------------------------
	


}
