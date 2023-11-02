package com.example.ps_android_hh_activofijo_c66.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.ps_android_hh_activofijo_c66.model.clases.DatabaseConf;
import com.example.ps_android_hh_activofijo_c66.model.clases.Usuario;

import java.util.ArrayList;
import java.util.Objects;

public class InterfazBD {
    private final ConexionBd con;
    private SQLiteDatabase db;

    public InterfazBD(Context context) {
        con = new ConexionBd(context);
        inicializaDB();
    }

    public void open() throws SQLiteException {
        db = con.getWritableDatabase();
    }

    public void close() throws SQLiteException {
        con.close();
    }

    public void inicializaDB() {
        open();
        ContentValues content;

        int numRegistros = countRegistro("config");
        int usuariosRegistros = countRegistro("usuarios");

        if (numRegistros < 1) {
            content = new ContentValues();
            content.put("ip", "0.0.0.0");
            content.put("base", "");
            content.put("usuario", "");
            content.put("contrasena", "");
            content.put("potencia", 5);
            db.insert("config", null, content);
        }

        if (usuariosRegistros < 1) {
            content = new ContentValues();
            content.put("_id", "1");
            content.put("usuario", "root");
            content.put("password", "$2a$10$wwSjqBatW2doHEQYVj/KMOmDMEc.AQONcEy4aBKW4iL9LSotPWYrm");
            content.put("salt", "$2a$10$wwSjqBatW2doHEQYVj/KMO");
            content.put("rol", "1");
            db.insert("usuarios", null, content);
        }
    }

    private int getLastInsertedId() {
        int ret = -1;
        String sql = "SELECT last_insert_rowid()";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            if (!c.isAfterLast()) {
                ret = c.getInt(0);
            }
        }
        c.close();
        return ret;
    }

    public int countRegistro(String tabla) {
        int ret;
        String consulta = "select _id from " + tabla + ";";
        Cursor c = db.rawQuery(consulta, null);
        ret = c.getCount();
        c.close();
        return ret;
    }

    public void truncarTabla(String tabla) {
        String query = "DELETE FROM " + tabla + ";";
        db.execSQL(query);
        String query2 = "delete from sqlite_sequence where name='" + tabla + "';";
        db.execSQL(query2);
    }

    public Usuario obtenerUsuario(String usuario) {
        Usuario user = null;
        open();
        String consulta = "SELECT usuario, password, salt, rol FROM usuarios where usuario = '" + usuario + "'";

        try {
            Cursor c = db.rawQuery(consulta, null);
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    user = new Usuario(c.getString(0), c.getString(1), c.getString(2), c.getInt(3));
                    c.moveToNext();
                }
            }
            c.close();
        } catch (SQLiteException e) {
            Log.d("Denver", Objects.requireNonNull(e.getMessage()));
        }
        return user;
    }

    public ArrayList<Usuario> obtenerUsuarios() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        open();
        String consulta = "SELECT usuario, password, salt, rol FROM usuarios";

        try {
            Cursor c = db.rawQuery(consulta, null);
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    usuarios.add(new Usuario(c.getString(0), c.getString(1), c.getString(2), c.getInt(3)));
                    c.moveToNext();
                }
            }
            c.close();
        } catch (SQLiteException e) {
            Log.d("Denver", Objects.requireNonNull(e.getMessage()));
        }
        return usuarios;
    }

    //* USUARIOS

    public void modificarUsuario(String usuario, String newUsuario, String pass, String salt, int rol) {
        ContentValues content;
        open();
        content = new ContentValues();
        content.put("usuario", newUsuario);
        content.put("password", pass);
        content.put("salt", salt);
        content.put("rol", rol);

        db.update("usuarios", content, "usuario = '" + usuario + "'", null);
    }

    public void eliminarUsuario(String usuario) {
        String query = "DELETE FROM usuarios WHERE usuario = '" + usuario + "';";
        db.execSQL(query);
    }

    public void insertarUsuario(String newUsuario, String newHashedPassword, String salt, int rol) {
        ContentValues content;
        content = new ContentValues();
        open();
        content.put("usuario", newUsuario);
        content.put("password", newHashedPassword);
        content.put("salt", salt);
        content.put("rol", rol);

        db.insert("usuarios", null, content);
    }

    public void actualizarSesion(String usuario) {
        ContentValues content;
        open();
        content = new ContentValues();
        //content.put("sesion", sesion);

        db.update("config", content, "usuario = '" + usuario + "'", null);
    }

    //* FIN USUARIOS

    //* DATABASE

    public DatabaseConf obtenerConfiguracionDatabase() {
        open();
        DatabaseConf configuracion = null;

        String consulta = "select ip, base, usuario, contrasena from config where _id = 1;";
        Cursor c = db.rawQuery(consulta, null);
        c.moveToFirst();
        if (c.getCount() != 0) {
            configuracion = new DatabaseConf(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
        }
        c.close();

        return configuracion;
    }

    public void modificarDatabase(DatabaseConf databaseConf) {
        ContentValues content;
        open();
        content = new ContentValues();
        content.put("ip", databaseConf.getUrl());
        content.put("base", databaseConf.getDatabase());
        content.put("usuario", databaseConf.getUser());
        content.put("contrasena", databaseConf.getPassword());
        db.update("config", content, "_id=1", null);
    }

    public String[] obtenerServidor() {
        open();
        String consulta = "select ip, base, usuario, contrasena from config where _id = 1;";

        Cursor c = db.rawQuery(consulta, null);
        c.moveToFirst();
        String[] res; //=new String[c.getString()];
        if (c.getCount() != 0) {
            res = new String[c.getColumnCount()];
            for (int i = 0; i < c.getColumnCount(); ++i) {
                res[i] = c.getString(i);
            }
        } else {
            return null;
        }
        c.close();
        return res;
    }

    public void insertarSesion(String usuario, int rol) {
        ContentValues content;
        content = new ContentValues();
        open();
        content.put("usuario", usuario);
        content.put("rol", rol);

        db.insert("sesiones", null, content);
    }

    public Usuario obtenerPermiso() {
        Usuario user = null;
        open();
        String consulta = "select usuario, rol from sesiones;";

        Cursor c = db.rawQuery(consulta, null);
        c.moveToFirst();
        if (c.getCount() != 0) {
            user = new Usuario(c.getString(0), c.getInt(1));
        }
        c.close();

        return user;
    }

}
