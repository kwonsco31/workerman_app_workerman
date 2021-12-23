package com.workerman.app.utils;

/**
 * <PRE>
 * 
 * 1. FileName : Utils.java
 * 2. Package : com.org.zenielmac.utils
 * 3. Comment : 앱내에서 공용으로 편리하게 사용할 수 있도록 하는 아이템을 모아 두는 클래스 입니다.
 * 4. 작성자  : bada
 * </PRE>
 */

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

public class TextViewUtils {

	private static String TAG = Utils.class.getSimpleName();
	private static final String _DATA = "_data";

	// private static Context mContext;
	// private static Toast mToast;

	// 지정된 범위의 정수 1개를 램덤하게 반환하는 메서드
	// n1 은 "하한값", n2 는 상한값
	public static int randomRange(int n1, int n2) {
		return (int) (Math.random() * (n2 - n1 + 1)) + n1;
	}


	/**
	 * @Method Name : setTextViewColorPartial
	 * @Author : "bada"
	 * @Method Desc : 전달된 텍스트뷰에 fulltext를 표시하고 subtext의 색상을 전달된 색상값으로 변경해 줍니다.
	 * @param view
	 * @param fulltext
	 * @param subtext
	 * @param color
	 */
	public static void setTextViewColorPartial(TextView view, String fulltext, String subtext, int color) {

		Log.d(TAG, String.format("setTextViewColorPartial(TextView:%s,fulltext:%s,subtext:%s,color:%d)",view,fulltext,subtext,color));
		view.setText(fulltext, TextView.BufferType.SPANNABLE);
		Spannable str = (Spannable) view.getText();
		int i = fulltext.indexOf(subtext);
		if(i >= 0){
			str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			str.setSpan(new StyleSpan(Typeface.BOLD), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	/**
	 * @Method Name : setTextViewStrike
	 * @Author : "bada"
	 * @Method Desc : 전달된 텍스트 뷰에 줄을 그어 줍니다.
	 * @param view
	 */
	public static void setTextViewStrike(TextView view) {
		view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	}

	/**
	 * @Method Name : setTextViewColorPartial
	 * @Author : "bada"
	 * @Method Desc : 전달된 체크박스에 fulltext를 표시하고 subtext의 색상을 전달된 색상값으로 변경해 줍니다.
	 * @param view
	 * @param fulltext
	 * @param subtext
	 * @param color
	 */
	public static void setTextViewColorPartial(CheckBox view, String fulltext, String subtext, int color) {
		view.setText(fulltext, TextView.BufferType.SPANNABLE);
		Spannable str = (Spannable) view.getText();
		int i = fulltext.indexOf(subtext);
		str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
}