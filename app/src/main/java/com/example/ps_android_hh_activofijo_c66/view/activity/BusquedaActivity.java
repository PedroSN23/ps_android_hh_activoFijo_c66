package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.pp_android_handheld_library.controller.rfid.RFIDController;
import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.ControlButtonsCircular;
import com.example.pp_android_handheld_library.model.SGTIN_96_Partition;
import com.example.pp_android_handheld_library.model.TagBuscado;
import com.example.pp_android_handheld_library.model.Utils;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.pp_android_handheld_library.model.resources.IconGenericEnum;
import com.example.pp_android_handheld_library.model.resources.StatusIcon;
import com.example.pp_android_handheld_library.view.clases.ButtonsCicularViewHolder;
import com.example.pp_android_handheld_library.view.fragment.BusquedaFragment;
import com.example.pp_android_handheld_library.view.fragment.ControlsFragment;
import com.example.pp_android_handheld_library.view.herencia.RFIDBarcodeControllActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class BusquedaActivity extends RFIDBarcodeControllActivity {
    private ControlsFragment controlsFragment;
    private BusquedaFragment busquedaFragment;
    private String epcStr = "";
    private final Pattern gray = Pattern.compile("^[ASIDEVRFQ][0-9]{8}$");
    private final Pattern sscc = Pattern.compile("^[CG][0-9]{8}$");
    private final Pattern acfix = Pattern.compile("^[A-Z]{1,10}[0-9]{1,10}$");
    private final Pattern ascii = Pattern.compile("^[A-Z]{1,11}[0-9]{1,11}$");
    private final Pattern sgtin = Pattern.compile("^[0-9]{12,13}$");
    private final Pattern lpn = Pattern.compile("^PN[0-9]{11}$");
    private final Pattern rep = Pattern.compile("^[0-9]{8}$");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        String epc = bundle.getString("epc");
        if(epc!=null) {
            freeRfid(false);
            epcStr=epc;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void rfidReading(boolean b) {
    }

    @Override
    protected void onStartRFIDLectura(RFIDController rfidController) {
        String epc = busquedaFragment.getEpc();
        if(epc!=null) {
            selectBarcode(false);
            busquedaFragment.setTextInputLayoutEnabled(false);
            controlsFragment.setButtonPressed(1, true);
            startLocationAlt(epc);
        } else {
            selectBarcode(true);
            startLectura();
        }
    }

    @Override
    protected void onStopRFIDLectura(RFIDController rfidController) {
        String epc = busquedaFragment.getEpc();
        if(epc!=null) {
            busquedaFragment.setTextInputLayoutEnabled(true);
            controlsFragment.setButtonPressed(1, false);
            stopLocationAlt();
        }
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

    }

    @Override
    public void reportLoc(int i, ArrayList<TagBuscado> arrayList) {
        if(i<=100 && i>=0) {
            busquedaFragment.changeValue(i);
            if (i > 80) soundNear();
            else if (i > 50) soundMed();
            else if (i > 20) soundFar();
        }
    }

    @Override
    public void rfidSearching(boolean b) {
        if(b) {
            setStatusBarIcon(StatusIcon.reading);
            setStatusBarTextMessage("Searching...");
        } else {
            setStatusBarIcon(StatusIcon.ok);
            setStatusBarTextMessage("Connected");
        }
    }

    @Override
    public void reportBarcode(String s, String s1) {
        if(isBarcodeSelected()) {
            if (s1.compareTo("EAN13") == 0) {
                if (sgtin.matcher(s).matches()) {
                    dialogoPartition(s);
                    return;
                }
            } else {
                int len = s.length();
                switch (len) {
                    case 8: //repuve
                        if(rep.matcher(s).matches()) {
                            busquedaFragment.setTextInputEditText(Utils.getEPCRepuve(s));
                            soundTagRead();
                            busquedaFragment.setInputError(false);
                            selectBarcode(false);
                            return;
                        }
                        break;
                    case 9: //cont gray
                        if (gray.matcher(s).matches()) {
                            busquedaFragment.setTextInputEditText(Utils.getEPCGrai(s));
                            soundTagRead();
                            busquedaFragment.setInputError(false);
                            selectBarcode(false);
                            return;
                        } else {
                            if (sscc.matcher(s).matches()) {
                                busquedaFragment.setTextInputEditText(Utils.getEPCSSCC(s));
                                soundTagRead();
                                busquedaFragment.setInputError(false);
                                selectBarcode(false);
                                return;
                            }
                        }
                        break;
                    case 11:
                        if (acfix.matcher(s).matches()) {
                            busquedaFragment.setTextInputEditText(Utils.getEPCActifoFijo(s));
                            soundTagRead();
                            busquedaFragment.setInputError(false);
                            selectBarcode(false);
                            return;
                        }
                        break;
                    case 12:
                        if (ascii.matcher(s).matches()) {
                            busquedaFragment.setTextInputEditText(Utils.getEPCAscii(s));
                            soundTagRead();
                            busquedaFragment.setInputError(false);
                            selectBarcode(false);
                            return;
                        } else {
                            if (sgtin.matcher(s).matches()) {
                                dialogoPartition(s);
                                return;
                            }
                        }
                        break;
                    case 13:
                        if (lpn.matcher(s).matches()) {
                            busquedaFragment.setTextInputEditText(Utils.getEPCLpnPartial(s));
                            soundTagRead();
                            busquedaFragment.setInputError(false);
                            selectBarcode(false);
                            return;
                        } else {
                            if (sgtin.matcher(s).matches()) {
                                dialogoPartition(s);
                                return;
                            }
                        }
                        break;
                }
            }
            busquedaFragment.setInputError(true);
            soundError();
        }
    }

    private void dialogoPartition(String s) {
        soundTagRead();

        LayoutInflater li = LayoutInflater.from(BusquedaActivity.this);
        View promptsView = li.inflate(R.layout.edit_text_layout, null);

        TextInputEditText partNum = promptsView.findViewById(R.id.textPart);
        TextInputLayout textInputLayout = promptsView.findViewById(R.id.inpuLayout);
        textInputLayout.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(BusquedaActivity.this, ColorEnum.menu2p.getCode())));
        textInputLayout.setBoxStrokeColor(ContextCompat.getColor(BusquedaActivity.this, ColorEnum.menu2p.getCode()));
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(BusquedaActivity.this).setView(promptsView)
                .setTitle("Tag Partition")
                .setMessage("Ingrese la partición dle tag")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .show();

        Button b = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(view -> {
            @SuppressWarnings("ConstantConditions") String pnum = partNum.getText().toString();
            if(!pnum.isEmpty()) {
                try {
                    int p = Integer.parseInt(pnum);
                    if(p < SGTIN_96_Partition.values().length && p>=0) {
                        busquedaFragment.setTextInputEditText(Utils.convertEpcFromUpcSearch(p, s));
                        busquedaFragment.setInputError(false);
                        selectBarcode(false);
                        alertDialog.dismiss();
                    } else {
                        mostrarMensajeDeErrorDialog("La paritición deber ser un número entre 0 y "+(SGTIN_96_Partition.values().length-1));
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    mostrarMensajeDeErrorDialog(e.getMessage());
                }
            } else {
                mostrarMensajeDeErrorDialog("Partición vacía");
            }
        });
    }

    @Override
    public void barcodeConnectedAbs(boolean b) {
    }

    @Override
    public void barcodeEmpty() {
        if(isBarcodeSelected()) {
            soundError();
            busquedaFragment.setInputError(true);
        }
    }

    @Override
    protected Fragment setContentFragment() {
        busquedaFragment = BusquedaFragment.getInstance(subMenus, epcStr); //new BusquedaFragment();
        return busquedaFragment;
    }

    @Override
    protected Fragment setControlsFragment() {
        ArrayList<ControlButtonsCircular> controlButtons = new ArrayList<>();
        controlButtons.add(new ControlButtonsCircular(1, "BUSCAR",
                IconGenericEnum.flaticon_magnifying_glass,
                true,
                "FINALIZAR",
                true,
                subMenus.getGroupStyle().getColor()));
        controlsFragment = new ControlsFragment(controlButtons, subMenus.getGroupStyle());
        controlsFragment.addControlsFragmentAdapter((view, pressed) -> {
            if(!isProgresoVisible()) {
                ButtonsCicularViewHolder buttonsViewHolder = (ButtonsCicularViewHolder) view.getTag();
                //iniciar finalizar bla
                if (buttonsViewHolder.cb.getIndex() == 1) { //buscar
                    if (isRfidReady.get()) {
                        if (!toggleBut) {
                            toggleBut = true;
                            startLectura();
                        } else {
                            stopLectura();
                            toggleBut = false;
                        }
                    }
                }
                toggleShowMenuPack(controlsFragment);
            }
        });
        return controlsFragment;
    }
}
