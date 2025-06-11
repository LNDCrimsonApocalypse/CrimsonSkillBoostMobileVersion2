package com.example.crimsonskillboostmobilev2;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static synchronized Retrofit getClient() {
        if (retrofit == null) {
            try {
                // Logging interceptor for detailed debugging
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                // OkHttpClient with enhanced retry mechanism and increased timeouts
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS) // Increased connection timeout
                        .readTimeout(60, TimeUnit.SECONDS)    // Increased read timeout
                        .writeTimeout(60, TimeUnit.SECONDS)   // Increased write timeout
                        .addInterceptor(loggingInterceptor)   // Enable detailed logging
                        .addInterceptor(chain -> {            // Retry mechanism
                            int tryCount = 0;
                            int maxLimit = 3;
                            while (tryCount < maxLimit) {
                                try {
                                    return chain.proceed(chain.request());
                                } catch (java.net.ProtocolException e) {
                                    tryCount++;
                                    if (tryCount >= maxLimit) {
                                        throw e;
                                    }
                                }
                            }
                            try {
                                throw new Exception("Max retry limit reached");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .build();

                // Retrofit instance
                retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2/CrimsonSkillBoost-Web/") // Update if needed
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retrofit;
    }
}