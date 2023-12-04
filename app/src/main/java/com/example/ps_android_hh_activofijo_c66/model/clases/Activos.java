package com.example.ps_android_hh_activofijo_c66.model.clases;

import java.util.ArrayList;
import java.util.Arrays;

public class Activos {
    private int row;
    private String id;
    private String[] data;
    private String[] head;
    private boolean modificado;
    private boolean inventariado;
    private int idSel;
    private ArrayList<Integer> indexModify;
    private int errTipo;

    public Activos(int row, String[] head, String[] data, int pkInd) {
        this.row=row;
        indexModify = new ArrayList<>();
        this.head=head;
        this.id = data[pkInd];
        this.data = data;
        this.modificado = false;
        this.inventariado = false;
        this.idSel=0;
        this.errTipo=0;

        System.out.println("Constructor Activos:");
        System.out.println("row: " + row);
        System.out.println("head: " + Arrays.toString(head));
        System.out.println("id: " + id);
        System.out.println("data: " + Arrays.toString(data));
        System.out.println("modificado: " + modificado);
        System.out.println("inventariado: " + inventariado);
        System.out.println("idSel: " + idSel);
        System.out.println("errTipo: " + errTipo);


    }

    public int getErrTipo() {
        return errTipo;
    }

    public void setErrTipo(int errTipo) {
        this.errTipo = errTipo;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCantidadDatos() {
        return this.data.length;
    }

    public String getData(int position) {
        return this.data[position];
    }

    public String getHead(int position) {
        return this.head[position];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public boolean isModificado() {
        return modificado;
    }

    public void setModificado(boolean modificado) {
        this.modificado = modificado;
    }

    public boolean isInventariado() {
        return inventariado;
    }

    public void setInventariado(boolean inventariado) {
        this.inventariado = inventariado;
    }

    public boolean compareTo(String numDeTag) {
        return (this.id.compareTo(numDeTag)==0);
    }

    public int compareTo(boolean inventariado, String id, int sort) {
        if(this.inventariado) {
            if(inventariado) {
                return (this.id.compareTo(id)*sort);
            } else {
                return sort;
            }
        } else {
            if(!inventariado) {
                return (this.id.compareTo(id)*sort);
            }
            else return -1*sort;
        }
    }

    public String getHeadId(int selected) {
        return head[selected];
    }

    public int getIdSel() {
        return idSel;
    }

    public void setIdSel(int idSel) {
        this.idSel = idSel;
    }

    @Override
    public String toString() {
        return head[idSel];
    }

    public void addIndex(int x) {
        boolean found = false;
        for(int i=0; i<indexModify.size(); i++) {
            if(indexModify.get(i)==x) {
                found=true;
                break;
            }
        }
        if(!found) {
            indexModify.add(x);
        }
    }

    public ArrayList<Integer> getIndexModify() {
        return indexModify;
    }

    public void setDataAt(int x, String valor) {
        this.data[x]=valor;
    }

    public void setHeadAt(int x, String valor) {
        this.head[x]=valor;
    }
}
