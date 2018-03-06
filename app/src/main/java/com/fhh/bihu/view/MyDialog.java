package com.fhh.bihu.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

import com.fhh.bihu.R;

/**
 * Created by FengHaHa on 2018/3/1 0001.
 * 自定义Dialog 用于修改密码
 */

public class MyDialog extends Dialog {
    private CardView passwordCard;
    private Button yes;
    private Button cancel;
    private TextInputLayout firstLayout;
    private TextInputLayout secondLayout;
    private TextInputEditText first;
    private TextInputEditText second;

    private CardView avatarCard;
    private Button takePhoto;
    private Button openAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initViews();
    }

    private void initViews() {
        passwordCard = findViewById(R.id.card_change_password);
        yes = findViewById(R.id.bt_yes);
        cancel = findViewById(R.id.bt_cancel);
        firstLayout = findViewById(R.id.first_text_layout);
        secondLayout = findViewById(R.id.second_text_layout);
        first = findViewById(R.id.first_edit);
        second = findViewById(R.id.second_edit);

        avatarCard = findViewById(R.id.card_change_avatar);
        takePhoto = findViewById(R.id.bt_take_photo);
        openAlbum = findViewById(R.id.bt_from_album);
    }

    public void setChangeAvatarView() {
        avatarCard.setVisibility(View.VISIBLE);

    }

    public void setChangePasswordView() {
        passwordCard.setVisibility(View.VISIBLE);
    }

    public Button getTakePhotoButton() {
        return takePhoto;
    }

    public Button getOpenAlbumButton() {
        return openAlbum;
    }

    public Button getYesButton() {
        return yes;
    }

    public Button getCancelButton() {
        return cancel;
    }

    public TextInputLayout getFirstLayout() {
        return firstLayout;
    }

    public TextInputLayout getSecondLayout() {
        return secondLayout;
    }

    public TextInputEditText getFirstEditText() {
        return first;
    }

    public TextInputEditText getSecondEditText() {
        return second;
    }


    public MyDialog(@NonNull Context context) {
        super(context);
    }
}
