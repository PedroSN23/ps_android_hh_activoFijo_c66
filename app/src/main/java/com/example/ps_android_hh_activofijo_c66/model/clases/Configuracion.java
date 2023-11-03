package com.example.ps_android_hh_activofijo_c66.model.clases;

public class Configuracion {
    private boolean result;
    private boolean fecha;
    private String archivoInName;
    private String archivoInPath;
    private String prefijoOut;

    public Configuracion(int result, int fecha, String archivoInName, String archivoInPath, String prefijoOut) {
        this.result = (result==1);
        this.fecha = (fecha==1);
        this.archivoInName = archivoInName;
        this.archivoInPath = archivoInPath;
        this.prefijoOut = prefijoOut;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isFecha() {
        return fecha;
    }

    public void setFecha(boolean fecha) {
        this.fecha = fecha;
    }

    public String getArchivoInName() {
        return archivoInName;
    }

    public void setArchivoInName(String archivoInName) {
        this.archivoInName = archivoInName;
    }

    public String getArchivoInPath() {
        return archivoInPath;
    }

    public void setArchivoInPath(String archivoInPath) {
        this.archivoInPath = archivoInPath;
    }

    public String getPrefijoOut() {
        return prefijoOut;
    }

    public void setPrefijoOut(String prefijoOut) {
        this.prefijoOut = prefijoOut;
    }
}
