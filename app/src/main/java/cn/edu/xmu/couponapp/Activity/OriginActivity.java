package cn.edu.xmu.couponapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import cn.edu.xmu.couponapp.R;
import cn.edu.xmu.couponapp.ui.TabShow;
import com.zhy.autolayout.AutoLayoutActivity;
//import com.nian.preferential.ui.TabShow;

import qiu.niorgai.StatusBarCompat;

public class OriginActivity extends AutoLayoutActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.origin);
        StatusBarCompat.translucentStatusBar(OriginActivity.this);
        Handler x = new Handler();
        x.postDelayed(new lunchhandler(), 2000);

    }

    class lunchhandler implements Runnable{

        public void run() {
            startActivity(new Intent(getApplication(),EmailPasswordActivity.class));
            OriginActivity.this.finish();
        }

    }
}