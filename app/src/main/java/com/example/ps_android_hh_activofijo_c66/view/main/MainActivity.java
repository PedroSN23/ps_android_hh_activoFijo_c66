package com.example.ps_android_hh_activofijo_c66.view.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

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

import java.util.ArrayList;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends MenuMainActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //establecer textos e íconos de menú principal
        setGroupIconText(0, IconGenericEnum.fontawesome_clipboard_check, "Valicación");
        setGroupIconText(1, IconGenericEnum.flaticon_search, "Búsqueda");
        setGroupIconText(2, IconGenericEnum.flaticon_toolbox, "Herramientas");
        setGroupIconText(3, IconGenericEnum.fontawesome_cogs, "Configuración");

        GroupStyle[] groupStyles = new GroupStyle[4];
        groupStyles[0] = new GroupStyle(0,
                DefaultButtonsEnum.button1.getCode(),
                DefaultButtonsEnum.button1_alt.getCode(),
                ColorEnum.menu1p.getCode(),
                ColorEnum.white.getCode());
        groupStyles[1] = new GroupStyle(1,
                DefaultButtonsEnum.button2.getCode(),
                DefaultButtonsEnum.button2_alt.getCode(),
                ColorEnum.menu2p.getCode(),
                ColorEnum.white.getCode());
        groupStyles[2] = new GroupStyle(2,
                DefaultButtonsEnum.button3.getCode(),
                DefaultButtonsEnum.button3_alt.getCode(),
                ColorEnum.menu3p.getCode(),
                ColorEnum.black.getCode());
        groupStyles[3] = new GroupStyle(3,
                DefaultButtonsEnum.button4.getCode(),
                DefaultButtonsEnum.button4_alt.getCode(),
                ColorEnum.menu4p.getCode(),
                ColorEnum.black.getCode());

        ArrayList<SubMenus> subMenusList = new ArrayList<>();
        subMenusList.add(new SubMenus("Validación",
                IconGenericEnum.fontawesome_clipboard_check,
                getPackageName()+".view.activity.ValidacionActivity",
                false,
                TemplateActivityEnum.four,
                groupStyles[0],
                DevicesEnabled.only_rfid));
        subMenusList.add(new SubMenus("Búsqueda",
                IconGenericEnum.flaticon_search,
                getPackageName()+".view.activity.BusquedaActivity",
                false,
                TemplateActivityEnum.four,
                groupStyles[1],
                DevicesEnabled.both));
        subMenusList.add(new SubMenus("RFID",
                IconGenericEnum.flaticon_rfid_3,
                getPackageName()+".view.activity.RFIDActivity",
                false,
                TemplateActivityEnum.four,
                groupStyles[2],
                DevicesEnabled.only_rfid));
        subMenusList.add(new SubMenus("Barcode",
                IconGenericEnum.flaticon_barcode,
                getPackageName()+".view.activity.BarcodeActivity",
                false,
                TemplateActivityEnum.four,
                groupStyles[2],
                DevicesEnabled.only_barcode));
        subMenusList.add(new SubMenus("Acerca",
                IconGenericEnum.flaticon_about,
                getPackageName()+".view.activity.AboutActivity",
                false,
                TemplateActivityEnum.two,
                groupStyles[3],
                DevicesEnabled.none));
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

        revisarListaSeriales();
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
}