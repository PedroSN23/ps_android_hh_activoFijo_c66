package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.Utils;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.pp_android_handheld_library.model.resources.IconGenericEnum;
import com.example.pp_android_handheld_library.view.clases.IconGeneric;
import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.Activos;
import com.example.ps_android_hh_activofijo_c66.model.clases.Cambios;
import com.example.ps_android_hh_activofijo_c66.model.clases.Configuracion;
import com.example.ps_android_hh_activofijo_c66.model.clases.Encabezados;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.activity.InventarioActivity;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class InventarioFragment extends Fragment {
    private InventarioAdapter inventarioAdapter;
    private int inpSel;
    private ArrayList<Integer> encabezadoPosicion;
    private RelativeLayout progressBar;
    private RelativeLayout filterbut;
    private RecyclerView mList;
    private TextView encabezado;
    private TextView progreso;
    private IconGeneric sortIcon;
    private SwitchCompat switchRfid;
    private final TextView [] cantidades = new TextView[2];
    private InterfazBD interfazBD;
    private ArrayList<String> filtrosPrefijo;
    private Configuracion configuracion;
    private ArrayList<Encabezados> encabezadosArrayList;
    private AsyncTask asyncTask;
    private Spinner spinerIndex;
    private MyInnerHandlerInv myInnerHandler;
    private final AtomicBoolean excelReady = new AtomicBoolean(false);
    private boolean cambios=false;
    private int filtInd = -1;
    private ArrayList<String> filtrosColumna;
    private ArrayList<String> epcs;
    ArrayList<String>filtros;
    public InventarioFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.inventario_fragment, container, false);
        mList = v.findViewById(R.id.recyler1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mList.setLayoutManager(layoutManager);
        epcs = new ArrayList<>();
        int indexPk = 0;
        inpSel = 0;

        spinerIndex = v.findViewById(R.id.indexSpinner);
        ArrayList<String> encabezadosIndex = new ArrayList<>();
        encabezadoPosicion = new ArrayList<>();
        filterbut = v.findViewById(R.id.butSelectFiltro);
        progressBar = v.findViewById(R.id.menu1ProgresoInv);
        progreso = v.findViewById(R.id.progreso);
        sortIcon = v.findViewById(R.id.sortIcon);
        encabezado = v.findViewById(R.id.encabezado);

        cantidades[0]= v.findViewById(R.id.cantFaltanteval);
        cantidades[1]= v.findViewById(R.id.cantLeidosval);

        switchRfid = v.findViewById(R.id.menu1s1switchRfid);

        myInnerHandler = new MyInnerHandlerInv(progressBar, getActivity(), cantidades[0], cantidades[1], excelReady, switchRfid, filtrosPrefijo);


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private static final float MAX_SWIPE_DISTANCE = 150;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Activos itemId = inventarioAdapter.getActivos(position);

                if (direction == ItemTouchHelper.LEFT) {
                    if (getActivity() instanceof InventarioActivity) {
                        InventarioActivity inventarioActivity = (InventarioActivity) getActivity();
                        if (inventarioActivity != null) {
                            inventarioActivity.consultaMethod(itemId);
                            inventarioAdapter.notifyItemChanged(position);
                        }
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    if (getActivity() instanceof InventarioActivity) {
                        InventarioActivity inventarioActivity = (InventarioActivity) getActivity();
                        if (inventarioActivity != null) {
                            inventarioActivity.buscarMethod(itemId);
                            inventarioAdapter.notifyItemChanged(position);
                        }
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
               /* if (Math.abs(dX) > MAX_SWIPE_DISTANCE) {
                    dX = Math.signum(dX) * MAX_SWIPE_DISTANCE;
                } */

                if (dX > 0) { // Deslizar a la derecha
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.azulAstlix.getCode()))
                            .addSwipeRightActionIcon(R.drawable.ic_search)
                            .create()
                            .decorate();

                } else if (dX < 0) { // Deslizar a la izquierda
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.amarillo.getCode()))
                            .addSwipeLeftActionIcon(R.drawable.edit)
                            .create()
                            .decorate();
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mList);

        interfazBD = new InterfazBD(getActivity());
        filtrosPrefijo = interfazBD.obtenerFiltros();
        filtros = new ArrayList<>();
        myInnerHandler = new MyInnerHandlerInv(progressBar, getActivity(), cantidades[0], cantidades[1], excelReady, switchRfid, filtrosPrefijo);

        SyncDataStInvExcel invSync = new SyncDataStInvExcel();

        if(filtrosPrefijo.size()>0) {
            configuracion = interfazBD.obtenerConfiguracion();
            if (configuracion != null) {
                encabezadosArrayList = interfazBD.obtenerEncabezados();
                if(encabezadosArrayList.size()>0) {
                    boolean pk = false;
                    for (int i = 0; i < encabezadosArrayList.size(); i++) {
                        if (encabezadosArrayList.get(i).isIndexado()) {
                            encabezadosIndex.add(encabezadosArrayList.get(i).getNombre());
                            encabezadoPosicion.add(i);
                            if (encabezadosArrayList.get(i).isLlavePrimaria()) {
                                encabezado.setText(encabezadosArrayList.get(i).getNombre());
                                indexPk = encabezadosIndex.size()-1;
                                pk=true;
                            }
                        }
                    }
                    if(pk) {
                        asyncTask=invSync.execute(configuracion.getArchivoInPath());
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.errNoPk), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.errNoEnc), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.errBDdat), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.errNoFilt), Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_spinner,encabezadosIndex);
        spinerIndex.setAdapter(adapter);
        spinerIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String encabezadoSelect = encabezadosIndex.get(position);
                for (int i = 0; i<encabezadosArrayList.size(); i++){
                    if (encabezadosArrayList.get(i).getNombre().equals(encabezadoSelect)){
                        if (inventarioAdapter!=null){
                            inventarioAdapter.setSelected(i);
                            encabezado.setText(encabezadoSelect);
                            inventarioAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinerIndex.setSelection(indexPk,true);

        filterbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeleccionarFiltro();
            }
        });

        sortIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleSort();
            }
        });

        return v;
    }

    public boolean estadoSwitch(){
        return switchRfid.isChecked();
    }

    public void BarcodeOperation(String barCode){
        switch(inventarioAdapter.newTagRead(barCode)) {
            case 0:
                myInnerHandler.playToneWrong();
                break;
            case 1:
                myInnerHandler.playToneOk();
                inventarioAdapter.notifyDataSetChanged();
                myInnerHandler.agregarCantidades(1);
                break;
            default:
                myInnerHandler.playToneWrong();
                break;
        }
    }
    public void sendTagsAlt(UHFTagsRead uhfTagsRead) {
        String epc = uhfTagsRead.getEpc();
        String activo = Utils.convertirActivo(epc);
        ArrayList<String> listaNombresActivos = new ArrayList<>();
        ArrayList<String> filtros = interfazBD.obtenerFiltros();
        ArrayList<Activos> activosInventariados = inventarioAdapter.getActivosInventariados();
        for (Activos activo1 : inventarioAdapter.getArrayListActivos()) {
            listaNombresActivos.add(activo1.getId());
        }
        boolean positive = false;
        for (String filtro : filtros) {
            if (activo.startsWith(filtro)) {
                positive = true;
                break;
            }
        }
        if (positive && !epcs.contains(activo) && listaNombresActivos.contains(activo) && !onInventario(activosInventariados, activo)) {
            epcs.add(activo);
            inventarioAdapter.newTagRead(activo);
            myInnerHandler.agregarCantidades(1);
            Collections.sort(inventarioAdapter.activos, (activos, a1) -> activos.compareTo(a1.isInventariado(), a1.getId(), myInnerHandler.sort));
            myInnerHandler.playToneOk();
            inventarioAdapter.notifyDataSetChanged();
        } else {

        }
    }
    private boolean onInventario(ArrayList<Activos> activosInventariados, String activo) {
        for (Activos activoInventariado : activosInventariados) {
            if (activoInventariado.compareTo(activo)) {
                return true;
            }
        }
        return false;
    }
    public void ReiniciarInventario() {
        if(excelReady.get()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.advCleanPrs))
                    .setCancelable(false)
                    .setPositiveButton( getResources().getString(R.string.butCont), (dialogInterface, i) -> {
                        interfazBD.eliminarCambios();
                        interfazBD.eliminarInventario();
                        epcs.clear();
                        excelReady.set(false);
                        SyncDataStInvExcel invSync = new SyncDataStInvExcel();
                        invSync.execute(configuracion.getArchivoInPath(), String.format(Locale.getDefault(), "%d", inventarioAdapter.getSelected()));
                    })
                    .setNegativeButton(getResources().getString(R.string.butCancel), (dialogInterface, i) -> dialogInterface.cancel());
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(arg0 -> {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.menu1p));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.menu1p));
            });
            alert.show();
        }
    }
    public void GuardarFinalizar() {
        if(cambios) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.menu1s1GuardarInv))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.butCont), (dialogInterface, i) -> {
                        dialogInterface.cancel();
                        SyncDataSaveInv saveSync = new SyncDataSaveInv();
                        saveSync.execute(configuracion.getArchivoInPath(), configuracion.getPrefijoOut());
                    })
                    .setNegativeButton(getResources().getString(R.string.butCancel), (dialogInterface, i) -> dialogInterface.cancel());
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(arg0 -> {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.menu1p));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.menu1p));
            });
            alert.show();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.menu1s1NoCambios), Toast.LENGTH_LONG).show();
        }
    }

    public boolean rutinaSalir() {
        System.out.println("ENTRO AQUI");
        if (cambios) {
            System.out.println("ENTRO AQUI PORQUE NO HAY CAMBIOS");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final boolean[] usuarioEligioOk = {false};
            builder.setMessage(getResources().getString(R.string.advBackPrs))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.butCont), (dialogInterface, i) -> {
                        getActivity().finish();
                        usuarioEligioOk[0] = true;
                    })
                    .setNegativeButton(getResources().getString(R.string.butCancel), (dialogInterface, i) -> {
                        dialogInterface.cancel();
                        usuarioEligioOk[0] = false;
                    });
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(arg0 -> {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.menu1p));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.menu1p));
            });
            alert.show();
            return usuarioEligioOk[0];
        } else {
            getActivity().finish();
            return true;
        }
    }

    private void rutinaEspera() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void ToggleSort() {
        final int tmpSort = myInnerHandler.changeSort();
        updateSortIcon(tmpSort);

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                mList,
                PropertyValuesHolder.ofFloat("scaleX", 1f, 0.8f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 0.8f),
                PropertyValuesHolder.ofFloat("alpha", 1f, 0.5f)
        );
        scaleDown.setDuration(250);

        ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                mList,
                PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0.5f, 1f)
        );
        scaleUp.setDuration(250);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(scaleDown, scaleUp);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();

        Collections.sort(inventarioAdapter.activos, (activos, a1) -> activos.compareTo(a1.isInventariado(), a1.getId(), tmpSort));
        inventarioAdapter.notifyDataSetChanged();
    }


    private void updateSortIcon(int tmpSort) {
        if (tmpSort == 1) {
            sortIcon.setText(getResources().getString(IconGenericEnum.fontawesome_sort_down.getCode()));
            sortIcon.init(IconGenericEnum.fontawesome_sort_down.getType());
        } else {
            sortIcon.setText(getResources().getString(IconGenericEnum.fontawesome_sort_up.getCode()));
            sortIcon.init(IconGenericEnum.fontawesome_sort_up.getType());
        }
    }


    public void SeleccionarFiltro(){
        if (filtInd>=0){
            crearDialogoFiltro();
        }
    }
    public void crearDialogoFiltro() {
        View promptsView = View.inflate(getActivity(), R.layout.select_filter, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        final LinearLayout radioPadre = promptsView.findViewById(R.id.radioPadreFiltros);

        final RadioGroup rg = new RadioGroup(getActivity());

        // Acceder a las columnas
        if (filtrosColumna.size() > 0) {
            for (int i = 0; i < filtrosColumna.size(); i++) {
                RadioButton rb = new RadioButton(getActivity());
                rb.setText(filtrosColumna.get(i));
                rb.setTag(i);
                rg.addView(rb);
            }

            if (filtrosColumna.size() > 0) {
                radioPadre.addView(rg);
                rg.clearCheck();
                rg.setTag(0);
                rg.setOnCheckedChangeListener((group, checkedId) -> {
                    // Lógica al seleccionar un RadioButton
                    inpSel = (int) rg.findViewById(checkedId).getTag();
                });
            }
            rg.check(rg.getChildAt(inpSel).getId());
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.errFiltEmpty), Toast.LENGTH_LONG).show();
        }

        alertDialogBuilder.setPositiveButton("OK", null);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(dialogInterface -> {
            Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setTextColor(ContextCompat.getColor(getActivity(), R.color.menu1p));
            button.setOnClickListener(view -> {
                // Obtener el RadioButton seleccionado del RadioGroup
                int checkedRadioButtonId = rg.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = rg.findViewById(checkedRadioButtonId);

                if (selectedRadioButton != null) {
                    String filtroSeleccionado = selectedRadioButton.getText().toString();
                    Log.e("Filtro", "Seleccion: " + filtroSeleccionado);
                    inventarioAdapter.aplicarFiltro(filtroSeleccionado);
                    myInnerHandler.setCantFaltante(inventarioAdapter.getCantidad());
                    myInnerHandler.resetCantCorrecta();
                    int cantAgreg = 0;
                    for (Activos activo : inventarioAdapter.activos) {
                        if (activo.isInventariado()) {
                            cantAgreg++;
                        }
                    }
                    myInnerHandler.agregarCantidades(cantAgreg);
                    inventarioAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            });
        });

        alertDialog.show();
    }
    private class SyncDataStInvExcel extends AsyncTask<String, Integer, Integer> {
        private String errorMsg;
        private ArrayList<Activos> activos;
        int indexSel = 0;
        int repetidos=0;
        int noEncabezado=0;
        int lenErr=0;
        int prcnt=0;

        @Override
        protected void onPreExecute() {
            Log.d("Seattle", "preexec");
            rutinaEspera();
            progreso.setVisibility(View.VISIBLE);
            progreso.setText("0%");
            activos = new ArrayList<>();
            filtrosColumna = new ArrayList<>();
            filtrosColumna.add("Sin Filtro");
            cambios=false;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            if(strings.length>1) {
                try {
                    indexSel = Integer.parseInt(strings[1]);
                } catch (NumberFormatException e) {
                    indexSel = 0;
                }
            }
            return readExcelData(strings[0]);
        }

        private int readExcelData(String filePath) {
            ArrayList<Integer> indexado = new ArrayList<>();
            int pkInd=encabezadosArrayList.size();

            for(int i=0; i<encabezadosArrayList.size(); i++) {
                if(encabezadosArrayList.get(i).isLlavePrimaria()) {
                    pkInd=i;
                    if (indexSel==0){
                        indexSel = i;
                    }
                }
                if(encabezadosArrayList.get(i).isIndexado()) {
                    indexado.add(i);
                }
                if (encabezadosArrayList.get(i).isFiltro()){
                    filtInd=i;
                }
            }
            if(pkInd<encabezadosArrayList.size() && indexado.size()>0) {
                File inputFile = new File(filePath);
                try {
                    InputStream inputStream = new FileInputStream(inputFile);
                    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    int rowsCount = sheet.getPhysicalNumberOfRows();
                    FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                    int cellsCount = sheet.getRow(0).getPhysicalNumberOfCells();
                    for (int r = 1; r < rowsCount; r++) { //iterador de lineas
                        int tmp = r*100/rowsCount;
                        if(tmp!=prcnt) {
                            prcnt=tmp;
                            publishProgress(prcnt);
                        }
                        Row row = sheet.getRow(r);
                        String[] dataComp = new String[cellsCount];
                        String[] dataHead = new String[indexado.size()];
                        for (int c = 0, m = 0; c < cellsCount; c++) { //iterador de celdas
                            if (encabezadosArrayList.get(c).getNombre().toLowerCase().contains("hora")) {
                                dataComp[c] = getCellAsString(row, c, formulaEvaluator, true);
                            } else {
                                dataComp[c] = getCellAsString(row, c, formulaEvaluator, false);
                            }
                            for(int i=0; i<indexado.size(); i++) {
                                if(indexado.get(i)==c) {
                                    dataHead[m++]= dataComp[c];
                                    break;
                                }
                            }
                            if (c==filtInd){
                                if (!filtrosColumna.contains(dataComp[c])){
                                    filtrosColumna.add(dataComp[c]);
                                }
                            }
                        }
                        boolean encEnc=false;
                        for(int i = 0; i< filtrosPrefijo.size(); i++) {
                            if(dataComp[pkInd].startsWith(filtrosPrefijo.get(i))&&dataComp[pkInd].length()==11) {
                                encEnc=true;
                                boolean found = false;
                                for(Activos activo: activos) {
                                    if(activo.compareTo(dataComp[pkInd])){
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found) {
                                    activos.add(new Activos(r, dataHead, dataComp, pkInd));
                                } else {
                                    repetidos++;
                                }
                                break;
                            }
                        }
                        if(!encEnc) {
                            if(dataComp[pkInd].length()!=11) {
                                Log.d("Laura", dataComp[pkInd].length()+"<<<<<<"+rowsCount);
                                lenErr++;
                            } else {
                                noEncabezado++;
                            }
                        }
                        if(isCancelled()) {
                            errorMsg = getResources().getString(R.string.errProCan);
                            return -1;
                        }
                    }
                } catch (FileNotFoundException e) {
                    errorMsg = getResources().getString(R.string.errFileNF);
                    return -1;
                } catch (IOException e) {
                    errorMsg = getResources().getString(R.string.errReadInpS);
                    return -1;
                }
            }
            return 0;
        }

        private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator, boolean hora) {
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
                            SimpleDateFormat formatter;
                            if(hora) {
                                formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                            } else {
                                formatter = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                            }
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

        @Override
        protected void onPostExecute(Integer s) {
            progreso.setVisibility(View.GONE);
            if(repetidos>0) {
                Toast.makeText(getActivity(), String.format(Locale.getDefault(), "%s %d %s", getResources().getString(R.string.errLinRep), repetidos, getResources().getString(R.string.errLinRep2)), Toast.LENGTH_LONG).show();
            }
            if(noEncabezado>0) {
                Toast.makeText(getActivity(), String.format(Locale.getDefault(), "%s %d %s", getResources().getString(R.string.errLinRep), noEncabezado, getResources().getString(R.string.errLinEnc)),  Toast.LENGTH_LONG).show();
            }
            if(lenErr>0) {
                Toast.makeText(getActivity(), String.format(Locale.getDefault(), "%s %d %s", getResources().getString(R.string.errLinRep), lenErr, getResources().getString(R.string.errLinLen)),  Toast.LENGTH_LONG).show();
            }
            if(s<0) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            }
            inventarioAdapter = new InventarioAdapter(getActivity(),activos, indexSel);
            mList.setAdapter(inventarioAdapter);
            myInnerHandler.respaldarAdapter(inventarioAdapter);
            myInnerHandler.setCantFaltante(inventarioAdapter.getCantidad());
            myInnerHandler.resetCantCorrecta();
            ArrayList<String[]> inventario = interfazBD.obtenerInventario();
            int cantAgreg=0;
            for(String[] inv: inventario) {
                if(inventarioAdapter.newTagRead(inv[0])==1) {
                    cantAgreg++;
                }
            }
            ArrayList<Cambios> cambios = interfazBD.obtenerCambios();
            for(Cambios cambio: cambios) {
                inventarioAdapter.realizarCambio(cambio);
            }
            myInnerHandler.agregarCantidades(cantAgreg);
            excelReady.set(true);
            sortIcon.setText(getResources().getString(IconGenericEnum.fontawesome_sort_down.getCode()));
            sortIcon.init(IconGenericEnum.fontawesome_sort_up.getType());
            Collections.sort(inventarioAdapter.activos, (activos, a1) -> activos.compareTo(a1.isInventariado(), a1.getId(), 1));
            inventarioAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progreso.setText(String.format(Locale.getDefault(), "%d%c", values[0], '%'));
            super.onProgressUpdate(values);
        }
    }

    static class MyInnerHandlerInv extends Handler {
        WeakReference<RelativeLayout> progreso;
        WeakReference<TextView> cantidadFaltante;
        WeakReference<TextView> cantidadCorrecta;
        WeakReference<Context> context;
        WeakReference<InventarioAdapter> inventarioAdapter;
        WeakReference<AtomicBoolean> excelReady;
        WeakReference<SwitchCompat>switchRfid;
        WeakReference<ImageView> imagenOndasWeakReference;
        int cantCorrecta;
        int cantFaltante;
        private boolean genAdRdy=false;

        private final ToneGenerator tonG;
        ArrayList<String>filtros;
        ArrayList<Double> history=new ArrayList<>();
        int sort=1;
        void playToneOk() {
            tonG.startTone(ToneGenerator.TONE_PROP_ACK, 100);
        }

        void playToneWrong() {
            tonG.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 500);
        }
        int changeSort() {
            sort*=-1;
            return sort;
        }
        MyInnerHandlerInv(RelativeLayout progreso, Context context, TextView cantidadFaltante, TextView cantidadCorrecta, AtomicBoolean excelReady, SwitchCompat switchRfid, ArrayList<String>filtros) {
            this.filtros = filtros;
            this.progreso = new WeakReference<>(progreso);
            this.context = new WeakReference<>(context);
            this.cantidadFaltante = new WeakReference<>(cantidadFaltante);
            this.cantidadCorrecta = new WeakReference<>(cantidadCorrecta);
            this.excelReady = new WeakReference<>(excelReady);
            this.switchRfid = new WeakReference<>(switchRfid);
            tonG = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
            cantCorrecta=0;
            cantFaltante=0;
        }

        private void respaldarAdapter(InventarioAdapter generalAdapterExcel) {
            this.inventarioAdapter = new WeakReference<>(generalAdapterExcel);
            genAdRdy=true;
        }

        private void respaldarDialogo(ImageView imagenOndas){
            this.imagenOndasWeakReference = new WeakReference<>(imagenOndas);
        }

        private void setCantFaltante(int cantFaltante) {
            this.cantFaltante = cantFaltante;
            this.cantidadFaltante.get().setText(String.format(Locale.getDefault(), "%d", cantFaltante));
        }

        private void resetCantCorrecta() {
            this.cantCorrecta = 0;
            this.cantidadCorrecta.get().setText(String.format(Locale.getDefault(), "%d", cantCorrecta));
        }

        public void agregarCantidades(int cant) {
            this.cantCorrecta+=cant;
            this.cantFaltante-=cant;
            if(this.cantFaltante<0) this.cantFaltante=0;
            this.cantidadCorrecta.get().setText(String.format(Locale.getDefault(), "%d", cantCorrecta));
            this.cantidadFaltante.get().setText(String.format(Locale.getDefault(), "%d", cantFaltante));
        }
    }

    private class SyncDataSaveInv extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(RelativeLayout.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... strings) {
            ArrayList<Activos> activos = inventarioAdapter.getArrayListActivos();
            Date da = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = df.format(da);

            for(Activos activo: activos) {
                if(activo.isInventariado()) {
                    interfazBD.insertarInventariado(activo.getId(), formattedDate);
                }
                if(activo.isModificado()) {
                    ArrayList<Integer> indexMod = activo.getIndexModify();
                    for(Integer x: indexMod) {
                        interfazBD.insertarCambios(activo.getId(), activo.getData(x), x);
                    }
                }
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer s) {
            progressBar.setVisibility(RelativeLayout.GONE);
            cambios=false;
            Toast.makeText(getActivity(), getResources().getString(R.string.msgGuardado), Toast.LENGTH_LONG).show();
        }
    }
    public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.ViewHolder> {
        private ArrayList<Activos> activos;
        private final ArrayList<Activos> listaCompletaActivos;
        private final Context context;
        private int selected;

        // Constructor
        public InventarioAdapter(Context context, ArrayList<Activos> activos, int selected) {
            this.listaCompletaActivos = new ArrayList<>(activos);
            this.activos = new ArrayList<>(activos);
            this.context = context;
            this.selected = selected;
        }

        public Activos getActivos(int position) {
            return activos.get(position);
        }

        public int getCantidad() {
            return activos.size();
        }

        public void setSelected(int selected) {
            this.selected = selected;
        }

        public int getSelected() {
            return selected;
        }

        private int newTagRead(String epcAscii) {
            int ret=-1;
            for(Activos activo: this.activos) {
                if(activo.compareTo(epcAscii)) {
                    ret=0;
                    if(!activo.isInventariado()) {
                        activo.setInventariado(true);
                        ret=1;
                        cambios=true;
                    }
                    break;
                }
            }
            return ret;
        }
        public void aplicarFiltro(String filtroSeleccionado) {
            activos = new ArrayList<>();
            if (filtroSeleccionado.equals("Sin Filtro")) {
                activos = new ArrayList<>(listaCompletaActivos);
            } else {
                for (Activos activo : listaCompletaActivos) {
                    if (activo.getData(selected).equals(filtroSeleccionado)) {
                        activos.add(activo);
                    }
                }
            }
        }

        public void realizarCambio(Cambios cambios) {
            for (Activos activo : activos) {
                if (activo.compareTo(cambios.getTag())) {
                    activo.setDataAt(cambios.getIndex(), cambios.getValor());
                    break;
                }
            }
        }
        public ArrayList<Activos> getActivosInventariados() {
            ArrayList<Activos> activosInventariados = new ArrayList<>();

            for (Activos activo : activos) {
                if (activo.isInventariado()) {
                    activosInventariados.add(activo);
                }
            }

            return activosInventariados;
        }

        public ArrayList<Activos> getArrayListActivos() {
            return activos;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventario_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Activos activo = activos.get(position);
            holder.textId.setText("ID: " + activo.getId());
            holder.textText.setText(activo.getData(selected));

            if (activo.isInventariado()) {
                holder.statusIcon.setText(context.getResources().getString(IconGenericEnum.fontawesome_check_circle.getCode()));
                holder.statusIcon.init(IconGenericEnum.fontawesome_check_circle.getType());
                holder.statusIcon.setTextColor(ContextCompat.getColor(context, R.color.bien));
            } else {
                holder.statusIcon.setText(context.getResources().getString(IconGenericEnum.fontawesome_question_circle.getCode()));
                holder.statusIcon.init(IconGenericEnum.fontawesome_question_circle.getType());
                holder.statusIcon.setTextColor(ContextCompat.getColor(context, R.color.faltante));
            }
        }

        @Override
        public int getItemCount() {
            return activos.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textText;
            TextView textId;
            IconGeneric statusIcon;

            public ViewHolder(View itemView) {
                super(itemView);
                textText = itemView.findViewById(R.id.maintxt);
                textId = itemView.findViewById(R.id.maintxtid);
                statusIcon = itemView.findViewById(R.id.semaforo);
            }
        }
    }

}
