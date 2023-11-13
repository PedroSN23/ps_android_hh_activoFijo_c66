package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.pp_android_handheld_library.view.clases.IconGeneric;
import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.Activos;
import com.example.ps_android_hh_activofijo_c66.model.clases.Configuracion;
import com.example.ps_android_hh_activofijo_c66.model.clases.Encabezados;
import com.example.ps_android_hh_activofijo_c66.model.clases.ViewWeightAnimationWrapper;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.activity.ConsultaActivity;
import com.example.ps_android_hh_activofijo_c66.view.adapter.ConsultaAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rscja.barcode.BarcodeFactory;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConsultaFragment extends Fragment {

    private static final int CAMERA_REQUEST = 1800;
    private boolean cambios=false;
    private ArrayList<Activos> activos;
    private ArrayList<Encabezados> encabezadosArrayList;
    private final AtomicBoolean excelReady = new AtomicBoolean(false);
    private int selectedId;
    private int indActivoGlob;
    private Activos activoGlobal;
    private Bitmap photo;
    private String imgName;
    private int numeroImagen;
    private CircleImageView imag_act;
    private IconGeneric icon_act;
    private int llavePrimaria;
    private ArrayList<EditText> et;
    private ConsultaHandler consultaHandler;
    private RelativeLayout progressBar;
    private TextView progreso;
    private LinearLayout father;
    private InterfazBD interfazBD;
    private ArrayList<String> filtros;
    private Configuracion configuracion;
    private AsyncTask asyncTask;
    private IconGeneric imagensiguiente;
    private IconGeneric imagenanterior;
    private String curentPhotoPath;
    public ConsultaFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.consulta_fragment, container, false);

        imag_act = v.findViewById(R.id.img_act);
        icon_act = v.findViewById(R.id.icon_act);
        imagensiguiente = v.findViewById(R.id.imgsiguiente);
        imagenanterior = v.findViewById(R.id.imganterior);

        llavePrimaria=-1;

        progressBar = v.findViewById(R.id.menu2ProgresoForm);
        progreso = v.findViewById(R.id.progresoForm);

        et = new ArrayList<>();

        father = v.findViewById(R.id.linearInsertForm);

        interfazBD = new InterfazBD(getActivity());

        consultaHandler = new ConsultaHandler(progressBar,getActivity(), excelReady);
        SyncDataStFormExcel invSync = new SyncDataStFormExcel();
        interfazBD = new InterfazBD(getActivity());
        filtros = interfazBD.obtenerFiltros();

        if(filtros.size()>0) {
            configuracion = interfazBD.obtenerConfiguracion();
            if (configuracion != null) {
                encabezadosArrayList = interfazBD.obtenerEncabezados();
                if(encabezadosArrayList.size()>0) {
                    boolean pk = false;
                    for (int i = 0; i < encabezadosArrayList.size(); i++) {
                        if (encabezadosArrayList.get(i).isIndexado()) {
                            if (encabezadosArrayList.get(i).isLlavePrimaria()) {
                                pk = true;
                                llavePrimaria=i;
                                break;
                            }
                        }
                    }
                    if(pk) {
                        asyncTask = invSync.execute(configuracion.getArchivoInPath());
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.errNoPk), Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.errNoEnc), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.errBDdat), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.errNoFilt), Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        imag_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TomarFoto();
            }
        });
        imagensiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagenSiguiente();
            }
        });

        imag_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagenAnterior();
            }
        });

        return v;
    }
    public void AbrirDialogo() {
      crearDialogo();
    }

    public void TomarFoto() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        photoFile = createFile();
        if (photoFile !=null){
            //Log.e("TAG","photoFile: "+photoFile.getAbsolutePath());
            Uri photoUri = FileProvider.getUriForFile(getActivity(),"com.example.ps_android_hh_activoFijo_c66",photoFile);
            //Log.e("TAG","uri: "+photoUri.getPath());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }
    private void getImageFromFile(String fname) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Download/ActImages");
        myDir.mkdirs();
        File file = new File(myDir, fname);
        if (file.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imag_act.setImageBitmap(myBitmap);
            imag_act.setVisibility(View.VISIBLE);
            icon_act.setVisibility(View.GONE);
        } else {
            imag_act.setVisibility(View.GONE);
            icon_act.setVisibility(View.VISIBLE);
        }
    }

    private File createFile(){
        String imgNameSlect = imgName+"("+numeroImagen+")";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(imgNameSlect,".jpg",storageDir);
            curentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void ImagenAnterior(){
        if (numeroImagen>1){
            numeroImagen --;
            imgName = activoGlobal.getId();
            String imgNameSlect = imgName+"("+numeroImagen+").jpg";
            getImageFromFile(imgNameSlect);
        }
    }

    public void ImagenSiguiente(){
        if (numeroImagen<3){
            numeroImagen ++;
            imgName = activoGlobal.getId();
            String imgNameSlect = imgName+"("+numeroImagen+").jpg";
            getImageFromFile(imgNameSlect);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            //photo = (Bitmap) data.getExtras().get("data");
            //imag_act.setImageBitmap(photo);
            imag_act.setImageURI(Uri.parse(curentPhotoPath));
            imag_act.setVisibility(View.VISIBLE);
            icon_act.setVisibility(View.GONE);
            saveImageToFile();

        }
    }

    private void saveImageToFile() {
          File origen = new File(curentPhotoPath);
        String root = Environment.getExternalStorageDirectory().toString()+"/Download/ActImages/"+imgName+"("+numeroImagen+").jpg";
        File destino = new File(root);
        if (destino.exists()){
            destino.delete();
        }

        try {
            InputStream in = new FileInputStream(origen);
            OutputStream out = new FileOutputStream(destino);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            MediaScannerConnection.scanFile(getActivity(),
                    new String[]{destino.getAbsolutePath()}, null,
                    (path, uri) -> {
                    });
            origen.delete();
            in.close();
            out.close();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void crearDialogo() {
        selectedId=0;
        View promptsView = View.inflate(getActivity(), R.layout.buscar_activo_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setView(promptsView);

        final Spinner spinner = promptsView.findViewById(R.id.activosIdsSpinner);
        final ConsultaAdapter consultaAdapter = new ConsultaAdapter(getActivity(),R.layout.spinner, R.id.textSpinner, activos);
        spinner.setAdapter(consultaAdapter);

        LinearLayout padreSlide = promptsView.findViewById(R.id.layoutbotonesSlide);

        final ArrayList<LinearLayout> butSlide = new ArrayList<>();

        boolean first=false;
        for(int i=0; i<encabezadosArrayList.size(); i++) {
            if (encabezadosArrayList.get(i).isIndexado()) {
                LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                size.width = 0;
                View view = View.inflate(getActivity(), R.layout.slide_button, null);
                if (!first) {
                    first = true;
                    size.weight = 6;
                    view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.menu2_button_alt));
                } else {
                    size.weight = 1;
                }
                view.setLayoutParams(size);
                IconGeneric icon = view.findViewById(R.id.iconSlideButForm);
                TextView tv = view.findViewById(R.id.textSlideButForm);
                if (encabezadosArrayList.get(i).isLlavePrimaria()) {
                    icon.setText(getResources().getString(R.string.zmdi_key));
                } else {
                    icon.setText(getResources().getString(R.string.zmdi_bookmark));
                }
                tv.setText(encabezadosArrayList.get(i).getNombre());
                padreSlide.addView(view);
                butSlide.add((LinearLayout) view);
            }
        }

        final LinearLayout butOk = promptsView.findViewById(R.id.okProptFormulario);
        final LinearLayout butExit = promptsView.findViewById(R.id.exitProptFormulario);

        final SwitchCompat switchRfidForm = promptsView.findViewById(R.id.switchRfidForm);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        consultaHandler.respaldarDialog(spinner, activos);

        alertDialog.setOnShowListener(dialogInterface -> {
            butOk.setOnClickListener(view -> {
                indActivoGlob = spinner.getSelectedItemPosition();
                activoGlobal = activos.get(indActivoGlob);

                if(activoGlobal!=null) {
                    photo = null;
                    numeroImagen = 1;
                    imgName = activoGlobal.getId();
                    String imgNameSlect = imgName+"("+numeroImagen+").jpg";
                    getImageFromFile(imgNameSlect);

                    for(int i=0, j=0; i<encabezadosArrayList.size() && j<et.size(); i++) {
                        if(encabezadosArrayList.get(i).isVisible()) {
                            et.get(j).setText(activoGlobal.getData(i));
                            j++;
                        }
                    }
                    Toast t;
                    switch (activoGlobal.getErrTipo()) {
                        case 0: //no error
                            break;
                        case 1: //repetido
                            Toast.makeText(getActivity(), getResources().getString(R.string.errIdRep), Toast.LENGTH_LONG).show();
                            break;
                        case 2: //longitud
                            Toast.makeText(getActivity(), getResources().getString(R.string.errIdLen), Toast.LENGTH_LONG).show();
                            break;
                        case 3: //encabezado
                            Toast.makeText(getActivity(), getResources().getString(R.string.errIdEnc), Toast.LENGTH_LONG).show();
                            break;
                    }
                    alertDialog.dismiss();
                }
            });
            butExit.setOnClickListener(view -> {
                alertDialog.dismiss();
                getActivity().finish();
            });
            for(int i=0; i<butSlide.size(); i++) {
                butSlide.get(i).setOnClickListener(view -> {
                    ViewWeightAnimationWrapper animationWrapper = new ViewWeightAnimationWrapper(butSlide.get(selectedId));
                    ObjectAnimator anim = ObjectAnimator.ofFloat(animationWrapper,
                            "weight",
                            animationWrapper.getWeight(),
                            1);
                    if (anim != null) {
                        anim.setDuration(500);
                        anim.start();
                    }
                    ViewWeightAnimationWrapper animationWrapper1 = new ViewWeightAnimationWrapper(view);
                    ObjectAnimator anim1 = ObjectAnimator.ofFloat(animationWrapper1,
                            "weight",
                            animationWrapper1.getWeight(),
                            6);
                    if (anim1 != null) {
                        anim1.setDuration(500);
                        anim1.start();
                    }

                    final int sdk = Build.VERSION.SDK_INT;
                    if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                        butSlide.get(selectedId).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.menu2_button));
                        view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.menu2_button_alt));
                    }
                    for(int i12 = 0; i12 <butSlide.size(); i12++) {
                        if(butSlide.get(i12).equals(view)) {
                            selectedId= i12;
                            break;
                        }
                    }
                    for(Activos activo: activos) {
                        activo.setIdSel(selectedId);
                    }
                    consultaAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    public void GuardarFormularioForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.menu2s1GuardarForm))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.butCont), (dialogInterface, i) -> {
                    if(photo!=null) {
                        imgName = activoGlobal.getId();
                        //String imgNameSlect = imgName+"("+numeroImagen+").jpg";
                        //saveImageToFile(photo, imgNameSlect);
                    }
                    boolean cambios = false;
                    boolean error = false;
                    for(int x=0, y=0; y<et.size() && x<activoGlobal.getCantidadDatos() && x<encabezadosArrayList.size(); x++) {
                        if(encabezadosArrayList.get(x).isVisible()) {
                            String valor= et.get(y).getText().toString();
                            y++;
                            if(encabezadosArrayList.get(x).isEditable()) {
                                if (valor.compareTo(activoGlobal.getData(x)) != 0) { //hay cambios
                                    if (x == llavePrimaria) {
                                        boolean found = false;
                                        if (valor.length() == 11) {
                                            for (int a = 0; a < filtros.size(); a++) {
                                                if (valor.startsWith(filtros.get(a))) {
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if (found) {
                                                boolean repetido = false;
                                                for (int j = 0; j < activos.size(); j++) {
                                                    if (j != indActivoGlob) {
                                                        if (valor.compareTo(activos.get(j).getId()) == 0) {
                                                            repetido = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (!repetido) {
                                                    cambios = true;
                                                    activoGlobal.setDataAt(x, valor);
                                                    activoGlobal.setId(valor);
                                                } else {
                                                    error = true;
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.errIdRep), Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                error = true;
                                                Toast.makeText(getActivity(), getResources().getString(R.string.errIdEnc), Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            error = true;
                                            Toast.makeText(getActivity(), getResources().getString(R.string.errIdLen), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        cambios = true;
                                        activoGlobal.setDataAt(x, valor);
                                    }
                                }
                            }
                        }
                    }
                    if(cambios && !error) {
                        dialogInterface.cancel();
                        SyncDataSaveInv saveSync = new SyncDataSaveInv(getActivity(), progressBar, activoGlobal, encabezadosArrayList);
                        saveSync.execute(configuracion.getArchivoInPath());
                    } else {
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.butCancel), (dialogInterface, i) -> dialogInterface.cancel());
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(arg0 -> {
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.azulAstlix));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.rojodif));
        });
        alert.show();
    }


    private class SyncDataStFormExcel extends AsyncTask<String, Integer, Integer> {
        private String errorMsg;
        int repetidos=0;
        int noEncabezado=0;
        int lenErr=0;
        int prcnt;

        @Override
        protected void onPreExecute() {
            progreso.setVisibility(View.VISIBLE);
            progreso.setText("0%");
            prcnt=0;
            activos = new ArrayList<>();
            cambios=false;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            return readExcelData(strings[0]);
        }

        private int readExcelData(String filePath) {
            Log.d("SyncDataStFormExcel", "Entrando en readExcelData");
            ArrayList<Integer> indexado = new ArrayList<>();
            int pkInd=encabezadosArrayList.size();
            for(int i=0; i<encabezadosArrayList.size(); i++) {
                if(encabezadosArrayList.get(i).isLlavePrimaria()) {
                    pkInd=i;
                }
                if(encabezadosArrayList.get(i).isIndexado()) {
                    indexado.add(i);
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
                        Log.d("SyncDataStFormExcel", "Leyendo fila: " + r);
                        Row row = sheet.getRow(r);
                        String[] dataComp = new String[cellsCount];
                        String[] dataHead = new String[indexado.size()];
                        for (int c = 0, m = 0; c < cellsCount; c++) { //iterador de celdas
                            Log.d("SyncDataStFormExcel", "Leyendo celda: " + c);
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
                        }
                        boolean encEnc=false;
                        Activos actTemp = new Activos(r, dataHead, dataComp, pkInd);
                        for(int i=0; i<filtros.size(); i++) {
                            if(dataComp[pkInd].startsWith(filtros.get(i))&&dataComp[pkInd].length()==11) {
                                encEnc=true;
                                for(Activos activo: activos) {
                                    if(activo.compareTo(dataComp[pkInd])){
                                        repetidos++;
                                        actTemp.setErrTipo(1);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        if(!encEnc) {
                            if(dataComp[pkInd].length()!=11) {
                                actTemp.setErrTipo(2);
                                lenErr++;
                            } else {
                                actTemp.setErrTipo(3);
                                noEncabezado++;
                            }
                        }
                        activos.add(actTemp);
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
            for(int x=0; x<encabezadosArrayList.size(); x++) {
                if(encabezadosArrayList.get(x).isVisible()) {
                    View ll = View.inflate(getActivity(), R.layout.generic_object, null);
                    TextInputLayout tv = ll.findViewById(R.id.genTextViewForm);
                    tv.setHint(encabezadosArrayList.get(x).getNombre());
                    TextInputEditText etTemp = ll.findViewById(R.id.getnEditTextForm);
                    if(encabezadosArrayList.get(x).isEditable()) {
                        tv.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.verdedif)));
                        tv.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.verdedif)));
                        etTemp.setClickable(true);
                        etTemp.setFocusable(true);
                        etTemp.setFocusableInTouchMode(true);
                    }
                    et.add(etTemp);
                    father.addView(ll);
                }
            }
            excelReady.set(true);

            if(s<0) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            }
            crearDialogo();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progreso.setText(String.format(Locale.getDefault(), "%d%c", values[0], '%'));
            super.onProgressUpdate(values);
        }
    }



    private static class SyncDataSaveInv extends AsyncTask<String, String, Integer> {
        private final WeakReference<Context> contextWeakReference;
        private final WeakReference<RelativeLayout> progressBarWeakReference;
        private final Activos activoGlob;
        private final ArrayList<Encabezados> encabezadosArrayList;
        private boolean error;
        private String errMsg;

        private SyncDataSaveInv(Context context, RelativeLayout progressBar, Activos activoGlob, ArrayList<Encabezados> encabezadosArrayList) {
            this.contextWeakReference = new WeakReference<>(context);
            this.progressBarWeakReference = new WeakReference<>(progressBar);
            this.activoGlob = activoGlob;
            this.encabezadosArrayList = encabezadosArrayList;
            error=false;
        }

        @Override
        protected void onPreExecute() {
            progressBarWeakReference.get().setVisibility(RelativeLayout.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... strings) {
            actualizarArchivo(strings[0]);
            return 1;
        }

        private void actualizarArchivo(String archOr) {
            Cell cell;
            Row row;
            boolean cambios = false;
            try {
                InputStream inputStream = new FileInputStream(archOr);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                XSSFSheet sheet = workbook.getSheetAt(0);
                FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                int cellsCount = sheet.getRow(0).getPhysicalNumberOfCells();
                row = sheet.getRow(activoGlob.getRow());
                if(row!=null) {
                    for(int c=0; c<cellsCount; c++) {
                        String value;
                        if(encabezadosArrayList.size()<c && encabezadosArrayList.get(c).getNombre().toLowerCase().contains("hora")) {
                            value = getCellAsString(row, c, formulaEvaluator, true);
                        } else {
                            value = getCellAsString(row, c, formulaEvaluator, false);
                        }
                        if(value.compareTo(activoGlob.getData(c))!=0) {
                            cell = row.getCell(c);
                            if(cell==null) {
                                cell = row.createCell(c);
                            }
                            cell.setCellValue(activoGlob.getData(c));
                            cambios=true;
                        }
                    }
                    if(cambios) {
                        try {
                            OutputStream outputStream = new FileOutputStream(new File(archOr));
                            workbook.write(outputStream);
                            outputStream.flush();
                            outputStream.close();
                            MediaScannerConnection.scanFile(contextWeakReference.get(),
                                    new String[]{archOr}, null,
                                    (path, uri) -> {
                                    });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        error=true;
                        errMsg = contextWeakReference.get().getResources().getString(R.string.warNoChanges);
                    }
                } else {
                    error=true;
                    errMsg = contextWeakReference.get().getResources().getString(R.string.errDesc);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            if(error) {
                Toast.makeText(contextWeakReference.get(), errMsg, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(contextWeakReference.get(), contextWeakReference.get().getResources().getString(R.string.msgGuardado), Toast.LENGTH_LONG).show();
                ((ConsultaActivity)contextWeakReference.get()).finish();
            }
        }
    }

    static class ConsultaHandler extends Handler{
        WeakReference<RelativeLayout> progreso;
        WeakReference<Context> context;
        WeakReference<Spinner> spinner;
        WeakReference<ArrayList<Activos>> activos;
        WeakReference<AtomicBoolean> excelReady;
        private boolean variablesRdy;

        ConsultaHandler(RelativeLayout progreso,  Context context, AtomicBoolean excelReady) {
            this.progreso = new WeakReference<>(progreso);
            this.context = new WeakReference<>(context);
            this.excelReady = new WeakReference<>(excelReady);
            variablesRdy=false;
        }

        private void respaldarDialog(Spinner spinner, ArrayList<Activos> activos) {
            this.activos = new WeakReference<>(activos);
            this.spinner = new WeakReference<>(spinner);
            variablesRdy=true;
        }
    }


}
