package com.mmc.mretrofit.bean;

public class ResponseBean<T> {
    T data;
    int errorCode;
    String errorMsg;
}
