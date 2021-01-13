package com.mmc.mretrofit.core;

import com.mmc.mretrofit.core.callback.Callback;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Request;
import okhttp3.Response;

public class OkHttpCall<T> implements Call<T> {

    private RequestFactory requestFactory;
    private okhttp3.Call.Factory callFactory;
    private Executor callbackExecutor;
    private Object[] args;

    public OkHttpCall(okhttp3.Call.Factory callFactory, RequestFactory requestFactory, Object[] args, Executor callbackExecutor) {
        this.requestFactory = requestFactory;
        this.callFactory = callFactory;
        this.callbackExecutor = callbackExecutor;
        this.args = args;
    }

    @Override
    public void enqueue(Callback<T> callback) {
        Request request = requestFactory.request(args);
        okhttp3.Call call = callFactory.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callbackExecutor.execute(() -> callback.onFailure(OkHttpCall.this, e));
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String result = response.body().string();
                callbackExecutor.execute(() -> callback.onResponse(OkHttpCall.this, result));
            }
        });
    }
}
