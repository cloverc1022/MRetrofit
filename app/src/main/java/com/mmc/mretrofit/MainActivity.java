package com.mmc.mretrofit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.mmc.mretrofit.core.Call;
import com.mmc.mretrofit.core.Retrofit;
import com.mmc.mretrofit.core.callback.Callback;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void request(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .callFactory(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .baseUrl("https://www.wanandroid.com")
                .build();

        WanAndroidService service = retrofit.create(WanAndroidService.class);

        Call<String> call = service.getArticles(3);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, String response) {
                Log.d("Retrofit", response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Retrofit", t.getMessage());
            }
        });
    }
}