package com.example.ps_android_hh_activofijo_c66.model;
public enum DevicesLocal {
    none(-1),
    mail(0),
    file(3);

    private final int code;

    DevicesLocal(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
