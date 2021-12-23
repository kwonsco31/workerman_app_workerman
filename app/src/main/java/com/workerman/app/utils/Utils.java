package com.workerman.app.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.workerman.app.Defines;
import com.workerman.app.filter.ByteLengthFilter;
import com.workerman.app.manager.PrefManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Utils {
	private static final String TAG = "Utils";
	/**
	 * <PRE>
	 * 1. MethodName : openInternetBrowser
	 * 2. ClassName  : Utils
	 * 3. Comment   : 전달된 URL을 단말기 내부 브라우져를 통해 열도록 합니다.
	 * 4. 작성자    : bada
	 * </PRE>
	 *   @return void
	 *   @param _context
	 *   @param url
	 */
	public static void openInternetBrowser(Context _context, String url) {
		if (url != null) {
			if (!url.equals("")) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				Uri u = Uri.parse(url);
				i.setData(u);
				_context.startActivity(i);
			}
		}
	}  
	
	/**
	 * <PRE>
	 * 1. MethodName : isEmpty
	 * 2. ClassName  : Utils
	 * 3. Comment   : param으로 전달된 데이터가 null인지 체크 합니다.
	 * 4. 작성자    : bada
	 * </PRE>
	 *   @return boolean
	 *   @param _value
	 *   @return
	 */
	@SuppressLint("NewApi")
	public static boolean isEmpty(String _value){
		
		boolean result = false;
		
		if(_value == null || _value.isEmpty() || _value.equals(""))
			return true;
		
		return result;
	}
	
	public static void gotoPlayStore(Context _context, String _marketUrl){
		
		if(!isEmpty(_marketUrl)){
			Uri uri = Uri.parse(_marketUrl);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			try {
				_context.startActivity(intent);
			} catch (Exception e) {
            	Toast.makeText(_context, _marketUrl + "설치 url이 올바르지 않습니다" , Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public static void gotoStore(Context _context, String _marketUrl){
		
		if(!isEmpty(_marketUrl)){
			
			PackageManager pm = _context.getPackageManager();
			String strStore = pm.getInstallerPackageName("com.priviatravel");
//			gotoOneStore(_context, "onestore://common/product/0000703000?[view_type=2]");
	        if("com.android.vending".equals(strStore)){
	            //google play를 통한 설치
	        	gotoPlayStore(_context, _marketUrl);
	        }else{
//	        	스토어 버전	비고				패키지네임							지원버전
//	        	T store		-				com.skt.skaf.A000Z00040			Versionname : 4.54 versioncode : 121 이상
//	        	olleh		마켓	프리로드 버전	com.kt.olleh.storefront			버전 코드: 4101, 버전 네임: 4.1.01 이상
//	        	olleh		마켓	인스톨 버전	com.kt.olleh.istore				버전 코드: 4001, 버전 네임: 4.0.01 이상
//	        	U+스토어		LTE 단말			com.lguplus.appstore			Version Name = 02.00.00 / Version Code = 20000 이상
//	        	U+스토어		3G 단말			android.lgt.appstore			Version Name = 02.00.00 / Version Code = 20000 이상
	        	
	        	if("com.skt.skaf.A000Z00040".equals(strStore) || "com.kt.olleh.storefront".equals(strStore) || "com.kt.olleh.istore".equals(strStore) || "com.lguplus.appstore".equals(strStore) || "android.lgt.appstore".equals(strStore)){
	        	//one-store로 연결
	        		gotoOneStore(_context, "onestore://common/product/0000703000?[view_type=2]");
	        		//background update mode
//	    			gotoOneStore(_context, "onestore://common/product/bg_update/0000703000");

	        	}else{
	        		gotoPlayStore(_context, _marketUrl);
	        	}
	        }
			
		}
	}
	
	private static void gotoOneStore(Context _context, String _marketUrl) {
		if(!isEmpty(_marketUrl)){
			Uri uri = Uri.parse(_marketUrl);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			try {
				_context.startActivity(intent);
			} catch (Exception e) {
            	Toast.makeText(_context, _marketUrl + "설치 url이 올바르지 않습니다" , Toast.LENGTH_SHORT).show();
			}
		}
	}

	public static int getAppVersionCode(Context _context){
		try {
            PackageInfo packageInfo = _context.getPackageManager().getPackageInfo(_context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Coult not get package name: " + e);
        }		
		
	}

//about LG Uplus PG start
// App 체크 메소드 // 존재:true, 존재하지않음:false
	public static boolean isPackageInstalled(Context ctx, String pkgName) {
		try {
			ctx.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
//about LG Uplus PG end

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static boolean isNotificationEnabled(Context context) {

	    AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

	    ApplicationInfo appInfo = context.getApplicationInfo();

	    String pkg = context.getApplicationContext().getPackageName();

	    int uid = appInfo.uid;

	    Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

	    try {

	        appOpsClass = Class.forName(AppOpsManager.class.getName());

	        Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);

	        Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
	        int value = (Integer) opPostNotificationValue.get(Integer.class);
	        
	        int val = (Integer) checkOpNoThrowMethod.invoke(mAppOps,value, uid, pkg);

	        return (val == AppOpsManager.MODE_ALLOWED);

	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (NoSuchMethodException e) {
	        e.printStackTrace();
	    } catch (NoSuchFieldException e) {
	        e.printStackTrace();
	    } catch (InvocationTargetException e) {
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public static void gotoNotificationSetting(Context _context){
		//use isNotificationEnabled method
		//false is notificaiton disabled, true is enabled notification
		try {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
				Intent intent = new Intent();
				intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				//intent.setData(Uri.parse("package:" + getPackageName()));
				intent.putExtra("app_package", _context.getPackageName());
				intent.putExtra("app_uid", _context.getApplicationInfo().uid);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				_context.startActivity(intent);
			}else{
				String packageName = "com.priviatravel";
				String SCHEME = "package";
				String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
				String APP_PKG_NAME_22 = "pkg";
				String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
				String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
				Intent intent = new Intent();
				final int apiLevel = Build.VERSION.SDK_INT;
				if (apiLevel >= 9) { // above 2.3
					intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					Uri uri = Uri.fromParts(SCHEME, packageName, null);
					intent.setData(uri);
				}else { // below 2.3
					final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
					intent.putExtra(appPkgName, packageName);
				}
				_context.startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
		
//    public static Dialog makeProgressDialog(Context _context){
//
//    	if(_context == null)
//    		return null;
//
//    	Dialog pDialog = new Dialog(_context, R.style.TransProgressDialog);
//		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View v = inflater.inflate(R.layout.progress_circle, null);
//		pDialog.setContentView(v);
//		pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				dialog.dismiss();
//			}
//		});
//    	return pDialog;
//    }

	/* configuration 변경 : 앱내의 언어설정을 변경해 준다.*/
	public static void changeConfigulation(Context _context, String _lang) {
		Locale locale = new Locale(_lang);
		Resources resources = _context.getResources();
		Configuration configuration = resources.getConfiguration();
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
			configuration.setLocale(locale);
			_context.getApplicationContext().createConfigurationContext(configuration);
		}
		else{
			configuration.locale = locale;
			resources.updateConfiguration(configuration,displayMetrics);
		}
	}

	public static String getWhatKindOfNetwork(Context _context){
		ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				return Defines.WIFE_STATE;
			} else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				return Defines.MOBILE_STATE;
			}
		}
		return Defines.NONE_STATE;
	}

	public static boolean isNetworkAvailable(Context paramContext)
	{
		NetworkInfo localNetworkInfo = ((ConnectivityManager)paramContext.getSystemService(paramContext.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		return (localNetworkInfo != null) && (localNetworkInfo.isConnectedOrConnecting());
	}

	public static String makeStringComma(String str) {
		if(str != null) {
			str = str.replaceAll(",", "");

			if (str.length() == 0)
				return "";
			long value = Long.parseLong(str);
			DecimalFormat format = new DecimalFormat("###,###");
			return format.format(value);
		}else{
			return "";
		}
	}

	public static String makeStringComma(int number) {
		String str = Integer.toString(number);
		if (str.length() == 0)
			return "";
		long value = Long.parseLong(str);
		DecimalFormat format = new DecimalFormat("###,###");
		return format.format(value);
	}

	public static String makeStringComma(Long number) {
		String str = Long.toString(number);
		if (str.length() == 0)
			return "";
		long value = Long.parseLong(str);
		DecimalFormat format = new DecimalFormat("###,###");
		return format.format(value);
	}


	/**
	 * String code point를 이용한 한글/영어 구분으로 바이트 단위 길이 체크를 합니다.
	 *
	 * @param string the string
	 * @return ex) 가나다 == 6, abcd = 4
	 */
	public static int getLength(String string) {
		if (StringUtils.isEmpty(string)) {
			return 0;
		}
		int length = string.length();
		int charLength = 0;
		for (int i = 0; i < length; i++) {
			charLength += string.codePointAt(i) > 0x00ff ? 2 : 1;
		}
		return charLength;
	}


	public static HashMap<String, String> getDefaultParam(Context _context) {
		HashMap<String, String> params = new HashMap<String, String>();
		String lang_code = PrefManager.getInstance().getString(_context, PrefManager.PREFKEY_LANGUAGE_TYPE, "1");
		params.put(Defines.PARAM_KEY_OS_TYPE, "android");
		/*params.put(Defines.PARAM_KEY_OS_VERSION, Defines.getOSVersion());
		params.put(Defines.PARAM_KEY_APP_VER, Defines.getAppVersion(_context));
		params.put(Defines.PARAM_KEY_DEVICE, Defines.getDevice());
		params.put(Defines.PARAM_KEY_LANG_TYPE, lang_code);
		params.put(Defines.PARAM_KEY_RPT_DAY, DateUtil.getNowDay2());*/

		/*String uuid = getVoteInfo(_context).getUuid();
		params.put(Defines.PARAM_KEY_UUID, uuid);

		if(!StringUtils.isEmpty(getUserNo(_context))) {
			params.put(Defines.PARAM_KEY_USER_NO, getUserNo(_context));
		}
		if(!StringUtils.isEmpty(getUserEmail(_context))){
		    params.put(Defines.PARAM_KEY_USER_EMAIL, getUserEmail(_context));
        }

		String adid = PrefManager.getInstance().getString(_context, PrefManager.PREFKEY_PHONE_ADID, "");
		if(!StringUtils.isEmpty(adid)){
			params.put(Defines.PARAM_KEY_ADID, adid);
		}*/

		return params;
	}

	public static HashMap<String, String> encodeParam(HashMap<String, String> _paramData){
		JSONObject json = new JSONObject(_paramData);
		HashMap<String, String> param = new HashMap<>();
		try {
			param.put("data", ShaPasswordEncoder.AES_Encode(json.toString()));
			param.put(Defines.PARAM_KEY_VERSION, "2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return param;
	}


	public static String getUserNo(Context _context){
		return PrefManager.getInstance().getString(_context.getApplicationContext(), PrefManager.PREFKEY_USER_NO, "");
	}

	public static String getUserEmail(Context _context){
		return PrefManager.getInstance().getString(_context.getApplicationContext(), PrefManager.PREFKEY_USER_EMAIL, "");
	}

	public static boolean isGPSEnabled(Context _context)
	{
		LocationManager locationManager = (LocationManager)_context.getSystemService(Context.LOCATION_SERVICE);
		boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		return isGpsEnabled;
	}

	public static int toInt(String _number){
		int result = 0;
		int nNumber = 0;


		if(!isEmpty(_number)){
			nNumber = Integer.parseInt(_number);

			if(nNumber > 0){
				result = nNumber;
			}
		}

		return result;
	}

	public static String getAdid(Context _context) throws Exception{
		AdvertisingIdClient.Info adInfo = null;

		try {
			adInfo = AdvertisingIdClient.getAdvertisingIdInfo(_context);
		} catch (IllegalStateException e) {
			Log.e(TAG, String.format("Adid error : %s", e.getMessage()));
			e.printStackTrace();
		} catch (GooglePlayServicesRepairableException e) {
			Log.e(TAG, String.format("Adid GooglePlayServicesRepairableException error : %s", e.getMessage()));
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, String.format("Adid IOException error : %s", e.getMessage()));
			e.printStackTrace();
		} catch (GooglePlayServicesNotAvailableException e) {
			Log.e(TAG, String.format("Adid GooglePlayServicesNotAvailableException error : %s", e.getMessage()));
			e.printStackTrace();
		}

		return adInfo.getId();
	}

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager =
				(InputMethodManager) activity.getSystemService(
						Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(
				activity.getCurrentFocus().getWindowToken(), 0);
	}

	public static InputFilter[] getByteFilter(Context _context, int _length) {
		InputFilter[] byteFilter = new InputFilter[]{new ByteLengthFilter(_context, _length, Defines.FILTER_DEFALUT_ENCODING)};//Byte와 CharSet을 지정한다.
		return byteFilter;
	}


	public static String getAppCert(Context _context) {
		PackageManager pm = _context.getPackageManager();
		String packageName = _context.getPackageName();
		String cert = null;
		Signature certSignature = null;
		try {
			PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			certSignature = packageInfo.signatures[0];
//			MessageDigest msgDigest = MessageDigest.getInstance("SHA1");
			MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
			msgDigest.update(certSignature.toByteArray());
			cert = Base64.encodeToString(msgDigest.digest(), Base64.DEFAULT);
//			Debug.d(TAG, "cert:["+cert+"]");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return cert;
	}

	public static String getKeyHash(Context _context){
		String strKeyHash = "";
		try {
			PackageInfo info = _context.getPackageManager().getPackageInfo("com.workerman.user.app", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
				strKeyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return strKeyHash;

	}


}
