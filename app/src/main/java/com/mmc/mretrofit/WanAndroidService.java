package com.mmc.mretrofit;

import com.mmc.mretrofit.core.Call;
import com.mmc.mretrofit.core.annotation.Get;
import com.mmc.mretrofit.core.annotation.Path;

public interface WanAndroidService {

    @Get("article/list/{page}/json")
    Call<String> getArticles(@Path("page") int page);
}
