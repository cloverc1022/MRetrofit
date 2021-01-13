package com.mmc.mretrofit.core;


import com.mmc.mretrofit.core.callback.Callback;

public interface Call<T> {

    void enqueue(Callback<T> callback);
}
