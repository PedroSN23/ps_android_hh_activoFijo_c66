package com.example.ps_android_hh_activofijo_c66.controller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.ps_android_hh_activofijo_c66.controller.files.FileResp;
import com.example.ps_android_hh_activofijo_c66.model.DevicesLocal;
import com.example.ps_android_hh_activofijo_c66.view.activity.BarcodeActivity;
import com.example.ps_android_hh_activofijo_c66.view.activity.RFIDActivity;
import com.example.ps_android_hh_activofijo_c66.view.activity.ValidacionActivity;
import com.example.pp_android_handheld_library.controller.mail.MailResp;
import com.example.pp_android_handheld_library.view.herencia.GenericActivity;

public class MainHandler extends Handler {
    private final Activity activity;

    @SuppressWarnings("deprecation")
    public MainHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        DevicesLocal devices = DevicesLocal.none;
        for (DevicesLocal d : DevicesLocal.values()) {
            if (d.getCode() == msg.arg1) {
                devices = d;
                break;
            }
        }
        switch (devices) {
            case mail:
                handleMailMessage(msg);
                break;
            case file:
                handleFileMessage(msg);
                break;
        }
        super.handleMessage(msg);
    }

    private void handleMailMessage(Message msg) {
        Bundle bundle;
        switch (MailResp.valueOf((String) msg.obj)) {
            case error:
                bundle = msg.getData();
                ((GenericActivity)activity).setProgresoVisible(false);
                ((GenericActivity)activity).mostrarMensajeDeErrorDialog(bundle.getString("msg"));
                break;
            case ok:
                ((GenericActivity)activity).setProgresoVisible(false);
                ((GenericActivity) activity).mostrarMensajeExito("Correo enviado");
                break;
        }
    }

    private void handleFileMessage(Message msg) {
        Bundle bundle;
        switch (FileResp.valueOf((String) msg.obj)) {
            case ready:
                if(activity.getClass().getSimpleName().compareTo(ValidacionActivity.class.getSimpleName())==0) {
                    ((ValidacionActivity)activity).ready();
                }
                break;
            case excel_error:
                bundle = msg.getData();
                ((GenericActivity)activity).setProgresoVisible(false);
                ((GenericActivity) activity).mostrarMensajeDeErrorDialog(bundle.getString("msg"));
                break;
            case excel_ok:
                bundle = msg.getData();
                ((GenericActivity)activity).mostrarMensajeExito("Archivo creado");
                if(activity.getClass().getSimpleName().compareTo(RFIDActivity.class.getSimpleName())==0) {
                    ((RFIDActivity) activity).setFileExported(bundle.getStringArray("filepath"));
                } else {
                    if(activity.getClass().getSimpleName().compareTo(BarcodeActivity.class.getSimpleName())==0) {
                        ((BarcodeActivity) activity).setFileExported(bundle.getStringArray("filepath"));
                    } else {
                        if(activity.getClass().getSimpleName().compareTo(ValidacionActivity.class.getSimpleName())==0) {
                            ((ValidacionActivity) activity).setFileExported(bundle.getStringArray("filepath"));
                        }
                    }
                }
                break;
            case archborrado:
                if(activity.getClass().getSimpleName().compareTo(ValidacionActivity.class.getSimpleName())==0) {
                    ((ValidacionActivity)activity).archivoBorrado();
                }
                ((GenericActivity)activity).mostrarMensajeExito("Archivo borrado");
                break;
            case files:
                bundle = msg.getData();
                if(activity.getClass().getSimpleName().compareTo(ValidacionActivity.class.getSimpleName())==0) {
                    ((ValidacionActivity)activity).showDialogFiles(bundle.getParcelableArrayList("foundfiles"));
                }
                break;
            case respEnvio:
                break;
            case excel_content:
                bundle = msg.getData();
                if(activity.getClass().getSimpleName().compareTo(ValidacionActivity.class.getSimpleName())==0) {
                    ((ValidacionActivity)activity).detallesDeTabla(bundle.getParcelableArrayList("list"), bundle.getParcelable("file"));
                }
                break;
        }
    }
}
