package com.example.sellerapp.models;

import com.example.sellerapp.models.herejsonaddress.AlamatList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReverseGeoAddressService {

    @GET("revgeocode")
    Call<AlamatList> reverseGeoAddress(@Query("apiKey") String apiKey, @Query("at") String input);
}
