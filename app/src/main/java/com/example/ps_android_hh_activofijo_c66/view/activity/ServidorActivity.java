package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.DatabaseConf;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.fragment.ServidorFragment;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.pp_android_handheld_library.view.herencia.GenericActivity;

import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class ServidorActivity extends GenericActivity {
    private Context context;
    private InterfazBD interfazDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        interfazDb = new InterfazBD(context);
    }

    @Override
    protected Fragment setContentFragment() {

        ServidorFragment servidorFragment = new ServidorFragment();
        servidorFragment.addServerConfigFragmentListener(new ServidorFragment.ServidorFragmentListener() {
            @Override
            public void camposVacios(int i) {
                String[] strings = {"URL", "BASE DE DATOS", "USUARIO", "CONTRASEÑA"};
                Toast.makeText(context, String.format(Locale.getDefault(), "El campo %s no puede estar vacío", strings[i]), Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void modificarCampos(DatabaseConf databaseConf) {
                interfazDb.modificarDatabase(databaseConf);
                new StyleableToast.Builder(context)
                        .text("Parámetros guardados")
                        .backgroundColor(ContextCompat.getColor(context, ColorEnum.bien.getCode()))
                        .textColor(ContextCompat.getColor(context, R.color.white))
                        .length(Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return servidorFragment;
    }

    @Override
    protected Fragment setControlsFragment() {
        return null;
    }
}
