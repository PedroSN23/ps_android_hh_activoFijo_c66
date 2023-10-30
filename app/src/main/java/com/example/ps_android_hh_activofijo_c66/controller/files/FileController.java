package com.example.ps_android_hh_activofijo_c66.controller.files;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.ps_android_hh_activofijo_c66.model.Archivos;

import java.lang.ref.WeakReference;

public class FileController extends Thread {
    private static FileController fileController;
    private static FileHandler fileHandler;
    private WeakReference<Handler> mainHandler;
    private WeakReference<Context> contextWeakReference;

    public static FileController getInstance(Handler mainHandler, Context context) {
        if(fileController ==null) {
            fileController = new FileController();
            fileController.setMainHandler(mainHandler, context);
            fileController.start();
        } else {
            fileController.setMainHandler(mainHandler, context);
        }
        return fileController;
    }

    private FileController() {
    }

    public void setMainHandler(Handler mainHandler, Context context) {
        this.mainHandler = new WeakReference<>(mainHandler);
        this.contextWeakReference = new WeakReference<>(context);
        if(fileHandler!=null) {
            fileHandler.setMainHandler(mainHandler, context);
        }
    }

    @Override
    public void run() {
        Looper.prepare();

        fileHandler = new FileHandler();
        if(mainHandler.get()!=null) {
            fileHandler.setMainHandler(mainHandler.get(), contextWeakReference.get());
        }
        fileReady();

        Looper.loop();
    }

    private void fileReady() {
        if(fileHandler!=null) {
            Message message = fileHandler.obtainMessage();
            message.obj = FileInst.ready.name();
            fileHandler.sendMessage(message);
        }
    }
    public void end() {
        if(fileHandler!=null) {
            Message message = fileHandler.obtainMessage();
            message.obj = FileInst.end.name();
            fileHandler.sendMessage(message);
            fileController = null;
            fileHandler = null;
        }
    }

    public void borrarArchivo(Archivos tempAr) {
        if(fileHandler!=null) {
            Message message = fileHandler.obtainMessage();
            message.obj = FileInst.borrarArchivo.name();
            Bundle bundle = new Bundle();
            bundle.putParcelable("file", tempAr);
            message.setData(bundle);
            fileHandler.sendMessage(message);
        }
    }

    public void obtenerArchivos(String terminacion, String folder) {
        if(fileHandler!=null) {
            Message message = fileHandler.obtainMessage();
            message.obj = FileInst.obtener_files.name();
            Bundle bundle = new Bundle();
            bundle.putString("folder", folder);
            bundle.putString("terminacion", terminacion);
            message.setData(bundle);
            fileHandler.sendMessage(message);
        }
    }

    public void generarArchivoExcel(String[][] values, String[] headers, String title, String folder) {
        if(fileHandler!=null) {
            Message message = fileHandler.obtainMessage();
            message.obj = FileInst.excel.name();
            Bundle bundle = new Bundle();
            bundle.putSerializable("values", values);
            bundle.putStringArray("headers", headers);
            bundle.putString("title", title);
            bundle.putString("folder", folder);
            message.setData(bundle);
            fileHandler.sendMessage(message);
        }
    }

    public void loadFileContent(FileInst fi, Archivos a, String[] headers) {
        if(fileHandler!=null) {
            Message message = fileHandler.obtainMessage();
            message.obj = fi.name();
            Bundle bundle = new Bundle();
            bundle.putParcelable("file", a);
            bundle.putStringArray("headers", headers);
            message.setData(bundle);
            fileHandler.sendMessage(message);
        }
    }
}
