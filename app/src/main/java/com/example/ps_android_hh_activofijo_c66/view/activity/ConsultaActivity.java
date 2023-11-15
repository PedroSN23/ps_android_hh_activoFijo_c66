package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.controller.MainHandler;
import com.example.ps_android_hh_activofijo_c66.controller.files.FileController;
import com.example.ps_android_hh_activofijo_c66.view.fragment.ConsultaFragment;
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

import java.util.ArrayList;
import java.util.Locale;

public class ConsultaActivity extends RFIDBarcodeControllActivity {
    private ConsultaFragment consultaFragment;
    private ControlsFragment controlsFragment;
    private MailController mailController;
    private FileController fileController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getProgresBar();

        MainHandler mainHandler1 = new MainHandler(this);
        fileController = FileController.getInstance(mainHandler1, this);
        mailController = MailController.getInstance(mainHandler1);
    }

    @Override
    protected void onNextPressed() {

    }

    @Override
    protected void onPrevPressed() {
        onBackPressed();
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
        if(consultaFragment!=null) {

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
    }

    @Override
    public void reportBarcode(String s, String s1) {
    }

    @Override
    public void barcodeConnectedAbs(boolean b) {
    }


    @Override
    public void barcodeEmpty() {
    }

    @Override
    protected Fragment setContentFragment() {
        consultaFragment = new ConsultaFragment();
        return consultaFragment;
    }

    @Override
    protected Fragment setControlsFragment() {
        ArrayList<ControlButtonsCircular> controlButtons = new ArrayList<>();
        controlButtons.add(new ControlButtonsCircular(1, "INICIAR",
                IconGenericEnum.fontawesome_wifi,
                true,
                "FINALIZAR",
                true,
                ColorEnum.bien.getCode()));
        controlButtons.add(new ControlButtonsCircular(2, "CAMBIAR",
                IconGenericEnum.fontawesome_edit,
                false,
                "CAMBIAR",
                false,
                ColorEnum.status_blue.getCode()));
        controlButtons.add(new ControlButtonsCircular(3, "GUARDAR",
                IconGenericEnum.fontawesome_save,
                false,
                "GUARDAR",
                false,
                ColorEnum.amarillo.getCode()));
        controlButtons.add(new ControlButtonsCircular(4, "BUSCAR",
                IconGenericEnum.fontawesome_search,
                false,
                "BUSCAR",
                false,
                ColorEnum.status_orange.getCode()));

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
                        consultaFragment.AbrirDialogo();
                        break;
                    case 3:
                        consultaFragment.GuardarFormularioForm();
                        break;
                    case 4:
                        String activo = consultaFragment.getActiveText();
                        Intent intent = new Intent(ConsultaActivity.this, BusquedaActivity.class);
                        intent.putExtra("ACT", activo);
                        startActivity(intent);
                        break;
                }
                toggleShowMenuPack(controlsFragment);
            }
        });
        return controlsFragment;
    }
}
