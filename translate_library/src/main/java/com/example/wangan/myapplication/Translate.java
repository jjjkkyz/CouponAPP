package com.example.wangan.myapplication;

import android.net.http.AndroidHttpClient;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Translate {
    public String str2="";
    //json组件
    JSONObject jo;
    JSONArray ja;

    public String translate(final String word){
        new Thread() {
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    //设置连接超时和读取超时时间
                    httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 9000);
                    httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 9000);
                    //这里我创建了json对象jo,并创建了json数组
                    try {

                        jo = new JSONObject();
                        jo.put("sxy", word);
                    }catch (Exception e){

                    }
                    HttpPost httpRequst = new HttpPost("https://www.onlinetkk.com/TRANSLATE/sxy.php");
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("sxy",jo.toString()));
                    try {
                        httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                        HttpResponse response = httpClient.execute(httpRequst);
                        if (response.getStatusLine().getStatusCode() == 200) {
                            String str = EntityUtils.toString(response.getEntity());
                            str=str.substring(str.indexOf(",")+1,str.length());
                            str=str.substring(2,str.indexOf(",")-2);
                            Log.e("Error",str);
                            str2=str;
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                }
            }
        }.start();
        try {
            Thread.sleep(5000);
        }catch (Exception e){

        }

        return str2;
    }
}
