package com.fhh.bihu.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.fhh.bihu.R;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.ImageUtil;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.MyTextUtils;
import com.fhh.bihu.util.ToastUtil;
import com.fhh.bihu.view.MyDialog;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ChangeAvatarActivity extends BaseActivity {
    private ImageView picture;
    private Button mChooseButton;


    private MyDialog dialog;

    private Bitmap imageBitmap;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);
        setUpViews();
    }

    private void setUpViews() {
        //初始化ToolBar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }
        mChooseButton = findViewById(R.id.bt_choose_image);
        mChooseButton.setOnClickListener(v -> chooseImage());

        picture = findViewById(R.id.avatar_for_change);
        HttpUtil.loadImage(MyApplication.getUser().getAvatarUrl(), new HttpUtil.ImageCallback() {
            @Override
            public void onResponse(Bitmap bitmap, String info) {
                if ("success".equals(info))
                    picture.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.yes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.sure:
                changeAvatar();
                break;
        }
        return true;
    }

    private void chooseImage() {
        dialog = new MyDialog(ChangeAvatarActivity.this);
        //去掉标题
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.setChangeAvatarView();
        dialog.getOpenAlbumButton().setOnClickListener(v -> checkAlbumPermission());
        dialog.getTakePhotoButton().setOnClickListener(v -> checkCameraPermission());
    }

    private void changeAvatar() {
        // 上传头像
        if (imagePath != null) {
            HttpUtil.uploadToQiNiu(imagePath, imageName, (key, info, response) -> {
                if (info.isOK()) {
                    String param = "token=" + MyApplication.getToken() + "&avatar="
                            + ApiParam.MY_QINIU_URL + imageName;
                    //成功上传到7牛后  用逼乎api修改头像
                    HttpUtil.sendHttpRequest(ApiParam.MODIFY_AVATAR, param, new HttpUtil.HttpCallBack() {
                        @Override
                        public void onResponse(HttpUtil.Response response) {
                            if (response.getInfo().equals("success")) {
                                MyApplication.getUser().setAvatarUrl(ApiParam.MY_QINIU_URL + imageName);
                                ToastUtil.makeToast("修改成功!");
                                finish();
                                //todo改掉头像显示
                            } else {
                                ToastUtil.makeToast(response.getInfo());
                            }
                        }

                        @Override
                        public void onFail(String reason) {
                            ToastUtil.makeToast("服务器连接错误");
                        }
                    });
                } else {
                    ToastUtil.makeToast("修改失败,请检查网络稍后再试");
                }
            });
        } else {
            ToastUtil.makeToast("你还未选择头像!");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        dialog.dismiss();
                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        imagePath = getExternalCacheDir().getPath()+"/"+imageName;
                        Log.d("TAKEPHOTO", "path=m"+imagePath);
                        int degree = getExifOrientation(imagePath);
                        Log.d("DEGREE", String.valueOf(degree));
                        //Roate preview icon according to exif orientation
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        picture.setImageBitmap(Bitmap.createBitmap(imageBitmap, 0, 0,
                                imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case OPEN_ALBUM:
                if (resultCode == RESULT_OK) {
                    imageName = MyApplication.getUser().getUsername() + "avatar" + MyApplication.getToken().substring(0, 6)
                            + ".jpg";
                    imagePath = ImageUtil.parseImageUri(data);
                    if (imagePath != null) {
                        dialog.dismiss();
                        imageBitmap = BitmapFactory.decodeFile(imagePath);
                        picture.setImageBitmap(imageBitmap);
                    } else {
                        ToastUtil.makeToast("未能成功获得图片,请稍后再试!");
                    }
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog!=null)
            dialog.dismiss();
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ChangeAvatarActivity.class);
        context.startActivity(intent);
    }
}
