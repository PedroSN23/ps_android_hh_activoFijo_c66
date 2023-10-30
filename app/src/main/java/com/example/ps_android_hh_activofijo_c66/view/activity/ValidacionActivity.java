package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.controller.MainHandler;
import com.example.ps_android_hh_activofijo_c66.controller.files.FileController;
import com.example.ps_android_hh_activofijo_c66.controller.files.FileInst;
import com.example.ps_android_hh_activofijo_c66.model.Archivos;
import com.example.ps_android_hh_activofijo_c66.model.UHFTagsGroup;
import com.example.ps_android_hh_activofijo_c66.view.dialog.FilesDialog;
import com.example.ps_android_hh_activofijo_c66.view.dialog.FilesDialogListener;
import com.example.ps_android_hh_activofijo_c66.view.fragment.ValidacionFragment;
import com.example.pp_android_handheld_library.controller.mail.MailController;
import com.example.pp_android_handheld_library.controller.rfid.RFIDController;
import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.ControlButtonsCircular;
import com.example.pp_android_handheld_library.model.ReadingMode;
import com.example.pp_android_handheld_library.model.TagBuscado;
import com.example.pp_android_handheld_library.model.TagsTipo;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.pp_android_handheld_library.model.resources.IconGenericEnum;
import com.example.pp_android_handheld_library.model.resources.StatusIcon;
import com.example.pp_android_handheld_library.view.clases.ButtonsCicularViewHolder;
import com.example.pp_android_handheld_library.view.dialog.MailDialog;
import com.example.pp_android_handheld_library.view.fragment.ControlsFragment;
import com.example.pp_android_handheld_library.view.herencia.RFIDBarcodeControllActivity;

import java.util.ArrayList;
import java.util.Locale;

public class ValidacionActivity extends RFIDBarcodeControllActivity {
    private ValidacionFragment validacionFragment;
    private ControlsFragment controlsFragment;
    private MailController mailController;
    private FileController fileController;
    private FilesDialog filesDialog;
    private Archivos archName =null;
    private boolean validateRepuve = false;

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
        if(validacionFragment!=null) {
            validacionFragment.clearSort();
        }
        controlsFragment.setButtonPressed(1, true);
        if(validateRepuve) {
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
        if(validacionFragment!=null) {
            if(validacionFragment.sendTagsAlt(uhfTagsRead)) {
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
    }

    @Override
    public void barcodeConnectedAbs(boolean b) {
    }


    @Override
    public void barcodeEmpty() {
    }

    @Override
    protected Fragment setContentFragment() {
        validacionFragment = new ValidacionFragment();
        validacionFragment.addValidacionFragmentListener(epc -> {
            Intent intent = new Intent(ValidacionActivity.this, BusquedaActivity.class);
            intent.putExtra("submenu", subMenus);
            intent.putExtra("devices", subMenus);
            intent.putExtra("epc", epc);
            someActivityResultLauncher.launch(intent);
        });
        return validacionFragment;
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
            result -> setMainHandler()
    );

    @Override
    protected Fragment setControlsFragment() {
        ArrayList<ControlButtonsCircular> controlButtons = new ArrayList<>();
        controlButtons.add(new ControlButtonsCircular(1, "INICIAR",
                IconGenericEnum.fontawesome_wifi,
                true,
                "FINALIZAR",
                true,
                ColorEnum.menu1p.getCode()));
        controlButtons.add(new ControlButtonsCircular(2, "DIALOGO",
                IconGenericEnum.fontawesome_window_restore,
                false,
                "DIALOGO",
                false,
                ColorEnum.menu1p.getCode()));
        controlButtons.add(new ControlButtonsCircular(3, "FILTRAR",
                IconGenericEnum.flaticon_filter,
                true,
                "NO FILTRAR",
                true,
                ColorEnum.menu1p.getCode()));
        controlButtons.add(new ControlButtonsCircular(4, "GUARDAR",
                IconGenericEnum.fontawesome_save,
                false,
                "GUARDAR",
                false,
                ColorEnum.status_purple.getCode()));
        controlButtons.add(new ControlButtonsCircular(5, "LIMPIAR",
                IconGenericEnum.fontawesome_broom,
                false,
                "LIMPIAR",
                false,
                ColorEnum.faltante.getCode()));
        controlButtons.add(new ControlButtonsCircular(6, "REINICIAR",
                IconGenericEnum.fontawesome_trash,
                false,
                "REINICIAR",
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
                    case 2: //dialogo
                        ready();
                        break;
                    case 3: //filter
                        if(buttonsViewHolder.cb.isPressed()) {
                            controlsFragment.setButtonPressed(3, false);
                            if(validacionFragment!=null) {
                                validacionFragment.setFilterTags(false);
                            }
                        } else {
                            controlsFragment.setButtonPressed(3, true);
                            if(validacionFragment!=null) {
                                validacionFragment.setFilterTags(true);
                                validacionFragment.cleanList();
                            }
                        }
                        break;
                    case 4: //guardar
                        fileController.generarArchivoExcel(obtenerArreglo(validacionFragment.getList()),
                                getResources().getStringArray(R.array.encabezados_rfid),
                                archName.getName(),
                                "rfid");
                        setProgresoVisible(true);
                        break;
                    case 5: //limpiar
                        validacionFragment.cleanList();
                        break;
                    case 6: //reiniciar
                        validacionFragment.clearList();
                        break;
                }
                toggleShowMenuPack(controlsFragment);
            }
        });
        return controlsFragment;
    }

    public void ready() {
        fileController.obtenerArchivos(".xlsx", "rfid/");
    }

    public void showDialogFiles(ArrayList<Archivos> foundfiles) {
        FragmentManager fm = getSupportFragmentManager();
        filesDialog = FilesDialog.newInstance(ValidacionActivity.this,
                foundfiles);
        filesDialog.setCancelable(false);
        filesDialog.addFilesDialogListener(new FilesDialogListener() {
            @Override
            public void closeDialog() {
                filesDialog.dismiss();
                setProgresoVisible(false);
                if(archName==null) {
                    onBackPressed();
                }
            }

            @Override
            public void processFile(Archivos a) {
                filesDialog.dismiss();
                fileController.loadFileContent(FileInst.xlsx_content, a, getResources().getStringArray(R.array.encabezados_rfid));
            }


            @Override
            public void borrarArchivo(Archivos tempAr) {
                fileController.borrarArchivo(tempAr);
            }
        });
        filesDialog.show(fm, "dialog");
    }

    private String[][] obtenerArreglo(ArrayList<UHFTagsGroup> uhfTagsReadArrayList) {
        int size=0;
        for(UHFTagsGroup utr: uhfTagsReadArrayList) {
            size+=utr.getSize();
        }
        String[][] valores = new String[size][6];
        int i=0;
        for(UHFTagsGroup utr: uhfTagsReadArrayList) {
            for(int x=0; x<utr.getSize(); x++) {
                valores[i][0] = utr.getEpcAt(x);
                valores[i][1] = utr.getDetail();
                valores[i][2] = String.format(Locale.getDefault(), "%d", utr.getCantidadAt(x));
                valores[i][3] = utr.getRssiAt(x);
                valores[i][4] = utr.getTagsTipo().name();
                valores[i][5] = String.format(Locale.getDefault(), "%d", utr.getInventariadoAt(x));
                i++;
            }
        }
        return valores;
    }

    public void setFileExported(String[] msg) {
        setProgresoVisible(false);
        FragmentManager fm = getSupportFragmentManager();
        MailDialog mailDialog = new MailDialog();
        mailDialog.setMailDialogListener(s -> {
            setProgresoVisible(true);
            mailController.enviarCorreoAdjunto(s, msg[1], msg[0],"Archivo Validado", "Se anexa el archivo de la validación realizada por la terminal móvil");
        });
        mailDialog.show(fm, "dialog");
    }

    public void archivoBorrado() {
        if(filesDialog!=null) {
            filesDialog.notBusy();
        }
    }

    public void detallesDeTabla(ArrayList<UHFTagsGroup> uhfTagsGroups, Archivos file) {
        this.archName = file;
        if(validacionFragment!=null) {
            if(uhfTagsGroups.size()>0) {
                validateRepuve = (uhfTagsGroups.get(0).getTagsTipo()== TagsTipo.repuve);
            }
            validacionFragment.detallesDeTabla(uhfTagsGroups);
        }
    }
}
