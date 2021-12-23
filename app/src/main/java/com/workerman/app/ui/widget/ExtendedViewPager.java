package com.workerman.app.ui.widget;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 상세보기를 위한 ViewPager
 * - 이미지 확대 중 Scroll로 인하여 pager의 이동을 방지하기 위해.
 * Created by bada on 18. 7. 4..
 */
public class ExtendedViewPager extends ViewPager {

    public ExtendedViewPager(Context context) {
        super(context);
    }

    public ExtendedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof TouchImageView) {
            return ((TouchImageView) v).canScrollHorizontallyFroyo(-dx);
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }
}
