package com.example.ps_android_hh_activofijo_c66.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.pp_android_handheld_library.controller.DevicesEnabled;
import com.example.pp_android_handheld_library.model.SubMenus;
import com.example.pp_android_handheld_library.model.resources.IconGenericEnum;
import com.example.pp_android_handheld_library.model.resources.TemplateActivityEnum;
import com.example.ps_android_hh_activofijo_c66.R;

public class SeleccionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SeleccionarArchivo();
    }

    private void SeleccionarArchivo() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.seleccion_layout);
        dialog.setCanceledOnTouchOutside(false);

        Switch swit = dialog.findViewById(R.id.switchButton);
        LinearLayout okButton = dialog.findViewById(R.id.okButton);
        LinearLayout exitButton = dialog.findViewById(R.id.closeButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean switchState = swit.isChecked();
                if (switchState) {
                    Toast.makeText(SeleccionActivity.this, "Hola", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SeleccionActivity.this, ArchivosActivity.class);
                    SubMenus subMenus1 = (new SubMenus("Búsqueda",
                            IconGenericEnum.fontawesome_table,
                            getPackageName() + ".view.activity.BusquedaActivity",
                            false, TemplateActivityEnum.four,
                            subMenus.getGroupStyle(),
                            DevicesEnabled.none));
                    intent.putExtra("submenu", subMenus1);
                    intent.putExtra("devices", subMenus1);
                    startActivity(intent);
                    finish();

                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeleccionActivity.this.finish();
            }
        });

        dialog.show();
    }

}
