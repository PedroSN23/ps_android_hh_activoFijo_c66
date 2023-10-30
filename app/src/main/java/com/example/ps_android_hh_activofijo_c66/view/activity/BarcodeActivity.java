package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.controller.MainHandler;
import com.example.ps_android_hh_activofijo_c66.controller.files.FileController;
import com.example.ps_android_hh_activofijo_c66.model.BarcodeData;
import com.example.ps_android_hh_activofijo_c66.view.fragment.BarcodeFragment;
import com.example.pp_android_handheld_library.controller.mail.MailController;
import com.example.pp_android_handheld_library.controller.rfid.RFIDController;
import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.ControlButtonsCircular;
import com.example.pp_android_handheld_library.model.TagBuscado;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.pp_android_handheld_library.model.resources.IconGenericEnum;
import com.example.pp_android_handheld_library.model.resources.StatusIcon;
import com.example.pp_android_handheld_library.view.clases.ButtonsCicularViewHolder;
import com.example.pp_android_handheld_library.view.dialog.MailDialog;
import com.example.pp_android_handheld_library.view.fragment.ControlsFragment;
import com.example.pp_android_handheld_library.view.herencia.RFIDBarcodeControllActivity;

import java.util.ArrayList;
import java.util.Locale;

public class BarcodeActivity extends RFIDBarcodeControllActivity {
    private ControlsFragment controlsFragment;
    private BarcodeFragment bacodeFragment;
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
    protected void onDestroy() {
        fileController.end();
        mailController.end();
        super.onDestroy();
    }

    @Override
    public void rfidReading(boolean b) {
    }

    @Override
    protected void onStartRFIDLectura(RFIDController rfidController) {
    }

    @Override
    protected void onStopRFIDLectura(RFIDController rfidController) {
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
    protected void rfidConnectedAbs(boolean b) {
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
    }

    @Override
    public void reportLoc(int i, ArrayList<TagBuscado> arrayList) {

    }

    @Override
    public void rfidSearching(boolean b) {
    }

    @Override
    public void barcodeReading(boolean b) {
        if(b) {
            controlsFragment.setButtonPressed(1, true);
            setStatusBarIcon(StatusIcon.readingb);
            setStatusBarTextMessage("Reading...");
        } else {
            controlsFragment.setButtonPressed(1, false);
            setStatusBarIcon(StatusIcon.ok);
            setStatusBarTextMessage("Connected");
        }
    }

    @Override
    public void reportBarcode(String s, String s1) {
        soundTagRead();
        bacodeFragment.sendTagsAlt(s, s1);
        toggleBut=false;
    }

    @Override
    public void barcodeConnectedAbs(boolean b) {
        if(b) {
            setStatusBarIcon(StatusIcon.ok);
            setStatusBarTextMessage("Connected");
        } else {
            setStatusBarIcon(StatusIcon.error);
            setStatusBarTextMessage("Not Connected");
        }
    }

    @Override
    public void barcodeEmpty() {
        toggleBut=false;
        soundError();
    }

    @Override
    protected Fragment setContentFragment() {
        bacodeFragment = new BarcodeFragment();
        return bacodeFragment;
    }

    @Override
    protected Fragment setControlsFragment() {
        ArrayList<ControlButtonsCircular> controlButtons = new ArrayList<>();
        controlButtons.add(new ControlButtonsCircular(1, "INICIAR",
                IconGenericEnum.flaticon_barcode,
                true,
                "FINALIZAR",
                true,
                ColorEnum.menu3p.getCode()));
        controlButtons.add(new ControlButtonsCircular(2, "EXPORTAR",
                IconGenericEnum.flaticon_excel,
                false,
                "EXPORTAR",
                false,
                ColorEnum.excel.getCode()));
        controlButtons.add(new ControlButtonsCircular(3, "BORRAR",
                IconGenericEnum.fontawesome_trash,
                false,
                "BORRAR",
                false,
                ColorEnum.sobrante.getCode()));

        controlsFragment = new ControlsFragment(controlButtons, subMenus.getGroupStyle());
        controlsFragment.addControlsFragmentAdapter((view, pressed) -> {
            if(!isProgresoVisible()) {
                ButtonsCicularViewHolder buttonsViewHolder = (ButtonsCicularViewHolder) view.getTag();
                switch (buttonsViewHolder.cb.getIndex()) {
                    case 1: //iniciar finalizar bla
                        if (isBarcodeReady.get()) {
                            if (!toggleBut) {
                                toggleBut = true;
                                startLectura();
                            } else {
                                stopLectura();
                                toggleBut = false;
                            }
                        }
                        break;
                    case 2: //exportar
                        ArrayList<BarcodeData> barcodeDataArrayList = bacodeFragment.getList();
                        if(barcodeDataArrayList.size()>0) {
                            fileController.generarArchivoExcel(obtenerArreglo(barcodeDataArrayList),
                                    getResources().getStringArray(R.array.encabezados_barcode),
                                    "Lecturas_Barcode",
                                    "barcode");
                            setProgresoVisible(true);
                        } else {
                            mostrarMensajeDeErrorDialog("No hay lecturas");
                        }
                        break;
                    case 3: //limpiar
                        bacodeFragment.clearList();
                        break;
                }
                toggleShowMenuPack(controlsFragment);
            }
        });
        return controlsFragment;
    }

    private String[][] obtenerArreglo(ArrayList<BarcodeData> barcodeData) {
        String[][] valores = new String[barcodeData.size()][3];
        int i=0;
        for(BarcodeData bcd: barcodeData) {
            valores[i][0]=bcd.getValue();
            valores[i][1]=bcd.getName();
            valores[i][2]=String.format(Locale.getDefault(), "%d", bcd.getCant());
            i++;
        }
        return valores;
    }

    public void setFileExported(String[] msg) {
        setProgresoVisible(false);
        FragmentManager fm = getSupportFragmentManager();
        MailDialog mailDialog = new MailDialog();
        mailDialog.setMailDialogListener(s -> {
            setProgresoVisible(true);
            mailController.enviarCorreoAdjunto(s, msg[1], msg[0],"Archivo Lectura", "Se anexa el archivo de la lectura realizada por la terminal móvil");
        });
        mailDialog.show(fm, "dialog");
    }
}
