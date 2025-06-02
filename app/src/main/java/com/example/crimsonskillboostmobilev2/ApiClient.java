package com.example.crimsonskillboostmobilev2;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // Increase connection timeout
                    .readTimeout(30, TimeUnit.SECONDS)   // Increase read timeout
                    .writeTimeout(30, TimeUnit.SECONDS)  // Increase write timeout
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2/CrimsonSkillBoost-Web/") // Ensure this URL is correct
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}