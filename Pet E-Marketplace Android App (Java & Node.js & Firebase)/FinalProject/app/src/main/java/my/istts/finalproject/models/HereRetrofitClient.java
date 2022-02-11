package my.istts.finalproject.models;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HereRetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://geocode.search.hereapi.com/v1/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
