package com.edu.pu.cs.couponapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.edu.pu.cs.couponapp.Activity.Coupon_GridActivity;
import com.edu.pu.cs.couponapp.Activity.Coupon_ListActivity;
import com.edu.pu.cs.couponapp.R;

import java.util.List;
import java.util.Map;

/**
 * Created by hejunlin on 2016/8/25.
 */
public class HomePicAdapter extends PagerAdapter {


    List<Map<String, String>> data;
    private Context context;
    private String listorgrid = "list";

    public HomePicAdapter(Context context, List<Map<String, String>> data) {
        this.data = data;
        this.context = context;

    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.vp_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.child_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("当前点击了第" + position+1 + "张图片");
                Intent in = new Intent();
                String title = data.get(position).get("title");
                if(title.equals("kfc")){
                    in.putExtra("url", "coupon/kfc");
                    in.putExtra("map", "肯德基");
                    in.setClass(context, Coupon_GridActivity.class);

                }
                else if(title.equals("bk")){
                    in.putExtra("url", "coupon/bk");
                    in.putExtra("map", "汉堡王");
                    in.setClass(context, Coupon_GridActivity.class);
                }
                in.putExtra("title", data.get(position).get("title"));
                in.putExtra("imgAddress", data.get(position).get("imageAddress"));
                in.putExtra("content", data.get(position).get("content"));
                in.putExtra("directory",data.get(position).get("directory"));
                in.putExtra("TitleLogo",data.get(position).get("hotlogo"));
                in.putExtra("detailsimgOrnot",data.get(position).get("detailsimgOrnot"));
                context.startActivity(in);

            }
        });
        Glide.with(context).load(data.get(position).get("imageAddress")).into(imageView);
        container.addView(view);
        return view;
    }


}
