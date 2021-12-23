package com.workerman.app.fcm;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.workerman.app.Defines;
import com.workerman.app.R;
import com.workerman.app.manager.PrefManager;
import com.workerman.app.net.APICallService;
import com.workerman.app.service.LocationUpdatesService;
import com.workerman.app.ui.activity.ActSplash;
import com.workerman.app.utils.Debug;
import com.workerman.app.utils.Utils;
import com.workerman.app.vo.WorkerMemberPushData;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Iterator;
import java.util.Map;


/**
 * Created by bada
 */

public class WorkerManFirebaseMessagingService extends FirebaseMessagingService implements LocationListener {
    private static final String TAG = "WorkerManFirebaseMessag";
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private boolean mBound = false;
    private LocationUpdatesService mService = null;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            Log.d(TAG,"@ bind onServiceConnected");
            mService = binder.getService();
            mBound = true;
            mService.requestLocationUpdates();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Debug.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Debug.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();

            Iterator<String> keys = data.keySet().iterator();
            String full_param = "";
            while( keys.hasNext() ){
                String key = keys.next();
                Debug.d(TAG, String.format("키 : %s, 값 : %s", key, data.get(key)) );

                if("".equals(full_param)){
                    full_param = String.format("?%s=%s",key, data.get(key));
                }else{
                    full_param += String.format("&%s=%s",key, data.get(key));
                }
            }

            Gson gson = new Gson();
            String strMapData = gson.toJson(data);
            WorkerMemberPushData pushData = null;
            if(!Utils.isEmpty(strMapData)){
                pushData = gson.fromJson(strMapData, WorkerMemberPushData.class);
                pushData.setSendTime(Long.toString(System.currentTimeMillis()));
            }

            /*if(pushData != null){
                WorkerMemberInfo memberInfo = getMemberInfo();

                if(memberInfo != null){
                    memberInfo.setPushData(pushData);
                    setMemberInfo(memberInfo);
                }
            }*/

            String title = data.get("title");
            String badge = data.get("badge_count");
            String body = data.get("body");
            String workNo = data.get("work_no");
            String actionType = data.get("action_type");

            if(actionType != null && actionType.equals("req_location")){ // 위치찾기

                Handler mHandler = new Handler(Looper.getMainLooper());

                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //getGps();
                        bindService(new Intent(WorkerManFirebaseMessagingService.this, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
                        Log.d(TAG,"@ bind start");
                        if(mService != null){
                            mService.requestLocationUpdates();
                        }
                    }

                }, 0);

            }else{
                if (pushData != null) {
                    showNotification(pushData);
                } else {

                    if (!StringUtils.isEmpty(body) && !StringUtils.isEmpty(title)) {
                        showNotification(title, body);
                    } else {
                        if (!StringUtils.isEmpty(body)) {
                            showNotification("WorkerShop", body);
                        }
                    }
                }
            }

        }

        // Check if message contains a notification payload.
        /*if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification noti = remoteMessage.getNotification();

            if(!StringUtils.isEmpty(noti.getBody()) && !StringUtils.isEmpty(noti.getTitle())){
                showNotification(noti.getTitle(), noti.getBody());
            }else{
                if(!StringUtils.isEmpty(noti.getBody())){
                    showNotification("Kstar", noti.getBody());
                }
            }

            Map<String, String> data = remoteMessage.getData();
            String badge = data.get("badge");
            if(!StringUtils.isEmpty(badge)){
                setBadgeCount(badge);
            }
        }*/


//        showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
    }

    private void setBadgeCount(String _badgeCount){
        int badge_count = 0;
        try {
            badge_count = Utils.toInt(_badgeCount);
        }catch (NumberFormatException e){
            Debug.d(TAG, "NumberFormatException : " + e.getMessage());
            Number number = NumberUtils.createNumber(_badgeCount);
            badge_count = number.intValue();
        }

        PrefManager.getInstance().putInt(this, PrefManager.PREFKEY_BADGE_COUNT, badge_count);
//        ShortcutBadger.applyCount(this, badge_count);
    }

    private void showNotification(String title, String message) {
        Intent intent = new Intent(this, ActSplash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Defines.INTENT_KEY_FROM_PUSH, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        /*NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);*/
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void showNotification(WorkerMemberPushData _pushData) {
        Intent intent = new Intent(this, ActSplash.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Defines.INTENT_KEY_FROM_PUSH, true);
        intent.putExtra(Defines.INTENT_KEY_PUSH_DATA, _pushData);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        /*NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);*/
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(_pushData.getTitle())
                        .setContentText(_pushData.getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }

    // 위치 찾기 관련 이하
    private LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    @SuppressLint("MissingPermission")
    public void getGps(){

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d(TAG, "isGPSEnabled : "+isGPSEnabled);
        if (isGPSEnabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            if (locationManager != null){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,1,this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1,this);
            }
        }
    }

    @Override
    public void onLocationChanged(final Location location)
    {
        Log.d(TAG, "onLocationChanged");

        if(location != null){
            //  API CALL

            APICallService callService = new APICallService(this);
            callService.gspUpload(location);
            stopUsingGPS();
            mService.removeLocationUpdates();
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d(TAG, "onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.d(TAG, "onProviderEnabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){ Log.d(TAG, "onStatusChanged : "+status); }

    public void stopUsingGPS(){
        if(locationManager != null)
        {
            locationManager.removeUpdates(this);
        }
    }



}

