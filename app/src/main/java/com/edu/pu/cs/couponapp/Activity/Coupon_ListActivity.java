package com.edu.pu.cs.couponapp.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.navi.model.NaviLatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.edu.pu.cs.couponapp.Adapter.ListAdapter;

import com.edu.pu.cs.couponapp.Bean.Bean;
import com.edu.pu.cs.couponapp.Bean.Shop;
import com.edu.pu.cs.couponapp.R;
import com.edu.pu.cs.couponapp.ToastUtils;
import com.github.anzewei.parallaxbacklayout.ParallaxActivityBase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qiu.niorgai.StatusBarCompat;

/**
 * Created by Administrator on 2016/10/29.
 */

public class Coupon_ListActivity extends ParallaxActivityBase {
    private ListAdapter adapter;
    private ListView listView;
    private String[] a = new String[10];

    private List<String> BeginDateList = new ArrayList<>();
    private List<String> EndDateList = new ArrayList<>();
    // 标题集合
    private List<String> titleList = new ArrayList<String>();
    // 图片地址集合
    private List<String> imgAddressList = new ArrayList<String>();
    // 文本描述集合
    private List<String> ContentList = new ArrayList<String>();
    // 所有数据的MAP集合
    private ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

    ImageView titlelogo;

    private String TitleLogo = null;

    public static Coupon_ListActivity instance = null;

    private String listorgrid = "list";

    private List<String> DetailsimgList = new ArrayList<String>();

    private Context mContext;

    private DatabaseReference mFirebasestore;
    private DatabaseReference mFirebaselogo;

    //有效期
    private String begindate,enddate;
    private long servertimestamp;
    private String DateOver;

    private double longitude;
    private double latitude;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocationListener mLocationListener;
    private AMapLocation mCurrentLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackEnable(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_couponlist);
        StatusBarCompat.setStatusBarColor(Coupon_ListActivity.this, getResources().getColor(R.color.background_color_blue));

        instance = this;

        final ProgressDialog dialog5 = ProgressDialog.show(this, "稍候片刻", "折价券即将呈现", true, true);
        dialog5.onStart();
        titlelogo = (ImageView) findViewById(R.id.titlelogo);
        listView = (ListView) findViewById(R.id.listview_2);


        String url = getIntent().getStringExtra("url");
        //获取顶部logo和经纬度
        mFirebaselogo = FirebaseDatabase.getInstance().getReference(url);
        mFirebaselogo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shop shop = dataSnapshot.getValue(Shop.class);
                Glide.with(Coupon_ListActivity.this).load(shop.getTitlelogo()).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().thumbnail(0.1f).error(com.example.gridviewimage.R.mipmap.image_error).into(titlelogo);
                TitleLogo = shop.getTitlelogo();
                latitude = shop.getLatitude();
                longitude = shop.getLongitude();
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });

        getStringValue(url+"/list");
        //        获取数据后，在这里点击就会把标题图片和内容传递过去，这里前提是你得保证有数据
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                判断集合里边是否有数据
                Intent in = new Intent(Coupon_ListActivity.this, DetailsActivity.class);
                in.putExtra("title", list.get(i).get("title"));
                in.putExtra("imgAddress", list.get(i).get("imaAddress"));
                in.putExtra("content", list.get(i).get("content"));
                in.putExtra("TitleLogo", TitleLogo);
                in.putExtra("listorgrid", listorgrid);
                in.putExtra("detailsImages", list.get(i).get("detailsImages"));
                startActivity(in);
            }
        });
        dialog5.cancel();
    }


    private void getStringValue(String url) {

        mFirebasestore = FirebaseDatabase.getInstance().getReference(url);
        mFirebasestore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    Bean bean = postSnapshot.getValue(Bean.class);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("title", bean.getTitle());
                    map.put("content", bean.getContent());
                    map.put("imgAddress", bean.getImgaddress());
                    map.put("dateover", "1");
                    map.put("detailsImages", bean.getDetailsimg());
                    list.add(map);

                }
                adapter = new ListAdapter(list, Coupon_ListActivity.this);
                listView.setAdapter(adapter);

            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());

            }
        });
    }

    public void gomap(View v) {
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        mCurrentLocation = aMapLocation;
                    }else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.startLocation();
        Intent intent = new Intent(this, RouteNaviActivity.class);
        intent.putExtra("gps", false);
        intent.putExtra("start", new NaviLatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        intent.putExtra("end", new NaviLatLng(latitude, longitude));
        startActivity(intent);
    }

    public void backbtn(View v) {
        finish();
    }


    /**
     * 检测该包名所对应的应用是否存在
     * @param packageName
     * @return
     */
    public boolean checkPackage(String packageName)
    {
        if (packageName == null || "".equals(packageName))
            return false;
        try
        {
            getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }
}
