package com.workerman.app.net;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.workerman.app.Defines;
import com.workerman.app.manager.PrefManager;
import com.workerman.app.ui.activity.ActSplash;
import com.workerman.app.utils.ShaPasswordEncoder;
import com.workerman.app.utils.Utils;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bada on 2018-06-26.
 */

public class APIClient {
    private static Retrofit retrofit = null;
    private static Context mContext;

    public static Retrofit getClient(Context _context) {
        mContext = _context;

        if(Defines.RELEASE_TYPE.equals("DEV")){
            retrofit = null;
        }

        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    // 헤더를 자유 자재로 변경
                    Request.Builder builder = original.newBuilder();
                    builder.addHeader("Content-Type", "application/json; charset=utf-8");
                    builder.addHeader("Accept", "application/json; charset=utf-8");
                    builder.addHeader("User-Agent", System.getProperty("http.agent"));

                    String token = PrefManager.getInstance().getString(_context, PrefManager.PREFKEY_TOKEN, null);

                    if (!Utils.isEmpty(token)) {
                        builder.addHeader("Authorization", "Bearer " + token);
                    }

                    builder.method(original.method(), original.body());
                    Request request = builder.build();

                    Response response = chain.proceed(request);

                    // 아래 소스는 response로 오는 데이터가 URLEncode 되어 있을 때
                    String strResponse = response.body().string();
                    if (Defines.USE_ENCODING) {
                        try {
                            JSONObject json = new JSONObject(strResponse);
                            strResponse = ShaPasswordEncoder.AES_Decode(json.get("data").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (mContext != null) {
                        if (response.code() == 404) {
                            Intent intent = new Intent(mContext, ActSplash.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Defines.INTENT_KEY_CHECK_SERVER, true);
                            mContext.startActivity(intent);
                        }
                    }


                    // URLDecode 하는 소스 입니다.
                    return response.newBuilder()
                            .body(ResponseBody.create(response.body().contentType()
                                    , strResponse))
                            .build();
                }
            }).build();

            Log.d("test", Defines.REQ_DOMAIN);
            retrofit = new Retrofit.Builder()
                    .baseUrl(Defines.REQ_DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}