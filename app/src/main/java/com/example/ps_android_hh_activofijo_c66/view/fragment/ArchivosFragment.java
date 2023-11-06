package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.Archivos;
import com.example.ps_android_hh_activofijo_c66.model.clases.Configuracion;
import com.example.ps_android_hh_activofijo_c66.model.clases.ControlEncabezados;
import com.example.ps_android_hh_activofijo_c66.model.clases.Encabezados;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.adapter.ArchivosAdapter;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ArchivosFragment extends Fragment {
    private InterfazBD interfazBD;
    private final EditText[] archEdit=new EditText[2];
    private final SwitchCompat[] switches=new SwitchCompat[2];
    private EditText archEditIn;
    private EditText archEditOut;
    private LinearLayout butSelectIn;
    private LinearLayout butNuevoFiltro;
    private SwitchCompat switchResult;
    private SwitchCompat switchFecha;
    private RelativeLayout progreso;
    private Pattern pattern;
    private ArchivosAdapter ArchivoAdapter;
    private Context context;
    private Configuracion configuracion;
    private ControlEncabezados controlEncabezados = new ControlEncabezados();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.archivos_fragment, container, false);

        context = requireContext();
        archEditIn = rootView.findViewById(R.id.confArchIn);
        archEditOut = rootView.findViewById(R.id.confArchOut);
        switchResult = rootView.findViewById(R.id.switchResult);
        switchFecha = rootView.findViewById(R.id.switchFecha);
        archEdit[0] = rootView.findViewById(R.id.confArchIn);
        archEdit[1] = rootView.findViewById(R.id.confArchOut);
        switches[0] = rootView.findViewById(R.id.switchResult);
        switches[1] = rootView.findViewById(R.id.switchFecha);

        ListView listView = rootView.findViewById(R.id.lvArchivos);
        progreso = rootView.findViewById(R.id.menu4ProgresoArchivo);
        pattern = Pattern.compile("^.*\\.xlsx$");

        interfazBD = new InterfazBD(context);
        ArchivoAdapter = new ArchivosAdapter(context);
        listView.setAdapter(ArchivoAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> crearDialogo(i));

        archEditIn.setKeyListener(null);

        archEditOut.setInputType(InputType.TYPE_CLASS_TEXT);

        obtener_datos();
        butNuevoFiltro = rootView.findViewById(R.id.butNuevoFiltro);
        butSelectIn = rootView.findViewById(R.id.butSelectIn);

        butSelectIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelccionarArchivo(v);
            }
        });

        butNuevoFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuardarConfiguracion(v);
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
        nombreEnc.setText(ArchivoAdapter.encabezadosArrayList.get(index).getNombre());
        final SwitchCompat[] switches = new SwitchCompat[5];
        switches[0] = promptsView.findViewById(R.id.swVis);
        switches[1] = promptsView.findViewById(R.id.swEdi);
        switches[2] = promptsView.findViewById(R.id.swFil);
        switches[3] = promptsView.findViewById(R.id.swInd);
        switches[4] = promptsView.findViewById(R.id.swPrk);

        switches[0].setChecked(ArchivoAdapter.encabezadosArrayList.get(index).isVisible());
        switches[1].setChecked(ArchivoAdapter.encabezadosArrayList.get(index).isEditable());
        switches[2].setChecked(ArchivoAdapter.encabezadosArrayList.get(index).isFiltro());
        switches[3].setChecked(ArchivoAdapter.encabezadosArrayList.get(index).isIndexado());
        switches[4].setChecked(ArchivoAdapter.encabezadosArrayList.get(index).isLlavePrimaria());

        final LinearLayout butGuardar = promptsView.findViewById(R.id.guardarEnc);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(dialogInterface -> butGuardar.setOnClickListener(view -> {
            ArchivoAdapter.encabezadosArrayList.get(indexAdapter).setVisible(switches[0].isChecked());
            ArchivoAdapter.encabezadosArrayList.get(indexAdapter).setEditable(switches[1].isChecked());
            if(switches[2].isChecked()&&!ArchivoAdapter.encabezadosArrayList.get(indexAdapter).isFiltro()) {
                ArchivoAdapter.encabezadosArrayList.get(indexAdapter).setFiltro(controlEncabezados.firstFiltro()==1);
            } else {
                if(!switches[2].isChecked()&&ArchivoAdapter.encabezadosArrayList.get(indexAdapter).isFiltro()) {
                    controlEncabezados.resetFiltro();
                    ArchivoAdapter.encabezadosArrayList.get(indexAdapter).setFiltro(false);
                }
            }
            if(switches[3].isChecked()&&!ArchivoAdapter.encabezadosArrayList.get(indexAdapter).isIndexado()) {
                ArchivoAdapter.encabezadosArrayList.get(indexAdapter).setIndexado(controlEncabezados.canBeIndexado()==1);
            } else {
                if(!switches[3].isChecked()&&ArchivoAdapter.encabezadosArrayList.get(indexAdapter).isIndexado()) {
                    controlEncabezados.restIndexado();
                    ArchivoAdapter.encabezadosArrayList.get(indexAdapter).setIndexado(false);
                }
            }
            if(switches[4].isChecked()&&!ArchivoAdapter.encabezadosArrayList.get(indexAdapter).isLlavePrimaria()) {
                ArchivoAdapter.encabezadosArrayList.get(indexAdapter).setLlavePrimaria(controlEncabezados.fistPrimaryKey()==1);
            } else {
                if(!switches[4].isChecked()&&ArchivoAdapter.encabezadosArrayList.get(indexAdapter).isLlavePrimaria()) {
                    controlEncabezados.resetLlavePrimaria();
                    ArchivoAdapter.encabezadosArrayList.get(indexAdapter).setLlavePrimaria(false);
                }
            }
            ArchivoAdapter.notifyDataSetChanged();
            alertDialog.dismiss();
        }));

        alertDialog.setOnCancelListener(null);

        alertDialog.show();
    }

    private void obtener_datos()  {
        configuracion = interfazBD.obtenerConfiguracion();
        if(configuracion!=null) {
            archEdit[0].setText(configuracion.getArchivoInName());
            archEdit[1].setText(configuracion.getPrefijoOut());
            switches[0].setChecked(configuracion.isResult());
            switches[1].setChecked(configuracion.isFecha());
            ArchivoAdapter.cambiarEncabezados(interfazBD.obtenerEncabezados());
            ArchivoAdapter.notifyDataSetChanged();
            for(int i=0; i<ArchivoAdapter.encabezadosArrayList.size(); i++) {
                if(ArchivoAdapter.encabezadosArrayList.get(i).isLlavePrimaria()) {
                    if(controlEncabezados.fistPrimaryKey()!=1) {
                        ArchivoAdapter.encabezadosArrayList.get(i).setLlavePrimaria(false);
                    }
                }
                if(ArchivoAdapter.encabezadosArrayList.get(i).isIndexado()) {
                    if(controlEncabezados.canBeIndexado()!=1) {
                        ArchivoAdapter.encabezadosArrayList.get(i).setIndexado(false);
                    }
                }
            }
        } else {
            Toast.makeText(context, getResources().getString(R.string.errBDdat), Toast.LENGTH_SHORT).show();
        }
    }

    public void GuardarConfiguracion(View view) {
        if(configuracion!=null) {
            configuracion.setPrefijoOut(archEdit[1].getText().toString());
            configuracion.setResult(switches[0].isChecked());
            configuracion.setFecha(switches[1].isChecked());
            if(!configuracion.getArchivoInName().isEmpty()&&!configuracion.getPrefijoOut().isEmpty()&&ArchivoAdapter.getCount()>0) {
                interfazBD.modificarConfiguracion(configuracion);
                interfazBD.vaciarEncabezados();
                for(Encabezados enc: ArchivoAdapter.encabezadosArrayList) {
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

    public void SelccionarArchivo(View view) {
        SyncDataSearch fileSearch = new SyncDataSearch();
        fileSearch.execute();
    }

    public void SalirArchivos(View view) {
        getActivity().finish();
    }

    private static class SyncDataExcel extends AsyncTask<String, String, Boolean> {
        private ArrayList<Encabezados> encabezadosArrayList;
        private final ArrayList<Encabezados> encabezadosArrayListPrev;
        private final WeakReference<Context> contextWeakReference;
        private final WeakReference<ArchivosAdapter> ArchivoAdapterWeakReference;
        private String errorMsg;
        private final WeakReference<ControlEncabezados> controlEncabezadosWeakReference;
        private final WeakReference<RelativeLayout> progresoWeakReference;

        private SyncDataExcel(Context context, ArchivosAdapter ArchivoAdapter, ControlEncabezados controlEncabezados, RelativeLayout progreso, InterfazBD interfazBD) {
            this.contextWeakReference = new WeakReference<>(context);
            this.ArchivoAdapterWeakReference = new WeakReference<>(ArchivoAdapter);
            this.controlEncabezadosWeakReference = new WeakReference<>(controlEncabezados);
            this.progresoWeakReference = new WeakReference<>(progreso);
            encabezadosArrayListPrev = interfazBD.obtenerEncabezados();
        }

        @Override
        protected void onPreExecute() {
            encabezadosArrayList = new ArrayList<>();
            if(progresoWeakReference.get()!=null) {
                progresoWeakReference.get().setVisibility(View.VISIBLE);
            }
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean ret = false;
            if(strings.length==1) {
                ret = readExcelData(strings[0]);
            }
            return ret;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean) {
                if(ArchivoAdapterWeakReference.get()!=null) {
                    ArchivoAdapterWeakReference.get().cambiarEncabezados(encabezadosArrayList);
                    ArchivoAdapterWeakReference.get().notifyDataSetChanged();
                }
            } else {
                if(contextWeakReference.get()!=null) {
                    Toast.makeText(contextWeakReference.get(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }
            if(progresoWeakReference.get()!=null) {
                progresoWeakReference.get().setVisibility(View.GONE);
            }
            super.onPostExecute(aBoolean);
        }

        private boolean readExcelData(String filePath) {
            File inputFile = new File(filePath);
            try {
                InputStream inputStream = new FileInputStream(inputFile);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                XSSFSheet sheet = workbook.getSheetAt(0);
                FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                int cellsCount = sheet.getRow(0).getPhysicalNumberOfCells();
                Row row = sheet.getRow(0);
                for (int c = 0; c < cellsCount; c++) { //iterador de celdas
                    String value = getCellAsString(row, c, formulaEvaluator);
                    if(value.toLowerCase().compareTo("id")==0) {
                        encabezadosArrayList.add(new Encabezados(c, value, controlEncabezadosWeakReference.get().fistPrimaryKey(), controlEncabezadosWeakReference.get().canBeIndexado(), 0, 1, 0));
                    } else {
                        if(value.toLowerCase().compareTo("serie")==0 || value.toLowerCase().compareTo("serial")==0) {
                            encabezadosArrayList.add(new Encabezados(c, value, 0, controlEncabezadosWeakReference.get().canBeIndexado(), 0, 1, 0));
                        } else {
                            if(value.toLowerCase().compareTo("observaciones")==0) {
                                encabezadosArrayList.add(new Encabezados(c, value, 0, 0, 1, 1, 0));
                            } else {
                                encabezadosArrayList.add(new Encabezados(c, value, 0, 0, 0, 1, 0));
                            }
                        }
                    }
                }
                if(encabezadosArrayListPrev.size()==encabezadosArrayList.size()) {
                    for (int i = 0; i < encabezadosArrayListPrev.size(); i++) {
                        if(encabezadosArrayListPrev.get(i).getNombre().compareTo(encabezadosArrayList.get(i).getNombre())==0) {
                            encabezadosArrayList.get(i).setEditable(encabezadosArrayListPrev.get(i).isEditable());
                            encabezadosArrayList.get(i).setIndexado(encabezadosArrayListPrev.get(i).isIndexado());
                            encabezadosArrayList.get(i).setLlavePrimaria(encabezadosArrayListPrev.get(i).isLlavePrimaria());
                            encabezadosArrayList.get(i).setVisible(encabezadosArrayListPrev.get(i).isVisible());
                        }
                    }
                }
            } catch (FileNotFoundException e ) {
                if(contextWeakReference.get()!=null) {
                    errorMsg = contextWeakReference.get().getResources().getString(R.string.errFileNF);
                }
                return false;
            } catch (IOException e) {
                if(contextWeakReference.get()!=null) {
                    errorMsg = contextWeakReference.get().getResources().getString(R.string.errReadInpS);
                }
                return false;
            }
            return true;
        }

        private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
            String value = "";
            try {
                Cell cell = row.getCell(c);
                CellValue cellValue = formulaEvaluator.evaluate(cell);
                switch (cellValue.getCellType()) {
                    case Cell.CELL_TYPE_BOOLEAN:
                        value = ""+cellValue.getBooleanValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        double numericValue = cellValue.getNumberValue();
                        if(HSSFDateUtil.isCellDateFormatted(cell)) {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                            value = formatter.format(HSSFDateUtil.getJavaDate(numericValue));
                        } else {
                            if(numericValue%1==0) {
                                value = "" + (int) numericValue;
                            } else {
                                value = "" + numericValue;
                            }
                        }
                        break;
                    case Cell.CELL_TYPE_STRING:
                        value = ""+cellValue.getStringValue();
                        break;
                    default:
                }
            } catch (NullPointerException e ) {
                e.printStackTrace();
            }
            return value;
        }
    }

    private class SyncDataSearch extends AsyncTask<Integer, String, String> {
        private ArrayList<Archivos> foundFiles;
        private ListView listViewAr;

        @Override
        protected void onPreExecute() {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            View dialogView = View.inflate(context, R.layout.archivos_excel, null);
            alertDialog.setView(dialogView);
            listViewAr = dialogView.findViewById(R.id.listViewArchivos);
            alertDialog.setPositiveButton(context.getResources().getString(R.string.butClose), (dialogInterface, i) -> {
            });
            final AlertDialog dialog = alertDialog.create();
            dialog.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.grisClaro)));
            dialog.show();
            listViewAr.setOnItemClickListener((adapterView, view, i, l) -> {
                if(configuracion!=null) {
                    configuracion.setArchivoInName(foundFiles.get(i).getName());
                    configuracion.setArchivoInPath(foundFiles.get(i).getPath());
                } else {
                    configuracion = new Configuracion((switches[0].isChecked()) ? 1 : 0, (switches[1].isChecked()) ? 1 : 0, foundFiles.get(i).getName(), foundFiles.get(i).getPath(), archEdit[1].getText().toString());
                }
                archEdit[0].setText(foundFiles.get(i).getName());
                controlEncabezados = new ControlEncabezados();
                new SyncDataExcel(context, ArchivoAdapter, controlEncabezados, progreso, interfazBD).execute(foundFiles.get(i).getPath());
                dialog.dismiss();
            });
            progreso.setVisibility(RelativeLayout.VISIBLE);
            foundFiles = new ArrayList<>();
        }

        @Override
        protected String doInBackground(Integer... cual) {
            checkInternalStorage(System.getenv("EXTERNAL_STORAGE")+"/Download/");
            return "";
        }

        private void checkInternalStorage(String path) {
            File[] listFile;
            File file = null;
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    file = new File(path);
                }
                if(file!=null) {
                    listFile = file.listFiles();
                    if (listFile != null) {
                        for(File file1 : listFile) {
                            checkFile(file1.getAbsolutePath());
                        }
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("Error Archivo", e.getMessage());
            }
        }

        private void checkFile(String path) {
            File[] listFile;
            File file = null;
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    file = new File(path);
                }
                if(file!=null) {
                    listFile = file.listFiles();
                    if (listFile == null) {
                        if (pattern.matcher(path).matches()) {
                            foundFiles.add(new Archivos(path, file.getName()));
                        }
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("Error Archivo", e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progreso.setVisibility(RelativeLayout.GONE);
            try {
                MyInvAdapterArchivos myAppAdapter = new MyInvAdapterArchivos(foundFiles, context);
                listViewAr.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listViewAr.setAdapter(myAppAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public class MyInvAdapterArchivos extends BaseAdapter {
            private class ViewHolder {
                TextView nombreArchivo;
                LinearLayout fondo;
            }

            private final List<Archivos> archList;

            public Context context;

            private MyInvAdapterArchivos(List<Archivos> archs, Context context) {
                this.archList = archs;
                this.context = context;
            }

            @Override
            public int getCount() {
                return archList.size();
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View rowView = convertView;
                ViewHolder viewHolder;
                if(rowView == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    rowView = inflater.inflate(R.layout.list_archivos, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.nombreArchivo = rowView.findViewById(R.id.idArchivo);
                    viewHolder.fondo = rowView.findViewById(R.id.fondoList);
                    rowView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.nombreArchivo.setText(archList.get(position).getName());
                if (position%2==0){
                    viewHolder.fondo.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.filaGris));
                }else {
                    viewHolder.fondo.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white));
                }
                return rowView;
            }
        }
    }
}