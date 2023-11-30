package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.pp_android_handheld_library.controller.DevicesEnabled;
import com.example.pp_android_handheld_library.model.SubMenus;
import com.example.pp_android_handheld_library.model.resources.IconGenericEnum;
import com.example.pp_android_handheld_library.model.resources.TemplateActivityEnum;
import com.example.pp_android_handheld_library.view.herencia.GenericActivity;
import com.example.ps_android_hh_activofijo_c66.view.fragment.SeleccionFragment;

public class SeleccionActivity extends GenericActivity {

    private SeleccionFragment seleccionFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onNextPressed() {

    }
    @Override
    protected void onPrevPressed() {

    }

    @Override
    protected Fragment setContentFragment() {
        seleccionFragment = new SeleccionFragment();
        return seleccionFragment;
    }

    @Override
    protected Fragment setControlsFragment() {
        return null;
    }
    private void launchActivity(Class<?> targetActivity) {
        Intent intent = new Intent(SeleccionActivity.this, targetActivity);
        SubMenus subMenus1 = new SubMenus("Búsqueda",
                IconGenericEnum.fontawesome_table,
                getPackageName() + ".view.activity.ActivosBaseActivity",
                false, TemplateActivityEnum.four,
                subMenus.getGroupStyle(),
                DevicesEnabled.none);

        intent.putExtra("submenu", subMenus1);
        intent.putExtra("devices", subMenus1);

        pedrosActivityResultLauncher.launch(intent);
    }

    public void activosbaseMethod() {
        launchActivity(ActivosBaseActivity.class);
    }

    public void archivosMethod() {
        launchActivity(ArchivosActivity.class);
    }

    ActivityResultLauncher<Intent> pedrosActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //setMainHandler();
            }
    );
}
