package kr.co.geosoft.metabits;

import android.util.Log;

/**
 * Created by 성관 on 2018-01-04.
 */

public class MLog {   // TODO Mapbox 변경 - MLog 클래스 추가

    private static final String LOG_TAG = "MapBox_Debug";
    private static final String FORMAT = "[%s]: %s";

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG)            // 디버그 모드에서만 로그표시하도록...
            Log.d(LOG_TAG, String.format(FORMAT, tag, msg));
    }

}
