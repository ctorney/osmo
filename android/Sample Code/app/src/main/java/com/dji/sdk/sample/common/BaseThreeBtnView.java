package com.dji.sdk.sample.common;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dji.sdk.sample.R;

/**
 * Created by dji on 15/12/20.
 */
public abstract class BaseThreeBtnView extends RelativeLayout implements View.OnClickListener {
    protected TextView mTexInfo;

    protected Button middleBtn;
    protected Button leftBtn;
    protected Button rightBtn;

    protected Button mUpBtn;
    protected Button mDownBtn;
    protected Button mLeftBtn;
    protected Button mRightBtn;

    public BaseThreeBtnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        View content = layoutInflater.inflate(R.layout.view_three_btn, null, false);
        addView(content, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mTexInfo = (TextView) findViewById(R.id.text_info);

        middleBtn = (Button) findViewById(R.id.btn_middle);
        leftBtn = (Button) findViewById(R.id.btn_left);
        rightBtn = (Button) findViewById(R.id.btn_right);

        mUpBtn = (Button) findViewById(R.id.btn_mup);
        mDownBtn = (Button) findViewById(R.id.btn_mdown);
        mLeftBtn = (Button) findViewById(R.id.btn_mleft);
        mRightBtn = (Button) findViewById(R.id.btn_mright);

        mTexInfo.setText(getString(getInfoResourceId()));

        middleBtn.setText(getString(getMiddleBtnTextResourceId()));
        leftBtn.setText(getString(getLeftBtnTextResourceId()));
        rightBtn.setText(getString(getRightBtnTextResourceId()));

        middleBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        mUpBtn.setOnClickListener(this);
        mDownBtn.setOnClickListener(this);
        mLeftBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);

    }

    private String getString(int id) {
        return getResources().getString(id);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_middle :
                getMiddleBtnMethod();
                break;

            case R.id.btn_left :
                getLeftBtnMethod();
                break;

            case R.id.btn_right:
                getRightBtnMethod();
                break;

            case R.id.btn_mup:
                getMUpBtnMethod();
                break;

            case R.id.btn_mdown:
                getMDownBtnMethod();
                break;

            case R.id.btn_mleft:
                getMLeftBtnMethod();
                break;

            case R.id.btn_mright:
                getMRightBtnMethod();
                break;
        }
    }

    protected abstract int getMiddleBtnTextResourceId();
    protected abstract int getLeftBtnTextResourceId();
    protected abstract int getRightBtnTextResourceId();

    protected abstract int getInfoResourceId();

    protected abstract void getMiddleBtnMethod();
    protected abstract void getLeftBtnMethod();
    protected abstract void getRightBtnMethod();
    protected abstract void getMUpBtnMethod();
    protected abstract void getMDownBtnMethod();
    protected abstract void getMRightBtnMethod();
    protected abstract void getMLeftBtnMethod();

}
