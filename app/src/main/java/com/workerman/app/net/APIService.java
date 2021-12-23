package com.workerman.app.net;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by bada
 */

public interface APIService {

    @POST("/admin/gps/upload")
    Call<Map<String, Object>> gspUpload(@Body Map<String, Object> param);


}
