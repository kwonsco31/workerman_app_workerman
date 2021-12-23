package com.workerman.app.utils;

//import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.workerman.app.manager.ConfigManager;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//import android.provider.Settings.System;

public class CommonUtils {	
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private static int densityDpi = 0;

	public static void makeWebCacheFolder() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/priviatravel/.webCache/";
		try {
			File file = new File(path);
			file.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static String getWebCacheFolder() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/priviatravel/.webCache/";
	}
	
	public static void clearWebCacheFolder() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/priviatravel/.webCache/";
		try {
			File file = new File(path);
			File[] childFileList = file.listFiles();
		    for(File childFile : childFileList) {
//		        if(childFile.isDirectory()) {
	//	            DeleteDir(childFile.getAbsolutePath());     //하위 디렉토리 루프 
//		        }
//		        else {
		            childFile.delete();    //하위 파일삭제
//		        }
		    }      
		    file.delete();    //root 삭제
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Bitmap loadImageFromFile(String path, String filename) {
		String fullPath = path+filename;
		File srcFile = new File(fullPath);
		Bitmap bmp = null;
		
		try {
			if(srcFile.exists()) {
				bmp = BitmapFactory.decodeFile(fullPath);
			}
		} catch(OutOfMemoryError e) {
			e.printStackTrace();
			
			System.gc();
		}
		return bmp;
	}
	public static boolean isAirPlaneMode(Context paramContext)
	{
	   int i = Settings.System.getInt(paramContext.getContentResolver(), "airplane_mode_on", 0);
	   boolean bool = false;
	   if (i != 0) {
		   bool = true;
	   }
	   return bool;
	}
 
	public static boolean isNetworkAvailable(Context paramContext)
	{
	   NetworkInfo localNetworkInfo = ((ConnectivityManager)paramContext.getSystemService(paramContext.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
	   return (localNetworkInfo != null) && (localNetworkInfo.isConnectedOrConnecting());
	}
	  
	public static Resolution getResolution() {
		Resolution resolution = Resolution.DEFAULT;
		int width = getScreenWidth();
		int height = getScreenHeight();
		int densityDpi = getDPI();
		
		if(width >= 600 && width < 720) {
			resolution = Resolution.WSVGA;
		} else if(width >= 720 && width < 768) {
			if(height > 1184) resolution = Resolution.WXGA;
			else resolution = Resolution.WXGA_IC;
		} else if(width >= 768 && width < 800) {
			resolution = Resolution.WXGA_T;
		} else if(width >= 800) {
			if(densityDpi == 160) resolution = Resolution.WXGA_T;
			else resolution = Resolution.WXGA_S;
		}
		return resolution;
	}
	
	public static int getDPI() {
		if(densityDpi == 0) densityDpi = (int) ConfigManager.getMainContext().getResources().getDisplayMetrics().densityDpi;
		return densityDpi;
	}
	
	public static int getPxFromDip(int dip) {
		return dip * getDPI() / 160;
	}
	
	public enum Resolution {
		DEFAULT("others"),
		WSVGA("600X1024"),
		WXGA("720X1280"),
		WXGA_IC("720X1280"),
		WXGA_S("800X1280"),
		WXGA_T("1280X768");

		private String description;

		Resolution(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return this.description;
		}		
	}

	public static int getScreenWidth() {
		screenWidth = ConfigManager.getMainContext().getResources().getDisplayMetrics().widthPixels;
		return screenWidth;
	}

	public static int getScreenHeight() {
		screenHeight = ConfigManager.getMainContext().getResources().getDisplayMetrics().heightPixels;
		return screenHeight;
	}
	
	public static double getScreenInch() {
		int widthPixels = getScreenWidth();
		int heightPixels = getScreenHeight();
		
		float x_inch = widthPixels / ConfigManager.getMainContext().getResources().getDisplayMetrics().xdpi;
		float y_inch = heightPixels / ConfigManager.getMainContext().getResources().getDisplayMetrics().ydpi;
		
		return Math.sqrt(x_inch*x_inch + y_inch*y_inch);
	}
	
	public static void memoryInfo(Activity activity) {
		android.app.ActivityManager activityManager = (android.app.ActivityManager) activity.getSystemService(Activity.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);

		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

		Map<Integer, String> pidMap = new TreeMap<Integer, String>();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			pidMap.put(runningAppProcessInfo.pid, runningAppProcessInfo.processName);
		}

		Collection<Integer> keys = pidMap.keySet();

		for(int key : keys) {
			int pids[] = new int[1];
			pids[0] = key;
			android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pids);
			for(android.os.Debug.MemoryInfo pidMemoryInfo: memoryInfoArray) {
				if(pidMap.get(pids[0]).equals("com.priviatravel")) {
					Toast.makeText(activity, String.valueOf(pidMemoryInfo.getTotalPss()), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	public static boolean isLandScape() {
		if(ConfigManager.getMainContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		}
		return false;
	}
	
	public static boolean isPortrait() {
		if(ConfigManager.getMainContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return true;
		}
		return false;
	}
	
	public static void unbindDrawables(View view) {
		if(view == null)
			return;
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}

			try {
				if(view instanceof AdapterView) {
					
				} else {
					((ViewGroup) view).removeAllViews();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 클립보드에 주소 복사 기능
	 * @param context
	 * @param link
	 */
//	public static void copyToClipBoard(Context context , String link){
//
//		ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
//		ClipData clipData = ClipData.newPlainText("label", link);
//		clipboardManager.setPrimaryClip(clipData);
//		Toast.makeText(context, context.getString(R.string.toast_msg_copy_clipboard), Toast.LENGTH_SHORT).show();
//
//	}


}