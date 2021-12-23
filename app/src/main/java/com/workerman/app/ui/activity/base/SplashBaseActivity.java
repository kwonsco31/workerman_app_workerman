package com.workerman.app.ui.activity.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.workerman.app.Defines;
import com.workerman.app.R;
import com.workerman.app.manager.PrefManager;
import com.workerman.app.net.APIClient;
import com.workerman.app.net.APIService;
import com.workerman.app.ui.activity.ActSplash;
import com.workerman.app.ui.widget.WorkerManDialog;
import com.workerman.app.utils.Debug;
import com.workerman.app.vo.WifiInformation;

import org.apache.commons.lang3.StringUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by bada
 */

public class SplashBaseActivity extends AppCompatActivity {
    private static final String TAG = "SplashBaseActivity";
    private Toast mToast;

    protected WorkerManDialog mAlertDialog;
    protected WorkerManDialog mConfirmDialog;
    protected Dialog mLoadingDialog;

    protected APIService mService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        changeConfigulation(getLanguageSet());
        initialize();
    }

    private void initialize() {
        mService = APIClient.getClient(this).create(APIService.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if(Defines.USE_ROOTING_CHECK) {
            RootBeer rootBeer = new RootBeer(this);
            if (rootBeer.isRooted()) {
                //we found indication of root
                showAlert(getStringRes(R.string.splash_error_use_emulator), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        }*/
    }

    protected void showToast(CharSequence _message) {
        if (!StringUtils.isEmpty(_message)) {

            if (mToast == null) {
                mToast = Toast.makeText(this, String.format("%s", _message), Toast.LENGTH_SHORT);
            } else {
                mToast.setText(_message);
            }

            mToast.show();
        }
    }

    protected void showLog( CharSequence _message) {

        if (!StringUtils.isEmpty(_message)) {
            Debug.d(TAG, _message.toString());
        }
    }

    private View.OnClickListener alertConfirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismissDialog();
        }
    };

    private View.OnClickListener confirmCalcel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismissDialog();
        }
    };


    protected void showAlert(String _message, View.OnClickListener _confirm) {

        dismissDialog();

        if(StringUtils.isEmpty(_message)) {
            return;
        }

        if (_confirm == null) {
            _confirm = alertConfirm;
        }

        mAlertDialog = new WorkerManDialog(SplashBaseActivity.this, _message, _confirm);

        if(!isFinishing()) {
            mAlertDialog.show();
        }
    }

    protected WorkerManDialog makeConfirm(String _message, View.OnClickListener cancel, View.OnClickListener confirm) {
//가져 간후 show()할것.

        if (cancel == null) {
            cancel = confirmCalcel;
        }

        dismissDialog();

        mConfirmDialog = new WorkerManDialog(this, _message, cancel, confirm);
        return mConfirmDialog;
    }

    protected WorkerManDialog makeConfirm(String _message, View.OnClickListener cancel, View.OnClickListener confirm, String _left, String _right) {
//가져 간후 show()할것.

        if (cancel == null) {
            cancel = confirmCalcel;
        }

        mConfirmDialog = new WorkerManDialog(this, _message, cancel, confirm, _left, _right);

        return mConfirmDialog;
    }

    protected WorkerManDialog makeConfirm(String _message, View.OnClickListener cancel, View.OnClickListener _centerListener, View.OnClickListener confirm, String _left, String _center, String _right) {
//가져 간후 show()할것.

        if (cancel == null) {
            cancel = confirmCalcel;
        }

        mConfirmDialog = new WorkerManDialog(this, _message, cancel, _centerListener, confirm, _left, _center, _right);
        return mConfirmDialog;
    }

    protected void showProgress(String _message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mLoadingDialog != null && mLoadingDialog.isShowing()){
                    mLoadingDialog.dismiss();
                }

                mLoadingDialog = new Dialog(SplashBaseActivity.this, R.style.TransProgressDialog);
                mLoadingDialog.setCancelable(false);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.progress_circle, null);

                if (!StringUtils.isEmpty(_message)) {
                    TextView txtLoadingMessage = (TextView) v.findViewById(R.id.txtLoadingMsg);
                    txtLoadingMessage.setText(_message);
                    txtLoadingMessage.setVisibility(View.VISIBLE);
                }

                mLoadingDialog.setContentView(v);

                if (!isFinishing())
                    mLoadingDialog.show();
            }
        });
    }

    protected void dismissPrgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog != null) {
                    if (mLoadingDialog.isShowing())
                        mLoadingDialog.dismiss();
                }
            }
        });
    }

    protected String getStringRes(int _resId) {

        String result = "";
        try {
            result = getResources().getString(_resId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    protected int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();

        if (mToast != null)
            mToast = null;
        if(mService != null){
            mService = null;
        }
    }


    /*protected void changeConfigulation(String _locale) {
        Debug.d(TAG, String.format("changeConfigulation: :%s", _locale));
        Locale locale = new Locale(_locale);
//        if(_locale.equals(Locale.KOREA.getLanguage())){
//            locale = Locale.KOREA;
//        }else{
//            locale = Locale.US;
//        }
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }*/

    protected boolean isOnline() {
        CheckConnect cc = new CheckConnect(Defines.CONNECTION_CONFIRM_CLIENT_URL);
        cc.start();
        boolean result = false;
        try {
            cc.join();
            result = cc.isSuccess();
            if (result == false) {
                showToast(getStringRes(R.string.fail_to_connect_network));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(String.format("%s:%s", getStringRes(R.string.fail_to_connect_network), e.getMessage()));
        }
        return result;
    }

    private class CheckConnect extends Thread {
        private boolean success;
        private String host;

        public CheckConnect(String host) {
            this.host = host;
        }

        @Override
        public void run() {

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(host).openConnection();
                conn.setRequestProperty("User-Agent", "Android");
                conn.setConnectTimeout(1000);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode == 204) success = true;
                else success = false;
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        public boolean isSuccess() {
            return success;
        }

    }


    protected WifiInformation getWifiInfo() {
        // wifi 가 연결 됬는지 확인 하는것
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean wificon = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

        if (wificon == false) {
            return null;  // 연결이 됬는지 확인
        }
        // wifi 정보 가져오기
        WifiManager wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        DhcpInfo dhcpInfo = wifimanager.getDhcpInfo();
        WifiInfo wifiInfo = wifimanager.getConnectionInfo();

        int wIp = dhcpInfo.ipAddress;
        int wNetmask = dhcpInfo.netmask;
        int wGateway = dhcpInfo.gateway;
        int wDns1 = dhcpInfo.dns1;

        String ip = String.format("%d.%d.%d.%d", (wIp & 0xff), (wIp >> 8 & 0xff), (wIp >> 16 & 0xff), (wIp >> 24 & 0xff));
        String subnet = String.format("%d.%d.%d.%d", (wNetmask & 0xff), (wNetmask >> 8 & 0xff), (wNetmask >> 16 & 0xff), (wNetmask >> 24 & 0xff));
        String gateway = String.format("%d.%d.%d.%d", (wGateway & 0xff), (wGateway >> 8 & 0xff), (wGateway >> 16 & 0xff), (wGateway >> 24 & 0xff));
        String dns = String.format("%d.%d.%d.%d", (wDns1 & 0xff), (wDns1 >> 8 & 0xff), (wDns1 >> 16 & 0xff), (wDns1 >> 24 & 0xff));
        String ssid = wifiInfo.getSSID().replace("\"", "");
        String mac_address = wifiInfo.getMacAddress();
        WifiInformation info = new WifiInformation(ip, subnet, gateway, dns, ssid, mac_address);

        Debug.d(TAG, String.format("ip:[%s]\nsubnet:[%s]\ngateway:[%s]\ndns:[%s]\nssid:[%s]", ip, subnet, gateway, dns, ssid));
        return info;
    }

    protected String getLanguageSet() {
        String strLangType = getSystemLanguageSet();

        if("1".equals(strLangType)){
            //ko
            return Locale.KOREA.getLanguage();
        }else if("2".equals(strLangType)){
//            en
            return Locale.ENGLISH.getLanguage();
        }else if("3".equals(strLangType)){
            //zh
            return Locale.CHINA.getLanguage();
        }else{
            //jp
            return Locale.JAPAN.getLanguage();
        }
    }

    public String getSystemLanguageSet(){
        Locale systemLocale = getResources().getConfiguration().locale;
        String strLanguage = Locale.getDefault().getDisplayLanguage();

        String defaultSytemLanguageType = "1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e("Locale", getCurrentLocale().getLanguage());
            strLanguage = getCurrentLocale().getLanguage();
        } else {
            Log.e("Locale", getCurrentLocale2().toString());
            strLanguage = getCurrentLocale2().toString();
        }

        if(strLanguage.equalsIgnoreCase(Locale.KOREA.getLanguage())){
            defaultSytemLanguageType = "1";
        }else if(strLanguage.equalsIgnoreCase(Locale.ENGLISH.getLanguage())){
            defaultSytemLanguageType = "2";
        }else if(strLanguage.equalsIgnoreCase(Locale.CHINA.getLanguage())){
            defaultSytemLanguageType = "3";
        }else if(strLanguage.equalsIgnoreCase(Locale.JAPAN.getLanguage())){
            defaultSytemLanguageType = "4";
        }else{
            defaultSytemLanguageType = "2";
        }

        String strLangType = PrefManager.getInstance().getString(getApplicationContext(), PrefManager.PREFKEY_LANGUAGE_TYPE, defaultSytemLanguageType);

        return strLangType;
    }

    @SuppressWarnings("deprecation")
    public String getCurrentLocale2() {
        return Resources.getSystem().getConfiguration().locale.getLanguage();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale() {
        getResources();
        return Resources.getSystem().getConfiguration().getLocales().get(0);
    }

    protected void dismissDialog(){
        if(mConfirmDialog != null){
            mConfirmDialog.dismiss();
            mConfirmDialog = null;
        }

        if(mAlertDialog != null){
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        if(mLoadingDialog != null){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    /*protected AppIntializeData getAppInfo(){

        AppIntializeData appInitData = null;
        try {
            String strAppData = PrefManager.getInstance().getString(getApplicationContext(), PrefManager.PREFKEY_APP_INIT_DATA, "");

            Gson gson = new Gson();
            if (!StringUtils.isEmpty(strAppData)) {
                appInitData = gson.fromJson(strAppData, AppIntializeData.class);
                if(appInitData.getBanner() != null){
                    appInitData.getBanner();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appInitData;
    }

    protected void setAppInfo(AppIntializeData _initData){
        if(_initData != null) {
            try {
                Gson gson = new Gson();
                if(StringUtils.isEmpty(_initData.getUserinfo().getIsDeveloper())){
                    _initData.getUserinfo().setIsDeveloper("NO");
                }

                String strAppData = gson.toJson(_initData);
                PrefManager.getInstance().putString(getApplicationContext(), PrefManager.PREFKEY_APP_INIT_DATA, strAppData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    protected String getStrVoteInfo(){
        String strVoteInfo = PrefManager.getInstance().getString(getApplicationContext(), PrefManager.PREFKEY_APP_VOTE_INFO, "");
        return strVoteInfo;
    }

    /*protected VoteInfomation getVoteInfo(){

        VoteInfomation voteInfo = null;
        try {
            String strVoteInfo = PrefManager.getInstance().getString(getApplicationContext(), PrefManager.PREFKEY_APP_VOTE_INFO, "");

            Gson gson = new Gson();
            if (!StringUtils.isEmpty(strVoteInfo)) {
                voteInfo = gson.fromJson(strVoteInfo, VoteInfomation.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return voteInfo;
    }

    protected void setVoteInfo(VoteInfomation _voteInfo){
        if(_voteInfo != null) {
            try {
                Gson gson = new Gson();
                String strVoteInfo = gson.toJson(_voteInfo);
                PrefManager.getInstance().putString(getApplicationContext(), PrefManager.PREFKEY_APP_VOTE_INFO, strVoteInfo);
                File dir = FileManager.getDir();
                File file = FileManager.getFile();
                if(!file.exists()){
                    FileManager.makeFile(dir, FileManager.getFilePath());
                }
                JSONObject json = new JSONObject();
                json.put("data", ShaPasswordEncoder.AES_Encode(strVoteInfo));

                FileManager.writeFile(file, json.toString().getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    protected void startCheckServerConnection() {
        Intent crashedIntent = new Intent(this, ActSplash.class);
        crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        crashedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(crashedIntent);
        finish();
    }

   /* protected void setUserTicketCount(int _ticket){
        AppIntializeData appInfo = getAppInfo();
        appInfo.getUserinfo().setTicketCount(_ticket);
        setAppInfo(appInfo);
    }*/

    protected void setToken(String _token) {
        Defines.userToken = _token;
        PrefManager.getInstance().putString(getApplicationContext(), PrefManager.PREFKEY_TOKEN, _token);
    }

    protected String getToken() {
        String strToken = "";
        strToken = PrefManager.getInstance().getString(getApplicationContext(), PrefManager.PREFKEY_TOKEN, "");
        Defines.userToken = strToken;
        return strToken;
    }

    /*protected void setBuildingList(ResponseBuildList.BuildingList _building) {
        if(_building != null) {
            try {
                Gson gson = new Gson();
                String strBuilding = gson.toJson(_building);
                PrefManager.getInstance().putString(getApplicationContext(), PrefManager.PREFKEY_BUILDING_TYPE, strBuilding);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    /*protected ResponseBuildList.BuildingList getBuildingList() {
        ResponseBuildList.BuildingList buildingList = null;
        try {
            String strBuilding = PrefManager.getInstance().getString(getApplicationContext(), PrefManager.PREFKEY_BUILDING_TYPE, "");

            Gson gson = new Gson();
            if (!StringUtils.isEmpty(strBuilding)) {
                buildingList = gson.fromJson(strBuilding, ResponseBuildList.BuildingList.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buildingList;
    }

    protected void setCategoryList(ResponseWorkCategory.CategoryList _category) {
        if(_category != null) {
            try {
                Gson gson = new Gson();
                String strCategory = gson.toJson(_category);
                PrefManager.getInstance().putString(getApplicationContext(), PrefManager.PREFKEY_WORK_CATEGORY, strCategory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected ResponseWorkCategory.CategoryList getCategoryList() {
        ResponseWorkCategory.CategoryList categoryList = null;
        try {
            String strCategoryList = PrefManager.getInstance().getString(getApplicationContext(), PrefManager.PREFKEY_WORK_CATEGORY, "");

            Gson gson = new Gson();
            if (!StringUtils.isEmpty(strCategoryList)) {
                categoryList = gson.fromJson(strCategoryList, ResponseWorkCategory.CategoryList.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryList;
    }*/

}
