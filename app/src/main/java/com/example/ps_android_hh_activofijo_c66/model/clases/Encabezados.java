package com.example.ps_android_hh_activofijo_c66.model.clases;

public class Encabezados {
    private int id;
    private String nombre;
    private boolean llavePrimaria;
    private boolean indexado;
    private boolean editable;
    private boolean visible;
    private boolean filtro;

    public Encabezados(int id, String nombre, int llavePrimaria, int indexado, int editable, int visible, int filtro) {
        this.id = id;
        this.nombre = nombre;
        this.llavePrimaria = (llavePrimaria==1);
        this.indexado = (indexado==1);
        this.editable = (editable==1);
        this.visible = (visible==1);
        this.filtro = (filtro==1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isLlavePrimaria() {
        return llavePrimaria;
    }

    public void setLlavePrimaria(boolean llavePrimaria) {
        this.llavePrimaria = llavePrimaria;
    }

    public boolean isIndexado() {
        return indexado;
    }

    public void setIndexado(boolean indexado) {
        this.indexado = indexado;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isFiltro() {
        return filtro;
    }

    public void setFiltro(boolean filtro) {
        this.filtro = filtro;
    }
}
