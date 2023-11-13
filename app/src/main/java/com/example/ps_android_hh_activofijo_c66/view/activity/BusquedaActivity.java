package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.poi.hpsf.Util;

import java.util.ArrayList;
import java.util.Locale;
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
    private RadioGroup[] rg;
    private ArrayList<String> filtros;
    private Context context;
    private int inpSel;
    private InterfazBD interfazBD;
    private EditText userinput;
    private String epcFin;
    private String Vfinal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        interfazBD = new InterfazBD(this);
        context = this;
        Bundle bundle = getIntent().getExtras();
        String epc = bundle.getString("epc");
        if(epc!=null) {
            freeRfid(false);
            epcStr=epc;
        }
        filtros = interfazBD.obtenerFiltros();
        super.onCreate(savedInstanceState);
        crearDialogo();
    }

    @Override
    protected void onNextPressed() {

    }

    @Override
    protected void onPrevPressed() {
        onBackPressed();
    }

    @Override
    public void rfidReading(boolean b) {
    }

    @Override
    protected void onStartRFIDLectura(RFIDController rfidController) {
        String epc = Vfinal;
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
        String epc = Vfinal;
        if(epc!=null) {
            busquedaFragment.setTextInputLayoutEnabled(false);
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
        controlButtons.add(new ControlButtonsCircular(2, "CAMBIAR",
                IconGenericEnum.fontawesome_edit,
                false,
                "CAMBIAR",
                false,
                subMenus.getGroupStyle().getColor()));
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
                        crearDialogo();
                        break;
                       }
                toggleShowMenuPack(controlsFragment);
            }
        });
        return controlsFragment;
    }
    public void crearDialogo() {
        View promptsView = View.inflate(context, R.layout.dialog_buscar_tag, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setView(promptsView);

        final LinearLayout radioPadre = promptsView.findViewById(R.id.radioPadre);

        final RadioButton[] rb = new RadioButton[filtros.size()];

        rg = new RadioGroup[(filtros.size()/4)+1];

        if(filtros.size()>0) {
            int j=0;
            for (int i = 0; i < filtros.size(); i++) {
                if(i%4==0) {
                    if(i!=0) {
                        radioPadre.addView(rg[j]);
                        j++;
                    }
                    rg[j] = (RadioGroup) View.inflate(context, R.layout.radiogroup, null);
                }
                rb[i] = (RadioButton) View.inflate(context, R.layout.radiobutton, null);
                rb[i].setText(filtros.get(i));
                rb[i].setTag(i);
                rg[j].addView(rb[i]);
            }
            if(filtros.size()>0) {
                radioPadre.addView(rg[j]);
                j++;
                for(int i=0; i<j; i++) {
                    rg[i].clearCheck();
                    rg[i].setTag(i);
                    rg[i].setOnCheckedChangeListener(listener);
                }

            }
            rb[inpSel].setChecked(true);
        } else {
            Toast.makeText(context, getResources().getString(R.string.errFiltEmpty), Toast.LENGTH_LONG).show();
            BusquedaActivity.this.finish();
        }

        userinput = promptsView.findViewById(R.id.editTextDialogUserInput);

        InputFilter inputFilter = (charSequence, start, fin, dest, dstart, dfin) -> {
            String tmpEnc = rb[inpSel].getText().toString();
            int lenFilt = 11-tmpEnc.length();
            if(lenFilt>0) {
                if (dfin > lenFilt) return "";
                for (int i = start; i < fin; i++) {
                    if (!Character.isDigit(charSequence.charAt(i))) {
                        return "";
                    }
                }
            } else {
                return "";
            }
            return null;
        };
        userinput.setFilters(new InputFilter[]{inputFilter});
        userinput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        alertDialogBuilder.setPositiveButton("OK", null);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(dialogInterface -> {
            Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setTextColor(ContextCompat.getColor(context, R.color.black));
            button.setOnClickListener(view -> {
                String idTxt = userinput.getText().toString();
                if(!idTxt.isEmpty()) {
                    String encTxt = rb[inpSel].getText().toString();
                    int lenFilt = 11-encTxt.length();
                    if(lenFilt>0) {
                        if (Pattern.matches(String.format(Locale.getDefault(), "^\\d{1,%d}$", lenFilt), idTxt)) {
                            epcFin = String.format("%s%0"+lenFilt+"d", encTxt, Integer.parseInt(idTxt));
                            busquedaFragment.setTextInputEditText(String.format(Locale.getDefault(), "ID: %s", epcFin));
                            Vfinal = Utils.getEPCActifoFijo(epcFin);
                            busquedaFragment.setTextInputLayoutEnabled(false);
                            alertDialog.dismiss();
                        }
                    }
                }
            });
        });

        alertDialog.setOnCancelListener(dialog -> BusquedaActivity.this.finish());

        alertDialog.show();
    }

    private final RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId != -1) {
                int tag = (int) group.getTag();
                for(int i=0; i<(filtros.size()/4)+1; i++) {
                    if(i!=tag) {
                        rg[i].setOnCheckedChangeListener(null);
                        rg[i].clearCheck();
                        rg[i].setOnCheckedChangeListener(listener);
                    }
                }
            }
        }
    };
    public void onRadioButClick(View view) {
        RadioButton rb = (RadioButton) view;
        inpSel = (int) rb.getTag();
    }
}
