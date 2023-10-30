package com.example.ps_android_hh_activofijo_c66.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BarcodeData implements Parcelable {
    private final String value;
    private final String name;
    private int cant;

    public BarcodeData(String value, String name) {
        this.value = value;
        this.name = name;
        this.cant = 1;
    }

    public BarcodeData(Parcel in) {
        value = in.readString();
        name = in.readString();
        cant = in.readInt();
    }

    public static final Creator<BarcodeData> CREATOR = new Creator<BarcodeData>() {
        @Override
        public BarcodeData createFromParcel(Parcel in) {
            return new BarcodeData(in);
        }

        @Override
        public BarcodeData[] newArray(int size) {
            return new BarcodeData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(value);
        parcel.writeString(name);
        parcel.writeInt(cant);
    }
    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public int getCant() {
        return cant;
    }

    public void addCant() {
        cant++;
    }

    public void setCantidad(int i) {
        cant=i;
    }

    public int compareTo(String value) {
        return this.value.compareTo(value);
    }

    public int compareTo(int cant) {
        return cant - this.cant;
    }
}
