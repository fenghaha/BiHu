package com.fhh.bihu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.ToastUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by FengHaHa on 2018/3/3 0003.
 * 封装选择图片的一些方法
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    final int TAKE_PHOTO = 3;
    final int OPEN_ALBUM = 4;

    String imageName;
    Uri imageUri;

    void checkAlbumPermission() {
        if (ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, OPEN_ALBUM);
        }else {
            openAlbum();
        }
    }
    void checkCameraPermission(){
        //判断相机权限
        if(Build.VERSION.SDK_INT>=23){
            if(ContextCompat.checkSelfPermission(BaseActivity.this,Manifest.permission.CAMERA)
                    !=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(BaseActivity.this,new String[]{Manifest.permission.CAMERA},TAKE_PHOTO);
            }else{
                takePhoto();
            }
        }else{
            takePhoto();
        }
    }

    void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_ALBUM);
    }

    void takePhoto() {
        imageName = MyApplication.getUser().getUsername() + "avatar" + MyApplication.getToken().substring(0, 6)
                + ".jpg";
        File outputImage = new File(getExternalCacheDir(), imageName);
        if (outputImage.exists())
            outputImage.delete();
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(BaseActivity.this,
                    "com.fhh.bihu.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }


    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            // MmsLog.e(ISMS_TAG, "getExifOrientation():", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    default:
                        break;
                }
            }
        }
        return degree;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    ToastUtil.makeToast("你拒绝了权限请求,该功能无法使用!");
                }
                break;
            case OPEN_ALBUM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    ToastUtil.makeToast("你拒绝了权限请求,该功能无法使用!");
                }
                break;
        }
    }
}
