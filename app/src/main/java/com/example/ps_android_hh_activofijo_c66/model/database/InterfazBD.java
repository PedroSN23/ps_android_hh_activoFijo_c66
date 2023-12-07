package com.example.ps_android_hh_activofijo_c66.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.ps_android_hh_activofijo_c66.model.clases.Cambios;
import com.example.ps_android_hh_activofijo_c66.model.clases.Configuracion;
import com.example.ps_android_hh_activofijo_c66.model.clases.DatabaseConf;
import com.example.ps_android_hh_activofijo_c66.model.clases.Encabezados;
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

        int numRegistros = countRegistro("baseSQL");
        int usuariosRegistros = countRegistro("usuarios");
        int configuracionRegistros = countRegistro("configuracion");
        int modelRegistros = countRegistro("model");

        if (numRegistros < 1) {
            content = new ContentValues();
            content.put("ip", "0.0.0.0");
            content.put("base", "");
            content.put("usuario", "");
            content.put("contrasena", "");
            content.put("slug", "");
            content.put("id_companie", "");
            db.insert("baseSQL", null, content);
        }
        if (numRegistros < 1) {
            content = new ContentValues();
            content.put("_id", "1");
            content.put("modo", "0");
            db.insert("model", null, content);
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
        if(configuracionRegistros == 0) {
            content = new ContentValues();
            content.put("_id", "1");
            content.put("archivo_in_path", "");
            content.put("archivo_in_name", "");
            content.put("prefijo_out", "Salida");
            content.put("fecha", 0);
            content.put("result", 0);
            db.insert("configuracion", null, content);
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

    public Configuracion obtenerConfiguracion() {
        Configuracion configuracion=null;
        open();

        String consulta = "select archivo_in_name, archivo_in_path, prefijo_out, fecha, result from configuracion where _id = 1;";
        try {
            Cursor c = db.rawQuery(consulta, null);
            c.moveToFirst();
            if(c.getCount() != 0) {
                configuracion = new Configuracion(c.getInt(4), c.getInt(3), c.getString(0), c.getString(1), c.getString(2));
            }
            c.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return configuracion;
    }

    public long modificarConfiguracion (Configuracion configuracion) {
        ContentValues content;
        open();
        content = new ContentValues();
        content.put("archivo_in_path", configuracion.getArchivoInPath());
        content.put("archivo_in_name", configuracion.getArchivoInName());
        content.put("prefijo_out", configuracion.getPrefijoOut());
        content.put("fecha", (configuracion.isFecha())? 1:0);
        content.put("result", (configuracion.isResult())? 1:0);
        return db.update("configuracion", content, "_id=1",  null);
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

        String consulta = "select ip, base, usuario, contrasena from baseSQL where _id = 1;";
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
        db.update("baseSQL", content, "_id=1", null);
    }

    public String[] obtenerServidor() {
        open();
        String consulta = "select ip, base, usuario, contrasena from baseSQL where _id = 1;";

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
    /*********************CONFIGURACION DE MODO*************************/

    public void actualizarModo(boolean modo) {
        ContentValues content;
        open();
        content = new ContentValues();
        content.put("modo", modo);
        db.update("model", content, "_id=1", null);
    }

    public boolean obtenerModo() {
        boolean modo = false;

        open();

        Cursor cursor = db.query("model", new String[]{"modo"}, "_id=?", new String[]{"1"}, null, null, null);

        if (cursor.moveToFirst()) {
            int modoInt = cursor.getInt(cursor.getColumnIndex("modo"));
            modo = (modoInt == 1);
        }

        cursor.close();
        return modo;
    }



    /*********************CONSULTAS DE SLUG*************************/

    public void insertarData(String slug, String id) {
        ContentValues content;
        open();
        content = new ContentValues();
        content.put("slug", slug);
        content.put("id_companie", id);
        db.update("baseSQL", content, "_id=1", null);
    }

    public String obtenerSlug() {
        String slug = null;
        open();

        Cursor cursor = db.query("baseSQL", new String[]{"slug"}, "_id=?", new String[]{"1"}, null, null, null);

        if (cursor.moveToFirst()) {
            slug = cursor.getString(cursor.getColumnIndex("slug"));
        }

        cursor.close();
        return slug;
    }
    public String obtenerIdCompanie() {
        String id = null;
        open();

        Cursor cursor = db.query("baseSQL", new String[]{"id_companie"}, "_id=?", new String[]{"1"}, null, null, null);

        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex("id_companie"));
        }

        cursor.close();
        return id;
    }

    /*********************ENCABEZADOS*************************/
    public ArrayList<Encabezados> obtenerEncabezados() {
        ArrayList<Encabezados> encabezados = new ArrayList<>();
        open();

        String consulta = "select _id, nombre, llave_primaria, indexado, editable, visible, filtro from encabezados;";
        try {
            Cursor c = db.rawQuery(consulta, null);
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    encabezados.add(new Encabezados(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3), c.getInt(4), c.getInt(5), c.getInt(6)));
                    c.moveToNext();
                }
            }
            c.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return encabezados;
    }

    public long modificarEncabezado (Encabezados encabezado) {
        ContentValues content;
        open();
        content = new ContentValues();
        content.put("nombre", encabezado.getNombre());
        content.put("llave_primaria", (encabezado.isLlavePrimaria())? 1:0);
        content.put("indexado", (encabezado.isIndexado())? 1:0);
        content.put("editable", (encabezado.isEditable())? 1:0);
        content.put("visible", (encabezado.isVisible())? 1:0);
        return db.update("encabezados", content, "_id="+encabezado.getId(),  null);
    }

    public void insertarEncabezado(Encabezados encabezado) {
        ContentValues content;
        open();
        content = new ContentValues();
        try {
            content.put("_id", encabezado.getId());
            content.put("nombre", encabezado.getNombre());
            content.put("llave_primaria", (encabezado.isLlavePrimaria())? 1:0);
            content.put("indexado", (encabezado.isIndexado())? 1:0);
            content.put("editable", (encabezado.isEditable())? 1:0);
            content.put("visible", (encabezado.isVisible())? 1:0);
            content.put("filtro", (encabezado.isFiltro())? 1:0);
            db.insert("encabezados", null, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void vaciarEncabezados() {
        truncarTabla("encabezados");
    }




    /********************************FILTROS*****************************/
    public ArrayList<String> obtenerFiltros() {
        ArrayList<String> filtros = new ArrayList<>();
        open();
        String query = "select filtro from filtros;";
        try {
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    filtros.add(c.getString(0));
                    c.moveToNext();
                }
            }
            c.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return filtros;
    }

    public void insertarFiltro(String filtro) {
        ContentValues content;
        open();
        content = new ContentValues();
        try {
            content.put("filtro", filtro);
            db.insert("filtros", null, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarFiltro(String s) {
        String query= "delete from filtros where filtro='"+s+"';";
        db.execSQL(query);
    }


    /********************************CAMBIOS*****************************/
    public ArrayList<Cambios> obtenerCambios() {
        ArrayList<Cambios> cambios = new ArrayList<>();
        open();
        String query = "select num_tag, indice_data, valor from cambios;";
        try {
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    cambios.add(new Cambios(c.getString(0), c.getInt(1), c.getString(2)));
                    c.moveToNext();
                }
            }
            c.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return cambios;
    }

    public void insertarCambios(String tag, String data, int index) {
        ContentValues content;
        open();
        content = new ContentValues();
        try {
            content.put("num_tag", tag);
            content.put("indice_data", index);
            content.put("valor", data);
            db.insert("cambios", null, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarCambios() {
        truncarTabla("cambios");
    }



    /********************************INVENTARIO*****************************/
    public ArrayList<String[]> obtenerInventario() {
        ArrayList<String[]> tags = new ArrayList<>();
        open();
        String query = "select num_tag, fecha from inventario;";
        try {
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    String[] tmp = new String[2];
                    tmp[0]=c.getString(0);
                    tmp[1]=c.getString(1);
                    tags.add(tmp);
                    c.moveToNext();
                }
            }
            c.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return tags;
    }

    public void insertarInventariado(String tag, String fecha) {
        ContentValues content;
        open();
        content = new ContentValues();
        try {
            content.put("num_tag", tag);
            content.put("fecha", fecha);
            db.insertWithOnConflict("inventario", null, content, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void eliminarInventario() {
        truncarTabla("inventario");
    }

}
