package com.example.ps_android_hh_activofijo_c66.model.clases;

import android.os.Parcel;
import android.os.Parcelable;

public class DatabaseConf implements Parcelable {
    private String url;
    private String database;
    private String user;
    private String password;

    public DatabaseConf(String url, String database, String user, String password) {
        this.url = url;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    protected DatabaseConf(Parcel in) {
        url = in.readString();
        database = in.readString();
        user = in.readString();
        password = in.readString();
    }

    public static final Creator<DatabaseConf> CREATOR = new Creator<DatabaseConf>() {
        @Override
        public DatabaseConf createFromParcel(Parcel in) {
            return new DatabaseConf(in);
        }

        @Override
        public DatabaseConf[] newArray(int size) {
            return new DatabaseConf[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.url);
        parcel.writeString(this.database);
        parcel.writeString(this.user);
        parcel.writeString(this.password);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}