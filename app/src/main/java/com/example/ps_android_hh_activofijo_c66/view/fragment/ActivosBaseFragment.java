package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.Configuracion;
import com.example.ps_android_hh_activofijo_c66.model.clases.ControlEncabezados;
import com.example.ps_android_hh_activofijo_c66.model.clases.Encabezados;
import com.example.ps_android_hh_activofijo_c66.model.database.ConexionMysql;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.adapter.ActivosBaseAdapter;

import java.util.List;

public class ActivosBaseFragment extends Fragment {
    private InterfazBD interfazBD;
    private LottieAnimationView butNuevoFiltro;
    private RelativeLayout progreso;
    private ActivosBaseAdapter activosBaseAdapter;
    private Context context;
    private Configuracion configuracion;
    private ControlEncabezados controlEncabezados = new ControlEncabezados();
    private String ip, database, user, pass;

    public ActivosBaseFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activosbasefragment, container, false);
        context = requireContext();
        interfazBD = new InterfazBD(context);

        ListView listView = rootView.findViewById(R.id.lvArchivos);
        progreso = rootView.findViewById(R.id.menu4ProgresoArchivo);
        butNuevoFiltro = rootView.findViewById(R.id.butNuevoFiltro);
        butNuevoFiltro.setSpeed(2.5f);
        interfazBD.vaciarEncabezados();

        activosBaseAdapter = new ActivosBaseAdapter(context);
        listView.setAdapter(activosBaseAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> crearDialogo(i));

        obtener_datos();
        new Thread(new Runnable() {
            @Override
            public void run() {
                progreso.setVisibility(View.VISIBLE);
                String[] datos = interfazBD.obtenerServidor();
                ip = datos[0];
                database = datos[1];
                user = datos[2];
                pass = datos[3];
                ConexionMysql conMysql = new ConexionMysql(ip, database, user, pass);
                List<Encabezados> encabezadosList = conMysql.obtenerEncabezados(interfazBD.obtenerSlug());
                for (Encabezados encabezado : encabezadosList) {
                    interfazBD.insertarEncabezado(encabezado);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progreso.setVisibility(View.GONE);
                        activosBaseAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
        butNuevoFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butNuevoFiltro.playAnimation();
                butNuevoFiltro.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        GuardarConfiguracion();
                    }
                });
            }
        });

        return rootView;
    }

    private void crearDialogo(int index) {
        final int indexAdapter = index;
        View promptsView = View.inflate(context, R.layout.modificar_encabezado, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setView(promptsView);

        TextView nombreEnc = promptsView.findViewById(R.id.titDialMen4);
        nombreEnc.setText(activosBaseAdapter.encabezadosArrayList.get(index).getNombre());
        final SwitchCompat[] switches = new SwitchCompat[5];
        switches[0] = promptsView.findViewById(R.id.swVis);
        switches[1] = promptsView.findViewById(R.id.swEdi);
        switches[2] = promptsView.findViewById(R.id.swFil);
        switches[3] = promptsView.findViewById(R.id.swInd);
        switches[4] = promptsView.findViewById(R.id.swPrk);

        switches[0].setChecked(activosBaseAdapter.encabezadosArrayList.get(index).isVisible());
        switches[1].setChecked(activosBaseAdapter.encabezadosArrayList.get(index).isEditable());
        switches[2].setChecked(activosBaseAdapter.encabezadosArrayList.get(index).isFiltro());
        switches[3].setChecked(activosBaseAdapter.encabezadosArrayList.get(index).isIndexado());
        switches[4].setChecked(activosBaseAdapter.encabezadosArrayList.get(index).isLlavePrimaria());

        final LottieAnimationView butGuardar = promptsView.findViewById(R.id.guardarEnc);
        butGuardar.setSpeed(1.8f);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(dialogInterface -> butGuardar.setOnClickListener(view -> {
            butGuardar.playAnimation();
            butGuardar.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    activosBaseAdapter.encabezadosArrayList.get(indexAdapter).setVisible(switches[0].isChecked());
                    activosBaseAdapter.encabezadosArrayList.get(indexAdapter).setEditable(switches[1].isChecked());

                    if (switches[2].isChecked() && !activosBaseAdapter.encabezadosArrayList.get(indexAdapter).isFiltro()) {
                        activosBaseAdapter.encabezadosArrayList.get(indexAdapter).setFiltro(controlEncabezados.firstFiltro() == 1);
                    } else {
                        if (!switches[2].isChecked() && activosBaseAdapter.encabezadosArrayList.get(indexAdapter).isFiltro()) {
                            controlEncabezados.resetFiltro();
                            activosBaseAdapter.encabezadosArrayList.get(indexAdapter).setFiltro(false);
                        }
                    }

                    if (switches[3].isChecked() && !activosBaseAdapter.encabezadosArrayList.get(indexAdapter).isIndexado()) {
                        activosBaseAdapter.encabezadosArrayList.get(indexAdapter).setIndexado(controlEncabezados.canBeIndexado() == 1);
                    } else {
                        if (!switches[3].isChecked() && activosBaseAdapter.encabezadosArrayList.get(indexAdapter).isIndexado()) {
                            controlEncabezados.restIndexado();
                            activosBaseAdapter.encabezadosArrayList.get(indexAdapter).setIndexado(false);
                        }
                    }

                    if (switches[4].isChecked() && !activosBaseAdapter.encabezadosArrayList.get(indexAdapter).isLlavePrimaria()) {
                        activosBaseAdapter.encabezadosArrayList.get(indexAdapter).setLlavePrimaria(controlEncabezados.fistPrimaryKey() == 1);
                    } else {
                        if (!switches[4].isChecked() && activosBaseAdapter.encabezadosArrayList.get(indexAdapter).isLlavePrimaria()) {
                            controlEncabezados.resetLlavePrimaria();
                            activosBaseAdapter.encabezadosArrayList.get(indexAdapter).setLlavePrimaria(false);
                        }
                    }

                    activosBaseAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            });
        }));

        alertDialog.setOnCancelListener(null);

        alertDialog.show();
    }


    private void obtener_datos()  {
        configuracion = interfazBD.obtenerConfiguracion();
        if(configuracion!=null) {
            activosBaseAdapter.cambiarEncabezados(interfazBD.obtenerEncabezados());
            activosBaseAdapter.notifyDataSetChanged();
            for(int i=0; i<activosBaseAdapter.encabezadosArrayList.size(); i++) {
                if(activosBaseAdapter.encabezadosArrayList.get(i).isLlavePrimaria()) {
                    if(controlEncabezados.fistPrimaryKey()!=1) {
                        activosBaseAdapter.encabezadosArrayList.get(i).setLlavePrimaria(false);
                    }
                }
                if(activosBaseAdapter.encabezadosArrayList.get(i).isIndexado()) {
                    if(controlEncabezados.canBeIndexado()!=1) {
                        activosBaseAdapter.encabezadosArrayList.get(i).setIndexado(false);
                    }
                }
            }
        } else {
            Toast.makeText(context, getResources().getString(R.string.errBDdat), Toast.LENGTH_SHORT).show();
        }
    }

    public void GuardarConfiguracion() {
        if(configuracion!=null) {
            if(!configuracion.getArchivoInName().isEmpty()&&!configuracion.getPrefijoOut().isEmpty()&&activosBaseAdapter.getCount()>0) {
                interfazBD.vaciarEncabezados();
                for(Encabezados enc: activosBaseAdapter.encabezadosArrayList) {
                    interfazBD.insertarEncabezado(enc);
                }
                Toast.makeText(context, getResources().getString(R.string.msgGuardado), Toast.LENGTH_LONG).show();
                getActivity().finish();
            } else {
                Toast.makeText(context, getResources().getString(R.string.menu4s1ErrVacio), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, getResources().getString(R.string.menu4s1ErrVacio), Toast.LENGTH_LONG).show();
        }
    }
}