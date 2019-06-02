package cn.edu.xmu.couponapp.Activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.navi.model.NaviLatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import cn.edu.xmu.couponapp.Bean.Bean;
import cn.edu.xmu.couponapp.Bean.Shop;
import cn.edu.xmu.couponapp.R;
import cn.edu.xmu.couponapp.ToastUtils;
import com.example.gridviewimage.view.adapter.GridViewImageAdapter;
import com.example.gridviewimage.view.controls.ImageGridView;

import com.github.anzewei.parallaxbacklayout.ParallaxActivityBase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import qiu.niorgai.StatusBarCompat;

/**
 * Created by Administrator on 2016/10/27.
 */

public class Coupon_GridActivity extends ParallaxActivityBase {
    // 标题集合
    private List<String> titleList = new ArrayList<String>();
    // 图片地址集合
    private List<String> imgAddressList = new ArrayList<String>();
    // 文本描述集合
    private List<String> ContentList = new ArrayList<String>();

    private List<String> BeginDateList = new ArrayList<>();
    private List<String> EndDateList = new ArrayList<>();

    //Controls the initialization
    ImageGridView image_gridView = null;

    TextView title_big, title_xx;

    private String map = null;
    ImageView titlelogo;
    private String TitleLogo = null;

    public static Coupon_GridActivity instance = null;

    private String listorgrid = "grid";

    private List<String> DetailsimgList = new ArrayList<String>();

    private Context mContext;

    private DatabaseReference mFirebasestore;
    private DatabaseReference mFirebaselogo;

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
        setContentView(R.layout.activity_coupongrid);
        StatusBarCompat.setStatusBarColor(Coupon_GridActivity.this, getResources().getColor(R.color.background_color_blue));
        initLocation();
        instance = this;


        final ProgressDialog dialog5 = ProgressDialog.show(this, "稍候片刻", "折价券即将呈现", true, true);
        dialog5.onStart();
        map = getIntent().getStringExtra("map");

        initView();

         String url = getIntent().getStringExtra("url");
        //获取顶部logo和经纬度
        mFirebaselogo = FirebaseDatabase.getInstance().getReference(url);
        mFirebaselogo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shop shop = dataSnapshot.getValue(Shop.class);
                Glide.with(Coupon_GridActivity.this).load(shop.getTitlelogo()).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().thumbnail(0.1f).error(com.example.gridviewimage.R.mipmap.image_error).into(titlelogo);
                TitleLogo = shop.getTitlelogo();
                latitude = shop.getLatitude();
                longitude = shop.getLongitude();
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });

        getStringValue(url+"/list");

//        获取数据后，在这里点击就会把标题图片和内容传递过去，这里前提是你得保证有数据
        image_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                判断集合里边是否有数据
                if (titleList.size() == 0 || imgAddressList.size() == 0 || ContentList.size() == 0) {
                    Toast.makeText(Coupon_GridActivity.this, "获取数据失败...", Toast.LENGTH_SHORT).show();
                } else {
//                    其实这两个判断可以和在一起，但是我现在我没法测试，所以我就没和，这样比较保险，后期有时间你和起来就行
//                    判断集合中获取出来的某个数据是否为空字符串
                    if (!titleList.get(i).equals("") || !imgAddressList.get(i).equals("") || !ContentList.get(i).equals("")) {
                        Intent in = new Intent(Coupon_GridActivity.this, DetailsActivity.class);
                        in.putExtra("title", titleList.get(i));
                        in.putExtra("imgAddress", imgAddressList.get(i));
                        in.putExtra("content", ContentList.get(i));
                        in.putExtra("TitleLogo", TitleLogo);
                        in.putExtra("map", map);
                        in.putExtra("listorgrid", listorgrid);
                        in.putExtra("detailsimgOrnot", DetailsimgList.get(i));
                        startActivity(in);


                    } else {
                        Toast.makeText(Coupon_GridActivity.this, "获取数据失败...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog5.cancel();

    }

        private void getStringValue(String url) {

        mFirebasestore = FirebaseDatabase.getInstance().getReference(url);
        mFirebasestore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    Bean bean = postSnapshot.getValue(Bean.class);
                    System.out.print("content=" + bean.getContent());
                    System.out.print("||");
                    System.out.print("imgaddress=" + bean.getImgaddress());
                    System.out.print("||");
                    System.out.print("title=" + bean.getTitle());
                    imgAddressList.add(bean.getImgaddress());
                    titleList.add(bean.getTitle());
                    ContentList.add(bean.getContent());
                    DetailsimgList.add(bean.getDetailsimg());
                    title_big.setText(titleList.get(0));
                    image_gridView.setAdapter(new GridViewImageAdapter(Coupon_GridActivity.this, imgAddressList, 320, 320));
                }

            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());

            }
        });
    }

    private void initView() {
        image_gridView = (ImageGridView) findViewById(R.id.image_gridView);
        titlelogo = (ImageView) findViewById(R.id.titlelogo);
        title_big = (TextView) findViewById(R.id.title_big);
        title_xx = (TextView) findViewById(R.id.title_xx);

    }

    public void gomap(View v) {
        Intent intent = new Intent(this, RouteNaviActivity.class);
        intent.putExtra("gps", false);
        intent.putExtra("start", new NaviLatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        intent.putExtra("end", new NaviLatLng(latitude, longitude));
        startActivity(intent);
    }


    /**
     * 初始化定位
     */
    private void initLocation() {
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
