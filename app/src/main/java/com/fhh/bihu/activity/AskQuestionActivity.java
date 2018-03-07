package com.fhh.bihu.activity;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.fhh.bihu.R;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.ImageUtil;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.MyTextUtils;
import com.fhh.bihu.util.ToastUtil;

import java.io.FileNotFoundException;

public class AskQuestionActivity extends BaseActivity {

    private TextInputEditText mTitle;
    private EditText mContent;
    private TextInputLayout mTittleLayout;

    private Button mCloseKeyborad;
    private Button mTakePhoto;
    private ImageView mCancelImage;
    private Button mOpenAlbum;
    private ImageView mAnswerImage;
    private FrameLayout mAllImage;

    private Bitmap imageBitmap;
    private String imagePath;

    private boolean hasImage = false;
    private boolean mIsEditStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
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
        //初始化输入框
        mTittleLayout = findViewById(R.id.text_input_layout);
        mTitle = findViewById(R.id.text_question_title);
        mContent = findViewById(R.id.text_question_content);

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsEditStatus = !(MyTextUtils.isEmpty(mTitle.getText().toString())
                        || MyTextUtils.isEmpty(mContent.getText().toString()));
                invalidateOptionsMenu();
                if (MyTextUtils.isEmpty(mTitle.getText().toString())) {
                    mTittleLayout.setErrorEnabled(true);
                    mTittleLayout.setError("标题不能为空");
                } else {
                    mTittleLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsEditStatus = !(MyTextUtils.isEmpty(mTitle.getText().toString())
                        || MyTextUtils.isEmpty(mContent.getText().toString()));
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mCloseKeyborad = findViewById(R.id.bt_down);
        mTakePhoto = findViewById(R.id.bt_take_photo);
        mOpenAlbum = findViewById(R.id.bt_open_album);
        mCancelImage = findViewById(R.id.cancel_image);
        mAllImage = findViewById(R.id.question_image_all);
        mAnswerImage = findViewById(R.id.answer_image);
        //todo  关闭键盘
        //mCloseKeyborad.setOnClickListener();

        mCancelImage.setOnClickListener(v -> {
            hasImage = false;
            mAllImage.setVisibility(View.GONE);
        });
        mTakePhoto.setOnClickListener(v -> checkCameraPermission());
        mOpenAlbum.setOnClickListener(v -> checkAlbumPermission());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mIsEditStatus) {
            menu.findItem(R.id.send).setVisible(false);
            menu.findItem(R.id.send_pre).setVisible(true);
        } else {
            menu.findItem(R.id.send).setVisible(true);
            menu.findItem(R.id.send_pre).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.send:
                if (hasImage) uploadImageAndSend();
                else send();
                break;
            case R.id.send_pre:
                if (hasImage) uploadImageAndSend();
                else send();
                break;
        }
        return true;
    }

    private void uploadImageAndSend() {
        ToastUtil.makeToast("正在上传图片...");
        HttpUtil.uploadToQiNiu(imagePath, imageName, (key, info, response) -> {
            if (info.isOK()) {
                send();
            } else {
                ToastUtil.makeToast("上传失败,请稍后再试");
            }
        });
    }

    private void send() {
        //标题和内容都不为空时 发送
        if (!MyTextUtils.isEmpty(mTitle.getText().toString()) &&
                !MyTextUtils.isEmpty(mContent.getText().toString())) {
            mTittleLayout.setErrorEnabled(false);
            //TODO 实现上传图片
            //TODO 实现上传图片
            String param;
            if (hasImage) {
                param = "title=" + mTitle.getText().toString() +
                        "&content=" + mContent.getText().toString() + "&images="
                        + ApiParam.MY_QINIU_URL + imageName + "&token=" + MyApplication.getToken();
            } else param = "title=" + mTitle.getText().toString()
                    + "&content=" + mContent.getText().toString()
                    + "&token=" + MyApplication.getToken();
            HttpUtil.sendHttpRequest(ApiParam.ASK_A_QUESTION, param, new HttpUtil.HttpCallBack() {
                @Override
                public void onResponse(HttpUtil.Response response) {
                    if (response.getInfo().equals("success")) {
                        ToastUtil.makeToast("发送成功");
                        finish();
                    } else {
                        ToastUtil.makeToast(response.getInfo());
                    }
                }

                @Override
                public void onFail(String reason) {
                    ToastUtil.makeToast("由于网络原因,发送失败");
                }
            });
        } else {
            if (MyTextUtils.isEmpty(mTitle.getText().toString())) {
                mTittleLayout.setErrorEnabled(true);
                mTittleLayout.setError("标题不能为空!");
            }


            if (MyTextUtils.isEmpty(mContent.getText().toString()))
                ToastUtil.makeToast("内容不能为空");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        hasImage = true;
                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        imagePath = getExternalCacheDir().getPath() + "/" + imageName;
                        Log.d("TAKEPHOTO", "path=m" + imagePath);
                        int degree = getExifOrientation(imagePath);
                        Log.d("DEGREE", String.valueOf(degree));
                        // preview icon according to exif orientation
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        mAnswerImage.setImageBitmap(Bitmap.createBitmap(imageBitmap, 0, 0,
                                imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true));
                        mAllImage.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    hasImage = false;
                    mAllImage.setVisibility(View.GONE);
                    ToastUtil.makeToast("未能成功获得图片,请稍后再试!");
                }
                break;
            case OPEN_ALBUM:
                if (resultCode == RESULT_OK) {
                    imageName = MyApplication.getUser().getUsername() + "questionImage"
                            + MyApplication.getToken().substring(0, 6) + ".jpg";
                    imagePath = ImageUtil.parseImageUri(data);
                    Log.d("imagePath", "imagePath: " + imagePath);

                    if (imagePath != null) {
                        hasImage = true;
                        imageBitmap = BitmapFactory.decodeFile(imagePath);
                        mAnswerImage.setImageBitmap(imageBitmap);
                        mAllImage.setVisibility(View.VISIBLE);
                    } else {
                        hasImage = false;
                        mAllImage.setVisibility(View.GONE);
                        ToastUtil.makeToast("未能成功获得图片,请稍后再试!");
                    }
                }
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AskQuestionActivity.class);
        context.startActivity(intent);
    }


    private String handleImageBeforeKitHat(Intent data) {
        Uri uri = data.getData();
        return getImagePath(uri, null);
    }


    @TargetApi(19)
    private String handleImageOnKitHat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是Document类型的uri,通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads")
                        , Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri 用普通方式的处理
            imagePath = uri.getPath();
        }

        return imagePath;
    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().
                query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
