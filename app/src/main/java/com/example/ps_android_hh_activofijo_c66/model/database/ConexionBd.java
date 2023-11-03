package com.example.ps_android_hh_activofijo_c66.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConexionBd extends SQLiteOpenHelper {
    public ConexionBd(Context context) {
        super(context, "activoFijo.db", null, 8);
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
        String filtros = "create table if not exists filtros (" +
                        "_id integer primary key autoincrement, " +
                        "filtro text not null);";
        String encabezados = "create table if not exists encabezados( " +
                "_id integer primary key," +
                " nombre text not null," +
                " llave_primaria integer not null," +
                " indexado integer not null," +
                " editable integer not null," +
                " visible integer not null," +
                " filtro integer not null);";


        db.execSQL(config);
        db.execSQL(usuarios);
        db.execSQL(encabezados);
        db.execSQL(sesion);
        db.execSQL(filtros);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists config;");
        db.execSQL("drop table if exists usuarios;");
        String encabezadosUpdate = "drop table if exists encabezados;";
        db.execSQL("drop table if exists sesiones;");
        db.execSQL("drop table if exists filtros;");
        onCreate(db);
    }
}
