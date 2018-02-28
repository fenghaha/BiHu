package com.fhh.bihu.util;

import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by FengHaHa on 2018/2/25 0025.
 * HTTP通信类,封装网络请求
 */

public class HttpUtil {
    private static final String TAG = "HttpUtil";

    public static void sendHttpRequest(String url, String param, HttpCallBack callBack) {

        Handler handler = new Handler();
        //把开启新线程的操作放在网络请求里
        new Thread(() -> {
            HttpURLConnection connection = null;
            //Log.d(TAG, "一次网络请求");
            try {
                URL mUrl = new URL(url);
                connection = (HttpURLConnection) mUrl.openConnection();

                connection.setRequestMethod("POST");
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(8000);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(param.getBytes());
                os.flush();
                os.close();
                Log.d("ResponseCode", "ResponseCode = " + connection.getResponseCode());
                String data = getStringFromIS(connection.getInputStream());
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //Log.d("data123456", data);
                    JSONObject json = new JSONObject(data);
                    String info = json.getString("info");
                    Log.d("6666", info);
                    //返回值为200
                    if ("success".equals(info)||"excited".equals(info)||"naive".equals(info)) {
                        Log.d("getInfo", "info =" + info);
                        handler.post(() -> callBack.onSuccess(data));
                    } else {
                        handler.post(() -> {
                            
                            Log.d(TAG, "sendHttpRequest:网络错误__返回值 = "+info);
                            callBack.onFail("参数");
                        });
                    }
                } else {
                    handler.post(() -> {
                        ToastUtil.makeToast("网络错误,请检查网络连接");
                        callBack.onFail("网络");
                    });
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();


    }

    private static String getStringFromIS(InputStream is) throws IOException {
        byte buff[] = new byte[1024];
        int len;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while ((len = is.read(buff)) != -1) {
            os.write(buff, 0, len);
        }
        String str = os.toString();
        is.close();
        os.close();

        return str;
    }

    public interface HttpCallBack {
        public void onSuccess(String data);

        public void onFail(String reason);;
    }

}
