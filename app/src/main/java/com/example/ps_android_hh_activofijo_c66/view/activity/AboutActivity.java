package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pp_android_handheld_library.view.fragment.AboutFragment;
import com.example.pp_android_handheld_library.view.herencia.GenericActivity;

public class AboutActivity extends GenericActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNextPressed() {

    }

    @Override
    protected void onPrevPressed() {
        onBackPressed();
    }

    @Override
    protected Fragment setContentFragment() {
        return new AboutFragment();
    }
    @Override
    protected Fragment setControlsFragment() {
        return null;
    }
}
