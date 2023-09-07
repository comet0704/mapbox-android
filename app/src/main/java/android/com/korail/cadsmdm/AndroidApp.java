package android.com.korail.cadsmdm;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.extrus.exafe.common.core.IJHProject_AndroidApp;
import com.extrus.exafe.common.core.JHProject_AndroidApp;

import android.app.Application;
import android.content.Context;



/**
 * Created by logos1611 on 2017. 11. 16..
 */



public class AndroidApp extends Application implements IJHProject_AndroidApp {
    private Context mContext = null;

    private String mGlobalString; // 전역변수 테스트

    public String getGlobalString(){
        return mGlobalString;
    }

    public void setGlobalString(String globalString){
        this.mGlobalString = globalString;
    }

    // [SRT] 20191211_SRT_CHECK
    public final static String GEO_TAG = "GEOSOFT";

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = AndroidApp.this;

        if (JHProject_AndroidApp.isInitialized() == false) {
            JHProject_AndroidApp.init_NoJHH(AndroidApp.this);
        }

    }


    @Override
    public String format(String pStr, Object... args) {
        // TODO Auto-generated method stub
        return String.format(pStr, args);
    }

    @Override
    public Date parseFromDateTimeFormat(String pDateTimeFormat) throws Exception {
        // TODO Auto-generated method stub
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(pDateTimeFormat);
    }

    @Override
    public Context getAppContext() {
        // TODO Auto-generated method stub
        return mContext;
    }

}
