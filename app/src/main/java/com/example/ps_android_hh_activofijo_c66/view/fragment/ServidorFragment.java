package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.DatabaseConf;
import com.example.ps_android_hh_activofijo_c66.model.clases.Usuario;
import com.example.ps_android_hh_activofijo_c66.model.database.ConexionMysql;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServidorFragment extends Fragment {
    private static final Pattern IP_ADDRESS = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]" + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]" + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}" + "|[1-9][0-9]|[0-9]))");
    private ServidorFragmentListener listener;

    private final EditText[] editTexts = new EditText[4];

    LottieAnimationView connectionStatus;
    RelativeLayout progreso;
    ProgressBar progress;
    InterfazBD interfazDb;

    public void addServerConfigFragmentListener(ServidorFragmentListener listener) {
        this.listener = listener;
    }

    public interface ServidorFragmentListener {
        void camposVacios(int i);

        void modificarCampos(DatabaseConf databaseConf);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.servidor_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        interfazDb = new InterfazBD(requireContext());

        connectionStatus = view.findViewById(R.id.connectionStatus);
        progreso = view.findViewById(R.id.relativeStatus);
        progress = view.findViewById(R.id.progress);

        DatabaseConf databaseConf = interfazDb.obtenerConfiguracionDatabase();

        editTexts[0] = view.findViewById(R.id.urlServer);
        editTexts[0].setText(databaseConf.getUrl());
        editTexts[1] = view.findViewById(R.id.baseServer);
        editTexts[1].setText(databaseConf.getDatabase());
        editTexts[2] = view.findViewById(R.id.userServer);
        editTexts[2].setText(databaseConf.getUser());
        editTexts[3] = view.findViewById(R.id.passServer);
        editTexts[3].setText(databaseConf.getPassword());
        Button guardarCambios = view.findViewById(R.id.butGuardar);
        Button serverTest = view.findViewById(R.id.butTest);

        Usuario usuario = interfazDb.obtenerPermiso();
        if (usuario.getRol() == 3) {
            for (int i = 0; i < 4; i++) {
                editTexts[i].setEnabled(false);
                serverTest.setEnabled(false);
                guardarCambios.setEnabled(false);
            }
        }

        guardarCambios.setOnClickListener(v -> {
            String[] strings = new String[4];
            for (int i = 0; i < 4; i++) {
                strings[i] = editTexts[i].getText().toString();
                if (strings[i] == null || strings[i].isEmpty()) {
                    if (listener != null) {
                        listener.camposVacios(i);
                    }
                    return;
                }
            }

            Matcher matcher = IP_ADDRESS.matcher(strings[0]);
            if (matcher.matches()) {
                databaseConf.setUrl(strings[0]);
                databaseConf.setDatabase(strings[1]);
                databaseConf.setUser(strings[2]);
                databaseConf.setPassword(strings[3]);
                if (listener != null) {
                    listener.modificarCampos(databaseConf);
                }
            } else {
                editTexts[0].setError("Formato de IP inválido");
            }
        });

        serverTest.setOnClickListener(v1 -> {
            progress.setVisibility(View.VISIBLE);
            connectionStatus.setVisibility(View.GONE);
            new Thread(() -> {
                boolean success = false;
                String ip, database, user, pass;
                String[] datos = interfazDb.obtenerServidor();
                ip = datos[0];
                database = datos[1];
                user = datos[2];
                pass = datos[3];

                ConexionMysql conMysql = new ConexionMysql(ip, database, user, pass);

                if (conMysql.getConnected()) {
                    success = true;
                }
                boolean finalSuccess = success;
                requireActivity().runOnUiThread(() -> {
                    connectionStatus.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);

                    if (finalSuccess) {
                        connectionStatus.setAnimation(R.raw.checked);
                    } else {
                        connectionStatus.setAnimation(R.raw.error);
                    }
                    connectionStatus.playAnimation();
                });
            }).start();
        });
    }
}
