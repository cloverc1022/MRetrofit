package com.mmc.mretrofit.core;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class Retrofit {

    private final Map<Method, ServiceMethod<?>> serviceMethodCache = new ConcurrentHashMap<>();
    private final Call.Factory callFactory;
    private final Executor callbackExecutor;
    private final HttpUrl baseUrl;

    public Call.Factory getCallFactory() {
        return callFactory;
    }

    public Executor getCallbackExecutor() {
        return callbackExecutor;
    }

    public HttpUrl getBaseUrl() {
        return baseUrl;
    }

    private Retrofit(Call.Factory callFactory, Executor callbackExecutor, String baseUrl) {
        this.callFactory = callFactory;
        this.callbackExecutor = callbackExecutor;
        this.baseUrl = HttpUrl.parse(baseUrl);

    }

    public static class Builder {
        Call.Factory callFactory;
        Executor callbackExecutor;
        String baseUrl;

        public Builder callFactory(Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        public Builder callbackExecutor(Executor callbackExecutor) {
            this.callbackExecutor = callbackExecutor;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }


        public Retrofit build() {
            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }
            if (callbackExecutor == null) {
                callbackExecutor = new MainThreadExecutor();
            }

            return new Retrofit(callFactory, callbackExecutor, baseUrl);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
            //构建ServiceMethod
            ServiceMethod<?> serviceMethod = loadServiceMethod(method);
            return serviceMethod.invoke(args);
        });
    }

    private ServiceMethod<?> loadServiceMethod(Method method) {
        ServiceMethod<?> serviceMethod = serviceMethodCache.get(method);
        if (serviceMethod == null) {
            synchronized (serviceMethodCache) {
                serviceMethod = serviceMethodCache.get(method);
                if (serviceMethod == null) {
                    serviceMethod = ServiceMethod.parseMethod(this, method);
                    serviceMethodCache.put(method, serviceMethod);
                }
            }
        }
        return serviceMethod;
    }

    /**
     * 主线程
     */
    static final class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable r) {
            handler.post(r);
        }
    }
}
