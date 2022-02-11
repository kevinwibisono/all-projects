package my.istts.finalproject.models;

import my.istts.finalproject.models.rajaongkirapi.RajaOngkirInput;
import my.istts.finalproject.models.rajaongkirapi.RajaOngkirResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RajaOngkirService {

    @POST("cost")
    Call<RajaOngkirResult> getOngkir(@Header("key") String apiKey, @Body RajaOngkirInput input);
}
