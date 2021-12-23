package com.workerman.app.manager;

import android.content.Context;
import android.os.Environment;

import com.workerman.app.Defines;
import com.workerman.app.utils.FileCache;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ConfigManager {
    private static String TAG = ConfigManager.class.getSimpleName();

    private static Context mContext = null;

    public static boolean debug = false;

    public static String appVersion = "";

    public static int theme = 0;

    public static String pushId = "";


    private static FileCache bitmapCache;

    public static String deviceId;

   /* public static String getDeviceId() {
        String result = null;
        if (deviceId == null) {
            TelephonyManager telManager = (TelephonyManager) getMainContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId1 = "0";
            if (telManager != null) {
                deviceId1 = telManager.getDeviceId();
                if (deviceId1 == null) {
                    deviceId1 = "0";
                }
            }
            if (deviceId1.equals("0")) {
                int version = android.os.Build.VERSION.SDK_INT;

                if (version >= 8) {
                    deviceId1 = Secure.getString(ConfigManager.getMainContext()
                            .getContentResolver(), Secure.ANDROID_ID);
                    if (deviceId1 == null)
                        deviceId1 = "0";
                }
            }

            result = makeMD5(deviceId1);
        } else {
            result = deviceId;
        }

        return result;
    }*/

    public static Context getMainContext() {
        return mContext;
    }

    public static void initContext(Context context) {
        mContext = context;
        Defines.GOOGLE_MARKET_URI = "market://details?id="
                + ConfigManager.getMainContext().getPackageName();

        appVersion = Defines.getAppVersion(context);

        bitmapCache = new FileCache(context, 20);

    }

    private static MessageDigest di = null;

    public static String makeMD5(String str) {
        if (di == null) {
            try {
                di = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        di.reset();
        di.update(str.getBytes());

        StringBuilder sb = null;
        String returnString = null;
        try {
            byte[] md5Code = di.digest();
            sb = new StringBuilder();
            synchronized (di) {
                for (int i = 0; i < md5Code.length; i++) {
                    sb.append(String.format("%02x", md5Code[i]));
                }
            }

            returnString = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sb = null;
        }

        return returnString;
    }

    public static String getTmpPath(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Defines.TMP_PATH;

        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return path;
    }
}