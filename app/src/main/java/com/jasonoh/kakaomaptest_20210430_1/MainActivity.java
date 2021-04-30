package com.jasonoh.kakaomaptest_20210430_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    final int MAP_PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getHashKey();

//위치정보 제공을 받기 위한 퍼미션 작업 추가..
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionResult != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions( permissions, MAP_PERMISSIONS_REQUEST_CODE );
            }
        }

        MapView mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.kakao_map_view);
        mapViewContainer.addView(mapView);

        //지도의 중심점 변경
        //true면 앱 실행 시 애니메이션 효과가 나오고 false면 애니메이션이 나오지않음.
        mapView.setMapCenterPoint( MapPoint.mapPointWithGeoCoord(37.496955, 127.024950), true );

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                mapView.setShowCurrentLocationMarker(true);
            } else{
//                String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
//                requestPermissions( permissions, MAP_PERMISSIONS_REQUEST_CODE );
            }
        }

        // 줌 레벨 변경
        // 숫자가 작을수록 내근처와 가깝게 보임
        mapView.setZoomLevel( 7, true );

        // Zoom In, Out 허용 여부
        mapView.zoomIn(true);
        mapView.zoomOut(true);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint((MapPoint.mapPointWithGeoCoord( 37.496955, 127.024950 )));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.addPOIItem(marker);

        mapView.setPOIItemEventListener(new MapView.POIItemEventListener() {
            @Override
            public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
                Log.e("TAG", mapPOIItem.getItemName());
                Toast.makeText(MainActivity.this, "adsf  ::  " + mapPOIItem.getItemName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

            }

            @Override
            public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

            }

            @Override
            public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MAP_PERMISSIONS_REQUEST_CODE :
                if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "위치 정보 사용이 거부하셨습니다.\n사용자의 위치 탐색기능이 제한 됩니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }

    }

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
}