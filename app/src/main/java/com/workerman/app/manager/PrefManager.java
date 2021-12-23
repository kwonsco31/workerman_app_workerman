package com.workerman.app.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.workerman.app.Defines;

import java.util.HashMap;

public class PrefManager {

    private static volatile PrefManager sPrefManager = new PrefManager();

    private PrefManager() {
    }

    public final static String PREFKEY_VERION_LATEST = "Version.latest";
    public final static String PREFKEY_READ_MANUAL = "Manual.Read";

    //    kstar pref start
    public static final String PREFKEY_USER_NO = "kstar.user.no";
    public static final String PREFKEY_LANGUAGE_TYPE = "kstar.language.type";
    public static final String PREFKEY_SNS_USER_ID = "kstar.sns.user.id";
    public static final String PREFKEY_SNS_TYPE = "kstar.sns.type";
    public static final String PREFKEY_USER_EMAIL = "kstar.user.email";
    public static final String PREFKEY_PHONE_ADID = "kstar.phone.adid";
    public static final String PREFKEY_VIEW_ADV_BUTTON = "kstar.view.advertisement.button";
    public static final String PREFKEY_FCM_TOKEN = "kstar.push.fcm.token";
    public static final String PREFKEY_FCM_READ_NOTIFICATION = "workerman.push.fcm.token.read";
//    kstar pref end

    public final static String PREFKEY_AUTO_LOGIN = "Auto.Login.Enable";
    public final static String PREFKEY_VERION_LATEST_SHOW_UPGRADE_POPUP = "Version.latest.show.upgrade.popup";

    public static final String PREFKEY_CATE_INFO = "workerman.category.infomation";
    public static final String PREFKEY_SOCIAL_INFO = "workerman.social.infomation";
    public static final String PREFKEY_TOKEN = "workerman.login.token";
    public static final String PREFKEY_ADMIN_NO = "workerman.login.admin.no";
    public static final String PREFKEY_BANNER_LIST = "workerman.banner.list";
    public static final String PREFKEY_APP_INIT_DATA = "workerman.app.initialize.data";
    public static final String PREFKEY_APP_VOTE_INFO = "workerman.app.vote.infomation";
    public static final String PREFKEY_BUILDING_TYPE = "workerman.app.building.type";
    public static final String PREFKEY_WORK_CATEGORY = "workerman.app.work.category";
    public static final String PREFKEY_APP_TAPJOY_DATA = "workerman.app.tapjoy.data";
    public static final String PREFKEY_BADGE_COUNT = "workerman.badge.count";
    public static final String PREFKEY_EMAIL_AUTH_REQ_TIME = "kstar.email.auth.req.time";
    public static final String PREFKEY_HEADER_COOKIE = "workerman.header.cookie";

    private HashMap<String, String> stringHashMap = new HashMap<String, String>();

    public static PrefManager getInstance() {
        return sPrefManager;
    }

    public synchronized boolean isInstalled(Context context) {
        boolean result = true;


        return result;
    }

    public synchronized String getString(Context context, String key, String defValue) {
        if (stringHashMap.containsKey(key)) {
            return stringHashMap.get(key);
        }
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        String value = pref.getString(key, defValue);
        stringHashMap.put(key, value);
        return value;
    }

    public synchronized long getLong(Context context, String key, long defValue) {
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        return pref.getLong(key, defValue);
    }

    public synchronized int getInt(Context context, String key, int defValue) {
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        return pref.getInt(key, defValue);
    }

    public synchronized boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        return pref.getBoolean(key, defValue);
    }

    public synchronized void putString(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
        stringHashMap.put(key, value);
    }

    public synchronized void putLong(Context context, String key, long value) {
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public synchronized void putInt(Context context, String key, int value) {
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public synchronized void putBoolean(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public synchronized void remove(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

    public synchronized void clear(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        stringHashMap.clear();
    }

    public boolean isGeneralUpgrade(Context context) {
        String latestVersion = PrefManager.getInstance().getString(context, PrefManager.PREFKEY_VERION_LATEST, null);
        if (latestVersion == null || latestVersion.length() <= 0)
            return false;
        String currentVersion = Defines.getAppVersion(context);
        try {
            String[] lastestVersionArray = latestVersion.split("\\.");
            String[] currentVersionArray = currentVersion.split("\\.");
            if (lastestVersionArray.length >= 3 && currentVersionArray.length >= 3) {
                int lastVersion1 = Integer.parseInt(lastestVersionArray[0]);
                int lastVersion2 = Integer.parseInt(lastestVersionArray[1]);
                int lastVersion3 = Integer.parseInt(lastestVersionArray[2]);
                int currentVersion1 = Integer.parseInt(currentVersionArray[0]);
                int currentVersion2 = Integer.parseInt(currentVersionArray[1]);

                int lastVersionInfoLength = currentVersionArray[2].length();
                for (int i = 0; i < lastVersionInfoLength; i++) {
                    char c = currentVersionArray[2].charAt(i);
                    if (c < '0' || c > '9') {
                        currentVersionArray[2] = currentVersionArray[2].substring(0, i);
                        break;
                    }
                }

                int currentVersion3 = Integer.parseInt(currentVersionArray[2]);
                if (lastVersion1 > currentVersion1)
                    return true;
                if (lastVersion1 == currentVersion1 && lastVersion2 > currentVersion2)
                    return true;
                if (lastVersion1 == currentVersion1 && lastVersion2 == currentVersion2 && lastVersion3 > currentVersion3)
                    return true;
            }
        } catch (Exception e) {
            //Debug.e("PrefManager", "isGereralUpgrade", e);
        }

        return false;
    }

    public boolean isViewGeneralUpgradePopup(Context context) {
        String latestVersion = PrefManager.getInstance().getString(context, PrefManager.PREFKEY_VERION_LATEST, null);
        if (latestVersion == null || latestVersion.length() <= 0)
            return false;
        String showUpgradeVersion = PrefManager.getInstance().getString(context, PrefManager.PREFKEY_VERION_LATEST_SHOW_UPGRADE_POPUP, null);
        if (isGeneralUpgrade(context) && (showUpgradeVersion == null || !showUpgradeVersion.equals(latestVersion)))
            return true;
        return false;
    }

    public void showGeneralUpgradePopup(Context context) {
        String latestVersion = PrefManager.getInstance().getString(context, PrefManager.PREFKEY_VERION_LATEST, null);
        if (latestVersion != null && latestVersion.length() > 0)
            PrefManager.getInstance().putString(context, PrefManager.PREFKEY_VERION_LATEST_SHOW_UPGRADE_POPUP, latestVersion);
    }
}
