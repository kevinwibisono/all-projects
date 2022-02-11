package my.istts.finalproject.models.backend;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendRetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://pawfriends-admin.herokuapp.com/";

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
