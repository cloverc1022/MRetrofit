package com.mmc.mretrofit.core;

public class ParameterHandler {

    String value;

    public ParameterHandler(String value) {
        this.value = value;
    }

    public static class FieldParameterHandler extends ParameterHandler{

        public FieldParameterHandler(String value) {
            super(value);
        }
    }

    public static class PathParameterHandler extends ParameterHandler{

        public PathParameterHandler(String value) {
            super(value);
        }
    }
}
