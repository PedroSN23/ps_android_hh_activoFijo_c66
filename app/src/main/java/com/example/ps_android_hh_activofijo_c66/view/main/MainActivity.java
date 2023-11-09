package com.example.ps_android_hh_activofijo_c66.view.main;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ps_android_hh_activofijo_c66.BuildConfig;
import com.example.ps_android_hh_activofijo_c66.R;
import com.example.pp_android_handheld_library.controller.DevicesEnabled;
import com.example.pp_android_handheld_library.controller.update.UpdateUtils;
import com.example.pp_android_handheld_library.controller.update.UpdateUtilsInterface;
import com.example.pp_android_handheld_library.model.GroupStyle;
import com.example.pp_android_handheld_library.model.SubMenus;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.pp_android_handheld_library.model.resources.DefaultButtonsEnum;
import com.example.pp_android_handheld_library.model.resources.IconGenericEnum;
import com.example.pp_android_handheld_library.model.resources.TemplateActivityEnum;
import com.example.pp_android_handheld_library.view.gui.MenuMainActivity;
import com.example.ps_android_hh_activofijo_c66.model.clases.Usuario;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends MenuMainActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        revisarListaSeriales();
        //onCreateLoginDialog();

        //establecer textos e íconos de menú principal
        setGroupIconText(0, IconGenericEnum.fontawesome_boxes, "INVENTARIO");
        setGroupIconText(1, IconGenericEnum.fontawesome_search, "BUSQUEDA");
        setGroupIconText(2, IconGenericEnum.fontawesome_tools, "HERRAMIENTAS");
        setGroupIconText(3, IconGenericEnum.zmdi_settings, "CONFIGURACIÓN");
        //setGroupIconTextSize(0, 50);
        //setGroupColor(3, this, R.color.prueba);

        GroupStyle[] groupStyles = new GroupStyle[4];
        groupStyles[0] = new GroupStyle(0, DefaultButtonsEnum.button1.getCode(), DefaultButtonsEnum.button1_alt.getCode(), ColorEnum.menu1p.getCode(), ColorEnum.white.getCode());
        groupStyles[1] = new GroupStyle(1, DefaultButtonsEnum.button2.getCode(), DefaultButtonsEnum.button2_alt.getCode(), ColorEnum.menu2p.getCode(), ColorEnum.white.getCode());
        groupStyles[2] = new GroupStyle(2, DefaultButtonsEnum.button3.getCode(), DefaultButtonsEnum.button3_alt.getCode(), ColorEnum.menu3p.getCode(), ColorEnum.black.getCode());
        groupStyles[3] = new GroupStyle(3, DefaultButtonsEnum.button4.getCode(), DefaultButtonsEnum.button4_alt.getCode(), ColorEnum.menu4p.getCode(), ColorEnum.black.getCode());

        ArrayList<SubMenus> subMenusList = new ArrayList<>();

        subMenusList.add(new SubMenus("Inventario", IconGenericEnum.fontawesome_clipboard_check, getPackageName() + ".view.activity.ValidacionActivity", false, TemplateActivityEnum.four, groupStyles[0], DevicesEnabled.only_rfid));
        subMenusList.add(new SubMenus("Exportar Archivo", IconGenericEnum.fontawesome_file_excel, getPackageName() + ".view.activity.ValidacionActivity", false, TemplateActivityEnum.four, groupStyles[0], DevicesEnabled.only_rfid));
        subMenusList.add(new SubMenus("Consulta de Activo", IconGenericEnum.fontawesome_table, getPackageName() + ".view.activity.ConsultaActivity", false, TemplateActivityEnum.four, groupStyles[1], DevicesEnabled.both));
        subMenusList.add(new SubMenus("Búsqueda", IconGenericEnum.fontawesome_search, getPackageName() + ".view.activity.BusquedaActivity", false, TemplateActivityEnum.four, groupStyles[1], DevicesEnabled.both));
        subMenusList.add(new SubMenus("RFID", IconGenericEnum.fontawesome_broadcast_tower, getPackageName() + ".view.activity.RFIDActivity", false, TemplateActivityEnum.four, groupStyles[2], DevicesEnabled.only_rfid));
        subMenusList.add(new SubMenus("Barcode", IconGenericEnum.fontawesome_barcode, getPackageName() + ".view.activity.BarcodeActivity", false, TemplateActivityEnum.four, groupStyles[2], DevicesEnabled.only_barcode));
        subMenusList.add(new SubMenus("Archivos", IconGenericEnum.fontawesome_file_excel, getPackageName() + ".view.activity.ArchivosActivity", false, TemplateActivityEnum.two, groupStyles[3], DevicesEnabled.none));
        subMenusList.add(new SubMenus("Servidor", IconGenericEnum.fontawesome_cloud, getPackageName() + ".view.activity.ServidorActivity", false, TemplateActivityEnum.two, groupStyles[3], DevicesEnabled.none));
        subMenusList.add(new SubMenus("Usuarios", IconGenericEnum.fontawesome_user, getPackageName() + ".view.activity.UsuariosActivity", false, TemplateActivityEnum.two, groupStyles[3], DevicesEnabled.none));
        subMenusList.add(new SubMenus("Filtros", IconGenericEnum.fontawesome_filter, getPackageName() + ".view.activity.FiltrosActivity", false, TemplateActivityEnum.two, groupStyles[3], DevicesEnabled.none));
        subMenusList.add(new SubMenus("Acerca", IconGenericEnum.fontawesome_info, getPackageName() + ".view.activity.AboutActivity", false, TemplateActivityEnum.two, groupStyles[3], DevicesEnabled.none));
        sendSubMenuList(subMenusList);


        UpdateUtils updateUtils = new UpdateUtils(Double.parseDouble(BuildConfig.VERSION_NAME),
                getResources().getString(R.string.app_name),
                this);
        updateUtils.addUpdateUtilsInterface(new UpdateUtilsInterface() {
            @Override
            public void updateprocedure(String fileName) {
                String url = String.format(Locale.getDefault(), "https://www.astlix.com/swUpdates/astlixdemo/%s", fileName).replaceAll(" ", "%20");
                updateUtils.updateProcedure(url);
            }

            @Override
            public void updateError(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void noNewVersion(String s) {
                //Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
        updateUtils.updateVerify("https://www.astlix.com/swUpdates/astlixdemo/getlist.php");
    }

    private void revisarListaSeriales() {
        boolean found=false;

        @SuppressLint("HardwareIds") String serial = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String [] stringArray = getResources().getStringArray(R.array.serial);

        for (String s : stringArray) {
            if (s.compareTo(serial) == 0) {
                found = true;
                break;
            }
        }
        if(!found) {
            new StyleableToast.Builder(MainActivity.this).text(serial)
                    .backgroundColor(ContextCompat.getColor(MainActivity.this, ColorEnum.status_blue.getCode()))
                    .textColor(ContextCompat.getColor(MainActivity.this, R.color.black))
                    .length(Toast.LENGTH_LONG)
                    .iconStart(R.drawable.ic_info)
                    .show();
            Log.d("SERIAL", serial);
            finishAndRemoveTask();
        }
    }
    private void onCreateLoginDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View promptsView = inflater.inflate(R.layout.dialog_autentificar_administrador, null);

        EditText usuario = promptsView.findViewById(R.id.admUser);
        EditText password = promptsView.findViewById(R.id.admPass);
        RelativeLayout animation = promptsView.findViewById(R.id.progresoLogin);
        Button aceptar = promptsView.findViewById(R.id.button1);
        Button cancelar = promptsView.findViewById(R.id.button2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(promptsView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false); // Evita que el diálogo se cierre al tocar fuera de él

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        cancelar.setOnClickListener(v -> MainActivity.this.finish());

        aceptar.setOnClickListener(v -> {
            animation.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {
                if (verificarUsuario(usuario.getText().toString(), password.getText().toString())) {
                    // Correcto
                    alertDialog.dismiss();
                } else {
                    animation.setVisibility(View.GONE);
                    new StyleableToast.Builder(this)
                            .text("Error de autenticación")
                            .textColor(ContextCompat.getColor(this, R.color.white))
                            .backgroundColor(ContextCompat.getColor(this, ColorEnum.sobrante.getCode()))
                            .show();
                }
            }, 3000);
        });

    }


    private boolean verificarUsuario(String usuario, String pass) {
        boolean resultado = false;

        InterfazBD interfazDb = new InterfazBD(this);
        Usuario user = interfazDb.obtenerUsuario(usuario);

        if (user == null) {
            return false;
        } else {
            if (BCrypt.checkpw(pass, user.getPass())) {
                Log.d("pass", "¡La contraseña coincide!");
                resultado = true;
            } else {
                Log.d("pass", "¡La contraseña NO coincide!");
            }
        }
        interfazDb.truncarTabla("sesiones");
        interfazDb.insertarSesion(usuario, user.getRol());

        return resultado;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean anyFragmentDisplayed = false;

        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null && fragment.isVisible()) {
                anyFragmentDisplayed = true;
                break;
            }
        }

        if (anyFragmentDisplayed) {
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Desea cerrar la sesión activa?")
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                        InterfazBD interfazDb = new InterfazBD(this);
                        interfazDb.truncarTabla("sesiones");
                    })
                    .create()
                    .show();
        }
    }
}