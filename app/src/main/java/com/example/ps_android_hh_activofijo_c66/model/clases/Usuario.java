package com.example.ps_android_hh_activofijo_c66.model.clases;

public class Usuario {
    private int id;
    private String usuario;
    private int rol;
    private String pass;
    private String salt;

    public Usuario(String usuario, String pass, String salt, int rol) {
        this.usuario = usuario;
        this.pass = pass;
        this.salt = salt;
        this.rol = rol;
    }

    public Usuario(String usuario, int rol) {
        this.usuario = usuario;
        this.rol = rol;
    }

    public String getPass() {
        return pass;
    }

    public String getSalt() {
        return salt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }
}
