package android.com.korail.cadsmdm;

/**
 * Created by logos1611 on 2017. 3. 29..
 */


import android.content.Context;

import android.os.Bundle;

import android.speech.tts.TextToSpeech;

import android.util.Log;

import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;

import com.mapbox.mapboxsdk.camera.CameraPosition;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import android.os.Environment;



public class AccidentMapActivity extends AppCompatActivity{



    // 지도의 기본좌표  35.679826,139.689493

    public double DEFAULT_MAP_LONGITUDE = 126.9703719;
    public double DEFAULT_MAP_LATITUDE = 37.55225282;



    private InputMethodManager inputMethodManager;



    // 좌표 출력 스레드
    Thread myThread = null;
    // 스레드 기동 체크
    Boolean isRunning = true;


    // 이미지뷰 마커(중앙)
    public ImageView trainMarker;
    public TextView txtView_speed;

    // 수신된 현재위치
    LatLng currPosition;

    // 맵뷰 및 맵박스 데이터저장
    private MapView mMapView;
    private MapboxMap mMapboxMap;

    // 손으로 움직이고 있을때
    Boolean isFingerMoving = false;
    // 터치 중 카운트 업



    // 뷰리스트
    RelativeLayout relativeLayoutMap;   // 지도뷰



    RelativeLayout relativeMessage; // 세부정보




    //search
    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private ArrayList<ArrayList<String>> mChildFilePath = null; // 추가


    private String mCadsPath;

    //train search



    public TextView textViewstationStr;
    public TextView textViewmessageStr;
    public TextView textViewarrvStr;




    public boolean loadpushcheck = true; // 푸시 조회만 막을때


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("pushcontrol", "PUSH ON ");
        loadpushcheck = true;

        // 지도 Access Key 설정

        Mapbox.getInstance(AccidentMapActivity.this, "pk.eyJ1IjoiemlreW1pIiwiYSI6ImNqMGFuam9wcDAweTAyd2xjenUyam5laGEifQ.4RznQYhVwJg2gyLMFzfcKw");


        setContentView(R.layout.activity_accident_map);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /*
        tts = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
*/

        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        // 상단 바
        //LinearLayout mFab = (LinearLayout) findViewById(R.id.floating_bar);
        // 그림자 만들기
        //ViewCompat.setElevation(mFab, 12);
        // 투명처리
        //ViewCompat.setAlpha(mFab, 0.7f);

        // 테스트
        final TextView txtViewZoomLv = (TextView) findViewById(R.id.txtView_zoomLv);
        final TextView txtViewCenter = (TextView) findViewById(R.id.txtView_center);

        // 고정마커
        trainMarker = (ImageView) findViewById(R.id.imageMarker);
        txtView_speed = (TextView) findViewById(R.id.txtView_speed);

        //editText4 = (EditText) findViewById(R.id.editText4);

        // 화면부제어
        relativeLayoutMap = (RelativeLayout) findViewById(R.id.relativelayoutmap); // 지도뷰



        mCadsPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CADS";






        mMapView = (MapView) findViewById(R.id.accidentmapview);
        mMapView.onCreate(new Bundle());

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap geoMap) {

                mMapboxMap = geoMap;

                geoMap.getUiSettings().setLogoEnabled(false);
                geoMap.getUiSettings().setAttributionEnabled(false);
                // 코레일
                geoMap.setStyleUrl("geomap://styles/geomap/cads-v1");



                // 첫 카메라 배치
                CameraPosition normalCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(DEFAULT_MAP_LATITUDE, DEFAULT_MAP_LONGITUDE)) // 서울역
                        .zoom(13)
                        .bearing(0)
                        .tilt(0)
                        .build();
                geoMap.setCameraPosition(normalCameraPosition);





                // 현재위치
                currPosition = new LatLng(DEFAULT_MAP_LATITUDE, DEFAULT_MAP_LONGITUDE);


                // 카메라 위치변동 추적
                geoMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {

                        LatLng position = mMapboxMap.getLatLng();


                        LatLng mapCenterPoint = new LatLng(DEFAULT_MAP_LATITUDE, DEFAULT_MAP_LONGITUDE);
                        LatLng mapMovePoint = new LatLng(DEFAULT_MAP_LATITUDE, DEFAULT_MAP_LONGITUDE);

                        double centerdistance = mapCenterPoint.distanceTo(mapMovePoint);


                        // 마커 표현
                        if(centerdistance > 200){
                            // 움직여서 현재 위치와 화면 중앙이 멀어졌을때
                            isFingerMoving = true;
//                            trainMarker.setAlpha(0.0f);
//                            txtView_speed.setAlpha(0.0f);
                        }else{
                            // 화면이 움직여서 지도 가운데 왔을때
                            isFingerMoving = false;
                            trainMarker.setAlpha(1.0f);
                            txtView_speed.setAlpha(1.0f);
                        }


                    }
                });



            }
        });



        // search



        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();
        mChildFilePath = new ArrayList<ArrayList<String>>();


        mGroupList.clear();
        mChildList.clear();
        mChildFilePath.clear();






    }







    @Override
    protected void onStart() {

        Log.i("edit", "onStart");


        super.onStart();
        Log.i("pushcontrol", "PUSH ON ");
        loadpushcheck = true;
    }


    @Override
    protected void onRestart(){
        Log.i("edit", "onRestart");
        super.onRestart();
        Log.i("pushcontrol", "PUSH ON ");
        loadpushcheck = true;

    }

    @Override
    protected void onResume() {
        Log.i("edit", "onResume");
        super.onResume();
        Log.i("pushcontrol", "PUSH ON ");
        loadpushcheck = true;
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("edit", "onPause");
        super.onPause();
        Log.i("pushcontrol", "PUSH OFF ");
        loadpushcheck = false;
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("edit", "onStop");
        Log.i("pushcontrol", "PUSH OFF ");
        loadpushcheck = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("edit", "onDestroy");
        super.onDestroy();
        Log.i("pushcontrol", "PUSH OFF ");
        loadpushcheck = false;


        mMapboxMap.clear();
        mMapView.removeAllViews();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
       mMapView.onLowMemory();
    }







}
