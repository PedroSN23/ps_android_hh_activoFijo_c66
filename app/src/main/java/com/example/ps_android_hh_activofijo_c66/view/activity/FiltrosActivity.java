package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pp_android_handheld_library.view.herencia.GenericActivity;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.fragment.UsuariosFragment;

public class FiltrosActivity extends  GenericActivity {
    public InterfazBD interfazBD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected Fragment setContentFragment() {
        return new UsuariosFragment();
    }
    @Override
    protected Fragment setControlsFragment() {
        return null;
    }
}


