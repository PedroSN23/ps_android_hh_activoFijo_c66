package com.example.ps_android_hh_activofijo_c66.model.database;


import android.content.Context;

import com.example.ps_android_hh_activofijo_c66.model.clases.Activos;
import com.example.ps_android_hh_activofijo_c66.model.clases.Encabezados;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    public String[] obtenerData(String serial) {
        String[] result = new String[2];
        String slug = null;
        String id = null;

        try {
            String query = "SELECT slug, id FROM companies WHERE id = (SELECT company_id FROM restrictions WHERE android_id = ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, serial);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                slug = rs.getString("slug");
                id = rs.getString("id");
            }

            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        result[0] = slug;
        result[1] = id;

        return result;
    }



    public ArrayList<Encabezados> obtenerEncabezados(String slug){
        ArrayList<Encabezados> filtros = new ArrayList<>();
        Integer editableS = 0;
        try {
            String query = "SELECT id, excel_nombre, es_llavep, es_buscable ,es_editable, es_hh_show , es_filtro FROM columns WHERE company_id = (SELECT id FROM companies WHERE slug = ?)";
            PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
            pstmt.setString(1, slug);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Integer id = Integer.valueOf(rs.getString("id"));
                String excel_nombre = rs.getString("excel_nombre");
                Integer llavep = Integer.valueOf(rs.getString("es_llavep"));
                Integer indexable = Integer.valueOf(rs.getString("es_buscable"));////////////////////////////
                Integer editable = Integer.valueOf(rs.getString("es_editable"));
                Integer visible = Integer.valueOf(rs.getString("es_hh_show"));
                Integer filtro = Integer.valueOf(rs.getString("es_filtro"));

                Encabezados encabezados = new Encabezados(id, excel_nombre, llavep, indexable, editableS, visible, filtro);
                filtros.add(encabezados);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filtros;
    }


    /*******************************************ACTIVOS*********************************/
    public List<String[]> obtenerActivos(String slug) {
        List<String[]> activos = new ArrayList<>();
        List<String> nombresColumnas = obtenerNombresColumnas(slug);

        try {
            StringBuilder queryBuilder = new StringBuilder("SELECT ");
            for (int i = 0; i < nombresColumnas.size(); i++) {
                queryBuilder.append(nombresColumnas.get(i));

                if (i < nombresColumnas.size() - 1) {
                    queryBuilder.append(", ");
                }
            }
            String nombreTabla = "activos_" + slug;
            queryBuilder.append(" FROM ").append(nombreTabla);

            try (PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String[] fila = new String[nombresColumnas.size()];
                        for (int i = 0; i < nombresColumnas.size(); i++) {
                            fila[i] = rs.getString(nombresColumnas.get(i));
                        }
                        activos.add(fila);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activos;
    }



    public List<String> obtenerNombresColumnas(String slug) {
        List<String> nombresColumnas = new ArrayList<>();
        try {
            String query = "SELECT nombre FROM columns WHERE company_id = (SELECT id FROM companies WHERE slug = ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, slug);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String nombre = rs.getString("nombre");
                        nombresColumnas.add(nombre);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombresColumnas;
    }

    public void insertarDatosInventariados(String slug, String id, ArrayList<Activos> activos) {
        try {
            String queryNombre = "SELECT nombre FROM columns WHERE company_id = ? AND es_llavep = 1";
            String nombreColumna = null;

            try (PreparedStatement pstmtNombre = conn.prepareStatement(queryNombre)) {
                pstmtNombre.setString(1, id);
                ResultSet rsNombre = pstmtNombre.executeQuery();

                if (rsNombre.next()) {
                    nombreColumna = rsNombre.getString("nombre");
                }
            }

            String queryActivos = "UPDATE activos_" + slug + " SET inventariado = inventariado + 1 WHERE " + nombreColumna + " = ?";

            try (PreparedStatement pstmtActivos = conn.prepareStatement(queryActivos)) {
                for (Activos activo : activos) {
                    String epc = activo.getId();

                    pstmtActivos.setString(1, epc);
                    pstmtActivos.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> obtenerPrefijo(String slug, String id) {
        ArrayList<String> resultados = new ArrayList<>();

        try {
            String queryNombre = "SELECT nombre FROM columns WHERE company_id = ? AND es_llavep = 1";
            String nombreColumna = null;

            try (PreparedStatement pstmtNombre = conn.prepareStatement(queryNombre)) {
                pstmtNombre.setString(1, id);
                ResultSet rsNombre = pstmtNombre.executeQuery();

                if (rsNombre.next()) {
                    nombreColumna = rsNombre.getString("nombre");
                }
            }

            if (nombreColumna != null) {
                String queryPrefijos = "SELECT SUBSTRING(" + nombreColumna + ", 1, 6) AS prefijo FROM activos_" + slug;

                try (PreparedStatement pstmtPrefijos = conn.prepareStatement(queryPrefijos)) {
                    ResultSet rsPrefijos = pstmtPrefijos.executeQuery();

                    Set<String> prefijosUnicos = new HashSet<>();

                    while (rsPrefijos.next()) {
                        String prefijo = rsPrefijos.getString("prefijo");

                        // Verifica que el prefijo no sea nulo y no esté repetido
                        if (prefijo != null && !prefijo.isEmpty() && prefijosUnicos.add(prefijo)) {
                            resultados.add(prefijo);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultados;
    }









}