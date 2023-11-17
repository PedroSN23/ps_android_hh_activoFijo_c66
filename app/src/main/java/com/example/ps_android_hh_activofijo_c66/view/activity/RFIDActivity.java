package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.app.AlertDialog;
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

public class RFIDActivity extends RFIDBarcodeControllActivity {
    private RFIDFragment rfidFragment;
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
        if(rfidFragment.isRepuveFiltered()) {
            startRfidInventory(false, "", ReadingMode.epc_tid_user, 4, 9);
        } else {
            startRfidInventory(false, "", ReadingMode.epc, 0, 0);
        }
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
        if(rfidFragment!=null) {
            if(rfidFragment.sendTagsAlt(uhfTagsRead)) {
                soundTagRead();
            }
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
        System.out.println("String " +s+ " Y EL OTRO ES " +s1);
    }

    @Override
    public void barcodeConnectedAbs(boolean b) {
    }


    @Override
    public void barcodeEmpty() {
    }

    @Override
    protected Fragment setContentFragment() {
        rfidFragment = new RFIDFragment();
        return rfidFragment;
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
        controlButtons.add(new ControlButtonsCircular(2, "EXPORTAR",
                IconGenericEnum.fontawesome_file_excel,
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
                        ArrayList<UHFTagsRead> uhfTagsReadArrayList = rfidFragment.getList();
                        if(uhfTagsReadArrayList.size()>0) {
                            solicitarNombreArchivo(uhfTagsReadArrayList);
                        } else {
                            mostrarMensajeDeErrorDialog("No hay lecturas");
                        }
                        break;
                    case 3: //limpiar
                        rfidFragment.clearList();
                        break;
                }
                toggleShowMenuPack(controlsFragment);
            }
        });
        return controlsFragment;
    }

    private void solicitarNombreArchivo(ArrayList<UHFTagsRead> uhfTagsReadArrayList) {
        LayoutInflater li = LayoutInflater.from(RFIDActivity.this);
        View promptsView = li.inflate(R.layout.archivos_prompt_export, null);

        TextInputEditText nombreArchivoEt = promptsView.findViewById(R.id.textArchivo);
        TextInputLayout textInputLayout = promptsView.findViewById(R.id.inputArchivo);
        textInputLayout.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(RFIDActivity.this, ColorEnum.menu3p.getCode())));
        textInputLayout.setBoxStrokeColor(ContextCompat.getColor(RFIDActivity.this, ColorEnum.menu3p.getCode()));
        final AlertDialog alertDialog = new AlertDialog.Builder(RFIDActivity.this).setView(promptsView)
                .setTitle("Nombre de Archivo")
                .setMessage("Ingrese el nombre del archivo a exportar")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .show();

        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(view -> {
            @SuppressWarnings("ConstantConditions") String archName = nombreArchivoEt.getText().toString();
            if(!archName.isEmpty()) {
                fileController.generarArchivoExcel(obtenerArreglo(uhfTagsReadArrayList),
                        getResources().getStringArray(R.array.encabezados_rfid),
                        archName,
                        "rfid");
                setProgresoVisible(true);
                alertDialog.dismiss();
            } else {
                mostrarMensajeDeErrorDialog("Nombre de archivo vacío");
            }
        });
    }

    private String[][] obtenerArreglo(ArrayList<UHFTagsRead> uhfTagsReadArrayList) {
        String[][] valores = new String[uhfTagsReadArrayList.size()][6];
        int i=0;
        for(UHFTagsRead utr: uhfTagsReadArrayList) {
            valores[i][0]=utr.getEpc();
            valores[i][1]=utr.getDetail();
            valores[i][2]=String.format(Locale.getDefault(), "%d", utr.getCantidad());
            valores[i][3]=utr.getRssi();
            valores[i][4]=utr.getTipo().name();
            valores[i][5]=String.format(Locale.getDefault(), "%d", utr.getInventariado());
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
