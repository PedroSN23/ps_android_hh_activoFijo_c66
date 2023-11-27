package com.example.ps_android_hh_activofijo_c66.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConexionBd extends SQLiteOpenHelper {
    public ConexionBd(Context context) {
        super(context, "activoFijo.db", null, 14);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String baseSQL = "create table if not exists baseSQL (" +
                "_id integer primary key autoincrement," +
                "ip text not null," +
                "base text not null," +
                "usuario text not null," +
                "contrasena text not null);";

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

        String configuracion = "create table if not exists configuracion ( " +
                "_id integer primary key autoincrement," +
                "archivo_in_name text not null," +
                "archivo_in_path text not null," +
                "prefijo_out text not null," +
                "fecha integer not null," +
                "result integer not null);";

        String cambios = "create table if not exists cambios (" +
                "num_tag text not null," +
                "indice_data integer not null," +
                "valor text not null);";

        String inventario = "create table if not exists inventario (" +
                "num_tag text primary key," +
                "fecha text not null);";



        db.execSQL(configuracion);
        db.execSQL(baseSQL);
        db.execSQL(usuarios);
        db.execSQL(encabezados);
        db.execSQL(sesion);
        db.execSQL(cambios);
        db.execSQL(filtros);
        db.execSQL(inventario);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists baseSQL;");
        db.execSQL("drop table if exists usuarios;");
        db.execSQL("drop table if exists encabezadosUpdate;");
        db.execSQL("drop table if exists configuracion;");
        db.execSQL("drop table if exists sesiones;");
        db.execSQL("drop table if exists cambios;");
        db.execSQL("drop table if exists filtros;");
        db.execSQL("drop table if exists inventario;");
        onCreate(db);
    }
}
