package com.example.ps_android_hh_activofijo_c66.model.clases;

public class Cambios {
    private String tag;
    private int index;
    private String valor;

    public Cambios(String tag, int index, String valor) {
        this.tag = tag;
        this.index = index;
        this.valor = valor;
    }

    public String getTag() {
        return tag;
    }

    public int getIndex() {
        return index;
    }

    public String getValor() {
        return valor;
    }
}
