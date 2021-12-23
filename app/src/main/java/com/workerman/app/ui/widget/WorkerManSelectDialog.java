package com.workerman.app.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.workerman.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bada
 */


public class WorkerManSelectDialog extends Dialog {
    private static final String TAG = "WorkerManSelectDialog";

    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.imgBtnClose)
    ImageButton imgBtnClose;
    @BindView(R.id.lyContentBody)
    LinearLayout lyContentBody;


    private OnItemClickListener mOnItemClickListener;

    private String mTitle;
    private CharSequence[] mListData;

    private Context mContext;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener _itemClickListener) {
        this.mOnItemClickListener = _itemClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_custom_select);
        ButterKnife.bind(this);

        txtTitle.setText(mTitle);
        refreshList();

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
//    public WorkerManSelectDialog(Context _context, String _title, List<String> _listItem) {
//        super(_context, android.R.style.Theme_Translucent_NoTitleBar);
//        this.mContext = _context;
//        this.mTitle = _title;
//        this.mListData = _listItem;
//    }

    public WorkerManSelectDialog(Context _context, String _title, int textArrayResId) {
        super(_context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = _context;
        this.mTitle = _title;
        this.mListData = mContext.getResources().getTextArray(textArrayResId);
    }

    private void refreshList() {
        if (mListData != null && mListData.length > 0) {
            lyContentBody.removeAllViews();
            int length = mListData.length;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < length; i++) {
                String item = mListData[i].toString();
                LinearLayout layout = new LinearLayout(mContext);
                layout = (LinearLayout) inflater.inflate(R.layout.row_dialog_select_list, null);
                TextView txtLabel = (TextView) layout.findViewById(R.id.txtLabel);
                txtLabel.setText(item);
                layout.setTag(i);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int position = (Integer) v.getTag();
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(v, position);
                        }
                        dismiss();
                    }
                });
                lyContentBody.addView(layout);
            }
        }
    }

}
