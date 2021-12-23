package com.workerman.app.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.workerman.app.WorkerManApplication;

/**
 * Created by bada on 2017-12-26.
 */

public class DisplayUtil
{
    //dp를 px로 변환 (dp를 입력받아 px을 리턴)
    public static int convertDpToPixel(float dp){
        Context context = WorkerManApplication.getInstance().getApplicationContext();
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(px);
    }

    //px을 dp로 변환 (px을 입력받아 dp를 리턴)
    public static int convertPixelsToDp(float px){
        Context context = WorkerManApplication.getInstance().getApplicationContext();
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(px);
    }




}
