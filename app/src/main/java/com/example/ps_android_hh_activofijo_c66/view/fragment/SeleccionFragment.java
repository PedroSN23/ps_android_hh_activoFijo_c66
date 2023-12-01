package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.activity.SeleccionActivity;

public class SeleccionFragment extends Fragment {
    private AlertDialog dialog;
    private InterfazBD interfazBD;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interfazBD = new InterfazBD(getActivity());
        seleccionarArchivo();
    }

    public void seleccionarArchivo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Selecciona la obtención de los activos");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.seleccion_layout, null);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        Boolean active = interfazBD.obtenerModo();
        Switch switchButton = view.findViewById(R.id.switchButton);
        LinearLayout okButton = view.findViewById(R.id.okButton);
        LinearLayout exitButton = view.findViewById(R.id.closeButton);
        switchButton.setChecked(active);

        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);


        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (vibrator != null) {
                    vibrator.vibrate(50);
                }
            }
        });


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean switchState = switchButton.isChecked();
                if (switchState) {
                    if (getActivity() instanceof SeleccionActivity) {
                        SeleccionActivity seleccionActivity = (SeleccionActivity) getActivity();
                        if (seleccionActivity != null) {
                            seleccionActivity.activosbaseMethod();
                        }
                    }
                } else {
                    if (getActivity() instanceof SeleccionActivity) {
                        SeleccionActivity seleccionActivity = (SeleccionActivity) getActivity();
                        if (seleccionActivity != null) {
                            seleccionActivity.archivosMethod();
                        }
                    }
                }
                if(switchState != active){
                    interfazBD.vaciarEncabezados();
                }
                interfazBD.actualizarModo(switchState);
                dialog.dismiss();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });

        dialog.show();
    }
}


