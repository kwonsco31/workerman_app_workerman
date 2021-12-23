package com.workerman.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.workerman.app.manager.ConfigManager;

public class Defines {
	String TAG = Defines.class.getSimpleName();
	public static final String OS = "android";
	// 마켓 배포시에는 꼭 MNG로 설정하기 바랍니다.
	public static final String RELEASE_TYPE = "MNG"; // MNG:운영, DEV:개발

	public static final boolean IS_LOCAL = false; // 로컬테스트시 true

	public static final boolean USE_ENCODING = false;

	public static final String REQ_DOMAIN_LOCAL = "http://192.168.0.60:8070"; // 로컬주소 내주소
	//public static final String REQ_DOMAIN_LOCAL_API = "http://192.168.0.3:7070"; // 로컬주소API
	public static final String REQ_DOMAIN_LOCAL_API = "http://devapi.iworkerman.com"; // 로컬대신 devapi 사용
	public static final String REQ_DOMAIN_DEV = (IS_LOCAL ? REQ_DOMAIN_LOCAL_API : "http://devapi.iworkerman.com") ;
	public static final String REQ_DOMAIN_MNG = "http://api.iworkerman.com";
	//public static final String WEBVIEW_DOMAIN_DEV = (IS_LOCAL ? REQ_DOMAIN_LOCAL : "http://devwebview.iworkerman.com");
	// http://devwebadmin.iworkerman.com/
	// new admin url
	public static final String WEBVIEW_DOMAIN_DEV = (IS_LOCAL ? REQ_DOMAIN_LOCAL : "http://devwebadmin.iworkerman.com");
	//public static final String WEBVIEW_DOMAIN_MNG = "http://webview.iworkerman.com";
	// new admin url
	public static final String WEBVIEW_DOMAIN_MNG = "http://webadmin.iworkerman.com"; // webview mng domain

	public static String GOOGLE_MARKET_URI = "market://details?id=com.workerman.user.app";

	public static String REQ_DOMAIN = RELEASE_TYPE.equals("MNG") ? "http://api.iworkerman.com": (IS_LOCAL ? REQ_DOMAIN_LOCAL_API : "http://devapi.iworkerman.com");

	//public static String WEBVIEW_DOMAIN = RELEASE_TYPE.equals("MNG")? "http://webview.iworkerman.com" : (IS_LOCAL ? REQ_DOMAIN_LOCAL : "http://devwebview.iworkerman.com");
	// new admin url
	public static String WEBVIEW_DOMAIN = RELEASE_TYPE.equals("MNG")? WEBVIEW_DOMAIN_MNG : (IS_LOCAL ? REQ_DOMAIN_LOCAL : "http://devwebview.iworkerman.com");
	public static String WORKERMAN_LANDING_URL = WEBVIEW_DOMAIN + "/admin/listActivity?admin_no=";

	//public static String WEBVIEW_MAIN_URL = WEBVIEW_DOMAIN + "/admin/main";
	// new admin url
	public static String WEBVIEW_MAIN_URL = WEBVIEW_DOMAIN + "/main/index";

	public static String WORKERMAN_AGREE_URL_KO = WEBVIEW_DOMAIN + "/termsForm";

	public static final String PACKAGE_NAME = "com.workerman.user.app";
	public static final String TMP_PATH = "/Android/data/" + PACKAGE_NAME + "/cache/";

	private static int APP_VERSION = -1;

	public static String userToken = "";

	public static final String PARAM_KEY_OS_TYPE = "os_type";
	public static final String PARAM_KEY_VERSION = "version";

	public static final String WIFE_STATE = "WIFE";
	public static final String MOBILE_STATE = "MOBILE";
	public static final String NONE_STATE = "NONE";

	public static final String INTENT_KEY_URL = "WorkerMan.Load.Url";
	public static final String INTENT_KEY_LANDING_URL = "WorkerMan.Load.Landing.Url";
	public static final String INTENT_KEY_CHECK_SERVER = "WorkerMan.Check.Server.Connection";
	public static final String INTENT_KEY_FROM_PUSH = "WorkerMan.From.Push";
	public static final String INTENT_KEY_FROM_PUSH_NEED_FINISH = "WorkerMan.From.Push.Need.Finish";
	public static final String INTENT_KEY_PUSH_DATA = "WorkerMan.Push.Data";

	public static final String CONNECTION_CONFIRM_CLIENT_URL = "http://clients3.google.com/generate_204";

	public static final String FILTER_DEFALUT_ENCODING = "UTF-8";

	/**
	 * 위치갱신 API주소
	 */
	public static String API_URL_GPS_UPLOAD = REQ_DOMAIN+"/admin/gps/upload";

	public static final int getAppVersionCode() {
		try {
			if (APP_VERSION == -1) {
				APP_VERSION = ConfigManager.getMainContext().getPackageManager().getPackageInfo(ConfigManager.getMainContext().getPackageName(), 0).versionCode;
			}
		} catch (NameNotFoundException e) {
			return 100;
		} catch (Exception e) {
			return 100;
		}

		return APP_VERSION;
	}

	public static final String getOSVersion() {
		// 사용자의 핸드폰 OS 버전 추출
		String osVer = "";

		osVer = Build.VERSION.RELEASE;

		return osVer;
	}

	public static final String getDevice() {
		String device = "";

		device = Build.DEVICE;

		return device;
	}

	private static String APP_NAME = null;

	public static final String getAppVersion(Context context) {
		try {
			if (APP_NAME == null) {
				APP_NAME = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			}
		} catch (NameNotFoundException e) {
			return "1.0.0.0";
		}

		return APP_NAME;
	}

	public static final String getAppPackageName() {
		return getAppPackageName(ConfigManager.getMainContext());
	}

	public static final String getAppPackageName(Context context) {
		return context.getPackageName();
	}

	public static final boolean isDebuggable() {
		try {
			int flags = ConfigManager.getMainContext().getPackageManager().getPackageInfo(ConfigManager.getMainContext().getPackageName(), 0).applicationInfo.flags;
			boolean isDebugMode = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
			return isDebugMode;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public static void setDefaultUrl(boolean _isDebug){

		if(_isDebug){
			REQ_DOMAIN = REQ_DOMAIN_DEV;
			WEBVIEW_DOMAIN = WEBVIEW_DOMAIN_DEV;
		}else{
			REQ_DOMAIN = REQ_DOMAIN_MNG;
			WEBVIEW_DOMAIN = WEBVIEW_DOMAIN_MNG;
		}

		API_URL_GPS_UPLOAD = REQ_DOMAIN+"/admin/gps/upload";
		WORKERMAN_LANDING_URL = WEBVIEW_DOMAIN + "/admin/listActivity?admin_no=";
		//WEBVIEW_MAIN_URL = WEBVIEW_DOMAIN + "/admin/main";
		// new admin url
		WEBVIEW_MAIN_URL = WEBVIEW_DOMAIN + "/main/index";
		WORKERMAN_AGREE_URL_KO = WEBVIEW_DOMAIN + "/termsForm";
	}
}
