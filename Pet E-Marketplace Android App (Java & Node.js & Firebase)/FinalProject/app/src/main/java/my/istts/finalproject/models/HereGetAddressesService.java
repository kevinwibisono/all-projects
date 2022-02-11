package my.istts.finalproject.models;

import my.istts.finalproject.models.herejsonaddress.AlamatList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HereGetAddressesService {

    @GET("geocode")
    Call<AlamatList> getAddreses(@Query("apiKey") String apiKey, @Query("q") String input, @Query("limit") int limit);

    @GET("revgeocode")
    Call<AlamatList> reverseGeoAddress(@Query("apiKey") String apiKey, @Query("at") String input);
}
