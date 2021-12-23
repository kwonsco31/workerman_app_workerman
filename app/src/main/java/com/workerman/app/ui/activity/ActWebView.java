package com.workerman.app.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Message;
import android.provider.Browser;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.workerman.app.Defines;
import com.workerman.app.R;
import com.workerman.app.iamportsdk.InicisWebViewClient;
import com.workerman.app.ui.activity.base.BaseActivity;
import com.workerman.app.utils.Debug;
import com.workerman.app.utils.RealPathUtil;
import com.workerman.app.utils.Utils;
import com.workerman.app.vo.WorkerMemberPushData;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnErrorListener;
import gun0912.tedimagepicker.builder.listener.OnMultiSelectedListener;
import gun0912.tedimagepicker.builder.listener.OnSelectedListener;

public class ActWebView extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ActWebView";

    @BindView(R.id.webview)
    WebView mWebView;

    private GestureDetector mGestureDetector;
    private boolean isLockOnHorizontialAxis;

    private final String APP_SCHEME = "workerman://";

    private CountDownTimer mCountTimer;

    private boolean isExit = false;

    private static final String TYPE_IMAGE = "image/*";
    private static final int INPUT_FILE_REQUEST_CODE = 1;

    private ValueCallback<Uri> mFileCallbackSingle;
    private ValueCallback<Uri[]> mFileCallbackMulti;
    private String mCameraPhotoPath;

    protected String mUrl = "";
    List<? extends Uri> mUriList = new ArrayList<>();
    public static ActWebView mInstance;
    private String mStrLandingUrl;

    public static ActWebView getInstance(){
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_webview);
        ButterKnife.bind(this);

        setDefaultSettings();
//        swipeRefreshEnable(Defines.USE_PULL_TO_REFRESH);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.getInstance().startSync();
        }

        if (mFileCallbackMulti != null) mFileCallbackMulti.onReceiveValue(null);
        if (mFileCallbackSingle != null) mFileCallbackSingle.onReceiveValue(null);
        mFileCallbackMulti = null;
        mFileCallbackSingle = null;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountTimer != null) {
            mCountTimer.cancel();
        }
        if(mInstance != null)
            mInstance = null;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.getInstance().stopSync();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String url = intent.getDataString();
        if (!Utils.isEmpty(url) && url.startsWith(APP_SCHEME)) {
            String redirectURL = url.substring(APP_SCHEME.length() + 3);
            mWebView.loadUrl(redirectURL);
        }

        Bundle bundle = intent.getExtras();
        if(bundle != null){
            Log.d(TAG, "onNewIntent: " + bundle.toString());
            WorkerMemberPushData pushData = (WorkerMemberPushData) bundle.getSerializable(Defines.INTENT_KEY_PUSH_DATA);
            if (pushData != null) {
                String strLandingUrl = Defines.WORKERMAN_LANDING_URL + pushData.getAdmin_no();
                mWebView.loadUrl(strLandingUrl);
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (mWebView != null) {

            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                if (isExit == false) {
                    if (mCountTimer != null) {
                        isExit = true;
                        showToast(getStringRes(R.string.common_exit_app));
                        mCountTimer.start();
                    }
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (intent != null) {
                if(mFileCallbackSingle != null){
                    mFileCallbackSingle.onReceiveValue(intent.getData());
                    mFileCallbackSingle = null;
                }else if(mFileCallbackMulti != null) {
                    Uri [] dataUris = null;

                    try {
                        if(intent.getDataString() != null){
                            dataUris = new Uri[] { Uri.parse(intent.getDataString())};

                        }else{
                            if (Build.VERSION.SDK_INT >= 16) {
                                if (intent.getClipData() != null) {
                                    final int numSelectedFiles = intent.getClipData().getItemCount();

                                    dataUris = new Uri[numSelectedFiles];

                                    for (int i = 0; i < numSelectedFiles; i++) {
                                        dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                    }
                                }
                            }
                        }
                    }catch  (Exception ignored) { }
                    mFileCallbackMulti.onReceiveValue(dataUris);
                    mFileCallbackMulti = null;
                }
            }
        } else {
            if (mFileCallbackMulti != null) mFileCallbackMulti.onReceiveValue(null);
            if (mFileCallbackSingle != null) mFileCallbackSingle.onReceiveValue(null);
            mFileCallbackMulti = null;
            mFileCallbackSingle = null;
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private Uri getResultUri(Intent data) {
        Uri result = null;
        if (data == null || TextUtils.isEmpty(data.getDataString())) {
            // If there is not data, then we may have taken a photo
            if (mCameraPhotoPath != null) {
                result = Uri.parse(mCameraPhotoPath);
            }
        } else {
            String filePath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                filePath = data.getDataString();
            } else {
                filePath = "file:" + RealPathUtil.getRealPath(this, data.getData());
            }
            result = Uri.parse(filePath);
        }

        return result;
    }

    /*public void swipeRefreshEnable(boolean usePullToRefresh) {
        Debug.d(TAG, "swipeRefreshEnable: " + usePullToRefresh);
        if(mSwipeRefreshLayout != null) {
            if(usePullToRefresh) {
                mSwipeRefreshLayout.setOnRefreshListener(this);
                mSwipeRefreshLayout.setEnabled(usePullToRefresh);
            }else{
                mSwipeRefreshLayout.setEnabled(usePullToRefresh);
            }
        }
    }*/

    private void setDefaultSettings() {
        setDefaultWebSetting(mWebView);

        Intent intent = getIntent();
        mUrl = intent.getStringExtra(Defines.INTENT_KEY_URL);
        Uri intentData = intent.getData();

        if (StringUtils.isEmpty(mUrl)) {
            mUrl = Defines.WORKERMAN_AGREE_URL_KO;
            loadUrl(mUrl);
        } else {
            if (intentData != null) {
                //isp 인증 후 복귀했을 때 결제 후속조치
                String url = intentData.toString();
                if (url.startsWith(APP_SCHEME)) {
                    String redirectURL = url.substring(APP_SCHEME.length() + 3);
                    loadUrl(redirectURL);
                }
            } else {
                //일반 웹 페이지 연동
                loadUrl(mUrl);
            }
        }
        mCountTimer = new CountTimer(2000, 500);
    }

    private void loadUrl(String _url) {
        if (mWebView != null) {
            if (!StringUtils.isEmpty(mUrl)) {
                mWebView.loadUrl(_url);
            }
        }
    }

    private void setDefaultWebSetting(WebView _view) {
        WebSettings webSettings = _view.getSettings();
//        fixWebViewJSInterface(_view, new TheStarWebBridge(ActWebView.this, _view), Defines.JAVASCRIP_INTERFACE_NAME, "_gbjsfix:");
//		fixWebViewJSInterface(WebViewSub, new PriviaTravelWebBridge(ActWebView.this, WebViewSub), "PriviaTravelApp", "_gbjsfix:");
        if(getIntent() != null) {
            mStrLandingUrl = getIntent().getStringExtra(Defines.INTENT_KEY_LANDING_URL);
        }else{
            mStrLandingUrl = "";
        }

        _view.setWebViewClient(new InicisWebViewClient(this, mWebView, mStrLandingUrl));
        _view.setWebChromeClient(new GingerbreadWebViewChrome());

        // javascript를 사용 가능 하게.
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(false);
        // javascript로 Window를 띄울 수 있게 함.
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // DomStorage를 사용한 경우, true로 해주지 않으면 비정상 동작함.
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            webSettings.setDisplayZoomControls(false);
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            webSettings.setTextZoom(100);
        }

//        if(Defines.isDebuggable() && !"MNG".equals(Defines.RELEASE_TYPE)){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            WebView.setWebContentsDebuggingEnabled(true);
//        }

        webSettings.setSupportMultipleWindows(true);

        //롤리팝 적용
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //롤리팝 적용
            //5.0에서 부터는 혼합된 컨텐츠와 서드파티 쿠키가 설정에 따라 Webview 에서 Block 시키는 게 기본이 됬다는 내용으로 허용하도록 적용시킴.
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(_view, true);
        }
        //누가 이상 적용
        if (Build.VERSION.SDK_INT >= 24) {
            webSettings.setSaveFormData(false);
        }
    }

    /*@SuppressLint("JavascriptInterface")*/
    /*public void fixWebViewJSInterface(WebView webview, Object jsInterface, String jsInterfaceName, String jsSignature) {
        if (Build.VERSION.RELEASE.startsWith("2.3")) {
            webview.addJavascriptInterface(jsInterface, jsInterfaceName);
        }
        else {
            webview.addJavascriptInterface(jsInterface, jsInterfaceName);
        }

        webview.setWebViewClient(new GingerbreadWebViewClient(jsInterface, jsInterfaceName, jsSignature));
        webview.setWebChromeClient(new GingerbreadWebViewChrome(jsInterface, jsSignature));
    }*/

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private void initialize() {
        mInstance = this;

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.lo_refresh_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onRefresh() {

        String url = mWebView.getUrl();

//        if(url != null && (url.indexOf("/listWorkVisit") != -1 || url.indexOf("/listWork") != -1)){
//            Log.d(TAG, "@ URL  : "+url);
            mWebView.reload();
//        }

        mSwipeRefreshLayout.setRefreshing(false);
    }


    public static class XScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceX) > Math.abs(distanceY);
        }
    }

    /**
     * Handle JS injection and re-injection to fix the Android 2.3 JSInterface bug.
     */
    private class GingerbreadWebViewClient extends WebViewClient {
        private Object jsInterface;
        private String jsInterfaceName;
        private String jsSignature;

        public GingerbreadWebViewClient(Object jsInterface, String jsInterfaceName, String jsSignature) {
            this.jsInterface = jsInterface;
            this.jsInterfaceName = jsInterfaceName;
            this.jsSignature = jsSignature;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            /*
             * URL별로 분기가 필요합니다. 어플리케이션을 로딩하는것과
             * WEB PAGE를 로딩하는것을 분리 하여 처리해야 합니다.
             * 만일 가맹점 특정 어플 URL이 들어온다면
             * 조건을 더 추가하여 처리해 주십시요.
             */

            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
                try {
                    // Starts With String
                    if (url.startsWith("sms:")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
                        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(i);
                        return true;
                    } else if (url.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());

                        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(i);
                        return true;
                    } else if (url.startsWith("mailto:")) {
                        MailTo mt = MailTo.parse(url);
                        Intent i = newEmailIntent(ActWebView.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                        startActivity(i);
                        view.reload();
                        return true;
                    } else if (url.toLowerCase().startsWith("protocol://close")) {
                        finish();
                    } else if (url.toLowerCase().startsWith("protocol://workdate")) {

                    } else if (url.toLowerCase().startsWith("protocol://detail")) {

                    } else if (url.toLowerCase().startsWith("protocol://work_stat")) {
                        System.out.println("INTO work_stat !!!!");
                    }

                    // ends With String
                    if (url.endsWith(".mp3")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url), "audio/*");            // Audio
                        view.getContext().startActivity(intent);
                        return true;
                    } else if (url.endsWith(".mp4") || url.endsWith(".3gp")) {        // Movie
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url), "video/*");
                        view.getContext().startActivity(intent);
                        return true;
                    }

                } catch (Exception e) {
                    showToast(getStringRes(R.string.fail_to_run));
                    return true;
                }

                Intent intent;

                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                } catch (URISyntaxException ex) {
                    Log.e("<INIPAYMOBILE>", "URI syntax error : " + url + ":" + ex.getMessage());
                    return false;
                }

                Uri uri = Uri.parse(intent.getDataString());
                intent = new Intent(Intent.ACTION_VIEW, uri);

                try {

                    startActivity(intent);

	    			/*가맹점의 사정에 따라 현재 화면을 종료하지 않아도 됩니다.
	    			    삼성카드 기타 안심클릭에서는 종료되면 안되기 때문에
	    			    조건을 걸어 종료하도록 하였습니다.*/
                    if (url.startsWith("ispmobile://")) {
                        //finish();
                    }

                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "INIPAYMOBILE, ActivityNotFoundException INPUT >> " + url);
                    Log.e(TAG, "INIPAYMOBILE, uri.getScheme()" + intent.getDataString());
                }

            }

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);

            Debug.d(TAG, "*onPageStarted : " + url);
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
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            if (Utils.isNetworkAvailable(ActWebView.this)) {
                if (mWebView != null) {
                    mWebView.reload();
                }
            } else {
                showToast("Network Error Occur!");
            }

            Debug.d(TAG, "onReceivedError : " + description + " - " + failingUrl);
//			Debug.writeLog("onReceivedError : " + description + " - " + failingUrl);
        }


        @Override
        public void onLoadResource(WebView view, String url) {

            Debug.d(TAG, "onLoadResource L " + url);
        }

        private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_CC, cc);
            intent.setType("message/rfc822");
            return intent;
        }

    }

    /**
     * Handle JS calls to fix the Android 2.3 JSInterface bug.
     */
    private class GingerbreadWebViewChrome extends WebChromeClient {

        public GingerbreadWebViewChrome() {
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @SuppressLint("NewApi")
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
            if (view == null)
                return false;
            try {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    builder = new AlertDialog.Builder(view.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                } else {
                    builder = new AlertDialog.Builder(view.getContext());
                }
                builder.setTitle(R.string.app_name_worker_man);
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            } catch (WindowManager.BadTokenException e) {
                return false;
            }
        }
        ;


        @SuppressLint("NewApi")
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final android.webkit.JsResult result) {
            if (view == null)
                return false;

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                builder = new AlertDialog.Builder(view.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            } else {
                builder = new AlertDialog.Builder(view.getContext());
            }

            builder.setTitle(R.string.app_name_worker_man);
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });
            builder.setNegativeButton(android.R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        // For Android Version < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            Log.d(TAG, "WebViewActivity OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU), n=1");
            mFileCallbackSingle = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(TYPE_IMAGE);
            startActivityForResult(intent, INPUT_FILE_REQUEST_CODE);
        }

        // For 3.0 <= Android Version < 4.1
        public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
            Log.d(TAG, String.format("%s, acceptType:%s", uploadFile.toString(), acceptType));

            //System.out.println("WebViewActivity 3<A<4.1, OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU,aT), n=2");
//            openFileChooser(uploadMsg, acceptType, "");
            openFileInput(uploadFile, null, false, acceptType);
        }

        // For 4.1 <= Android Version < 5.0
        public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
            //mFileCallbackSingle = uploadFile;
            //imageChooser();
            openFileInput(uploadFile, null, false, acceptType);
        }

        // For Android Version 5.0+
        // Ref: https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
        public boolean onShowFileChooser(WebView webView,  ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

            if (Build.VERSION.SDK_INT >= 21) {

                String strAcceptType[] = fileChooserParams.getAcceptTypes();
                if(fileChooserParams.getAcceptTypes() != null){
                    for (int i = 0; i < strAcceptType.length; i++) {
                        Log.d(TAG, String.format("fileChooserParams.getAcceptTypes():[%s]", strAcceptType[i]));
                    }
                }

                final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;
                /*if (mFileCallbackMulti != null) {
                    mFileCallbackMulti.onReceiveValue(null);
                }

                if(mFileCallbackSingle != null){
                    mFileCallbackSingle.onReceiveValue(null);
                }

                mFileCallbackMulti = filePathCallback;*/

                //imageChooser(allowMultiple);
                openFileInput(null, filePathCallback, allowMultiple, strAcceptType);

                return true;
            }else {
                return false;
            }
        }

        private void openFileInput(final ValueCallback<Uri> fileUploadCallbackSingle, final ValueCallback<Uri[]> fileUploadCallbackMulti, final boolean allowMultiple, final String arrAcceptType[]) {

            if(mFileCallbackMulti != null){
                mFileCallbackMulti.onReceiveValue(null);
            }

            mFileCallbackMulti = fileUploadCallbackMulti;

            if(mFileCallbackSingle != null){
                mFileCallbackSingle.onReceiveValue(null);
            }

            mFileCallbackSingle = fileUploadCallbackSingle;

            boolean isImage = false;
            String strAcceptType = "";

            for (int i = 0; i < arrAcceptType.length; i++) {
                if (arrAcceptType[i] != null && arrAcceptType[i].length() != 0) {
                    if(strAcceptType.length() == 0){
                        strAcceptType = arrAcceptType[i];
                    }else{
                        strAcceptType += ";" + arrAcceptType[i];
                    }
                }
            }

            if (strAcceptType.length() == 0)
                strAcceptType = "image/*";

            if("image/*".equals(strAcceptType)){
                isImage = true;
            }

            if(allowMultiple){
                if(isImage == true){
                    openTedMultiImagePicker();
                }else{
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(strAcceptType);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrAcceptType);
                    }
                    startActivityForResult(intent, INPUT_FILE_REQUEST_CODE);
                }
            }else{
                if(isImage) {
                    openTedSingleImagePicker();
                }else{
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(strAcceptType);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrAcceptType);
                    }

                    startActivityForResult(intent, INPUT_FILE_REQUEST_CODE);
                }
            }
        }

        private void openFileInput(final ValueCallback<Uri> fileUploadCallbackSingle, final ValueCallback<Uri[]> fileUploadCallbackMulti, final boolean allowMultiple, final String strAcceptType) {

            if(mFileCallbackMulti != null){
                mFileCallbackMulti.onReceiveValue(null);
            }

            mFileCallbackMulti = fileUploadCallbackMulti;

            if(mFileCallbackSingle != null){
                mFileCallbackSingle.onReceiveValue(null);
            }

            mFileCallbackSingle = fileUploadCallbackSingle;

            boolean isImage = false;

            if("image/*".equals(strAcceptType)){
                isImage = true;
            }

            if(allowMultiple){
                if(isImage) {
                    openTedMultiImagePicker();
                }else{
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(strAcceptType);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, strAcceptType);
                    }
                    startActivityForResult(intent, INPUT_FILE_REQUEST_CODE);
                }
            }else{
                if(isImage){
                    openTedSingleImagePicker();
                }else{
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(strAcceptType);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, strAcceptType);
                    }
                    startActivityForResult(intent, INPUT_FILE_REQUEST_CODE);
                }
            }
        }

        /* window open */
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

            /* 지출결의, IT팀에 요청하기, Help Desk일 경우 system browser open */
            Message href = view.getHandler().obtainMessage();
            view.requestFocusNodeHref(href);
            String url = href.getData().getString("url");
            if(url != null && (url.indexOf("docs.google.com") > -1 || url.indexOf("www.notion.so") > -1)){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                return false; // true 이면 docs.google.com 후 팝업을 열면, docs.google.com 로그인 화면으로 이동하는 bug
            }

            // Dialog Create Code
            WebView newWebView = new WebView(view.getContext());
            WebSettings webSettings = newWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            final Dialog dialog = new Dialog(view.getContext());
            dialog.setContentView(newWebView);

            ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
            dialog.show();

            newWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onCloseWindow(WebView window) {
                    dialog.dismiss();
                }
            });

            // WebView Popup에서 내용이 안보이고 빈 화면만 보여 아래 코드 추가
            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }
            });

            ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;
        }
    }

    private void openTedMultiImagePicker() {
        TedImagePicker.with(this)
                .errorListener(new OnErrorListener() {
                    @Override
                    public void onError(@NotNull String s) {
                        showAlert("ERROR: " + s,  null);
                    }
                })
                .max(50, "최대 50장까지만 선택가능합니다.")
                .startMultiImage(new OnMultiSelectedListener() {
                    @Override
                    public void onSelected(@NotNull List<? extends Uri> uriList) {
                        mUriList = uriList;

                        Uri[] dataUris = new Uri[mUriList.size()];

                        for (int i = 0; i < mUriList.size(); i++) {
                            dataUris[i] = uriList.get(i);
                        }

                        if (dataUris != null) {
                            mFileCallbackMulti.onReceiveValue(dataUris);
                            mFileCallbackMulti = null;
                        } else {
                            if (mFileCallbackMulti != null) mFileCallbackMulti.onReceiveValue(null);
                            mFileCallbackMulti = null;
                        }
                    }

                });

    }

    private void openTedSingleImagePicker(){
        TedImagePicker.with(this)
                .start(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NotNull Uri uri) {
                        if(mFileCallbackSingle != null){
                            if(uri != null){
                                mFileCallbackSingle.onReceiveValue(uri);
                                mFileCallbackSingle = null;
                            }else{
                                if (mFileCallbackSingle != null) mFileCallbackSingle.onReceiveValue(null);
                                mFileCallbackSingle = null;
                            }
                        }
                    }
                });
    }


    @SuppressLint("NewApi")
    public class CountTimer extends CountDownTimer {
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            isExit = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // some script here
        }
    }

    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }





}
