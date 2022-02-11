package com.example.sellerapp.models.backend;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BackendRetrofitService {

    @GET("sendNotif")
    Call<SendNotifResponse> sendNotif(@Query("judul") String judul, @Query("isi") String isi, @Query("topik") String topik);

    @POST("verifyEmail")
    Call<SendVerifResponse> sendVerification(@Query("email") String email, @Query("code") String code);
}
