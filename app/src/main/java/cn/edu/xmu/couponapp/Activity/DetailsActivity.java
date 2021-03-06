package cn.edu.xmu.couponapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import cn.edu.xmu.couponapp.Adapter.DetailsGridAdapter;
import cn.edu.xmu.couponapp.ui.GridViewDetails;
import cn.edu.xmu.couponapp.R;
import cn.edu.xmu.couponapp.VibratorHelper;
import cn.edu.xmu.couponapp.ui.TabShow;

import com.example.wangan.myapplication.Translate;
import com.github.anzewei.parallaxbacklayout.ParallaxBackActivityHelper;
import com.github.anzewei.parallaxbacklayout.ParallaxBackLayout;
import com.zhy.autolayout.AutoLayoutActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import me.iwf.photopicker.PhotoPreview;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import mehdi.sakout.fancybuttons.FancyButton;
import qiu.niorgai.StatusBarCompat;

/**
 * 优惠券详情页
 */
public class DetailsActivity extends AutoLayoutActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private String title, imgAddress, content, TitleLogo;
    private FancyButton Btn_like;
    private Button translateButton;


    ImageView imageView;
    ArrayList<String> imgList = new ArrayList<String>();
    TextView textView;
    TextView textView2;
    TextView shareTextView;
    ImageView titlelogo;
    private boolean isLike;
    //others
    private Button Others;
    private Button Sharebtn;

    private String map = null;

    private String listorgrid = null;

    private GridViewDetails gridView;

    private ParallaxBackActivityHelper mHelper;


    //摇一摇
    private SensorManager sensorManager;
    private SensorEventListener shakeListener;

    private boolean isRefresh = false;
    private boolean isEnglish = false;

    private String detailsImages;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_details);
        StatusBarCompat.setStatusBarColor(DetailsActivity.this, getResources().getColor(R.color.background_color_orange));
        mHelper = new ParallaxBackActivityHelper(this);

        //摇一摇
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        shakeListener = new ShakeSensorListener();
        //

        gridView = (GridViewDetails) findViewById(R.id.detailsgrid);

        //others的设置
        Others = (Button) findViewById(R.id.others);
        Others.setOnClickListener(this);


        title = getIntent().getStringExtra("title");
        imgAddress = getIntent().getStringExtra("imgAddress");
        content = getIntent().getStringExtra("content");
        TitleLogo = getIntent().getStringExtra("TitleLogo");
        listorgrid = getIntent().getStringExtra("listorgrid");
        detailsImages = getIntent().getStringExtra("detailsImages");

        System.out.println(TitleLogo + "-----------titlelogo");

        //翻译调用
        translateButton = (Button) findViewById(R.id.button);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEnglish){
                    Translate translate = new Translate();
                    shareTextView.setText(translate.translate(getResources().getString(R.string.shake2share)));
                }
            }
        });
        Btn_like = (FancyButton) findViewById(R.id.btn_like);


        initView();
        //加载顶部Logo
        Glide.with(DetailsActivity.this).load(TitleLogo).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().thumbnail(0.1f).error(com.example.gridviewimage.R.mipmap.image_error).into(titlelogo);


        isLike = false;
        Btn_like.setText("使用优惠券");
        Btn_like.setBackgroundColor(Color.parseColor("#ea5413"));
        Btn_like.setFocusBackgroundColor(Color.parseColor("#ff5b14"));
        Btn_like.setIconResource("\uf08a");



        //把传递过来的title和content加载到预设的textview里面
        textView.setText(title);
        textView2.setText(Html.fromHtml(content));
        //再次读取图片detailsimg
        System.out.println(detailsImages + "------detailsImages");
        if (detailsImages == null) {
            imgList.add(imgAddress);
        } else {
            String[] temp = detailsImages.split("[|]");
            for (int i = 0; i < temp.length; i++) {
                imgList.add(temp[i]);
            }

        }


        gridView.setAdapter(new DetailsGridAdapter(DetailsActivity.this, imgList));

        //显示大图
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PhotoPreview.builder()
                        .setPhotos(imgList)
                        .setCurrentItem(i)
                        .setShowDeleteButton(false)
                        .start(DetailsActivity.this);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                OnekeyShare oks = new OnekeyShare();
                oks.setImageUrl(imgList.get(i));
                oks.show(DetailsActivity.this);
                return true;
            }
        });
        textView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                OnekeyShare oks = new OnekeyShare();
                oks.setText(content);
                oks.show(DetailsActivity.this);
                return false;
            }
        });

        ShareSDK.initSDK(this);

        gridView.setFocusable(false);
    }


    //摇一摇
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(shakeListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // acitivity后台时取消监听
        sensorManager.unregisterListener(shakeListener);


    }

    private class ShakeSensorListener implements SensorEventListener {
        private static final int ACCELERATE_VALUE = 35;

        @Override
        public void onSensorChanged(SensorEvent event) {

//          Log.e("zhengyi.wzy", "type is :" + event.sensor.getType());

            // 判断是否处于刷新状态(例如微信中的查找附近人)
//            这里判断为true,则就返回什么都不做，为false就执行下边的
            if (isRefresh) {
                return;
            }

            float[] values = event.values;

            /**
             * 一般在这三个方向的重力加速度达到20就达到了摇晃手机的状态 x : x轴方向的重力加速度，向右为正 y :
             * y轴方向的重力加速度，向前为正 z : z轴方向的重力加速度，向上为正
             */
            float x = Math.abs(values[0]);
            float y = Math.abs(values[1]);
            float z = Math.abs(values[2]);

            Log.e("zhengyi.wzy", "x is :" + x + " y is :" + y + " z is :" + z);

            if (x >= ACCELERATE_VALUE || y >= ACCELERATE_VALUE
                    || z >= ACCELERATE_VALUE) {
//                Toast.makeText(
//                        DetailsActivity.this,
//                        "accelerate speed :"
//                                + (x >= ACCELERATE_VALUE ? x
//                                : y >= ACCELERATE_VALUE ? y : z),
//                        Toast.LENGTH_SHORT).show();

                Toast.makeText(DetailsActivity.this, "摇一摇成功，快分享给你的朋友吧", Toast.LENGTH_SHORT).show();
//                这里把值改为true，默认不执行
                isRefresh = true;
//                这里启动了震动，但是这里的震动是一个子线程，因为震动是不可以去影响界面UI的进行的，所以肯定是子线程
//                这里的300为你总共的震动时长
                VibratorHelper.Vibrate(DetailsActivity.this, 300);
//                在这里我做了个子线程，
//                在这个子线程没有执行完的时候，你的isRefresh肯定一直为true，这个时候是没法进行摇一摇的
                new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        try {
//                            这里为让子线程休息300毫秒
                            sleep(300);
//                            子线程休息完300毫秒的时候就会执行下边这一行，这样isRefres=false了，你就可以再次摇一摇了
                            isRefresh = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                showShare();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

    }
    //

    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        titlelogo = (ImageView) findViewById(R.id.titlelogo);
        Sharebtn = (Button) findViewById(R.id.share);
        shareTextView = (TextView) findViewById(R.id.textView3);


    }

    public void backbtn(View v) {
        finish();
    }

    //分享菜单的设置
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
//关闭sso授权
        oks.disableSSOWhenAuthorize();
// titleUrl是标题的网络链接，QQ和QQ空间等使用
//        oks.setTitleUrl("http://sharesdk.cn");
// text是分享文本，所有平台都需要这个字段
        oks.setText(title);
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath(path);//确保SDcard下面存在此张图片
        oks.setImageUrl(imgAddress);
        //oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
// 启动分享GUI
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                if ("Line".equals(platform.getName())) {
                    paramsToShare.setText(null);
                }
            }
        });
        oks.show(this);
    }

    //点击按钮后，加载弹出式菜单
    @Override
    public void onClick(View v) {
        //创建弹出式菜单对象（最低版本11）
        PopupMenu popup = new PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.main, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(this);

        try {
            Field field = popup.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popup);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        popup.show(); //这一行代码不要忘记了

    }

    //弹出式菜单的单击事件处理
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.share:
                showShare();
                break;
            case R.id.map:
                Intent it = new Intent();
                Uri u;
                it.setAction(Intent.ACTION_VIEW);
                u = Uri.parse(map);
                it.setData(u);
                startActivity(it);
                break;
            case R.id.home:

                if (listorgrid.equals("list")) {
                    Intent a = new Intent(this, TabShow.class);
                    startActivity(a);
                    Coupon_ListActivity.instance.finish();
                    finish();
                } else if (listorgrid.equals("grid")) {
                    Intent a = new Intent(this, TabShow.class);
                    startActivity(a);
                    Coupon_GridActivity.instance.finish();
                    finish();
                } else if (listorgrid.equals("like")) {
                    Intent a = new Intent(this, TabShow.class);
                    startActivity(a);
                    finish();
                } else if (listorgrid.equals("banner")) {
                    finish();
                }


                break;
            default:
                break;
        }
        return false;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.onActivityDestroy();
    }

    public ParallaxBackLayout getBackLayout() {
        return mHelper.getBackLayout();
    }

    public void setBackEnable(boolean enable) {
        getBackLayout().setEnableGesture(enable);
    }

    public void scrollToFinishActivity() {
        mHelper.scrollToFinishActivity();
    }

    @Override
    public void onBackPressed() {
        scrollToFinishActivity();
    }

    public void addLike(View view) {
        if (!isLike) {
            Btn_like.setText("优惠券已使用");
            Btn_like.setBackgroundColor(Color.parseColor("#00a0f0"));
            Btn_like.setFocusBackgroundColor(Color.parseColor("#0fafff"));
            Btn_like.setIconResource("\uf00d");
            isLike = true;
        } else if (isLike) {
            Btn_like.setText("使用优惠券");
            Btn_like.setBackgroundColor(Color.parseColor("#ea5413"));
            Btn_like.setFocusBackgroundColor(Color.parseColor("#ff5b14"));
            Btn_like.setIconResource("\uf08a");
            isLike = false;
        }

    }
}
