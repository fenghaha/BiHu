package com.fhh.bihu.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;


import com.qiniu.android.common.FixedZone;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by FengHaHa on 2018/2/25 0025.
 * HTTP通信类,封装网络请求
 */

public class HttpUtil {
    private static final String TAG = "HttpUtil";

    private static Handler handler;

    public static void loadImage(String address, ImageCallback callback) {
        String imageName;
        Log.d(TAG, "原address=" + address);
        //截取出文件名
        String[] names = address.split("com/");
        imageName = names[names.length - 1];
        if (imageName.contains("/"))
            imageName = imageName.replaceAll("/", "");


        Log.d(TAG, "文件名=" + imageName);
        File file = new File(MyApplication.getContext().getExternalCacheDir().getPath() + "/" + imageName + ".png");
        if (file.exists()) {
            //文件存在则从文件中读取
            try {
                Log.d(TAG, "文件存在!!    name=" + imageName + "\n" + "path=" + file.getPath() + "\n" + file.getAbsolutePath() +
                        "\n" + file.getCanonicalPath());
                callback.onResponse(BitmapFactory.decodeFile(file.getPath()), "success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "文件不存在  name=" + imageName);
            //文件不存在则从网络中读取
            String finalImageName = imageName;
            new Thread(() -> {
                HttpURLConnection connection;
                try {
                    URL mUrl = new URL(address);
                    connection = (HttpURLConnection) mUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(8000);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);//不缓存
                    connection.connect();

                    Log.d(TAG, "ResponseCod " + connection.getResponseCode());

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                        handler.post(() -> callback.onResponse(bitmap, "success"));
                        //缓存图片
                        File file1 = new File(MyApplication.getContext().getExternalCacheDir() + "/" + finalImageName + ".png");
                        FileOutputStream os = new FileOutputStream(file1);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 70, os);
                    } else {
                        handler.post(() ->callback.onResponse(null,"failed"));
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void uploadToQiNiu(String filePath, String name, UpCompletionHandler upCompletionHandler) {
        Configuration config = new Configuration.Builder().zone(FixedZone.zone0).build();
        UploadManager uploadManager = new UploadManager(config);
        //据说把key放在代码不安全...
        //等我学会写服务器接口了再说吧2333
        String param = "accessKey=gAeLEjccJb17Ygy13jT2HD98X9LOsSOjTgKbMNE8"
                + "&secretKey=vpgfLqXMu7NuGgssvNXB6und8j2BAvdSMfzIr89L"
                + "&bucket=fhh-123";
        sendHttpRequest(ApiParam.GET_TOKEN, param, new HttpCallBack() {
            @Override
            public void onResponse(Response response) {
                if (response.getInfo().equals("success")) {
                    String token = JsonParse.getElement(new String(response.getBytes()), "token");
                    uploadManager.put(filePath, name, token, upCompletionHandler, null);
                } else {
                    ToastUtil.makeToast(response.getInfo());
                }
            }

            @Override
            public void onFail(String reason) {
                ToastUtil.makeToast("获取七牛token失败");
            }
        });

    }

    public static void sendHttpRequest(String url, String param, HttpCallBack callBack) {

        handler = new Handler();
        //把开启新线程的操作封装在网络请求里
        new Thread(() -> {
            HttpURLConnection connection = null;
            //Log.d(TAG, "网络请求");
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
                int responseCode = connection.getResponseCode();
                Log.d("ResponseCode", "ResponseCode = " + responseCode);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Response response = new Response(getByteArrayFromIS(connection.getInputStream()));
                    // Log.d(TAG, "info = " + response.getInfo() + "status = " + response.getStatus());
                    handler.post(() -> callBack.onResponse(response));
                } else {
                    handler.post(() -> {
                        ToastUtil.makeToast("服务器连接错误 responseCode = " + responseCode);
                        callBack.onFail("网络");
                    });
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }

    private static byte[] getByteArrayFromIS(InputStream is) throws IOException {
        byte buff[] = new byte[1024];
        int len;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while ((len = is.read(buff)) != -1) {
            os.write(buff, 0, len);
        }
        byte[] bytes = os.toByteArray();
        is.close();
        os.close();
        return bytes;
    }

    public static class Response {
        private int status;
        private String info;
        private String data;
        private byte[] bytes;

        public Response(byte[] content) {
            bytes = content;
            String contentString = new String(content);

            if (JsonParse.getElement(contentString, "status") != null) {
                status = Integer.parseInt(JsonParse.getElement(contentString, "status"));
            } else {
                status = 200;
            }

            info = JsonParse.getElement(contentString, "info");
            data = JsonParse.getElement(contentString, "data");
        }

        public int getStatus() {
            return status;
        }

        public String getInfo() {
            return info;
        }

        public String getData() {
            return data;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }

    public interface HttpCallBack {
        void onResponse(Response response);

        void onFail(String reason);
    }

    public interface ImageCallback {
        void onResponse(Bitmap bitmap, String info);
    }

}