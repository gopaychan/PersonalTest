package com.bbk.gopay;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by gopaychan on 2016/11/10.
 */
public interface INetService {
    @GET("help/mobilesys/other/20160510614227.html")
    Call<ResponseBody> loginNuomi();

    @POST("gopaychan/happy_ending")
    Call<String> post(@Field("user_no") String user_no);
}
