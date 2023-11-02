package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.activity.FiltrosActivity;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class FiltrosFragment extends AppCompatActivity {
        private InterfazBD interfazBD;
        private GeneralAdapterFiltros generalAdapterFiltros;

        private Pattern p;

        private float xPosClick;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.filtros_lfragment);
            xPosClick=0;
            p = Pattern.compile("^[A-Za-z]{1,9}$");

            RecyclerView listView = findViewById(R.id.listViewFiltros);

            interfazBD = new InterfazBD(this);
            generalAdapterFiltros = new GeneralAdapterFiltros(interfazBD.obtenerFiltros());
            listView.setAdapter(generalAdapterFiltros);

            listView.setOnTouchListener((v, event) -> {
                xPosClick = event.getX();
                return false;
            });

            listView.setOnItemClickListener((parent, view, position, id) -> {
                //if(xPosClick>880.0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FiltrosActivity.this);
                builder.setMessage(getResources().getString(R.string.advEraseFilt))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.butCont), (dialogInterface, i) -> {
                            interfazBD.eliminarFiltro(generalAdapterFiltros.filtros.get(position));
                            generalAdapterFiltros.borrarFiltro(position);
                            generalAdapterFiltros.notifyDataSetChanged();
                        })
                        .setNegativeButton(getResources().getString(R.string.butCancel), (dialogInterface, i) -> dialogInterface.cancel());
                final AlertDialog alert = builder.create();
                alert.setOnShowListener(arg0 -> {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(FiltrosActivity.this, R.color.grisTrans));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(FiltrosActivity.this, R.color.grisTrans));
                });
                alert.show();
                //}
            });
        }

        @Override
        protected void onDestroy() {
            interfazBD.close();
            super.onDestroy();
        }

        public void SalirConfigFiltros(View view) {
            FiltrosActivity.this.finish();
        }

        public void NuevoFiltro(View view) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(FiltrosActivity.this);
            View dialogView = View.inflate(FiltrosActivity.this, R.layout.dialog_filtro_nuevo, null);
            alertDialog.setView(dialogView);
            final EditText etFilt = dialogView.findViewById(R.id.editFiltro);

            LinearLayout butG = dialogView.findViewById(R.id.digGuardarFilt);
            LinearLayout butC = dialogView.findViewById(R.id.diagCancelFilt);
            final AlertDialog dialog = alertDialog.create();

            butG.setOnClickListener(view1 -> {
                String f = etFilt.getText().toString();
                if(!f.isEmpty()) {
                    if(p.matcher(f).matches()) {
                        if(!generalAdapterFiltros.agregarFiltro(f)) {
                            generalAdapterFiltros.notifyDataSetChanged();
                            interfazBD.insertarFiltro(f);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(FiltrosActivity.this, getResources().getString(R.string.errFitlRep), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(FiltrosActivity.this, getResources().getString(R.string.errFiltFormat), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(FiltrosActivity.this, getResources().getString(R.string.errFiltEmpty), Toast.LENGTH_LONG).show();
                }
            });

            butC.setOnClickListener(view12 -> dialog.dismiss());

            dialog.show();
        }

        public class GeneralAdapterFiltros extends BaseAdapter {
            private boolean agregarFiltro(String valor) {
                boolean found=false;
                for(String val: filtros) {
                    if(val.compareTo(valor)==0) {
                        found=true;
                        break;
                    }
                }
                if(!found) {
                    filtros.add(valor);
                }
                return found;
            }

            public void borrarFiltro(int position) {
                filtros.remove(position);
            }

            private class ViewHolder {
                TextView textText;
                LinearLayout fondo;
            }

            private final ArrayList<String> filtros;

            private GeneralAdapterFiltros(ArrayList<String> filtros) {
                this.filtros = filtros;
            }

            @Override
            public int getCount() {
                return filtros.size();
            }

            @Override
            public Object getItem(int position) {
                return filtros.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 1;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View rowView = convertView;
                ViewHolder viewHolder;
                if(rowView == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    rowView = inflater.inflate(R.layout.list_filtros, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.textText = rowView.findViewById(R.id.valorFiltro);
                    viewHolder.fondo = rowView.findViewById(R.id.lista_back);
                    rowView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.textText.setText(filtros.get(position));
                if (position%2==0){
                    viewHolder.fondo.setBackgroundColor(ContextCompat.getColor(FiltrosActivity.this,R.color.grisAstlix));
                }else {
                    viewHolder.fondo.setBackgroundColor(ContextCompat.getColor(FiltrosActivity.this,R.color.white));
                }
                return rowView;
            }
        }
    }

}
