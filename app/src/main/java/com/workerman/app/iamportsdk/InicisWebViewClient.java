package com.workerman.app.iamportsdk;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.provider.Settings;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.workerman.app.R;
import com.workerman.app.manager.PrefManager;
import com.workerman.app.net.APICallService;
import com.workerman.app.net.APIClient;
import com.workerman.app.net.APIService;
import com.workerman.app.utils.Debug;
import com.workerman.app.utils.Utils;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicisWebViewClient extends WebViewClient {
	private static final String TAG = "InicisWebViewClient";
	private Activity activity;
	private String mLandingUrl="";

	private GpsTracker gpsTracker; //kwonsco
	protected APIService mService; //kwonsco

	WebView mWebView;

	public InicisWebViewClient(Activity activity, WebView _webview, String _landingUrl) {
		this.activity = activity;
		this.mWebView = _webview;
		this.mLandingUrl = _landingUrl;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
			Intent intent = null;

			try {
				// Starts With String
				if(url.startsWith("sms:")){
					intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID, activity.getPackageName());
					Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
					activity.startActivity(i);
					return true;
				}else if (url.startsWith("tel:")) {
					intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID, activity.getPackageName());

					Intent i = new Intent(Intent.ACTION_DIAL , Uri.parse(url));
					activity.startActivity(i);
					return true;
				}else if (url.startsWith("mailto:")) {
					MailTo mt = MailTo.parse(url);
					Intent i = newEmailIntent(activity, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
					activity.startActivity(i);
					view.reload();
					return true;
				}else if(url.toLowerCase().startsWith("protocol://close")){
					activity.finish();
					return true;
				}else if(url.toLowerCase().startsWith("protocol://workdate")){

				}else if(url.toLowerCase().startsWith("protocol://detail")){

				}else if(url.toLowerCase().startsWith("protocol://admin")){

					String _admin_no = url.split("/")[3];
					String _token = url.split("/")[4];

					// 로그인 토크저장
					PrefManager.getInstance().putString(activity, PrefManager.PREFKEY_TOKEN, _token);
					PrefManager.getInstance().putString(activity, PrefManager.PREFKEY_ADMIN_NO, _admin_no);

					Log.d("test", "set token : "+_token);

					String device_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
					String strToken = PrefManager.getInstance().getString(activity, PrefManager.PREFKEY_FCM_TOKEN, "");
					String os_type = "android";
					view.loadUrl("javascript:getDeviceInfo('" + device_id + "','" + strToken + "','" + os_type + "')");

					return true;
				}
				// 작업상테 업데이트시 워커맨 위치 정보 송신
				else if (url.toLowerCase().startsWith("protocol://work_stat")) {
					try {
						if(url.split("/").length > 5){
							String _work_no = url.split("/")[3];
							String _work_stat = url.split("/")[4];
							String _admin_no = url.split("/")[5];

							Log.d("WORK_GPS:", "set work_no : "+_work_no);
							Log.d("WORK_GPS:", "set work_stat : "+_work_stat);
							Log.d("WORK_GPS:", "set admin_no : "+_admin_no);

							Map<String, Object> param = new HashMap<String, Object>();
							param.put("admin_no", _admin_no);
							param.put("work_no", _work_no);
							param.put("work_stat", _work_stat);
							gpsTracker = new GpsTracker(activity);
							double latitude = gpsTracker.getLatitude();
							double longitude = gpsTracker.getLongitude();
							param.put("latitude", String.valueOf(latitude));
							param.put("longitude", String.valueOf(longitude));

							//mService = APIClient.getClient(_context).create(APIService.class);

							mService = APIClient.getClient(activity.getApplicationContext()).create(APIService.class);
							Call<Map<String, Object>> call = mService.gspUpload(param);

							call.enqueue(new Callback<Map<String, Object>>() {
								@Override
								public void onResponse(Call<Map<String, Object>> call, final Response<Map<String, Object>> response) {
									try {
										if (response != null) {
											Log.d(TAG,"@ WORK_GPS RESPONSE.CODE : "+response.code());
											if (200 == response.code()) {
												Log.d(TAG, "WORK_GPS API CALL SUCCESS: " + response.body().toString());
											} else {
												Log.e(TAG, "WORK_GPS API CALL ERROR1: " + response.code());
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
										Log.e(TAG, "WORK_GPS API CALL ERROR2: " + response.code());
									}
								}

								@Override
								public void onFailure(Call<Map<String, Object>> call, Throwable t) {
									call.cancel();
									Log.e(TAG, "WORK_GPS API CALL FAIL: " + t.getMessage());
								}
							});
							System.out.println("INTO work_stat !!!!");
						}
					} catch (Exception e){

					}

				}
			} catch (Exception e) {
				Toast.makeText(activity, activity.getResources().getString(R.string.fail_to_run), Toast.LENGTH_SHORT).show();
				return true;
			}

			try {
				intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
				Uri uri = Uri.parse(intent.getDataString());
				
				activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
				return true;
			} catch (URISyntaxException ex) {
				return false;
			} catch (ActivityNotFoundException e) {
				if ( intent == null )	return false;
				
				if ( handleNotFoundPaymentScheme(intent.getScheme()) )	return true;
				
				String packageName = intent.getPackage();
		        if (packageName != null) {
		            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
		            return true;
		        }
		        
		        return false;
			}
		}
		
		return false;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {

		super.onPageStarted(view, url, favicon);

		Debug.d(TAG, "onPageStarted : " + url);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);

		// Initialise the page
//			view.loadUrl("javascript: android_init();");
            /* NOTICE 로그인을 한 다음 앱을 종료하고, 다시 앱을 실행했을 때 간헐적으로 로그인이 안 된 상태가 된다.
             이는 웹뷰의 RAM과 영구 저장소 사이에 쿠키가 동기화가 안 되어 있기 때문이다. 따라서 강제로 동기화를 해준다. */
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			//noinspection deprecation
			CookieSyncManager.getInstance().sync();
		} else {
			// 롤리팝 이상에서는 CookieManager의 flush를 하도록 변경됨.
			CookieManager.getInstance().flush();
		}

		if(!Utils.isEmpty(mLandingUrl)){
			mWebView.loadUrl(mLandingUrl);
			mLandingUrl = "";
		}

	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);

		if ( Utils.isNetworkAvailable(activity) ) {
			if ( mWebView != null ) {
				mWebView.reload();
			}
		} else {
			Toast.makeText(activity,"Network Error Occur!",Toast.LENGTH_SHORT).show();
		}

		Debug.d(TAG, "onReceivedError : " + description + " - " + failingUrl);
//			Debug.writeLog("onReceivedError : " + description + " - " + failingUrl);
	}
	
	/**
	 * @param scheme
	 * @return 해당 scheme에 대해 처리를 직접 하는지 여부
	 * 
	 * 결제를 위한 3rd-party 앱이 아직 설치되어있지 않아 ActivityNotFoundException이 발생하는 경우 처리합니다.
	 * 여기서 handler되지않은 scheme에 대해서는 intent로부터 Package정보 추출이 가능하다면 다음에서 packageName으로 market이동합니다.  
	 * 
	 */
	protected boolean handleNotFoundPaymentScheme(String scheme) {
		//PG사에서 호출하는 url에 package정보가 없어 ActivityNotFoundException이 난 후 market 실행이 안되는 경우
		if ( PaymentScheme.ISP.equalsIgnoreCase(scheme) ) {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_ISP)));
			return true;
		} else if ( PaymentScheme.BANKPAY.equalsIgnoreCase(scheme) ) {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_BANKPAY)));
			return true;
		}
		
		return false;
	}

	private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
		intent.putExtra(Intent.EXTRA_TEXT, body);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_CC, cc);
		intent.setType("message/rfc822");
		return intent;
	}
}
