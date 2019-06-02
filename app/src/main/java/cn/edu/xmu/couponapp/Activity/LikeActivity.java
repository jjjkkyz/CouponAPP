package cn.edu.xmu.couponapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import cn.edu.xmu.couponapp.Bean.Bound;
import cn.edu.xmu.couponapp.Bean.Shop;
import cn.edu.xmu.couponapp.R;
import cn.edu.xmu.couponapp.overlay.PoiOverlay;
import cn.edu.xmu.couponapp.util.Utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LikeActivity extends AppCompatActivity implements AMapLocationListener, PoiSearch.OnPoiSearchListener, AMap.OnInfoWindowClickListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter {
    private AMap mMap;
    private AMapLocationClient mLocationClient;
    private PoiSearch mPoiSearch;
    private AMapLocationClientOption mLocationOption;
    private Marker mLocationMarker;
    private Circle mLocationCircle;
    private PoiOverlay mPoiOverlay;
    private AMapLocation mCurrentLocation;

    //Firebase实时数据库引用
    DatabaseReference myRef;
    DatabaseReference couponStore;

    private PoiResult result;
    private Map<String,String> snapshotMap=new HashMap<>();
    private Bound bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_main);
        setUpMapIfNeeded();
        initLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            mMap.setOnMarkerClickListener(this);
            mMap.setOnInfoWindowClickListener(this);
            mMap.setInfoWindowAdapter(this);
        }
    }

    /**
     * 进行poi搜索
     *
     * @param lat
     * @param lon
     */
    private void initPoiSearch(double lat, double lon) {
        if (mPoiSearch == null) {
            PoiSearch.Query poiQuery = new PoiSearch.Query("", "餐饮服务");
            LatLonPoint centerPoint = new LatLonPoint(lat, lon);
            PoiSearch.SearchBound searchBound = new PoiSearch.SearchBound(centerPoint, 5000);
            mPoiSearch = new PoiSearch(this.getApplicationContext(), poiQuery);
            mPoiSearch.setBound(searchBound);
            mPoiSearch.setOnPoiSearchListener(this);
            mPoiSearch.searchPOIAsyn();
        }
    }


    private void destroyLocation() {
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(this);
            mLocationClient.onDestroy();
        }
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation == null || aMapLocation.getErrorCode() != AMapLocation.LOCATION_SUCCESS) {
            Toast.makeText(this,aMapLocation.getErrorInfo()+"  "+aMapLocation.getErrorCode(), Toast.LENGTH_LONG).show();
            return;
        }
        mCurrentLocation = aMapLocation;
        LatLng curLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        if (mLocationMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(curLatLng);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked));
            mLocationMarker = mMap.addMarker(markerOptions);
        }
        if (mLocationCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(curLatLng);
            circleOptions.radius(aMapLocation.getAccuracy());
            circleOptions.strokeWidth(2);
            circleOptions.strokeColor(getResources().getColor(R.color.stroke));
            circleOptions.fillColor(getResources().getColor(R.color.fill));
            mLocationCircle = mMap.addCircle(circleOptions);
        }
        initPoiSearch(aMapLocation.getLatitude(), aMapLocation.getLongitude());
    }

    /**
     *  获取限定范围内的商铺
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i != AMapException.CODE_AMAP_SUCCESS || poiResult == null) {
            return;
        }
        if (mPoiOverlay != null) {
            mPoiOverlay.removeFromMap();
        }
        //得到当前位置500米范围内的店铺
        Double range=500.0;
        bound=new Bound(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude(),range);

        myRef = FirebaseDatabase.getInstance().getReference();
        couponStore = myRef.child("coupon");
        couponStore.orderByChild("longitude").startAt(bound.getLeftLo()).endAt(bound.getRightLo()).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  List<Shop> shopList=new ArrayList<>();
                  for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                          Shop bean = postSnapshot.getValue(Shop.class);
                          if(bean.getLatitude()>=bound.getBottomLa()&&bean.getLatitude()<=bound.getTopLa()){
                              shopList.add(bean);
                              snapshotMap.put(postSnapshot.getKey(),bean.getTitle());
                          }
                  }
                  LatLng currentLocation=new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
                  mPoiOverlay = new PoiOverlay(mMap, shopList,currentLocation);
                  mPoiOverlay.addToMap();
                  mPoiOverlay.zoomToSpan();
              }
              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {
                  System.out.println("The read failed: " + databaseError.getMessage());
              }
          }
        );

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mLocationMarker == marker) {
            return false;
        }

        return false;
    }

    /**
     * 自定义marker点击弹窗内容
     *
     * @param marker
     * @return
     */
    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
                null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        ImageButton detail_button = (ImageButton) view
                .findViewById(R.id.go_store);
        // 调店铺优惠页
        detail_button.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String value = "";
             String name = "";
             for (String key:snapshotMap.keySet()) {
                 String title = snapshotMap.get(key);
                 if (marker.getTitle().contains(title)) {
                     name = name + title;
                     value = value + "coupon/" + key;
                     break;
                 }
             }
             store_click(value,name);
         }
     }
        );

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        int index = mPoiOverlay.getPoiIndex(marker);
        float distance = mPoiOverlay.getDistance(index);
        String showDistance = Utils.getFriendlyDistance((int) distance);
        snippet.setText("距当前位置" + showDistance);
        ImageButton button = (ImageButton) view
                .findViewById(R.id.start_amap_app);
        // 调起导航
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAMapNavi(marker);
            }
        });
        return view;
    }

    /**
     * 点击一键导航按钮跳转到导航页面
     *
     * @param marker
     */
    private void startAMapNavi(Marker marker) {
        if (mCurrentLocation == null) {
            return;
        }
        Intent intent = new Intent(this, RouteNaviActivity.class);
        intent.putExtra("gps", false);
        intent.putExtra("start", new NaviLatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        intent.putExtra("end", new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude));
        startActivity(intent);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public void store_click(String value, String name) {
        Intent it = new Intent(this, Coupon_ListActivity.class);
        it.putExtra("url", value);
        it.putExtra("map", name);
        startActivity(it);
    }
}
