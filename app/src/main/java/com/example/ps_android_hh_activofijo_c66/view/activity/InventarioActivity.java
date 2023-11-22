package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.controller.MainHandler;
import com.example.ps_android_hh_activofijo_c66.controller.files.FileController;
import com.example.ps_android_hh_activofijo_c66.model.clases.Cambios;
import com.example.ps_android_hh_activofijo_c66.model.clases.Configuracion;
import com.example.ps_android_hh_activofijo_c66.model.clases.Encabezados;
import com.example.ps_android_hh_activofijo_c66.model.clases.SyncDataSaveInven;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.fragment.InventarioFragment;
import com.example.ps_android_hh_activofijo_c66.view.fragment.RFIDFragment;
import com.example.pp_android_handheld_library.controller.mail.MailController;
import com.example.pp_android_handheld_library.controller.rfid.RFIDController;
import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.ControlButtonsCircular;
import com.example.pp_android_handheld_library.model.ReadingMode;
import com.example.pp_android_handheld_library.model.TagBuscado;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.pp_android_handheld_library.model.resources.IconGenericEnum;
import com.example.pp_android_handheld_library.model.resources.StatusIcon;
import com.example.pp_android_handheld_library.view.clases.ButtonsCicularViewHolder;
import com.example.pp_android_handheld_library.view.dialog.MailDialog;
import com.example.pp_android_handheld_library.view.fragment.ControlsFragment;
import com.example.pp_android_handheld_library.view.herencia.RFIDBarcodeControllActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class InventarioActivity extends RFIDBarcodeControllActivity {
    private InventarioFragment inventarioFragment;
    private ControlsFragment controlsFragment;
    private MailController mailController;
    private FileController fileController;
    private InterfazBD interfazBD;
    private final AtomicBoolean busy= new AtomicBoolean(false);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interfazBD = new InterfazBD(this);
        getProgresBar();

        MainHandler mainHandler1 = new MainHandler(this);
        fileController = FileController.getInstance(mainHandler1, this);
        mailController = MailController.getInstance(mainHandler1);
        selectBarcode(false);
    }
    public boolean handleBarcodeBasedOnSwitchState() {
        boolean switchState = inventarioFragment.estadoSwitch();
        selectBarcode(!switchState);
        return switchState;
    }
    @Override
    protected void onNextPressed() {

    }

    @Override
    protected void onPrevPressed() {
        onBackPressed();
        inventarioFragment.rutinaSalir();
    }

    @Override
    protected void onDestroy() {
        fileController.end();
        mailController.end();
        super.onDestroy();
    }

    @Override
    public void rfidReading(boolean b) {
        if(b) {
            setStatusBarIcon(StatusIcon.reading);
            setStatusBarTextMessage("Reading...");
        } else {
            setStatusBarIcon(StatusIcon.ok);
            setStatusBarTextMessage("Connected");
        }
    }

    @Override
    protected void onStartRFIDLectura(RFIDController rfidController) {
        controlsFragment.setButtonPressed(1, true);
        startRfidInventory(false,"",ReadingMode.epc,0,0);
    }

    @Override
    protected void onStopRFIDLectura(RFIDController rfidController) {
        controlsFragment.setButtonPressed(1, false);
        stopRfidInventory();
    }

    @Override
    public void setError(String s) {
        setStatusBarIcon(StatusIcon.error);
        setStatusBarTextMessage(s);
    }

    @Override
    public void writeSuccess() {

    }

    @Override
    public void writeError() {

    }

    @Override
    protected void rfidConnectedAbs(boolean b) {
        if(b) {
            setStatusBarIcon(StatusIcon.ok);
            setStatusBarTextMessage("Connected");
        } else {
            setStatusBarIcon(StatusIcon.error);
            setStatusBarTextMessage("Not connected");
        }
    }

    @Override
    public void writingStarted(boolean b) {

    }

    @Override
    public void sendWarning(String s) {
        setStatusBarIcon(StatusIcon.warning);
        setStatusBarTextMessage(s);
    }

    @Override
    public void reportTag(UHFTagsRead uhfTagsRead) {
        if(inventarioFragment!=null && handleBarcodeBasedOnSwitchState()) {
            inventarioFragment.sendTagsAlt(uhfTagsRead);
        }
    }

    @Override
    public void reportLoc(int i, ArrayList<TagBuscado> arrayList) {

    }

    @Override
    public void rfidSearching(boolean b) {
    }

    @Override
    public void barcodeReading(boolean b) {
        if(isBarcodeSelected()) {
            if (b) {
                controlsFragment.setButtonPressed(1, true);
                setStatusBarIcon(StatusIcon.readingb);
                setStatusBarTextMessage("Reading...");
            } else {
                controlsFragment.setButtonPressed(1, false);
                setStatusBarIcon(StatusIcon.ok);
                setStatusBarTextMessage("Connected");
            }
        }
    }
    @Override
    public void reportBarcode(String s, String s1) {
        if (!handleBarcodeBasedOnSwitchState()) {
            inventarioFragment.BarcodeOperation(s);
        }
    }

    @Override
    public void barcodeConnectedAbs(boolean b) {
    }


    @Override
    public void barcodeEmpty() {
    }

    @Override
    protected Fragment setContentFragment() {
        inventarioFragment = new InventarioFragment();
        return inventarioFragment;
    }

    @Override
    protected Fragment setControlsFragment() {
        ArrayList<ControlButtonsCircular> controlButtons = new ArrayList<>();
        controlButtons.add(new ControlButtonsCircular(1, "INICIAR",
                IconGenericEnum.fontawesome_wifi,
                true,
                "FINALIZAR",
                true,
                ColorEnum.menu3p.getCode()));
        controlButtons.add(new ControlButtonsCircular(2, "GUARDAR",
                IconGenericEnum.fontawesome_save,
                false,
                "GUARDAR",
                false,
                ColorEnum.status_orange.getCode()));
        controlButtons.add(new ControlButtonsCircular(3, "REINICIAR",
                IconGenericEnum.fontawesome_recycle,
                false,
                "REINICIAR",
                false,
                ColorEnum.status_blue.getCode()));
        controlButtons.add(new ControlButtonsCircular(4, "EXPORTAR",
                IconGenericEnum.fontawesome_file_excel,
                false,
                "EXPORTAR",
                false,
                ColorEnum.excel.getCode()));

        controlsFragment = new ControlsFragment(controlButtons, subMenus.getGroupStyle());
        controlsFragment.addControlsFragmentAdapter((view, pressed) -> {
            if(!isProgresoVisible()) {
                ButtonsCicularViewHolder buttonsViewHolder = (ButtonsCicularViewHolder) view.getTag();
                switch (buttonsViewHolder.cb.getIndex()) {
                    case 1: //iniciar finalizar bla
                        if (isRfidReady.get()) {
                            if (!toggleBut) {
                                toggleBut = true;
                                startLectura();
                            } else {
                                stopLectura();
                                toggleBut = false;
                            }
                        }
                        break;
                    case 2:
                        inventarioFragment.GuardarFinalizar();
                         break;
                    case 3:
                        inventarioFragment.ReiniciarInventario();
                        break;
                    case 4:
                       SyncDataSaveInven syncDataSaveInv = new SyncDataSaveInven(this, busy, interfazBD);
                        if(syncDataSaveInv.getError()) {
                            Toast.makeText(this, syncDataSaveInv.getErrMsg(), Toast.LENGTH_LONG).show();
                        } else {
                            busy.set(true);
                            syncDataSaveInv.execute();
                        }
                        break;
                }
                toggleShowMenuPack(controlsFragment);
            }
        });
        return controlsFragment;
    }
}
