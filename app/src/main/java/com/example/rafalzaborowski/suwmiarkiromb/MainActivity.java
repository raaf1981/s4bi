package com.example.rafalzaborowski.suwmiarkiromb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View.OnKeyListener;
import android.view.View;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import es.voghdev.pdfviewpager.library.asset.CopyAsset;
import es.voghdev.pdfviewpager.library.asset.CopyAssetThreadImpl;
import uk.co.senab.photoview.PhotoViewAttacher;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";
    public static String[][] strOfInd;
    public static int choosenInd;
    InputStreamReader indeksy;
    String readJsonString;
    JSONArray jArr;
    TableLayout tabLay1;
    Date currentDate;
    Date dataprocesu;
    IntentFilter filter = new IntentFilter();
    String targetPdf = "storage/emulated/0/downloadedPdf/index.pdf";
    ImageView pdfView;
    PhotoViewAttacher mAttacher;
    Context context;
    CountDownTimer cdt;
    private String[] newRowMeasure;
    private int licznikPom = 1;
    private int liczProb = 1;
    private int licznikPom2 = 1;
    private boolean pomiarOk, timerStart;
    private String[] headerRow = {"ID", "Godzina", "Indeks", "Wymiar", "T+", "T-", "Pomiar", "Punkt"};
    private String pomiarAktualny;
    private String newIndexPost;
    private String[] lista = new String[]{"Piła 1", "Piła 2", "Piła 3", "Piła 4", "Piła 5", "Piła 6", "Piła 7", "Obróbka 1", "Obróbka 2",};
    private ProgressDialog progressDialog;

    @Override
    protected void onPause() {
        super.onPause();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(this.getIntent().getAction())) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        cdt = new CountDown(900000, 1000, this);
        filter.addAction("akcja1");
        filter.addAction("akcja2");
        filter.addAction("akcja3");
        filter.addAction("akcja4");
        filter.addAction("akcja5");
        filter.addAction("akcja6");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        final TextView tvpozostal = (TextView) findViewById(R.id.tvpoz);
        final TextView textView2 = (TextView) findViewById(R.id.pomiartv);
        final EditText editText = (EditText) findViewById(R.id.editText1);
        editText.setBackgroundResource(android.R.color.transparent);
        editText.setOnKeyListener(new OnKeyListener() {
            Button btnStop1 = (Button) findViewById(R.id.stopprocbtn);
            Button btnStart1 = (Button) findViewById(R.id.startprocbtn);

            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                currentDate = new Date();
                if ((keyevent.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!tvpozostal.getText().toString().equals("")) {
                        if (Integer.parseInt(tvpozostal.getText().toString()) > 0 && !btnStart1.isClickable()) {
                            pomiarAktualny = editText.getText().toString();
                            textView2.setText(editText.getText());
                            editText.setText("");
                            checkMeasure();
                            if (pomiarOk) {
                                textView2.setTextColor(Color.parseColor("#55bb55"));
                            } else {
                                textView2.setTextColor(Color.RED);
                            }
                            newIndexPost = prepNewIndPost(textView2.getText().toString());

                            zapiszWynik();
                            editText.requestFocus();
                        } else if (Integer.parseInt(tvpozostal.getText().toString()) > 0 && btnStart1.isClickable()) {
                            showDialogCustYN("Rozpocząć proces pomiarów?", 3);
                        } else {
                            showDialogCust("Uwaga", "Wykonałeś wszystkie pomiary");
                        }
                    } else {
                        showDialogCust("Błąd", "Nie wybrano indeksu");
                    }


                    return true;
                }
                editText.requestFocus();
                return false;
            }

        });
        editText.requestFocus();

        pobierzIndeksy();

        TableLayout table = (TableLayout) findViewById(R.id.tabLayH);
        TableRow rowHeader = new TableRow(this);
        for (int i = 0; i < headerRow.length; i++) {
            TextView tv = new TextView(this);
            tv.setText(headerRow[i]);
            tv.setPadding(17, 5, 19, 5);
            rowHeader.addView(tv);
        }
        table.addView(rowHeader);
        TextView datetv = (TextView) findViewById(R.id.datatv);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd / MM / yyyy ");
        datetv.setText("Data:   " + mdformat.format(calendar.getTime()));

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, lista
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setPrompt("Miejsce pomiaru");
        //spinner.setSelection(0);

    }

    public void testMet(View v) {
        TextView tvlogged = (TextView) findViewById(R.id.textView6);
        if (strOfInd == null) {
            showDialogCust("Błąd", "Nie można pobrać bazy indeksów.\nWystąpił problem z komunikacją.\nSkontaktuj się z administratorem.");
            pobierzIndeksy();
        } else {
            if(tvlogged.getText().toString().equals("----")){
                showDialogCust("Błąd", "Zaloguj się!");
            }else{
                DialogFragment newFragment = new ChoosenIndex();
                newFragment.show(getSupportFragmentManager(), "choosen");
            }

        }
        //Toast.makeText(MainActivity.this, "buttonPressed", Toast.LENGTH_SHORT).show();

    }

    private String readFromStream(InputStreamReader inputStreamReader) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStreamReader != null) {
            BufferedReader reader = new BufferedReader(inputStreamReader, 100000);
            String line = reader.readLine();
            //System.out.println("output:  " +  line);
            while (line != null) {
                output.append(line);
                line = reader.readLine();

            }
        }
        return output.toString();
    }

    public void addTabRow(String[] tabTV) {
        tabLay1 = (TableLayout) findViewById(R.id.tabLay);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < 8; i++) {
            TextView newTV = new TextView(this);
            newTV.setText(tabTV[i]);
            if (pomiarOk) {
                newTV.setTextColor(Color.parseColor("#55bb55"));
            } else {
                newTV.setTextColor(Color.RED);
            }
            if (i == 1) {
                newTV.setPadding(20, 2, 17, 2);
            } else if (i == 2) {
                newTV.setPadding(16, 2, 17, 2);
            } else if (i == 4) {
                newTV.setPadding(28, 2, 17, 2);
            } else if (i == 7) {
                newTV.setPadding(35, 2, 17, 2);
            } else {
                newTV.setPadding(20, 2, 17, 2);

            }
            //newTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            newTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(newTV);


        }
        tabLay1.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        //ScrollView sv = (ScrollView) findViewById(R.id.sv1);
        //sv.fullScroll(ScrollView.FOCUS_DOWN);

    }

    private String[] prepareRow() {
        //TextView textViewTemp = (TextView) findViewById(R.id.test);
        TextView tvPom = (TextView) findViewById(R.id.pomiartv);
        //choosenInd = Integer.parseInt(textViewTemp.getText().toString());
        String[] newRowToAdd = new String[headerRow.length];
        newRowToAdd[0] = String.valueOf(licznikPom);
        licznikPom += 1;

        newRowToAdd[1] = new SimpleDateFormat("HH:mm:ss").format(currentDate);
        newRowToAdd[2] = strOfInd[choosenInd][2];
        newRowToAdd[3] = strOfInd[choosenInd][3];
        if (strOfInd[choosenInd][4] == "null") {
            newRowToAdd[4] = "0.0";
        } else {
            newRowToAdd[4] = strOfInd[choosenInd][4];
        }
        if (strOfInd[choosenInd][5] == "null") {
            newRowToAdd[5] = "0.0";
        } else {
            newRowToAdd[5] = strOfInd[choosenInd][5];
        }
        newRowToAdd[6] = tvPom.getText().toString();
        if (liczProb <= Integer.parseInt(strOfInd[choosenInd][6])) {
            newRowToAdd[7] = String.valueOf(liczProb);
            liczProb += 1;
        } else {
            liczProb = 1;
            newRowToAdd[7] = String.valueOf(liczProb);
            liczProb += 1;
        }
        if (liczProb > Integer.parseInt(strOfInd[choosenInd][6])) {
            licznikPom2 += 1;
        }
        return newRowToAdd;
    }

    private void checkMeasure() {
        double wymiar;
        double tolp;
        double tolm;
        double pomiar;

        if (strOfInd[choosenInd][3] != "null") {
            wymiar = Double.parseDouble(strOfInd[choosenInd][3]);
        } else {
            wymiar = 0.0;
        }

        if (strOfInd[choosenInd][4] != "null") {
            tolp = Double.parseDouble(strOfInd[choosenInd][4]);
        } else {
            tolp = 0.0;
        }

        if (strOfInd[choosenInd][5] != "null") {
            tolm = Double.parseDouble(strOfInd[choosenInd][5]);
        } else {
            tolm = 0.0;
        }

        if (pomiarAktualny != null) {
            pomiar = Double.parseDouble(pomiarAktualny);
        } else {
            pomiar = 0.0;
        }

        if (pomiar >= (wymiar - tolm) && pomiar <= (wymiar + tolp)) {
            pomiarOk = true;
        } else {
            pomiarOk = false;
        }
    }

    private String prepNewIndPost(String pomiarAkt) {
        String postStr = "{\"ididx\":\"" + strOfInd[choosenInd][0] + "\",\"idelem\":\"" + String.valueOf(licznikPom2) + "\", \"wymiar\":\"" + pomiarAkt + "\", \"operator\":\""+"xxx"+"\", \"data\":\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate) + "\", \"maszyna\":\"Piła1\",\"dataprocesu\":\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataprocesu) + "\"}";
        return postStr;
    }

    public void startclick(View v) {
        Button btnStop = (Button) findViewById(R.id.stopprocbtn);
        Button btnStart = (Button) findViewById(R.id.startprocbtn);
        TextView indtv = (TextView) findViewById(R.id.indextv);
        Button indbtn = (Button) findViewById(R.id.zmianaindbtn);
        TextView textView8 = (TextView) findViewById(R.id.textView8);
        TextView textView5 = (TextView) findViewById(R.id.textView5);
        if (indtv.getText().equals("-wybierz-")) {
            customToast("Wybierz indeks");
        } else {
            textView5.setTextColor(Color.GREEN);
            textView8.setTextColor(Color.GREEN);
            indbtn.setEnabled(false);
            licznikPom2 = 1;
            dataprocesu = new Date();
            btnStart.setClickable(false);
            btnStop.setClickable(true);
        }

        cdt.cancel();

    }

    public void stopclick(View v) {
        TextView tvpozost = (TextView) findViewById(R.id.tvpoz);
        Button btnStop = (Button) findViewById(R.id.stopprocbtn);
        TextView indtxt = (TextView) findViewById(R.id.indextv);
        Button indbtn = (Button) findViewById(R.id.zmianaindbtn);
        final TextView wym = (TextView) findViewById(R.id.wymiartv);
        final TextView tplus = (TextView) findViewById(R.id.tplustv);
        final TextView tminus = (TextView) findViewById(R.id.tminustv);
        if (!tvpozost.getText().equals("")) {
            if (Integer.parseInt(tvpozost.getText().toString()) > 0) {
                showDialogCustYN("Nie wykonałeś wszystkich pomiarów\nPozostało " + tvpozost.getText().toString() + "\nCzy na pewno zakończyć proces?", 2);
            } else {
                indbtn.setEnabled(true);
                tabLay1 = (TableLayout) findViewById(R.id.tabLay);
                tabLay1.removeAllViews();
                TextView textView2 = (TextView) findViewById(R.id.pomiartv);
                textView2.setText("0.0");
                wym.setText("-");
                tplus.setText("-");
                tminus.setText("-");
                tvpozost.setText("");
                textView2.setTextColor(Color.parseColor("#000000"));
                licznikPom = 1;
                liczProb = 1;
                licznikPom2 = 1;
                showDialogCust("Uwaga", "Zakończono pomiary dla indeksu " + indtxt);
            }
        }else{
            customToast("Nie rozpoczęto jeszcze procesu");
        }
        //cdt.start();
        //new BlinkingText(this);
    }


    private void countDownProb() {
        TextView tvpr = (TextView) findViewById(R.id.tvpoz);
        tvpr.setText(String.valueOf((Integer.parseInt(strOfInd[choosenInd][6]) * 10) - liczProb));
    }

    @Override
    protected void onStart() {

        super.onStart();
        registerReceiver(breceive, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(breceive);
    }

    private void customToast(String text) {
        Toast toast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
        View view = toast.getView();

        //To change the Background of Toast
        view.setBackgroundColor(Color.TRANSPARENT);
        TextView textV = (TextView) view.findViewById(android.R.id.message);

        //Shadow of the Of the Text Color
        textV.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        textV.setPadding(0, 0, 0, 0);
        textV.setTextColor(Color.YELLOW);
        textV.setTextSize(60);
        textV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toast.show();
    }

    private void openPDF() throws IOException {

        pdfView = (ImageView) findViewById(R.id.image);
        File file = new File(targetPdf);

        ParcelFileDescriptor fileDescriptor = null;
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

        PdfRenderer pdfRenderer = null;
        pdfRenderer = new PdfRenderer(fileDescriptor);

        PdfRenderer.Page rendererPage = pdfRenderer.openPage(0);
        int rendererPageWidth = rendererPage.getWidth();
        int rendererPageHeight = rendererPage.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(rendererPageWidth, rendererPageHeight, Bitmap.Config.ARGB_8888);
        rendererPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        pdfView.setImageBitmap(bitmap);
        mAttacher = new PhotoViewAttacher(pdfView);
        rendererPage.close();

        pdfRenderer.close();
        fileDescriptor.close();
    }

    private void zapiszWynik() {
        new DataSaver(this, newIndexPost);
    }

    private void showDialogCust(String title, String textAlert) {
        final TextView myView = new TextView(getApplicationContext());
        myView.setText(textAlert);
        myView.setTextSize(30);
        myView.setTextColor(Color.parseColor("#dddddd"));
        myView.setPadding(10,5,10,5);
        Button btnStop = (Button) findViewById(R.id.stopprocbtn);
        TextView indtxt = (TextView) findViewById(R.id.indextv);
        Button indbtn = (Button) findViewById(R.id.zmianaindbtn);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(title)
                .setView(myView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showDialogCustYN(String textAlert, final int action) {
        final TextView myView = new TextView(getApplicationContext());
        myView.setText(textAlert);
        myView.setTextSize(30);
        myView.setTextColor(Color.parseColor("#dddddd"));
        myView.setPadding(10,5,10,5);
        final TextView tvpozostal = (TextView) findViewById(R.id.tvpoz);
        final TextView textView2 = (TextView) findViewById(R.id.pomiartv);
        final EditText editText = (EditText) findViewById(R.id.editText1);
        Button btnStop = (Button) findViewById(R.id.stopprocbtn);
        final Button btnStart = (Button) findViewById(R.id.startprocbtn);
        TextView indtv = (TextView) findViewById(R.id.indextv);
        final Button indbtn = (Button) findViewById(R.id.zmianaindbtn);
        final TextView tvloggedp = (TextView) findViewById(R.id.textView6);
        final TextView wym = (TextView) findViewById(R.id.wymiartv);
        final TextView tplus = (TextView) findViewById(R.id.tplustv);
        final TextView tminus = (TextView) findViewById(R.id.tminustv);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Uwaga")
                .setView(myView)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (action == 1) {

                        } else if (action == 2) {
                            wym.setText("-");
                            tplus.setText("-");
                            tminus.setText("-");
                            tvpozostal.setText("");
                            indbtn.setEnabled(true);
                            tabLay1 = (TableLayout) findViewById(R.id.tabLay);
                            tabLay1.removeAllViews();
                            TextView textView2 = (TextView) findViewById(R.id.pomiartv);
                            textView2.setText("0.0");
                            textView2.setTextColor(Color.parseColor("#000000"));
                            licznikPom = 1;
                            liczProb = 1;
                            licznikPom2 = 1;
                        }else if (action == 3){
                            startclick(btnStart);
                            pomiarAktualny = editText.getText().toString();
                            textView2.setText(editText.getText());
                            editText.setText("");
                            checkMeasure();
                            if (pomiarOk) {
                                textView2.setTextColor(Color.parseColor("#55bb55"));
                            } else {
                                textView2.setTextColor(Color.RED);
                            }
                            newIndexPost = prepNewIndPost(textView2.getText().toString());

                            zapiszWynik();
                            editText.requestFocus();

                        }else if(action == 4){
                            tvloggedp.setText("----");
                        }
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void pobierzIndeksy() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //HttpHandler.doPost("ididx:\"4\",idelem:\"1\", wymiar:\"111.1\", operator:\"PRC1\", data:\"2018-06-06 12:00:00\", maszyna:\"Piła7\"");
                indeksy = HttpHandler.doGet();
                try {
                    readJsonString = readFromStream(indeksy);
                    System.out.println("printuję indeksy:  " + readJsonString);
                    jArr = new JSONArray(readJsonString);
                    strOfInd = new String[jArr.length()][7];
                    //JSONArray jArr = jObj.getJSONArray("indeks");
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject obj = jArr.getJSONObject(i);
                        strOfInd[i][0] = obj.getString("ididx");
                        strOfInd[i][1] = obj.getString("idrys");
                        strOfInd[i][2] = obj.getString("indeks");
                        strOfInd[i][3] = obj.getString("wymiar");
                        strOfInd[i][4] = obj.getString("tolerancjap");
                        strOfInd[i][5] = obj.getString("tolerancjam");
                        strOfInd[i][6] = obj.getString("iloscpomiarow");
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //System.out.println(indeksy.toString());
            }
        });
    }

    public void wyloguj(View v){
        TextView tvloggedp = (TextView) findViewById(R.id.textView6);
        if (!tvloggedp.getText().toString().equals("----")){
            showDialogCustYN("Czy wylogować użytkownika "+tvloggedp.getText().toString()+"?",4);
        }else{
            customToast("Nie jesteś zalogowany");
        }
    }

    private BroadcastReceiver breceive = new BroadcastReceiver() {
        public static final String akcja1 = "akcja1";
        public static final String akcja2 = "akcja2";
        public static final String akcja3 = "akcja3";
        public static final String akcja4 = "akcja4";
        public static final String akcja5 = "akcja5";
        public static final String akcja6 = "akcja6";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("akcja1")) {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Pobieranie obrazu...");
                    progressDialog.show();
                } else if (intent.getAction().equals("akcja2")) {
                    String str = intent.getStringExtra("chosenind");
                    Boolean outputFileB = intent.getBooleanExtra("outputFileB", false);
                    //Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                    try {
                        openPDF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (outputFileB) {
                            progressDialog.dismiss();
                            //Toast.makeText(context, "Downloaded Successfully", Toast.LENGTH_SHORT).show();
                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, 3000);
                            //Toast.makeText(context, "Problem z pobraniem obrazu", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                            showDialogCust("Błąd", "Nie można pobrać obrazu");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        //Change button text if exception occurs

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 3000);
                        //Toast.makeText(context, "Problem z pobraniem obrazu", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());
                        progressDialog.dismiss();
                        showDialogCust("Błąd", "Nie można pobrać obrazu");


                    }
                } else if (intent.getAction().equals("akcja3")) {
                    //Toast.makeText(context, "testowanie", Toast.LENGTH_SHORT).show();
                    System.out.println("broadcast dziala akcja 3");
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Zapisywanie pomiaru...");
                    progressDialog.show();

                } else if (intent.getAction().equals("akcja4")) {
                    Integer resp = intent.getIntExtra("respCode", 0);
                    progressDialog.dismiss();
                    if (resp == 200) {
                        EditText editTextMain = (EditText) findViewById(R.id.editText1);
                        ScrollView svx = (ScrollView) findViewById(R.id.sv1);
                        newRowMeasure = prepareRow();
                        addTabRow(newRowMeasure);
                        countDownProb();
                        svx.fullScroll(View.FOCUS_DOWN);
                        editTextMain.requestFocus();
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editTextMain.getWindowToken(), 0);

                    } else {
                        progressDialog.dismiss();
                        showDialogCust("Błąd", "Wystąpił problem z komunikacją.\nSkontaktuj się z administratorem.");
                    }

                } else if (intent.getAction().equals("akcja5")) {
                    TextView tv9 = (TextView) findViewById(R.id.textView9);
                    int tc = tv9.getCurrentTextColor();
                    //Toast.makeText(context, String.valueOf(tc), Toast.LENGTH_SHORT).show();
                    if (tv9.getVisibility() == View.VISIBLE) {
                        tv9.setVisibility(View.INVISIBLE);
                    } else {
                        tv9.setVisibility(View.VISIBLE);
                    }
                } else if (intent.getAction().equals("akcja6")) {
                    TextView tvLogged = (TextView) findViewById(R.id.textView6);
                    tvLogged.setText(intent.getStringExtra("nfcTag"));
                    //Toast.makeText(context, "NFC tag: "+intent.getStringExtra("nfcTag"), Toast.LENGTH_SHORT).show();
                }
            }
        }


    };


}