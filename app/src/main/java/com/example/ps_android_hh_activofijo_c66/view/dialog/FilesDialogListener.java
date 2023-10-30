package com.example.ps_android_hh_activofijo_c66.view.dialog;

import com.example.ps_android_hh_activofijo_c66.model.Archivos;

public interface FilesDialogListener {
    void closeDialog();
    void processFile(Archivos a);
    void borrarArchivo(Archivos tempAr);
}
