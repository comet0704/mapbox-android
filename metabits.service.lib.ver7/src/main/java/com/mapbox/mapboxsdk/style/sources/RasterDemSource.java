package com.mapbox.mapboxsdk.style.sources;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.net.URL;

/**
 * A raster DEM source. Currently only supports Mapbox Terrain RGB (mapbox://mapbox.terrain-rgb)
 *
 * @see <a href="https://www.mapbox.com/mapbox-gl-js/style-spec/#sources-raster-dem">The style specification</a>
 */
@UiThread
public class RasterDemSource extends Source {
  public static final int DEFAULT_TILE_SIZE = 512;

  /**
   * Internal use
   *
   * @param nativePtr - pointer to native peer
   */
  @Keep
  RasterDemSource(long nativePtr) {
    super(nativePtr);
  }

  /**
   * Create the raster dem source from an URL
   *
   * @param id  the source id
   * @param url the source url
   */
  public RasterDemSource(String id, URL url) {
    this(id, url.toExternalForm());
  }

  /**
   * Create the raster dem source from an URL
   *
   * @param id  the source id
   * @param url the source url
   */
  public RasterDemSource(String id, String url) {
    super();
    initialize(id, url, DEFAULT_TILE_SIZE);
  }

  /**
   * Create the raster source from an URL with a specific tile size
   *
   * @param id       the source id
   * @param url      the source url
   * @param tileSize the tile size
   */
  public RasterDemSource(String id, String url, int tileSize) {
    super();
    initialize(id, url, tileSize);
  }

  /**
   * Create the raster dem source from a {@link TileSet}
   *
   * @param id      the source id
   * @param tileSet the {@link TileSet}
   */
  public RasterDemSource(String id, TileSet tileSet) {
    super();
    initialize(id, tileSet.toValueObject(), DEFAULT_TILE_SIZE);
  }

  /**
   * Create the raster source from a {@link TileSet} with a specific tile size
   *
   * @param id       the source id
   * @param tileSet  the {@link TileSet}
   * @param tileSize tje tile size
   */
  public RasterDemSource(String id, TileSet tileSet, int tileSize) {
    super();
    initialize(id, tileSet.toValueObject(), tileSize);
  }

  /**
   * @return The url or null
   */
  @Nullable
  public String getUrl() {
    checkThread();
    return nativeGetUrl();
  }

  @Keep
  protected native void initialize(String layerId, Object payload, int tileSize);

  @Override
  @Keep
  protected native void finalize() throws Throwable;

  @NonNull
  @Keep
  protected native String nativeGetUrl();

}
