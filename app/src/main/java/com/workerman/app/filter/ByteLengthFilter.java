package com.workerman.app.filter;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import com.workerman.app.R;

import java.io.UnsupportedEncodingException;

public class ByteLengthFilter implements InputFilter {
    private String mCharset;

    protected int mMaxByte;
    private Context mContext;

    public ByteLengthFilter(Context _context, int maxbyte, String charset) {
        this.mMaxByte = maxbyte;
        this.mCharset = charset;
        this.mContext = _context;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {

        // TODO Auto-generated method stub

        String expected = new String();

        expected += dest.subSequence(0, dstart);

        expected += source.subSequence(start, end);

        expected += dest.subSequence(dend, dest.length());


        int keep = calculateMaxLength(expected) - (dest.length() - (dend - dstart));

        Log.i("filter", "CharSequence filter:" + keep);
        if (keep <= 0) {
            Log.i("filter", "CharSequence2 filter:" + keep);
            Toast.makeText(mContext, mContext.getResources().getString(R.string.toast_limit_over), Toast.LENGTH_SHORT).show();
            return "";

        } else if (keep >= end - start) {

            Log.i("filter", "CharSequence3 filter:" + keep);

            return null;

        } else {

            Log.i("filter", "CharSequence4 filter:" + keep);

            return source.subSequence(start, start + keep);

        }

    }

    protected int calculateMaxLength(String expected) {
        return mMaxByte - (getByteLength(expected) - expected.length());

    }

    public int getByteLength(String str) {
        try {

            return str.getBytes(mCharset).length;

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        return 0;
    }

}


