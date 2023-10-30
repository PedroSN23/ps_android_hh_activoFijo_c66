package com.example.ps_android_hh_activofijo_c66.controller.files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ps_android_hh_activofijo_c66.model.clases.Archivos;
import com.example.ps_android_hh_activofijo_c66.model.clases.DevicesLocal;
import com.example.ps_android_hh_activofijo_c66.model.clases.UHFTagsGroup;
import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.TagsTipo;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class FileHandler extends Handler {
    private WeakReference<Handler> mainHandler;
    private WeakReference<Context> contextWeakReference;
/*
    Pattern p = Pattern.compile("^(\\d{5})-?(\\d{0,5})$");
*/
    public void setMainHandler(Handler mainHandler, Context context) {
        this.mainHandler = new WeakReference<>(mainHandler);
        this.contextWeakReference = new WeakReference<>(context);
    }
    @Override
    public void handleMessage(@NonNull Message msg) {
        Bundle bundle;
        Archivos archivos;
        switch (FileInst.valueOf((String) msg.obj)) {
            case ready:
                //interfazDb = new InterfazDb(contextWeakReference.get());
                sendReady();
                break;
            case excel:
                bundle = msg.getData();
                try {
                    String[] headers = bundle.getStringArray("headers");
                    String[][] valores = (String[][]) bundle.getSerializable("values");
                    String[] filePath = actualizarArchivo(bundle.getString("title"),
                            headers,
                            valores,
                            bundle.getString("folder"));
                    if(filePath!=null) {
                        sendSuccess(FileResp.excel_ok, filePath, true);
                    } else {
                        sendError(FileResp.excel_error, "Error desconocido");
                    }
                } catch (IOException e) {
                    sendError(FileResp.excel_error, e.getMessage());
                }
                break;
            case borrarArchivo:
                bundle = msg.getData();
                archivos = bundle.getParcelable("file");
                fileDelete(archivos.getPath());
                enviarArchivoBorrado();
                break;
            case xlsx_content:
                Log.d("DATA", "xlsx_conent");
                bundle = msg.getData();
                archivos = bundle.getParcelable("file");
                ArrayList<UHFTagsGroup> uhfTagsGroups = readXlsxData(archivos.getPath(), bundle.getStringArray("headers"));
                if(uhfTagsGroups.size()==0) {
                    sendError(FileResp.excel_error, "Tabla vacia");
                } else {
                    enviarValidacion(uhfTagsGroups, archivos);
                    Log.d("DATA", "xlsx_conent "+uhfTagsGroups.size());
                }
                break;
            case obtener_files:
                bundle = msg.getData();
                ArrayList<Archivos> foundFiles3 = checkInternalStorage(System.getenv("EXTERNAL_STORAGE")+"/Download/"+bundle.getString("folder"), bundle.getString("terminacion"));
                sendFiles(foundFiles3);
                break;
            case xlsx_avance:
/*
                bundle = msg.getData();
                guardarAvanceXlsx(bundle.getParcelableArrayList("list"), bundle.getParcelable("file"));
                sendSuccessWithBundle(FileResp.avanceGuardado, bundle);
*/
                break;
            case end:
                //interfazDb.close();
                getLooper().quitSafely();
                break;
        }
        super.handleMessage(msg);
    }

    private ArrayList<Archivos> checkInternalStorage(String path, String terminacion) {
        ArrayList<Archivos> foundFiles = new ArrayList<>();
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
                        Archivos ar = checkFile(file1.getAbsolutePath(), terminacion);
                        if(ar!=null) {
                            foundFiles.add(ar);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return foundFiles;
    }

    private Archivos checkFile(String path, String terminacion) {
        File[] listFile;
        File file = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                file = new File(path);
            }
            if(file!=null) {
                listFile = file.listFiles();
                if (listFile == null) {
                    if (path.toLowerCase().endsWith(terminacion)) {
                        return new Archivos(path, file.getName());
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fileDelete(String path1) {
        File file = new File(path1);
        if(file.exists()) {
            if(file.delete()) {
                MediaScannerConnection.scanFile(contextWeakReference.get(),
                        new String[]{path1}, null,
                        (path, uri) -> {
                        });
            }
        }
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String[] actualizarArchivo(String sheetName, String[] headers, String[][] values, String folder) throws IOException {
        String[] fileData = new String[2];
        Cell cell;
        Row row;
        Pattern p = Pattern.compile("^(.+)_\\d{2}_\\d{2}_\\d{4}_\\d{2}_\\d{2}_\\d{2}\\.xlsx$");

        String directory = System.getenv("EXTERNAL_STORAGE")+"/Download/"+folder+"/";

        File file = new File(directory+sheetName);
        if(file.exists()) {
            file.delete();
            fileData[0] = sheetName;
            Matcher m = p.matcher(sheetName);
            if(m.matches()) {
                sheetName = m.group(1);
            }
        } else {
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
            fileData[0] = sheetName+"_"+dateFormat.format(calendar.getTime())+".xlsx";
            File file1 = new File(directory);
            if(!file1.exists()) {
                file1.mkdir();
            }
        }
        fileData[1] = directory+fileData[0];

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        if(sheet!=null) {
            int r = 0;
            row = sheet.createRow(r);

            if(row!=null) {
                int c = 0;
                for (String h : headers) {
                    cell = row.createCell(c);
                    if (cell != null) {
                        cell.setCellValue(h);
                    }
                    c++;
                }
                r++;
                for(String[] val: values) {
                    row = sheet.createRow(r);
                    if(row!=null) {
                        c = 0;
                        for (String v : val) {
                            cell = row.createCell(c);
                            if(cell!= null) {
                                cell.setCellValue(v);
                            }
                            c++;
                        }
                    }
                    r++;
                }
            }

            OutputStream outputStream = Files.newOutputStream(Paths.get(fileData[1]));
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            MediaScannerConnection.scanFile(contextWeakReference.get(),
                    new String[]{fileData[1]}, null,
                    (path, uri) -> {
                    });

            return fileData;
        }
        return null;
    }

    /*private void guardarAvanceXlsx(ArrayList<TelaCatalogo> list, Archivos file) {
        try {
            String[] header = {"ID MATERIAL", "COLOR", "DESCRIPCION", "CANTIDAD", "No DE ROLLO", "DCOLOR", "RESULTADO"};
            InputStream inputStream = Files.newInputStream(Paths.get(file.getPath()));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");

            XSSFSheet sheet = workbook.createSheet("Inventario_"+dateFormat.format(calendar.getTime()));

            int r=0;
            Row row = sheet.createRow(r);
            Cell cell;
            int c = 0;
            if(row!=null) {
                for (String h : header) {
                    cell = row.createCell(c);
                    if (cell != null) {
                        cell.setCellValue(h);
                    }
                    c++;
                }
                r++;
                for (TelaCatalogo tc : list) {
                    for (Telas t : tc.getTelasArray()) {
                        row = sheet.createRow(r++);
                        c=0;
                        if(row!=null) {
                            cell = row.createCell(c++);
                            if(cell!=null) {
                                cell.setCellValue(tc.getIdMat());
                            }
                            cell = row.createCell(c++);
                            if(cell!=null) {
                                cell.setCellValue(tc.getColor());
                            }
                            cell = row.createCell(c++);
                            if(cell!=null) {
                                cell.setCellValue(tc.getComposicion());
                            }
                            cell = row.createCell(c++);
                            if(cell!=null) {
                                cell.setCellValue(t.getCantidad());
                            }
                            cell = row.createCell(c++);
                            if(cell!=null) {
                                cell.setCellValue(t.getRollo()+"");
                            }
                            cell = row.createCell(c++);
                            if(cell!=null) {
                                cell.setCellValue(tc.getdColor()+"");
                            }
                            cell = row.createCell(c);
                            if(cell!=null) {
                                switch (t.getRecibido()) {
                                    case 0:
                                        cell.setCellValue("FALTANTE");
                                        break;
                                    case 1:
                                        cell.setCellValue("OK");
                                        break;
                                    case -1:
                                        cell.setCellValue("SOBRANTE");
                                        break;
                                }
                            }
                        }
                    }
                }
            }

            OutputStream outputStream = Files.newOutputStream(Paths.get(file.getPath()));
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            MediaScannerConnection.scanFile(contextWeakReference.get(),
                    new String[]{file.getPath()}, null,
                    (path, uri) -> {
                    });
        } catch (IOException | NullPointerException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
*/
    private ArrayList<UHFTagsGroup> readXlsxData(String path, String[] headers) {
        ArrayList<UHFTagsGroup> uhfTagsGroups = new ArrayList<>();
        boolean verificarEncabezados=true;
        try {
            InputStream inputStream = Files.newInputStream(Paths.get(path));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            XSSFSheet sheet = workbook.getSheetAt(workbook.getNumberOfSheets()-1);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            for (int r = sheet.getFirstRowNum(), rc = 0; rc < rowsCount; rc++, r++) { //iterador de lineas
                Row row = sheet.getRow(r);
                int cellsCount = sheet.getRow(r).getPhysicalNumberOfCells();
                String[] dataComp;
                dataComp=new String[headers.length];
                for (short c = row.getFirstCellNum(), cs = 0; cs < cellsCount; cs++, c++) {
                    String data = getCellAsString(row, c, formulaEvaluator);
                    Log.d("DATA", "xlsx_conent "+data);
                    if (verificarEncabezados) {
                        if (rc == 0) {
                            if (data.compareTo(headers[cs]) != 0) {
                                break;
                            } else {
                                if (headers.length - 1 == cs) {
                                    verificarEncabezados = false;
                                }
                            }
                        }
                    } else {
                        if (rc > 0) {
                            if(cs < headers.length) {
                                dataComp[cs] = data;
                                if (headers.length - 1 == cs) {
                                    UHFTagsRead uhfTagsRead = new UHFTagsRead(dataComp[0], "", "", "", dataComp[3]);
                                    uhfTagsRead.compileTipo();
                                    if(uhfTagsRead.getTipo()==TagsTipo.repuve) {
                                        uhfTagsRead.setDetail(dataComp[1]);
                                    }
                                    uhfTagsRead.setInventariado(Integer.parseInt(dataComp[5]));
                                    boolean found = false;
                                    if(uhfTagsRead.getTipo()==TagsTipo.sgtin96) {
                                        for(UHFTagsGroup tg: uhfTagsGroups) {
                                            if(tg.getTagsTipo()==TagsTipo.sgtin96 && tg.getDetail().compareTo(dataComp[1])==0) {
                                                found=true;
                                                tg.addTagFile(uhfTagsRead);
                                                break;
                                            }
                                        }
                                    }
                                    if(!found) {
                                        uhfTagsGroups.add(new UHFTagsGroup(uhfTagsRead));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | NullPointerException | NumberFormatException e) {
            e.printStackTrace();
        }
        return uhfTagsGroups;
    }

    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            if(cellValue!=null) {
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
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return value;
    }

    private void sendReady() {
        if(mainHandler.get()!=null) {
            Message message = mainHandler.get().obtainMessage();
            message.obj = FileResp.ready.name();
            message.arg1 = DevicesLocal.file.getCode();
            mainHandler.get().sendMessage(message);
        }
    }
    private void sendFiles(ArrayList<Archivos> foundFiles) {
        if(mainHandler.get()!=null) {
            Message message = mainHandler.get().obtainMessage();
            message.obj = FileResp.files.name();
            message.arg1 = DevicesLocal.file.getCode();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("foundfiles", foundFiles);
            message.setData(bundle);
            mainHandler.get().sendMessage(message);
        }
    }

    private void sendSuccess(FileResp fr, String[] filepath, boolean exportar) {
        if(mainHandler.get()!=null) {
            Message message = mainHandler.get().obtainMessage();
            message.obj = fr.name();
            message.arg1 = DevicesLocal.file.getCode();
            Bundle bundle = new Bundle();
            bundle.putStringArray("filepath", filepath);
            bundle.putBoolean("exportar", exportar);
            message.setData(bundle);
            mainHandler.get().sendMessage(message);
        }
    }

    private void sendError(FileResp fr, String msg) {
        if(mainHandler.get()!=null) {
            Message message = mainHandler.get().obtainMessage();
            message.obj = fr.name();
            message.arg1 = DevicesLocal.file.getCode();
            Bundle bundle = new Bundle();
            bundle.putString("msg", msg);
            message.setData(bundle);
            mainHandler.get().sendMessage(message);
        }
    }

    private void enviarArchivoBorrado() {
        if(mainHandler.get()!=null) {
            Message message = mainHandler.get().obtainMessage();
            message.obj = FileResp.archborrado.name();
            message.arg1 = DevicesLocal.file.getCode();
            mainHandler.get().sendMessage(message);
        }
    }

    private void enviarValidacion(ArrayList<UHFTagsGroup> uhfTagsGroups, Archivos archivos) {
        if(mainHandler.get()!=null) {
            Message message = mainHandler.get().obtainMessage();
            message.obj = FileResp.excel_content.name();
            message.arg1 = DevicesLocal.file.getCode();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("list", uhfTagsGroups);
            bundle.putParcelable("file", archivos);
            message.setData(bundle);
            mainHandler.get().sendMessage(message);
        }
    }
}