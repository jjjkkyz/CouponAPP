package com.edu.pu.cs.couponapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.edu.pu.cs.couponapp.Adapter.HomeGridAdapter;
import com.edu.pu.cs.couponapp.Bean.Bean;
import com.edu.pu.cs.couponapp.R;
import com.edu.pu.cs.couponapp.ui.HomePicAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hejunlin.superindicatorlibray.CircleIndicator;
import com.hejunlin.superindicatorlibray.LoopViewPager;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AutoLayoutActivity implements OnClickListener {
    private ImageView img1, img2, img3;
    private GridView gridview2,gridview3;
    private HomeGridAdapter adapter;
    private String[] b2 = new String[10];
    private String[] b3 = new String[10];

    private List<String> textlogoGird = new ArrayList<String>();
    // 图片地址集合
    private List<String> imgAddressGrid = new ArrayList<String>();
    //logo缩写
    private List<String> abbreviationGrid = new ArrayList<String>();

    private ArrayList<Map<String, String>> grid2 = new ArrayList<Map<String, String>>();
    private ArrayList<Map<String, String>> grid3 = new ArrayList<Map<String, String>>();

    //商家数据url集合，传参到Coupon_ListActivity
    private List<String> urlList2 = new ArrayList<String>();
    private List<String> urlList3 = new ArrayList<String>();
    //商家数据条数counturl集合，传参到Coupon_ListActivity
    private List<String> counturlList2 = new ArrayList<String>();
    private List<String> counturlList3 = new ArrayList<String>();
    //商家位置map集合，传参到Coupon_ListActivity
    private List<String> mapList2 = new ArrayList<String>();
    private List<String> mapList3 = new ArrayList<String>();
    //固定的数据库url
    String databaseurl = "https://coupon-ed7bf.firebaseio.com/coupon_";
    String databaseurl_wd = "coupon_";
    //map的固定形式
    String mapurl = "geo:0,0?q=";

    //Firebase实时数据库引用
    DatabaseReference myRef;
    DatabaseReference bannerRef;
    DatabaseReference myhomeadRef;
    DatabaseReference myStore2;
    DatabaseReference myStore3;
    //广告位信息
    String adimgaddress = null;
    String adcontent = null;
    String adtitle = null;
    String adwebsite ;
    // 标题集合

    // 文本描述集合
    List<Map<String,String>> list = new ArrayList();
    LoopViewPager viewpager;
    CircleIndicator indicator;

    ImageView Hometitle,Adimageview;
    TextView Adtitle,Adcontent;
    RelativeLayout Adview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRef = FirebaseDatabase.getInstance().getReference();
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setBackEnable(false);
        setContentView(R.layout.home);
//        StatusBarCompat.setStatusBarColor(HomeActivity.this,getResources().getColor(R.color.background_color_orange));
        gridview2 = (GridView) findViewById(R.id.gridview2);
        gridview3 = (GridView) findViewById(R.id.gridview3);
        gridview2.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridview3.setSelector(new ColorDrawable(Color.TRANSPARENT));

        initView();
        String resource = "http://couponapp.image.alimmdn.com/titlelogo/hometitle.png?t=1487999390623";
        Glide.with(HomeActivity.this).load(resource).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().thumbnail(0.1f).error(com.example.gridviewimage.R.mipmap.image_error).into(Hometitle);


        //banner 轮播图片加载及点击事件
        viewpager = (LoopViewPager) findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        bannerRef = myRef.child("banner");
        bannerRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Bean banner = dataSnapshot.getValue(Bean.class);
                System.out.println("hotlogo=" + banner.getImgaddress());
                Map<String,String> map = new HashMap<String, String>();
                map.put("id",dataSnapshot.getKey());
                map.put("imageAddress",banner.getImgaddress());
                map.put("title",banner.getTitle());
                map.put("content",banner.getContent());
                map.put("hotlogo",banner.getHotlogo());
                map.put("map",mapurl+banner.getTextlogo());
                map.put("detailsimgOrnot",banner.getDetailsimg());
                list.add(map);
                viewpager.setAdapter(new HomePicAdapter(HomeActivity.this, list));
                indicator.setViewPager(viewpager);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Bean banner = dataSnapshot.getValue(Bean.class);
                for (Map<String,String>  map : list){
                    if (map.get("id") == dataSnapshot.getKey()){
                        list.remove(map);
                        Map<String,String> newMap = new HashMap<String, String>();
                        newMap.put("id",dataSnapshot.getKey());
                        newMap.put("imageAddress",banner.getImgaddress());
                        newMap.put("title",banner.getTitle());
                        newMap.put("content",banner.getContent());
                        newMap.put("hotlogo",banner.getHotlogo());
                        newMap.put("map",mapurl+banner.getTextlogo());
                        newMap.put("detailsimgOrnot",banner.getDetailsimg());
                        list.add(newMap);
                        viewpager.setAdapter(new HomePicAdapter(HomeActivity.this, list));
                        viewpager.setLooperPic(true);//是否设置自动轮播
                        indicator.setViewPager(viewpager);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //加载第一排商户logo
        String imglogo1 = "http://couponapp.image.alimmdn.com/Logo/KFC.png?t=1487955075694";
        String imglogo2 = "http://couponapp.image.alimmdn.com/Logo/McDonald.png?t=1487955075602";
        String imglogo3 = "http://couponapp.image.alimmdn.com/Logo/BurgerKing.png?t=1487955075648";
        Glide.with(HomeActivity.this).load(imglogo1).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().thumbnail(0.1f).error(com.example.gridviewimage.R.mipmap.image_error).placeholder(com.example.gridviewimage.R.mipmap.loading).into(img1);
        Glide.with(HomeActivity.this).load(imglogo2).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().thumbnail(0.1f).error(com.example.gridviewimage.R.mipmap.image_error).placeholder(com.example.gridviewimage.R.mipmap.loading).into(img2);
        Glide.with(HomeActivity.this).load(imglogo3).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().thumbnail(0.1f).error(com.example.gridviewimage.R.mipmap.image_error).placeholder(com.example.gridviewimage.R.mipmap.loading).into(img3);

        //加载广告
        myhomeadRef = myRef.child("homead");
        myhomeadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bean homead = dataSnapshot.getValue(Bean.class);
                adimgaddress = homead.getImgaddress();
                adcontent = homead.getContent();
                adtitle = homead.getTitle();
                adwebsite = homead.getWebsite();
                if (adimgaddress.length() == 0 || adcontent.length() == 0 || adtitle.length() == 0 || adwebsite.length() == 0){
                    Adview.setVisibility(View.GONE);
                }else {
                    Adcontent.setText(adcontent);
                    Adtitle.setText(adtitle);
                    Glide.with(HomeActivity.this).load(adimgaddress).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().thumbnail(0.1f).error(com.example.gridviewimage.R.mipmap.image_error).placeholder(com.example.gridviewimage.R.mipmap.loading).into(Adimageview);
                    Adview.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });

        //加载商家Logo和名称，以及点击事件
        getStringValue2();



        //db.delete("like",null,null);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kfc_click();
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcd_click();
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bk_click();
            }
        });

        gridview2.setFocusable(false);
        gridview3.setFocusable(false);
    }



    //获取数据的方法
    ////第二排商户的GridView（便利商店）
    private void getStringValue2() {
        myStore2 = myRef.child("business_2");
        myStore2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                grid2.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                //Getting the data from snapshot
                Bean bean = postSnapshot.getValue(Bean.class);

                imgAddressGrid.add(bean.getImglogo());
                textlogoGird.add(bean.getTextlogo());
                abbreviationGrid.add(bean.getAbbreviation());
                Map<String, String> map = new HashMap<String, String>();
                map.put("textlogo", bean.getTextlogo());
                map.put("imgAddress", bean.getImglogo());

                grid2.add(map);
                //给三个传参集合添加数据
                urlList2.add("coupon/" + bean.getAbbreviation());
                mapList2.add(bean.getTextlogo());
                }
                gridview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                判断集合里边是否有数据
                         if (urlList2.size() == 0 || mapList2.size() == 0) {
                             Toast.makeText(HomeActivity.this, "获取数据失败...", Toast.LENGTH_SHORT).show();
                         } else {
//                    其实这两个判断可以和在一起，但是我现在我没法测试，所以我就没和，这样比较保险，后期有时间你和起来就行
//                    判断集合中获取出来的某个数据是否为空字符串
                             if (!urlList2.get(i).equals("") || !counturlList2.get(i).equals("") || !mapList2.get(i).equals("")) {
                                 Intent in = new Intent(HomeActivity.this, Coupon_ListActivity.class);
                                 in.putExtra("url", urlList2.get(i));
                                 in.putExtra("map", mapList2.get(i));

                                 startActivity(in);
                             } else {
                                 Toast.makeText(HomeActivity.this, "获取数据失败...", Toast.LENGTH_SHORT).show();
                             }
                         }
                     }
                 }
                );
                adapter = new HomeGridAdapter(grid2, HomeActivity.this);
                gridview2.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }

        }
        );
    }



    public void adclick(View v) {
        Intent it = new Intent(this, AdActivity.class);
        it.putExtra("adwebsite",adwebsite);
        startActivity(it);
    }

    public void kfc_click() {
        Intent it = new Intent(this, Coupon_GridActivity.class);
        it.putExtra("url", "coupon/kfc");
        it.putExtra("map", "肯德基");
        startActivity(it);

    }

    public void mcd_click() {
        Intent it = new Intent(this, Coupon_ListActivity.class);
        it.putExtra("url", "coupon/mc");
        it.putExtra("map", "麦当劳");
        startActivity(it);


    }

    public void bk_click() {
        Intent it = new Intent(this, Coupon_GridActivity.class);
        it.putExtra("url", "coupon/bk");
        it.putExtra("map", "汉堡王");
        startActivity(it);


    }


    private void initView() {
        img1 = (ImageView) findViewById(R.id.imageLogo_1);
        img2 = (ImageView) findViewById(R.id.imageLogo_2);
        img3 = (ImageView) findViewById(R.id.imageLogo_3);
        Hometitle = (ImageView) findViewById(R.id.hometitle);
        Adimageview = (ImageView) findViewById(R.id.adimageview);
        Adtitle = (TextView) findViewById(R.id.adtitle);
        Adcontent = (TextView) findViewById(R.id.adcontent);
        Adview = (RelativeLayout) findViewById(R.id.adview);

    }

    @Override
    public void onClick(View v) {

    }
}
