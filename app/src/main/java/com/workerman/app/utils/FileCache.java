package com.workerman.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.collection.LruCache;
import android.text.TextUtils;

public class FileCache extends LruCache<String, Bitmap> {

	private Context _context;
	
	public FileCache(Context context, int maxCacheSize) {
		super(maxCacheSize);
		_context = context;
	}
	
	public boolean containsKey(String key) {
		if (TextUtils.isEmpty(key)) return false;
		Bitmap bmp = get(key);
		if (bmp != null){
			if(!bmp.isRecycled()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public void clear() {
		evictAll();
	}
}
