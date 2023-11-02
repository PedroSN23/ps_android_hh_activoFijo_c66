package com.example.ps_android_hh_activofijo_c66.model.database;


import android.app.ProgressDialog;
import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class ConexionMysql {
    private Connection conn = null;
    private String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";
    private boolean connected = false;
    private Context context;
    public ConexionMysql(String ip, String database, String user, String pass) {
        this.context = context;
        System.out.println("LOS VALORES SON ESTOS: LA IP ES " + ip + " Y LA BASE DE DATOS ES " + database);
        String db_url = "jdbc:mysql://" + ip + "/" + database;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection(db_url, user, pass);
            if (conn != null) {
                connected = true;
                System.out.println("Conexión exitosa a la base de datos");
                // setSQLMODE();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            msg = writer.toString();
        }
    }


    public String getMsg() {
        return msg;
    }

    public boolean getConnected() {
        return connected;
    }

    private void setSQLMODE() {
        String query = "SET SESSION sql_mode = 'STRUCT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION'";
        try {
            conn.createStatement().executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Close() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /*******************************************INSERCIONES*********************************/



}