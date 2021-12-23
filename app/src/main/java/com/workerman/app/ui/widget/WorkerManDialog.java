package com.workerman.app.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.workerman.app.R;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bada on 2017-11-14.
 */


public class WorkerManDialog extends Dialog {

    @BindView(R.id.txtMessage)
    TextView txtMessage;
    @BindView(R.id.btnLeft)
    Button btnLeft;
    @BindView(R.id.btnRight)
    Button btnRight;
    @BindView(R.id.btnCenter)
    Button btnCenter;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mCenterClickListener;
    private View.OnClickListener mRightClickListener;

    private String mMessage;
    private String mTxtLeft= "", mTxtCenter="", mTxtRight="";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_custom);
        ButterKnife.bind(this);

        txtMessage.setText(mMessage);

        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null && mCenterClickListener != null) {
            btnLeft.setOnClickListener(mLeftClickListener);
            btnCenter.setOnClickListener(mCenterClickListener);
            btnRight.setOnClickListener(mRightClickListener);
            btnLeft.setVisibility(View.VISIBLE);
            btnRight.setVisibility(View.VISIBLE);
            btnCenter.setVisibility(View.VISIBLE);
        }else if (mLeftClickListener != null && mRightClickListener != null && mCenterClickListener == null) {
            btnLeft.setOnClickListener(mLeftClickListener);
            btnRight.setOnClickListener(mRightClickListener);
            btnLeft.setVisibility(View.VISIBLE);
            btnCenter.setVisibility(View.GONE);
            btnRight.setVisibility(View.VISIBLE);
        } else if (mLeftClickListener == null && mRightClickListener != null) {
            btnLeft.setVisibility(View.GONE);
            btnCenter.setVisibility(View.GONE);
            btnRight.setOnClickListener(mRightClickListener);
        }

        if (StringUtils.isEmpty(mTxtLeft)) {
            mTxtLeft = mContext.getResources().getString(R.string.common_cancel);
        }

        if (StringUtils.isEmpty(mTxtCenter)) {
            mTxtCenter = mContext.getResources().getString(R.string.common_regist_literally);
        }

        if (StringUtils.isEmpty(mTxtRight)) {
            mTxtRight = mContext.getResources().getString(R.string.common_confirm);
        }

        btnLeft.setText(mTxtLeft);
        btnCenter.setText(mTxtCenter);
        btnRight.setText(mTxtRight);
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public WorkerManDialog(Context _context, String _message,
                           View.OnClickListener _singleListener) {
        super(_context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = _context;
        this.mMessage = _message;
        this.mRightClickListener = _singleListener;
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public WorkerManDialog(Context _context,
                           String _message, View.OnClickListener _leftListener,
                           View.OnClickListener _rightListener) {
        super(_context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = _context;
        this.mMessage = _message;
        this.mLeftClickListener = _leftListener;
        this.mRightClickListener = _rightListener;
    }

    public WorkerManDialog(Context _context,
                           String _message, View.OnClickListener _leftListener,
                           View.OnClickListener _centerListener,
                           View.OnClickListener _rightListener) {
        super(_context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = _context;
        this.mMessage = _message;
        this.mLeftClickListener = _leftListener;
        this.mCenterClickListener = _centerListener;
        this.mRightClickListener = _rightListener;
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public WorkerManDialog(Context _context,
                           String _message,
                           View.OnClickListener _leftListener,
                           View.OnClickListener _rightListener,
                           String _left,
                           String _right) {
        super(_context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = _context;
        this.mMessage = _message;
        this.mLeftClickListener = _leftListener;
        this.mRightClickListener = _rightListener;

        this.mTxtLeft = _left;
        this.mTxtRight = _right;
    }

    public WorkerManDialog(Context _context,
                           String _message,
                           View.OnClickListener _leftListener,
                           View.OnClickListener _centerListener,
                           View.OnClickListener _rightListener,
                           String _left,
                           String _center,
                           String _right) {
        super(_context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = _context;
        this.mMessage = _message;
        this.mLeftClickListener = _leftListener;
        this.mCenterClickListener = _centerListener;
        this.mRightClickListener = _rightListener;

        if(!StringUtils.isEmpty(_left))
            this.mTxtLeft = _left;
        if(!StringUtils.isEmpty(_center))
            this.mTxtLeft = _center;
        if(!StringUtils.isEmpty(_right))
            this.mTxtRight = _right;
    }

    public void setMessage(String _message){
        this.mMessage = _message;
    }

}
