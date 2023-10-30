package com.example.ps_android_hh_activofijo_c66.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConexionBd extends SQLiteOpenHelper {
    public ConexionBd(Context context) {
        super(context, "activoFijo.db", null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String config = "create table if not exists config (" +
                "_id integer primary key autoincrement," +
                "ip text not null," +
                "base text not null," +
                "usuario text not null," +
                "contrasena text not null," +
                "potencia integer not null);";

        String usuarios = "create table if not exists usuarios (" +
                "_id integer primary key autoincrement, " +
                "usuario text not null, " +
                "password text not null, " +
                "salt text not null, " +
                "rol integer not null);";

        String sesion = "create table if not exists sesiones (" +
                "_id integer primary key autoincrement, " +
                "usuario text not null, " +
                "rol integer not null);";

        db.execSQL(config);
        db.execSQL(usuarios);
        db.execSQL(sesion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists config;");
        db.execSQL("drop table if exists usuarios;");
        db.execSQL("drop table if exists sesiones;");
        onCreate(db);
    }
}
