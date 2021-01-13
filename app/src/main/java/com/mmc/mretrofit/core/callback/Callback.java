package com.mmc.mretrofit.core.callback;


import com.mmc.mretrofit.core.Call;

public interface Callback<T> {
    void onResponse(Call<T> call, String response);

    void onFailure(Call<T> call, Throwable t);
}
