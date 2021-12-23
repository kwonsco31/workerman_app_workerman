package com.workerman.app.net;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.workerman.app.Defines;
import com.workerman.app.manager.PrefManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APICallService {

    private String TAG = "APICallService";

    private Gson gson;
    private Retrofit retrofit;
    private APIService service;

    private Context context;

    protected APIService mService;

    public APICallService(Context _context){
        this.context = _context;

        if (gson == null) {
            gson = new GsonBuilder()
                    .setLenient()
                    .create();
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Defines.REQ_DOMAIN) // 서버 호스트
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        if( service == null){
            service = retrofit.create(APIService.class);
        }

        mService = APIClient.getClient(_context).create(APIService.class);
    }

    public void gspUpload(Location location) {

        String admin_no = PrefManager.getInstance().getString(context, PrefManager.PREFKEY_ADMIN_NO, "");

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("admin_no", admin_no);
        param.put("latitude", location.getLatitude());
        param.put("longitude", location.getLongitude());

        Log.d(TAG, "@ GSP UPLOAD : "+param.toString());

        Call<Map<String, Object>> call = mService.gspUpload(param);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, final Response<Map<String, Object>> response) {
                try {
                    if (response != null) {
                        Log.d(TAG,"@ RESPONSE.CODE : "+response.code());
                        if (200 == response.code()) {
                            Log.d(TAG, "API CALL SUCCESS: " + response.body().toString());
                        } else {
                            Log.e(TAG, "API CALL ERROR1: " + response.code());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "API CALL ERROR2: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                call.cancel();
                Log.e(TAG, "API CALL FAIL: " + t.getMessage());
            }
        });
    }







}
