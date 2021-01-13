package com.mmc.mretrofit.core;

import com.mmc.mretrofit.core.annotation.Field;
import com.mmc.mretrofit.core.annotation.Get;
import com.mmc.mretrofit.core.annotation.Path;
import com.mmc.mretrofit.core.annotation.Post;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class RequestFactory {
    private HttpUrl httpUrl;
    private String relativeUrl;
    private String method;
    private ParameterHandler[] parameterHandlers;

    public RequestFactory(HttpUrl httpUrl, String relativeUrl, String method, ParameterHandler[] parameterHandlers) {
        this.httpUrl = httpUrl;
        this.relativeUrl = relativeUrl;
        this.method = method;
        this.parameterHandlers = parameterHandlers;
    }

    static RequestFactory parseMethod(Retrofit retrofit, Method method) {
        String requestType = null;
        HttpUrl baseUrl = retrofit.getBaseUrl();
        String url = null;

        if (method.isAnnotationPresent(Get.class)) {
            requestType = "GET";
            Get annotation = method.getAnnotation(Get.class);
            url = annotation.value();
        } else if (method.isAnnotationPresent(Post.class)) {
            requestType = "POST";
            Post annotation = method.getAnnotation(Post.class);
            url = annotation.value();
        }

        int paramCount = method.getParameterAnnotations().length;
        ParameterHandler[] parameterHandlers = new ParameterHandler[paramCount];
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation annotation = parameterAnnotations[i][0];
            if (annotation.annotationType() == Field.class) {
                String value = ((Field) annotation).value();
                parameterHandlers[i] = new ParameterHandler.FieldParameterHandler(value);
            } else if (annotation.annotationType() == Path.class) {
                String value = ((Path) annotation).value();
                parameterHandlers[i] = new ParameterHandler.PathParameterHandler(value);
            }
        }

        return new RequestFactory(baseUrl, url, requestType, parameterHandlers);
    }


    public Request request(Object[] args) {
        HttpUrl realUrl = null;
        if ("POST".equals(method)) {
            //TODO RequestBody
        } else if ("GET".equals(method)) {
            HashMap<String, String> queries = new HashMap<>();
            for (int i = 0; i < parameterHandlers.length; i++) {
                ParameterHandler parameterHandler = parameterHandlers[i];
                if (parameterHandler instanceof ParameterHandler.FieldParameterHandler) {
                    queries.put(parameterHandler.value, (String) args[i]);
                    httpUrl.newBuilder().addQueryParameter(parameterHandler.value, (String) args[i]).build();
                } else if (parameterHandler instanceof ParameterHandler.PathParameterHandler) {
                    relativeUrl = relativeUrl.replace("{" + parameterHandler.value + "}", String.valueOf(args[i]));
                }
            }
            String url = httpUrl.toString() + relativeUrl;
            realUrl = HttpUrl.parse(url);
            for (Map.Entry<String, String> stringStringEntry : queries.entrySet()) {
                realUrl.newBuilder().addQueryParameter(stringStringEntry.getKey(), stringStringEntry.getValue()).build();
            }
        }

        return new Request.Builder().url(realUrl).build();
    }
}
