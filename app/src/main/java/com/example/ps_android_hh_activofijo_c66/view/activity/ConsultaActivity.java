package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.pp_android_handheld_library.controller.DevicesEnabled;
import com.example.pp_android_handheld_library.model.SubMenus;
import com.example.pp_android_handheld_library.model.resources.TemplateActivityEnum;
import com.example.pp_android_handheld_library.view.herencia.GenericActivity;
import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.controller.MainHandler;
import com.example.ps_android_hh_activofijo_c66.controller.files.FileController;
import com.example.ps_android_hh_activofijo_c66.view.fragment.ConsultaFragment;
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

public class ConsultaActivity extends GenericActivity {
    private ConsultaFragment consultaFragment;
    private ControlsFragment controlsFragment;
    private MailController mailController;
    private FileController fileController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getProgresBar();
        Bundle bundle = getIntent().getExtras();
        String act = bundle.getString("ACT");;
        MainHandler mainHandler1 = new MainHandler(this);
        fileController = FileController.getInstance(mainHandler1, this);
        mailController = MailController.getInstance(mainHandler1);
        consultaFragment.getActivo(act);
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
    protected Fragment setContentFragment() {
        consultaFragment = new ConsultaFragment();
        return consultaFragment;
    }

    @Override
    protected Fragment setControlsFragment() {
        ArrayList<ControlButtonsCircular> controlButtons = new ArrayList<>();
        controlButtons.add(new ControlButtonsCircular(1, "BUSCAR",
                IconGenericEnum.fontawesome_search,
                false,
                "BUSCAR",
                false,
                ColorEnum.bien.getCode()));
        controlButtons.add(new ControlButtonsCircular(2, "CAMBIAR",
                IconGenericEnum.fontawesome_edit,
                false,
                "CAMBIAR",
                false,
                ColorEnum.status_blue.getCode()));
        controlButtons.add(new ControlButtonsCircular(3, "GUARDAR",
                IconGenericEnum.fontawesome_save,
                false,
                "GUARDAR",
                false,
                ColorEnum.amarillo.getCode()));

        controlsFragment = new ControlsFragment(controlButtons, subMenus.getGroupStyle());
        controlsFragment.addControlsFragmentAdapter((view, pressed) -> {
            if(!isProgresoVisible()) {
                ButtonsCicularViewHolder buttonsViewHolder = (ButtonsCicularViewHolder) view.getTag();
                switch (buttonsViewHolder.cb.getIndex()) {
                    case 1:
                        String activo = consultaFragment.getActiveText();
                        Intent intent = new Intent(ConsultaActivity.this, BusquedaActivity.class);
                        SubMenus subMenus1 = (new SubMenus("Búsqueda",
                                IconGenericEnum.fontawesome_table,
                                getPackageName() + ".view.activity.BusquedaActivity",
                                false, TemplateActivityEnum.four,
                                subMenus.getGroupStyle(),
                                DevicesEnabled.only_rfid));
                        intent.putExtra("submenu", subMenus1);
                        intent.putExtra("devices", subMenus1);
                        intent.putExtra("ACT", activo);
                        intent.putExtra("boolean", false);
                        pedrosActivityResultLauncher.launch(intent);
                        break;
                    case 2:
                        consultaFragment.AbrirDialogo();
                        break;
                    case 3:
                        consultaFragment.GuardarFormularioForm();
                        break;
                }
                toggleShowMenuPack(controlsFragment);
            }
        });
        return controlsFragment;
    }

    ActivityResultLauncher<Intent> pedrosActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //setMainHandler();
                }
    );
}
