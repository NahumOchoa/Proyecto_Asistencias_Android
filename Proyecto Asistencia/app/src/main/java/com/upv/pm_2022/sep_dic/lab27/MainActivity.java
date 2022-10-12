/*
Fuente: https://github.com/yangxiaoge/AndroidExcelReadWrite

Punto 0: No entender porque necesita el permito para manipular el sistemad e archivos (INVESTIGEN!)
 */

package com.upv.pm_2022.sep_dic.lab27;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.os.Environment;
import android.widget.Button;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;
    Button B0;
    Document documento;
    private final static String NOMBRE_DOCUMENTO = "asistencia.pdf";
    private final static String NOMBRE_DIRECTORIO = "Pdfs de asistencia";
    ArrayList<Estudiantes> estudiantes = new ArrayList<>();
    ArrayList<Estudiantes> reportes = new ArrayList<>();
    ArrayList<String> fechas = new ArrayList<>();
    public static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;
    private int FILE_SELECTOR_CODE = 10000;
    private int DIR_SELECTOR_CODE = 20000;
    private List<Map<Integer, Object>> readExcelList = new ArrayList<>();
    private RecyclerView recyclerView;
    //private ExcelAdapter excelAdapter;
    String path = "a";
    String Cadenota="";



    XSSFWorkbook workbook;
    XSSFSheet sheet;
    File CARPETA = new File(Environment.getExternalStorageDirectory(), NOMBRE_DIRECTORIO); // Subdirectorio
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissionOnly();

        mContext = this;



        //readExcelFileFromAssets("/sdcard/pluto.xlsx");


    }



    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.import_excel_btn:
                //readExcelFileFromAssets("/sdcard/pluto.xlsx");
                Intent intent = new Intent();
                //set type of file to be imported csv, excel, etc
                intent.setType("text/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video "), 33);

                break;
                //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                //path = getFilePath();
                //Toast.makeText(this, "Path: " + path, Toast.LENGTH_SHORT).show();
                //readCsvFileFromAssets("/sdcard/pluto.csv");
                //readExcelFileFromAssets("/sdcard/pluto.xlsx");


            case R.id.export_excel_btn:
                /*if (readExcelList.size() > 0) {
                    openFolderSelector();
                } else {
                    Toast.makeText(mContext, "please import excel first", Toast.LENGTH_SHORT).show();
                } */

                if(reportes.size() == 0){
                    Toast.makeText(this, "No hay datos que exportar", Toast.LENGTH_SHORT).show();

                }else {
                    CrearDocumento();

                }
                break;
            default:
                break;
        }
    }


    //override startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == FILE_SELECTOR_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri == null) return;
            Log.i(TAG, "onActivityResult: " + "filePath：" + uri.getPath());
            //select file and import
            importExcelDeal(uri);
        } else */
        if(requestCode == 33 && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            if (uri == null) return;
            Log.i(TAG, "onActivityResult: " + "filePath：" + uri.getPath());
            //select file and import
            File myFile = new File(uri.getPath());


            readCsvFileFromAssets(uri);



        }


        if (requestCode == DIR_SELECTOR_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri == null) return;
            Log.i(TAG, "onActivityResult: " + "filePath：" + uri.getPath());
            Toast.makeText(mContext, "Exporting...", Toast.LENGTH_SHORT).show();
            //you can modify readExcelList, then write to excel.
            //ExcelUtil.writeExcelNew(this, readExcelList, uri);
        }
    }




    private void askPermissionOnly() {
        this.askPermission(REQUEST_ID_WRITE_PERMISSION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        this.askPermission(REQUEST_ID_READ_PERMISSION,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);

    }


    // With Android Level >= 23, you have to ask the user
    // for permission with device (For example read/write data on the device).
    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);


            if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }


    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_READ_PERMISSION: {
                    if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Permission Lectura Concedido!", Toast.LENGTH_SHORT).show();
                    }
                }
                case REQUEST_ID_WRITE_PERMISSION: {
                    if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Permission Escritura Concedido!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permission Cancelled!", Toast.LENGTH_SHORT).show();
        }
    }

    //add checkbox a listview with object
    private void addCheckBox(String NombreAlumno, int ID) {
        ListView listView = (ListView) findViewById(R.id.TV2);
        CheckBox checkBox = new CheckBox(this);
        checkBox.setId(ID);
        checkBox.setText(NombreAlumno);
        checkBox.setChecked(true);
        checkBox.setSelected(false);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    Toast.makeText(MainActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "UnChecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{""});
        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.addHeaderView(checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: " + isChecked);

                // TODO Auto-generated method stub
                Estudiantes estudiante1 = searchArray(checkBox.getText().toString());
                //Select all checkbox in listview


                if (isChecked == true) {
                    if(checkBox.getText().toString() == "SELECCIONAR TODO"){
                        reportes.clear();
                        for ( int i=0; i <= estudiantes.size(); i++) {
                            listView.setItemChecked(i, true);
                            reportes.add(estudiantes.get(i));
                        }

                    }else{
                        listView.setItemChecked(checkBox.getId(), true);
                        //Toast.makeText(MainActivity.this, estudiante1.getNombreCompleto(), Toast.LENGTH_SHORT).show();
                        reportes.add(estudiante1);
                    }


                } else {
                    if (checkBox.getText().toString() == "SELECCIONAR TODO") {
                        for (int i = 0; i <= estudiantes.size(); i++) {
                            listView.setItemChecked(i, false);
                        }
                        reportes.clear();
                    }else{
                        listView.setItemChecked(checkBox.getId(), false);
                        reportes.remove(estudiante1);
                    }
                }
            }

        });






    }
    private void CrearDocumento () {

        documento = new Document();
        int clases = fechas.size()+1;
        // Creamos el fichero con el nombre que deseemos.
        File f;
        //f = crearFichero(NOMBRE_DOCUMENTO);
        //File CARPETA = Environment.getExternalStorageDirectory();
        if (!CARPETA.exists()) //
        { // NO EXISTE DIRECTORIO, DEBE CREARSE!
            CARPETA.mkdir();
        }
        else { // YA EXISTE, NO HACER NADA
        }
        // CREAR ARCHIVO
        f = new File(CARPETA, NOMBRE_DOCUMENTO);
        // Creamos el flujo de datos de salida para el fichero donde guardaremos el pdf.
        FileOutputStream ficheroPdf = null;
        try {
            ficheroPdf = new FileOutputStream(f.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Asociamos el flujo que acabamos de crear al documento.
        try {
            PdfWriter.getInstance(documento, ficheroPdf);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        documento.setPageSize(PageSize.A4.rotate());

        // Abrimos el documento.
        documento.open();

        // Creamos las fuentes que se utilizarán
        Font fuenteContenidoSI = FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL);

        // Creamos una tabla que contendrá el nombre, apellido y país
        for (int i = 0; i <reportes.size(); i++) {

            // Añadimos un título con fuente personalizada.
            try {
                documento.add(new Paragraph(""));
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            // Definimos tabla con columnas
            PdfPTable tabla = new PdfPTable(2);
            float[] columnWidths = {1, 1};
            PdfPTable tablaCon = new PdfPTable(columnWidths);
            PdfPTable tablaPie = new PdfPTable(2);

            // Definimos los nombres de las celdas y su contenido
            PdfPCell cellTitulo = new PdfPCell(new Phrase(
                    "   Nombre del estudiante: "+reportes.get(i).getNombreCompleto() +
                            "\n Clase: Programación móvil mayo-agosto", fuenteContenidoSI));
            PdfPCell cellSubTitulo = new PdfPCell(new Phrase("  Fecha   " +
                    "     Asistió", fuenteContenidoSI));
            PdfPCell cellSubTitulo2 = new PdfPCell(new Phrase("  Fecha  " +
                    "     Asistió", fuenteContenidoSI));
            PdfPCell cellPie = new PdfPCell(new Phrase("Total de clases: "+clases +
                    "\n     Clases sin asistencia:  "+reportes.get(i).getFaltas()+"/"+clases +
                    "\n     Clases con retardos: "+reportes.get(i).getRetardos()+"/"+clases +
                    "\n     Clases con asistencia "+reportes.get(i).getAsistencia()+"/"+clases, fuenteContenidoSI));
            PdfPCell cellContenido = new PdfPCell();

            // Diseño de las celdas y demás parámetros
            cellTitulo.setColspan(2);
            cellTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTitulo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellTitulo.setBorder(Rectangle.TOP | Rectangle.BOTTOM);

            cellPie.setColspan(2);
            cellPie.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellPie.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellPie.setBorder(Rectangle.TOP | Rectangle.BOTTOM);

            cellSubTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellSubTitulo.setBorder(Rectangle.TOP | Rectangle.RIGHT);
            cellSubTitulo2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellSubTitulo2.setBorder(Rectangle.TOP | Rectangle.LEFT);

            tablaCon.setWidthPercentage(80);
            tablaCon.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaCon.getDefaultCell().setUseAscender(true);
            tablaCon.getDefaultCell().setUseDescender(true);

            // Añadimos las celdas a la tabla
            tabla.addCell(cellTitulo);
            tabla.addCell(cellSubTitulo);
            tabla.addCell(cellSubTitulo2);
            int j = fechas.size()/2+1;
            ArrayList<String> registros = new ArrayList<>();

            Toast.makeText(MainActivity.this, reportes.get(i).getFechasAsistencias().size()+" "+fechas.size(), Toast.LENGTH_SHORT).show();

            for (int i2 = 0; i2 < fechas.size()/2+1; i2++) {

                if(i2+j>=fechas.size()){
                    registros.add(" ");
                    continue;
                }
                if(reportes.get(i).getFechasAsistencias().get(i2).equals("A")){
                    tablaCon.addCell("                        "+fechas.get(i2)+"     Asistió");

                }else{
                    tablaCon.addCell("                            "+fechas.get(i2)+"     No asistió");

                }

                if(reportes.get(i).getFechasAsistencias().get(j+i2).equals("A")){
                    tablaCon.addCell("                             "+fechas.get(j+i2)+"     Asistió");
                }else{
                    tablaCon.addCell("                             "+fechas.get(j+i2)+"     No asistió");
                }

            }
            tablaPie.addCell(cellPie);

            //Añadimos las tablas al documento
            try {
                documento.add(tabla);
                documento.add(tablaCon);
                documento.add(tablaPie);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            documento.newPage();
        }
        // Cerramos el documento.
        documento.close();

        // Mensaje de actualización
        //Toast.makeText(this, "Documento " + NOMBRE_DOCUMENTO + " creado correctamente", Toast.LENGTH_SHORT).show();
    }

    //search arraylist for id
    public Estudiantes searchArray(String nombre){
        for(int i = 0; i < estudiantes.size(); i++){
            if(estudiantes.get(i).getNombreCompleto() ==  nombre){
                return estudiantes.get(i);
            }
        }
        return null;
    }

    public int readCsvFileFromAssets(Uri uri){
        InputStream myInput = null;
        try {
            ArrayList<String>A = new ArrayList<>();
            String[]B = new String[1];
            myInput = getContentResolver().openInputStream(uri);

            //myInput = getAssets().open("test.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(myInput));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                String[] RowData = line.split(",");
                if(i == 0) {
                    //header
                    for (int j = 5; j < RowData.length; j++) {
                        fechas.add(RowData[j]);

                    }
                    addCheckBox("SELECCIONAR TODO",-1);
                }else{

                    Estudiantes estudiante = new Estudiantes(i, RowData[1], Integer.parseInt(RowData[2]), Integer.parseInt(RowData[3]), Integer.parseInt(RowData[4]));
                    int j2=0;
                    for (int j = 5; j < RowData.length; j++) {
                        estudiante.setfechasAsistencias(RowData[j]);
                        //estudiante.setFechaAsistencia(RowData[j]);
                        //Toast.makeText(MainActivity.this, RowData[j], Toast.LENGTH_SHORT).show();

                    }
                    //estudiante.setFechaAsistencia(B);

                    //Toast.makeText(getApplicationContext(), "estudiante: " + estudiante.toString(), Toast.LENGTH_SHORT).show();
                    estudiantes.add(estudiante);
                    addCheckBox(RowData[1],i);
                }

                i++;
            }

            return i;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }



    }














}
