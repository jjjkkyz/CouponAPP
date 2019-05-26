package com.edu.pu.cs.couponapp.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.edu.pu.cs.couponapp.Adapter.HomeGridAdapter;
import com.edu.pu.cs.couponapp.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.iwf.photopicker.PhotoPreview;


public class SearchAcitivity extends AutoLayoutActivity {
	SearchView sv = null;
	ListView lv = null;
	SQLiteDatabase db;
	String db_name = "coupon";
	String tb_name = "CouponList";
	ArrayList<Map<String, String>> grid = new ArrayList<Map<String, String>>();
	List<String> urlList = new ArrayList<String>();
	private String[] a = new String[10];
	GridView image_gridView = null;
	int countdata;
	DatabaseReference storeRef;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search);
//		StatusBarCompat.setStatusBarColor(SearchAcitivity.this,getResources().getColor(R.color.background_color_orange));
//		final ProgressDialog dialog = ProgressDialog.show(this, "稍候片刻", "折價券即將呈現", true, true);
		image_gridView=(GridView)findViewById(R.id.grid);
        image_gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		storeRef = FirebaseDatabase.getInstance().getReference("/coupon");

		sv = (SearchView) this.findViewById(R.id.sv);

//        sv.setIconifiedByDefault(false);
		sv.clearFocus();
		int currentapiVersion=android.os.Build.VERSION.SDK_INT;



		int search_mag_icon_id = sv.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
		ImageView search_mag_icon = (ImageView)sv.findViewById(search_mag_icon_id);//获取搜索图标
		search_mag_icon.setImageResource(R.drawable.searchview);



		sv.setSubmitButtonEnabled(true);

		if(sv == null) {
			return;
		}
		int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		TextView textView = (TextView) sv.findViewById(id);

		if (currentapiVersion>=21)
		{
			sv.setQueryHint(Html.fromHtml("<font color = #000000>" + getResources().getString(R.string.search_input) + "</font>"));
			textView.setTextColor(getResources().getColor(R.color.background_color_orange));
		}else if (currentapiVersion<21){
			sv.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search_input) + "</font>"));
			textView.setTextColor(Color.WHITE);
		}


		sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String str) {
				grid.clear();
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String str) {

				System.out.println(str+"--------------str");
				sv.clearFocus();
				storeRef.orderByKey().startAt(str).endAt(str+"\uf8ff").limitToFirst(6)
						.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						grid.clear();
						urlList.clear();
						for (DataSnapshot storeSnapshot : dataSnapshot.getChildren()) {
							Map<String, String> bean = (Map) storeSnapshot.getValue();
							Map<String, String> map = new HashMap<String, String>();
							map.put("textlogo", bean.get("title"));
							map.put("imgAddress", bean.get("titlelogo"));
							urlList.add(storeRef.getKey()+'/' +storeSnapshot.getKey());
							grid.add(map);
							image_gridView.setAdapter(new HomeGridAdapter(grid, SearchAcitivity.this));
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});

				//        单点事件
				image_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
						Intent in = new Intent();
						in.setClass(SearchAcitivity.this, Coupon_ListActivity.class);
						in.putExtra("url", urlList.get(i));//必传项,i为当前点击的位置
						startActivity(in);
//						PhotoPreview.builder()
//								.setPhotos(grid)
//								.setCurrentItem(i)
//								.setShowDeleteButton(false)
//								.start(SearchAcitivity.this);

					}
				});
				/**
				 *  MainActivity.this:为当前界面上下文
				 *  grid:photos为要显示的图片地址集合
				 * */

				return false;
			}

		});
	}

}