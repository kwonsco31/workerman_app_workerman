package com.workerman.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.workerman.app.Defines;
import com.workerman.app.R;
import com.workerman.app.manager.PrefManager;
import com.workerman.app.net.APIClient;
import com.workerman.app.net.APIService;
import com.workerman.app.service.LocationUpdatesService;
import com.workerman.app.ui.activity.base.SplashBaseActivity;
import com.workerman.app.utils.DeviceUuidFactory;
import com.workerman.app.utils.Utils;
import com.workerman.app.vo.WorkerMemberPushData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;

public class ActSplash extends SplashBaseActivity {
    private static final String TAG = "ActSplash";

    private CountDownTimer mCountTimer;
//    private RootBeer mRootBeer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        //IGAWorks
//        IgawAdbrix.firstTimeExperience("execApp");
        Intent intent = getIntent();
        if(intent != null){
            boolean isFromPush = intent.getBooleanExtra(Defines.INTENT_KEY_FROM_PUSH, false);
            boolean activityRunning = (ActWebView.getInstance() == null) ? false : true;
            //app이 실행중이 아니고 푸시메시지로 들어왔을 경우에만 즉시 메인으로 접속하고 그렇지 않을 경우에는 일반 경로로 진입
            if(isFromPush == false){
                setDefaultData();
            }else{
                if(activityRunning == false){
                    setDefaultData();
                }else{
                    goWorkerManMain();
                }
            }
        }else{
            setDefaultData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountTimer != null) {
            mCountTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        //do noting
    }

    private void setDefaultData() {

//        mRootBeer = new RootBeer(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if ((
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

            ) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showAlert(getStringRes(R.string.request_permission_message), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                        requestPermission();
                    }
                });

                return;
            }
        } else {
            if ((
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

            ) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showAlert(getStringRes(R.string.request_permission_message), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                        requestPermission();
                    }
                });

                return;
            }
        }


        mCountTimer = new CountTimer(2000, 1000);

        getToken();

        getSetFcmToken();

        if(Defines.RELEASE_TYPE.equals("DEV")) {
            mConfirmDialog = makeConfirm("API/WEB 호출 경로를 선택하세요", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //개발
                    Defines.setDefaultUrl(true);
                    mService = APIClient.getClient(ActSplash.this).create(APIService.class);
                    initialize();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //운영
                    Defines.setDefaultUrl(false);
                    mService = APIClient.getClient(ActSplash.this).create(APIService.class);
                    initialize();
                }
            }, "개발", "운영");
            mConfirmDialog.show();
        }else{
            initialize();
        }
//        startGetAppInfo();
//        Debug.d(TAG, "uuid : [" + getDeviceUuid() + "]");

    }

    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";
    protected volatile static UUID uuid;

    private UUID getDeviceUuid() {
        if (uuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        // Use the ids previously computed and stored in the
                        // prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        // Use the Android ID unless it's broken, in which case
                        // fallback on deviceId,
                        // unless it's not available, then fallback on a random
                        // number which we store to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId
                                        .getBytes("utf8"));
                            } else {
                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermission();
                                } else {
                                    final String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                                    uuid = deviceId != null ? UUID
                                            .nameUUIDFromBytes(deviceId
                                                    .getBytes("utf8")) : UUID
                                            .randomUUID();
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Write the value out to the prefs file
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                    }
                }
            }
        }

        return uuid;
    }

    private void getSetFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(ActSplash.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                PrefManager.getInstance().putString(getApplicationContext(), PrefManager.PREFKEY_FCM_TOKEN, newToken);
            }
        });
    }

    /*private void makeFolderAndFile() {
        //폴더 생성
//        File dir = FileManager.getDir();
        File file = FileManager.getFile();

        if (file.exists()) {
            //파일이 있음. 읽어와서 Preference에 설정
            readFile(file);
        } else {
            //파일이 없음 UUID생성하여 파일내에 쓰고 Prefrence에 저장
            VoteInfomation voteInfo = getVoteInfo();

            if (voteInfo == null) {
                String uuid = StringUtils.isEmpty(getDeviceUuid().toString()) ? UUID.randomUUID().toString() : getDeviceUuid().toString();
                voteInfo = new VoteInfomation();
                voteInfo.setUuid(uuid);
                voteInfo.setTodayMovieViewCount(0);
            } else {
                String uuid = voteInfo.getUuid();
                voteInfo.setUuid(uuid);
            }
            setVoteInfo(voteInfo);
        }
    }*/

    /*private void startGetCategoryInfo() {
        HashMap<String, String> params = Utils.getDefaultParam(this);

        Call<ResponseCategoryInfo> call = mService.getCategory(params);

        if (Defines.USE_ENCODING) {
            call = mService.getCategory(Utils.encodeParam(params));
        }
        call.enqueue(new Callback<ResponseCategoryInfo>() {
            @Override
            public void onResponse(Call<ResponseCategoryInfo> call, final retrofit2.Response<ResponseCategoryInfo> response) {
                dismissPrgress();
                try {
                    if (response != null) {

                        if ("0".equals(response.body().getCode())) {
                            if (response.body().getResult() != null) {
                                saveCategory(response.body().getResult());
                                goNextStep();
                            }
                        } else {
                            showAlert(response.body().getMessage(), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //                                finish();
                                    goNextStep();
                                }
                            });
                        }
                    }
                } catch (Exception e) {

                    Debug.d(TAG, "e:[" + e.getMessage() + "]");
                    Debug.d(TAG, String.format("response code:%d, response raw code:%d, response body[%s]", response.code(), response.raw().code(), response.raw().body().toString()));


                    if (response.code() == 404) {
                        startCheckServerConnection2();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseCategoryInfo> call, Throwable t) {
                dismissPrgress();
                call.cancel();
                Log.e(TAG, "onFailure: " + t.getMessage());
                goNextStep();
            }
        });
    }*/

    private void initialize() {

        if(mCountTimer != null){
            mCountTimer.start();
        }
    }

    private void goWorkerManMain() {
        Intent intent = null;
        Intent pushIntent = getIntent();
        Bundle bundle = getIntent().getExtras();
        //app running check 앱이 실행중이 아니면 false
        boolean needFinish = (ActWebView.getInstance() == null ) ? false : true;

        String strRead = PrefManager.getInstance().getString(ActSplash.this, PrefManager.PREFKEY_FCM_READ_NOTIFICATION, "");

        boolean isReaded = false;
        WorkerMemberPushData pushData = null;
        //recent check
        if(!Utils.isEmpty(strRead)){
            if(bundle != null){

                //app이 켜져 있을 때,
                pushData = (WorkerMemberPushData) bundle.getSerializable(Defines.INTENT_KEY_PUSH_DATA);

                //app이 꺼져 있었을 경우, 다르게 데이터가 들어옴
                if(pushData == null){
                    JSONObject json = new JSONObject();
                    try {
                        for (String key: bundle.keySet())
                        {
                            json.put(key, bundle.get(key));
                            Log.d (TAG, key + " is a key in the bundle");
                            Log.d (TAG, String.format("%s ",bundle.get(key)) + " is a value in the bundle");
                        }
                    }catch (JSONException e){
                        Log.e(TAG, "onCreate: " + e.getMessage() );
                    }

                    if(json != null){
                        Gson gson = new Gson();
                        pushData  = gson.fromJson(json.toString(), WorkerMemberPushData.class);

                        try {
                            if(json.get("google.sent_time") != null){
                                pushData.setSendTime(json.get("google.sent_time").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                if(pushData != null) {
                    if (strRead.indexOf(pushData.getSendTime()) > -1) {
                        isReaded = true;
                    }
                }
            }
        }

        if(bundle != null && isReaded == false){
            pushIntent.putExtra(Defines.INTENT_KEY_FROM_PUSH_NEED_FINISH, needFinish);
            pushIntent.setClass(this, ActWebView.class);
            intent = pushIntent;
            if(pushData != null) {

                if (Utils.isEmpty(strRead)) {
                    strRead = pushData.getSendTime();
                } else {
                    if(strRead.length() > 1000){
                        strRead = pushData.getSendTime();
                    }else{
                        strRead += ", " + pushData.getSendTime();
                    }
                }

                PrefManager.getInstance().putString(ActSplash.this, PrefManager.PREFKEY_FCM_READ_NOTIFICATION, strRead);

                String strLandingUrl = Defines.WORKERMAN_LANDING_URL +pushData.getAdmin_no();
                intent.putExtra(Defines.INTENT_KEY_LANDING_URL, strLandingUrl);
            }

        }else{
            intent = new Intent(this, ActWebView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        intent.putExtra(Defines.INTENT_KEY_URL, Defines.WEBVIEW_MAIN_URL);

        startActivity(intent);

        finish();
    }

    @SuppressLint("NewApi")
    public class CountTimer extends CountDownTimer {
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            goWorkerManMain();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // some script here
        }
    }

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                setDefaultData();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                setDefaultData();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setRationaleTitle("")
                    .setRationaleMessage("")
                    .setDeniedTitle("")
                    .setDeniedMessage("")
                    .setGotoSettingButtonText("")
                    .setPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.READ_PHONE_NUMBERS
                            , Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    .check();
        }else{
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setRationaleTitle("")
                    .setRationaleMessage("")
                    .setDeniedTitle("")
                    .setDeniedMessage("")
                    .setGotoSettingButtonText("")
                    .setPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.READ_PHONE_NUMBERS
                            , Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .check();
        }


    }

}
