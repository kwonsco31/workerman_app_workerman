package com.workerman.app;

import android.app.Application;

import com.workerman.app.manager.ConfigManager;

public class WorkerManApplication extends Application {
    public static final String TAG = WorkerManApplication.class.getSimpleName();

    private static WorkerManApplication mInstance;

    @Override
    public void onCreate() {

//		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//			@Override
//			public void uncaughtException(Thread thread, final Throwable ex) {
//
//				new Thread() {
//					@Override
//					public void run() {
//						// UI쓰레드에서 토스트 뿌림
//						Looper.prepare();
//						Toast.makeText(getApplicationContext(), "비정상 종료. 재시작합니다.", Toast.LENGTH_SHORT)
//								.show();
//						Looper.loop();
//					}
//				}.start();
//
//				try {
//					Thread.sleep(1500);
//				} catch (InterruptedException e) {
//				}
//
//				Intent crashedIntent = new Intent(SWFoodApplication.this, WebViewActivity.class);
//				crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//				crashedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(crashedIntent);
//
//				System.exit(0);
//			}
//		});

        super.onCreate();

        //IGAWorks
//        IgawCommon.autoSessionTracking(WorkerManApplication.this);

        mInstance = this;

        ConfigManager.initContext(this);

        init();

//        initTwitter();

    }

    /*private void initTwitter() {
        *//*TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))//pass the created app Consumer KEY and Secret also called API Key and Secret
                .debug(true)//enable debug mode
                .build();

        //finally initialize twitter with created configs
        Twitter.initialize(config);*//*
        Twitter.initialize(this);
    }*/

    public static synchronized WorkerManApplication getInstance() {
        return mInstance;
    }

    private void init() {
        /*SocialLogin.init(this);
        Map<SocialType, SocialConfig> availableTypeMap = new HashMap<>();

        KakaoConfig kakaoConfig = new KakaoConfig.Builder()
                .setRequireEmail()
                .setRequireAgeRange()
                .setRequireBirthday()
                .setRequireGender()
                .build();
        SocialLogin.addType(SocialType.KAKAO, kakaoConfig);

        availableTypeMap.put(SocialType.KAKAO, kakaoConfig);*/

        /*FacebookConfig facebookConfig = new FacebookConfig.Builder()
                .setApplicationId("123107718379169")
                .setRequireEmail()
                .build();
        availableTypeMap.put(SocialType.FACEBOOK, facebookConfig);

        NaverConfig localNaverConfig = new NaverConfig.Builder()
                .setAuthClientId("woR7sft4ZoNQBs7els0c")
                .setAuthClientSecret("CzRiDELLyE")
//                .setClientName(getString(2131558431))
                .build();
        availableTypeMap.put(SocialType.NAVER, localNaverConfig);*/

//        SocialLogin.init(this, availableTypeMap);
        /*SocialLogin.init(this);
        KakaoConfig kakaoConfig = new KakaoConfig.Builder()
                .setRequireEmail()
                .setRequireGender()
                .setRequireBirthday()
                .setRequireAgeRange()
                .build();

        SocialLogin.addType(SocialType.KAKAO, kakaoConfig);*/

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void onKillProcess() {
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
