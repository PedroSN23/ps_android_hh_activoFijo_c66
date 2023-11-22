package com.example.ps_android_hh_activofijo_c66.model.clases;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncDataSaveInven extends AsyncTask<String, Integer, Integer> {
    private final ArrayList<String[]> inventario;
    private ArrayList<Cambios> cambios;
    private Configuracion configuracion;
    private ArrayList<Encabezados> encabezados;
    private final WeakReference<InterfazBD> interfazBDWeakReference;
    private final WeakReference<Context> contextWeakReference;
    private final WeakReference<AtomicBoolean> busyAB;
    private boolean error;
    private String errMsg;

    public SyncDataSaveInven(Context context, AtomicBoolean busy, InterfazBD interfazBD) {
        error=false;
        interfazBDWeakReference = new WeakReference<>(interfazBD);
        contextWeakReference = new WeakReference<>(context);
        busyAB = new WeakReference<>(busy);
        inventario = interfazBDWeakReference.get().obtenerInventario();
        if(inventario.size()==0) {
            error=true;
            errMsg = context.getResources().getString(R.string.errInvVacio);
            return;
        }
        configuracion = interfazBDWeakReference.get().obtenerConfiguracion();
        if(configuracion==null) {
            error=true;
            errMsg = context.getResources().getString(R.string.errConfEmpty);
            return;
        }
        encabezados = interfazBDWeakReference.get().obtenerEncabezados();
        if(encabezados.size()==0) {
            error=true;
            errMsg = context.getResources().getString(R.string.errNoEnc);
            return;
        }
        cambios = interfazBDWeakReference.get().obtenerCambios();
    }

    public boolean getError() {
        return error;
    }

    public String getErrMsg() {
        return errMsg;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        actualizarArchivo();
        return 1;
    }

    private void actualizarArchivo() {
        int index=-1;
        Cell cell;
        Row row;
        int prcnt=0;
        CellStyle cellStyle=null;
        boolean styleCreated=false;

        for(int i=0; i<encabezados.size(); i++) {
            if(encabezados.get(i).isLlavePrimaria()) {
                index=i;
                break;
            }
        }

        if(index!=-1) {
            try {
                InputStream inputStream = new FileInputStream(configuracion.getArchivoInPath()); //.xlsx
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                XSSFSheet sheet = workbook.getSheetAt(0);
                int rowsCount = sheet.getPhysicalNumberOfRows();
                FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                int cellsCount = sheet.getRow(0).getPhysicalNumberOfCells();
                row = sheet.getRow(0);
                int tmp = cellsCount;
                cell = row.getCell(0);
                if (cell != null) {
                    cellStyle = cell.getCellStyle();
                    styleCreated = true;
                }
                if (configuracion.isResult()) {
                    cell = row.getCell(tmp);
                    if (cell == null) {
                        cell = row.createCell(tmp);
                        if (styleCreated) {
                            cell.setCellStyle(cellStyle);
                        }
                    }
                    cell.setCellValue("INVENTARIO");
                    tmp++;
                }
                if (configuracion.isFecha()) {
                    cell = row.getCell(tmp);
                    if (cell == null) {
                        cell = row.createCell(tmp);
                        if (styleCreated) {
                            cell.setCellStyle(cellStyle);
                        }
                    }
                    cell.setCellValue("FECHA");
                }
                for (int r = 1; r < rowsCount; r++) { //iterar filas a partir de la segunda
                    row = sheet.getRow(r);
                    if(row != null) {
                        cell = row.getCell(index);
                        if (cell != null) {
                            cellStyle = cell.getCellStyle();
                            String value = getCellAsString(row, index, formulaEvaluator); //obtener valor del tag
                            boolean found = false;
                            for (String[] inv : inventario) {
                                if (value.compareTo(inv[0]) == 0) {
                                    found = true;
                                    tmp = cellsCount;
                                    if (configuracion.isResult()) {
                                        cell = row.getCell(tmp);
                                        if (cell == null) {
                                            cell = row.createCell(tmp);
                                            cell.setCellStyle(cellStyle);
                                        }
                                        cell.setCellValue("OK");
                                        tmp++;
                                    }
                                    if (configuracion.isFecha()) {
                                        cell = row.getCell(tmp);
                                        if (cell == null) {
                                            cell = row.createCell(tmp);
                                            cell.setCellStyle(cellStyle);
                                        }
                                        cell.setCellValue(inv[1]);
                                    }
                                    break;
                                }
                            }
                            if (!found) {
                                if (configuracion.isResult()) {
                                    cell = row.getCell(cellsCount);
                                    if (cell == null) {
                                        cell = row.createCell(cellsCount);
                                        cell.setCellStyle(cellStyle);
                                    }
                                    cell.setCellValue("FALTANTE");
                                }
                            }
                            for (Cambios cambio : cambios) {
                                if (value.compareTo(cambio.getTag()) == 0) {
                                    cell = row.getCell(cambio.getIndex());
                                    if (cell == null) {
                                        cell = row.createCell(cambio.getIndex());
                                        cell.setCellStyle(cellStyle);
                                    }
                                    cell.setCellValue(cambio.getValor());
                                }
                            }
                        } else {
                            if (configuracion.isResult()) {
                                cell = row.getCell(cellsCount);
                                if (cell == null) {
                                    cell = row.createCell(cellsCount);
                                }
                                cell.setCellValue("ID VACÍO");
                            }
                        }
                    }
                    int tmpPrcnt = r*100/rowsCount;
                    if(tmpPrcnt!=prcnt) {
                        prcnt=tmpPrcnt;
                        publishProgress(prcnt);
                    }
                }
                try {
                    String outfile = configuracion.getArchivoInPath().substring(0, configuracion.getArchivoInPath().length()-5)+configuracion.getPrefijoOut()+".xlsx";
                    OutputStream outputStream = new FileOutputStream(outfile);
                    workbook.write(outputStream);
                    outputStream.flush();
                    outputStream.close();
                    MediaScannerConnection.scanFile(contextWeakReference.get(),
                            new String[]{outfile}, null,
                            (path, uri) -> {
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            error = true;
            errMsg = contextWeakReference.get().getResources().getString(R.string.errNoPk);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = "" + cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                        value = formatter.format(HSSFDateUtil.getJavaDate(numericValue));
                    } else {
                        if (numericValue % 1 == 0) {
                            value = "" + (int) numericValue;
                        } else {
                            value = "" + numericValue;
                        }
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = "" + cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        busyAB.get().set(false);
        if(error) {
            Toast.makeText(contextWeakReference.get(), errMsg, Toast.LENGTH_LONG).show();
        } else {
            interfazBDWeakReference.get().eliminarInventario();
            interfazBDWeakReference.get().eliminarCambios();
            Toast.makeText(contextWeakReference.get(), contextWeakReference.get().getResources().getString(R.string.msgExportado), Toast.LENGTH_LONG).show();
        }
    }
}