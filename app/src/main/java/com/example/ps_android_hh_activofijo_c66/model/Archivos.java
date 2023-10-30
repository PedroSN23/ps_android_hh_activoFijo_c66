package com.example.ps_android_hh_activofijo_c66.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Archivos implements Parcelable {
    private int id;
    private final String path;
    private final String name;

    public Archivos(String path, String name) {
        this.path = path;
        this.name = name;
        this.id = -1;
    }

    protected Archivos(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.id = in.readInt();
    }

    public static final Creator<Archivos> CREATOR = new Creator<Archivos>() {
        @Override
        public Archivos createFromParcel(Parcel in) {
            return new Archivos(in);
        }

        @Override
        public Archivos[] newArray(int size) {
            return new Archivos[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeString(name);
        parcel.writeInt(id);
    }
    
    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
