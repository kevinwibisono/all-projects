package com.example.sellerapp.models;

import com.example.sellerapp.models.herejsonaddress.AlamatList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetAddressesService {

    @GET("geocode")
    Call<AlamatList> getAddreses(@Query("apiKey") String apiKey, @Query("q") String input, @Query("limit") int limit);
}
