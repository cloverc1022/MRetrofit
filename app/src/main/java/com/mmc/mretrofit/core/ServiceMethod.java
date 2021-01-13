package com.mmc.mretrofit.core;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

public class ServiceMethod<T> {

    private final okhttp3.Call.Factory callFactory;
    //线程池
    private final Executor callbackExecutor;
    //封装网络请求参数，构建Request
    private final RequestFactory requestFactory;

    public ServiceMethod(okhttp3.Call.Factory callFactory, RequestFactory requestFactory, Executor callbackExecutor) {
        this.callFactory = callFactory;
        this.callbackExecutor = callbackExecutor;
        this.requestFactory = requestFactory;
    }

    public static <T> ServiceMethod<T> parseMethod(Retrofit retrofit, Method method) {
        RequestFactory requestFactory = RequestFactory.parseMethod(retrofit, method);
        okhttp3.Call.Factory callFactory = retrofit.getCallFactory();
        Executor callbackExecutor = retrofit.getCallbackExecutor();
        //TODO 解析返回值，适配不同 CallAdapter

        return new ServiceMethod<>(callFactory, requestFactory, callbackExecutor);
    }

    public Call<T> invoke(Object[] args) {
        //网络请求
        return new OkHttpCall<>(callFactory, requestFactory, args, callbackExecutor);
    }
}
