package com.example.ps_android_hh_activofijo_c66.model.clases;

import androidx.annotation.NonNull;

public class ControlEncabezados {
    private int indexado;
    private boolean primaryKey;
    private boolean filtro;

    public ControlEncabezados() {
        indexado=0;
        primaryKey=false;
        filtro = false;
    }

    public int fistPrimaryKey() {
        int ret=0;
        if(!primaryKey) {
            primaryKey=true;
            ret=1;
        }
        return ret;
    }

    public int firstFiltro(){
        int ret=0;
        if (!filtro) {
            filtro = true;
            ret = 1;
        }
        return ret;
    }

    public int canBeIndexado() {
        int ret=0;
        if(indexado<4) {
            indexado++;
            ret=1;
        }
        return ret;
    }

    public void restIndexado() {
        indexado--;
        if(indexado<0) indexado=0;
    }

    public void resetLlavePrimaria() {
        primaryKey=false;
    }

    public void resetFiltro(){filtro=false;}

    @NonNull
    @Override
    public String toString() {
        if(primaryKey) {
            return "PrimaryKey=ON Indexado="+indexado+" Filtro= "+filtro;
        }
        return "PrimaryKey=OFF Indexado="+indexado+" Filtro= "+filtro;
    }
}
