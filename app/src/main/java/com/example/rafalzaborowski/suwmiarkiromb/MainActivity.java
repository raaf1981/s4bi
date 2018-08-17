package com.example.rafalzaborowski.suwmiarkiromb;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import uk.co.senab.photoview.PhotoViewAttacher;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Main Activity", MIEJSCE = "Pila";
    private static final long MILLISMAIN = 1200000;
    public static String[][] strOfInd;
    public static int choosenInd;
    InputStreamReader indeksy, odpowiedz;
    String readJsonString;
    JSONArray jArr;
    JSONObject jObj;
    TableLayout tabLay1;
    Date currentDate;
    Date dataprocesu, dataprocesuBackup;
    IntentFilter filter = new IntentFilter();
    String targetPdf = "storage/emulated/0/downloadedPdf/index.pdf";
    ImageView pdfView;
    PhotoViewAttacher mAttacher;
    Context context;
    CountDownTimer cdt;
    private String[] newRowMeasure;
    private int licznikPomiarowGlowny = 1, trybKalibLicz = 0, trybKalibLiczPom = 0, trybNormalLicz = 0, trybNormalLiczPom = 0, liczSesja = 0, liczProbKalib = 1, liczProbKont = 1, trybKalibLiczMain = 0;
    private int liczProb = 1, indexPrev;
    private int licznikPomiarowTmp1 = 1, licznikPomiarowTmp1Kal = 1, licznikPomiarowTmp1Kont = 1;
    private boolean pomiarOk, timerStart;
    private String[] headerRow = {"ID", "Godzina", "Indeks", "Wymiar", "T+", "T-", "Pomiar", "Punkt", "Tryb"};
    private String[] trybRow = {"CYKL", "KRYT", "WRF", "AWR", "KLB", "INT", "KNT"};
    private String pomiarAktualny, kogut = "1", kogutSaved = "1", savedLogin = "----";
    private String newIndexPost, currentLog = "----";
    private String[] lista = new String[]{"Piła 1", "Piła 2", "Piła 3", "Piła 4", "Piła 5", "Piła 6", "Piła 7", "Obr 1", "Obr 2", "Obr 3"};
    private ProgressDialog progressDialog;
    CharSequence[] values = {"Zwykły", "Krytyczny", "Kosz", "Awaryjny"};
    private int trybPomiaru = -1, savedTryb = -1;
    private boolean elemOk = true, started = false, firstmeasure = true, lastmeasure = false, logged = false, kalibEnd = false;
    public static boolean indexchoosen = false, stopProc = false, comm = false;
    FileNotFoundException exception1 = null;
    public static long millis = -1;
    private UsbService usbService;
    private MyHandler mHandler;
    public static String prevLogin="";
    private SharedPreferences preferences;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        try {
            unbindService(usbConnection);
        } catch (IllegalArgumentException e) {

        }
        try {
            unregisterReceiver(mUsbReceiver);
        } catch (IllegalArgumentException e) {

        }
        try {
            unregisterReceiver(breceive);
        } catch (IllegalArgumentException e) {

        }
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(this.getIntent().getAction())) {
            finish();
        }
        super.onPause();

    }

    @Override
    protected void onResume() {

        //startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        cdt = new CountDown(MILLISMAIN, 1000, this);
        filter.addAction("akcja1");
        filter.addAction("akcja2");
        filter.addAction("akcja3");
        filter.addAction("akcja4");
        filter.addAction("akcja5");
        filter.addAction("akcja6");
        filter.addAction("akcja7");
        filter.addAction("akcja8");
        filter.addAction("akcja9");
        filter.addAction("akcja10");
        filter.addAction("akcja11");
        filter.addAction("akcja12");
        filter.addAction("akcja13");
        filter.addAction("akcja14");
        filter.addAction("akcja15");
        registerReceiver(breceive, filter);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        scheduleRun();
        final TextView tvpozostal = (TextView) findViewById(R.id.tvpoz);
        final TextView textView2 = (TextView) findViewById(R.id.pomiartv);
        final EditText editText = (EditText) findViewById(R.id.editText1);
        editText.setBackgroundResource(android.R.color.transparent);
        editText.setOnKeyListener(new OnKeyListener() {
            Button btnStop1 = (Button) findViewById(R.id.stopprocbtn);
            Button btnStart1 = (Button) findViewById(R.id.startprocbtn);

            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {

                if ((keyevent.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //editText.setClickable(false);
                    if (!logged && !indexchoosen && !started) {
                        showDialogCust("Błąd", "Aby wykonać pomiary należy się zalogować,\nwybrać indeks i rozpocząć proces.");
                        editText.setText("");
                    } else if (logged && !indexchoosen && !started) {
                        showDialogCust("Błąd", "Aby wykonać pomiary należy wybrać indeks\ni rozpocząć proces.");
                        editText.setText("");
                    } else if (logged && indexchoosen && !started) {
                        showDialogCust("Błąd", "Aby wykonać pomiary należy rozpocząć proces\nnaciskając przycisk START PROCES.");
                        editText.setText("");
                    } else if (!logged && indexchoosen && started) {
                        showDialogCust("Błąd", "Aby wykonać pomiary należy się zalogować");
                        editText.setText("");
                    } else {
                        TextView tvblink = (TextView) findViewById(R.id.textView9);
                        if (tvblink.getVisibility() == View.VISIBLE) {
                            tvblink.clearAnimation();
                            tvblink.setVisibility(View.GONE);
                        }
                        int iloscPunktow = Integer.parseInt(strOfInd[choosenInd][6]);
                        currentDate = new Date();
                        switch (trybPomiaru) {
                            case -1: //nieoznaczony
                                if(!logged){
                                    showDialogCust("Błąd", "Aby wykonać pomiary należy się zalogować");
                                }else{
                                    showDialogCust("Błąd", "Rozpocznij proces naciskając przycisk START PROCES.");
                                }
                                break;
                            case 0: //normalny
                                if (!kogut.equals("1")) {
                                    kogut = "1";
                                    if (usbService != null) { // if UsbService was correctly binded, Send data
                                        usbService.write(kogut.getBytes());
                                        //customToast("USB binded!!");
                                        //customToast("usb service toString:  "+usbService.toString());
                                        //customToast("usb data:  "+data);
                                    }
                                }
                                TextView tvpr = (TextView) findViewById(R.id.tvpoz);
                                if (Integer.parseInt(tvpr.getText().toString()) == ((Integer.parseInt(strOfInd[choosenInd][6]) * 10))) {
                                    dataprocesu = new Date();
                                    tabLay1 = (TableLayout) findViewById(R.id.tabLay);
                                    tabLay1.removeAllViews();
                                    licznikPomiarowGlowny=1;
                                }
                                if (logged && started && millis > 0) {
                                    showDialogCust("Uwaga", "Nie można wykonać pomiaru przed czasem.");
                                    editText.setText("");
                                } else {
                                    try {
                                        pomiarAktualny = editText.getText().toString();
                                        textView2.setText(editText.getText());
                                        editText.setText("");
                                        Double.parseDouble(pomiarAktualny);
                                        checkMeasure();
                                        if (pomiarOk) {
                                            textView2.setTextColor(Color.parseColor("#55bb55"));
                                        } else {
                                            textView2.setTextColor(Color.RED);
                                        }
                                        newIndexPost = prepNewIndPost(textView2.getText().toString());
                                        zapiszWynik();
                                    } catch (NumberFormatException e) {
                                        showDialogCust("Błąd", "Błędny pomiar!");
                                    }
                                    editText.requestFocus();
                                }
                                break;
                            case 1: //krytyczny
                                break;
                            case 2: //
                                break;
                            case 3:
                                break;
                            case 4: //kalibracja
                                try {
                                    /*TextView tvpr2 = (TextView) findViewById(R.id.tvpoz);
                                    if (Integer.parseInt(tvpr2.getText().toString()) == ((Integer.parseInt(strOfInd[choosenInd][6]) * 10))) {
                                        dataprocesu = new Date();
                                    }*/
                                    pomiarAktualny = editText.getText().toString();
                                    textView2.setText(editText.getText());
                                    editText.setText("");
                                    Double.parseDouble(pomiarAktualny);
                                    checkMeasure();
                                    if (pomiarOk) {
                                        textView2.setTextColor(Color.parseColor("#55bb55"));
                                    } else {
                                        textView2.setTextColor(Color.RED);
                                    }
                                    newIndexPost = prepNewIndPost(textView2.getText().toString());
                                    zapiszWynik();
                                } catch (NumberFormatException e) {
                                    showDialogCust("Błąd", "Błędny pomiar");
                                }
                                editText.requestFocus();
                                break;
                            case 5:

                                break;
                            case 6:
                                try {
                                    pomiarAktualny = editText.getText().toString();
                                    Double.parseDouble(pomiarAktualny);
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
                                } catch (NumberFormatException e) {
                                    showDialogCust("Błąd", "Błędny pomiar");
                                }
                                break;
                            case 7:
                                showDialogCust("Uwaga", "Należy zatrzymać proces naciskając przycisk STOP PROCES.");
                                break;
                            default:
                                editText.requestFocus();
                                break;
                        }
                    }
                }

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
        preferences = getSharedPreferences("myPrefs", Activity.MODE_PRIVATE);
        int getprefspinner = preferences.getInt("miejsce", 0);
        spinner.setSelection(getprefspinner);
        spinner.setEnabled(false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences.Editor preferencesEditor = preferences.edit();
                int spinnerSelect = position;
                preferencesEditor.putInt ("miejsce", spinnerSelect);
                preferencesEditor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //spinner.setClickable(false);
        //spinner.setSelection(0);


    }

    public void testMet(View v) {
        if (!logged) {
            showDialogCust("Błąd", "Zaloguj się!");
        } else {
            if (strOfInd == null) {
                pobierzIndeksy();
                showDialogCust("Błąd", "Nie można pobrać bazy indeksów.\nWystąpił problem z komunikacją.\nSkontaktuj się z administratorem.");

            } else {
                DialogFragment newFragment = new ChoosenIndex();
                newFragment.show(getSupportFragmentManager(), "choosen");
            }
        }

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
        for (int i = 0; i < 9; i++) {
            TextView newTV = new TextView(this);
            newTV.setText(tabTV[i]);
            if (pomiarOk) {
                newTV.setTextColor(Color.parseColor("#00cc66"));
            } else {
                newTV.setTextColor(Color.RED);
            }
            if (licznikPomiarowGlowny < 11) {
                switch (i) {
                    case 0:
                        newTV.setPadding(18, 2, 17, 2);
                        break;
                    case 1:
                        newTV.setPadding(25, 2, 17, 2);
                        break;
                    case 2:
                        newTV.setPadding(15, 2, 17, 2);
                        break;
                    case 3:
                        newTV.setPadding(30, 2, 17, 2);
                        break;
                    case 4:
                        newTV.setPadding(36, 2, 17, 2);
                        break;
                    case 5:
                        newTV.setPadding(12, 2, 17, 2);
                        break;
                    case 6:
                        newTV.setPadding(20, 2, 17, 2);
                        break;
                    case 7:
                        newTV.setPadding(44, 2, 17, 2);
                        break;
                    case 8:
                        newTV.setPadding(28, 2, 17, 2);
                        break;
                }
            } else if (licznikPomiarowGlowny >= 11 && licznikPomiarowGlowny < 100) {
                switch (i) {
                    case 0:
                        newTV.setPadding(12, 2, 17, 2);
                        break;
                    case 1:
                        newTV.setPadding(23, 2, 17, 2);
                        break;
                    case 2:
                        newTV.setPadding(14, 2, 17, 2);
                        break;
                    case 3:
                        newTV.setPadding(30, 2, 17, 2);
                        break;
                    case 4:
                        newTV.setPadding(35, 2, 17, 2);
                        break;
                    case 5:
                        newTV.setPadding(12, 2, 17, 2);
                        break;
                    case 6:
                        newTV.setPadding(22, 2, 17, 2);
                        break;
                    case 7:
                        newTV.setPadding(42, 2, 17, 2);
                        break;
                    case 8:
                        newTV.setPadding(26, 2, 17, 2);
                        break;
                }
            } else {
                switch (i) {
                    case 0:
                        newTV.setPadding(10, 2, 17, 2);
                        break;
                    case 1:
                        newTV.setPadding(19, 2, 17, 2);
                        break;
                    case 2:
                        newTV.setPadding(11, 2, 17, 2);
                        break;
                    case 3:
                        newTV.setPadding(30, 2, 17, 2);
                        break;
                    case 4:
                        newTV.setPadding(35, 2, 17, 2);
                        break;
                    case 5:
                        newTV.setPadding(12, 2, 17, 2);
                        break;
                    case 6:
                        newTV.setPadding(20, 2, 17, 2);
                        break;
                    case 7:
                        newTV.setPadding(40, 2, 17, 2);
                        break;
                    case 8:
                        newTV.setPadding(26, 2, 17, 2);
                        break;
                }
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
        TextView tvPom = (TextView) findViewById(R.id.pomiartv);
        String[] newRowToAdd = new String[headerRow.length];
        newRowToAdd[0] = String.valueOf(licznikPomiarowGlowny);
        licznikPomiarowGlowny += 1;

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
        if (trybPomiaru == 0) {
            if (liczProb <= Integer.parseInt(strOfInd[choosenInd][6])) {
                newRowToAdd[7] = String.valueOf(liczProb);
                if (trybPomiaru == 0) {
                    liczProb += 1;
                }
            } else {
                liczProb = 1;
                newRowToAdd[7] = String.valueOf(liczProb);
                liczProb += 1;
            }
        } else if (trybPomiaru == 4) {
            if (liczProbKalib <= Integer.parseInt(strOfInd[choosenInd][6])) {
                newRowToAdd[7] = String.valueOf(liczProbKalib);
                liczProbKalib += 1;

            } else {
                liczProbKalib = 1;
                newRowToAdd[7] = String.valueOf(liczProbKalib);
                liczProbKalib += 1;
            }
        } else if (trybPomiaru == 6) {
            if (liczProbKont <= Integer.parseInt(strOfInd[choosenInd][6])) {
                newRowToAdd[7] = String.valueOf(liczProbKont);
                liczProbKont += 1;
            } else {
                liczProbKont = 1;
                newRowToAdd[7] = String.valueOf(liczProbKont);
                liczProbKont += 1;
            }
        }
        newRowToAdd[8] = trybRow[trybPomiaru];
        if (trybPomiaru == 0) {
            if (liczProb > Integer.parseInt(strOfInd[choosenInd][6])) {
                licznikPomiarowTmp1 += 1;
            }
        } else if (trybPomiaru == 4) {
            if (liczProbKalib > Integer.parseInt(strOfInd[choosenInd][6])) {
                licznikPomiarowTmp1Kal += 1;
            }
        } else if (trybPomiaru == 6) {
            if (liczProbKont > Integer.parseInt(strOfInd[choosenInd][6])) {
                licznikPomiarowTmp1Kont += 1;
            }
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

        Double wymminus = BigDecimal.valueOf(new Double(wymiar-tolm))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        Double wymplus = BigDecimal.valueOf(new Double(wymiar+tolp))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        if ((pomiar >= wymminus) && (pomiar <= wymplus)) {
            pomiarOk = true;
        } else {
            pomiarOk = false;
        }

    }

    private String prepNewIndPost(String pomiarAkt) {
        TextView tvloggedp = (TextView) findViewById(R.id.textView6);
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        String tp = String.valueOf(trybPomiaru);
        String maszyna = spin.getSelectedItem().toString();
        int licznikPomiarowTmpX = 0;
        if (trybPomiaru == 0) {
            //tp = "00";
            licznikPomiarowTmpX = licznikPomiarowTmp1;
        } else if (trybPomiaru == 4) {
            licznikPomiarowTmpX = licznikPomiarowTmp1Kal;
        } else if (trybPomiaru == 6) {
            licznikPomiarowTmpX = licznikPomiarowTmp1Kont;
        }
        String postStr = "{\"ididx\":\"" + strOfInd[choosenInd][0] + "\",\"idelem\":\"" + String.valueOf(licznikPomiarowTmpX) + "\", \"wymiar\":\"" + pomiarAkt + "\", \"operator\":\"" + tvloggedp.getText().toString() + "\", \"data\":\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate) + "\", \"maszyna\":\""+maszyna+"\",\"dataprocesu\":\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataprocesu) + "\",\"tryb\":\"" + tp + "\"}";
        return postStr;
    }

    public void startclick(View v) {
        String data = "1";
        if (usbService != null) { // if UsbService was correctly binded, Send data
            usbService.write(data.getBytes());
            //customToast("USB binded!!");
            //customToast("usb service toString:  "+usbService.toString());
            //customToast("usb data:  "+data);
        }
        if (logged) {
            tabLay1 = (TableLayout) findViewById(R.id.tabLay);
            tabLay1.removeAllViews();
            //blinkingAlert();
            Button btnStop = (Button) findViewById(R.id.stopprocbtn);
            Button btnTryb = (Button) findViewById(R.id.btntryb);
            Button btnStart = (Button) findViewById(R.id.startprocbtn);
            TextView indtv = (TextView) findViewById(R.id.indextv);
            Button indbtn = (Button) findViewById(R.id.zmianaindbtn);
            TextView textView8 = (TextView) findViewById(R.id.textView8);
            TextView textView5 = (TextView) findViewById(R.id.textView5);
            if (!indexchoosen && !started) {
                customToast("Wybierz indeks");
            } else if (indexchoosen && started) {
                customToast("Proces został już rozpoczęty");
            } else {
                if (trybPomiaru != 4) {
                    ustawTryb(4);
                }
                textView8.setVisibility(View.VISIBLE);
                textView5.setText("Czas do następnego pomiaru: ");
                textView5.setX(630);
                textView5.setTextColor(Color.GREEN);
                textView8.setTextColor(Color.GREEN);
                indbtn.setEnabled(false);
                indtv.setClickable(false);
                licznikPomiarowTmp1 = 1;
                licznikPomiarowTmp1Kal = 1;
                licznikPomiarowTmp1Kont = 1;

                started = true;
                btnStop.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
            }

            //cdt.cancel();
        } else {
            showDialogCust("Błąd", "Zaloguj się");
        }

    }

    public void stopclick(View v) {
        TextView loginTv = (TextView) findViewById(R.id.textView6);
        if(loginTv.getText().toString().contains("KNT")){
            //showDialogCustYN("Zakończyć kontrolę i wylogować " + loginTv.getText().toString() + "?", 6);
            showDialogCustYN("Zakończyć kontrolę i zatrzymać proces?", 1);
        }else{
            if(logged && millis==0 && trybPomiaru==0 && liczSesja>0){
                showDialogCust("Uwaga", "Aby zakończyć proces należy wykonać wszystkie pomiary.");
            }else{
                stopProces("auto");
            }

        }

    }

    private void ustawTryb(int tryb) {
        TextView trybText = (TextView) findViewById(R.id.textView3);
        TextView tvpozlicz = (TextView) findViewById(R.id.tvpoz);
        TextView tvpoz = (TextView) findViewById(R.id.textView4);
        EditText edittext = (EditText) findViewById(R.id.editText1);
        Button btnCtrl = (Button) findViewById(R.id.btnControl);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        switch (tryb) {
            case -1: //nieustalony
                switch (trybPomiaru) {
                    case -1:
                        break;
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:

                        break;
                    default:
                        break;
                }
                spinner.setEnabled(false);
                break;
            case 0: //zwykły
                switch (trybPomiaru) {
                    case -1:
                        break;
                    case 0:
                        break;
                    case 1:
                        //liczProb = 1;
                        showDialogCust("Uwaga", "Zmierzono 3 poprawne elementy w punkcie krytycznym.\nSystem w trybie normalnym.");
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        liczProbKalib = 1;
                        if (started) {
                            TextView tvloggedp = (TextView) findViewById(R.id.textView6);
                            Button btnlog = (Button) findViewById(R.id.zalogbtn);
                            Button btnwylog = (Button) findViewById(R.id.wylogbtn);
                            showDialogCust("Uwaga", "Kalibracja zakończona poprawnie.\nSystem w trybie normalnym.");
                            cdt = new CountDown(MILLISMAIN, 1000, this);
                            cdt.start();
                            tvloggedp.setText("----");
                            logged = false;
                            btnlog.setVisibility(View.VISIBLE);
                            btnwylog.setVisibility(View.INVISIBLE);
                        }

                        break;
                    case 5:
                        break;
                    case 6:
                        dataprocesu=dataprocesuBackup;
                        liczProbKont = 1;
                        trybText.clearAnimation();
                        trybText.setTextColor(Color.WHITE);
                        btnCtrl.setVisibility(View.GONE);
                        if(millis>0) {
                            cdt = new CountDown(millis, 1000, this);
                            cdt.start();
                        }
                        if (!kogut.equals(kogutSaved)) {
                            kogut = kogutSaved;
                            if (usbService != null) { // if UsbService was correctly binded, Send data
                                usbService.write(kogut.getBytes());
                                //customToast("USB binded!!");
                                //customToast("usb service toString:  "+usbService.toString());
                                //customToast("usb data:  "+data);
                            }
                        }
                        break;
                    default:
                        break;
                }
                spinner.setEnabled(false);
                trybPomiaru = 0;
                trybText.setText("Wymagana ilość pomiarów:");
                tvpoz.setVisibility(View.VISIBLE);
                tvpozlicz.setVisibility(View.VISIBLE);
                break;
            case 1: //krytyczny
                switch (trybPomiaru) {
                    case -1:
                        break;
                    case 0:

                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    default:
                        break;
                }
                trybPomiaru = 1;
                trybText.setText("Tryb pomiaru: KRYTYCZNY");
                tvpoz.setVisibility(View.GONE);
                tvpozlicz.setVisibility(View.GONE);
                break;
            case 2: //kosz
                switch (trybPomiaru) {
                    case -1:
                        break;
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    default:
                        break;
                }
                trybPomiaru = 2;
                trybText.setText("Tryb pomiaru: KOSZ");
                tvpoz.setVisibility(View.GONE);
                tvpozlicz.setVisibility(View.GONE);
                break;
            case 3: //awaryjny
                switch (trybPomiaru) {
                    case -1:
                        break;
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    default:
                        break;
                }
                trybPomiaru = 3;
                trybText.setText("Tryb pomiaru: AWARYJNY");
                tvpoz.setVisibility(View.GONE);
                tvpozlicz.setVisibility(View.GONE);
                break;
            case 4: //kalibracja
                switch (trybPomiaru) {
                    case -1:
                        dataprocesu = new Date();
                        break;
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        dataprocesu=dataprocesuBackup;
                        liczProbKont = 1;
                        trybText.clearAnimation();
                        trybText.setTextColor(Color.WHITE);
                        btnCtrl.setVisibility(View.GONE);
                        if (!kogut.equals("1")) {
                            kogut = "1";
                            if (usbService != null) { // if UsbService was correctly binded, Send data
                                usbService.write(kogut.getBytes());
                                //customToast("USB binded!!");
                                //customToast("usb service toString:  "+usbService.toString());
                                //customToast("usb data:  "+data);
                            }
                        }
                        break;
                    default:
                        break;
                }
                spinner.setEnabled(false);
                trybPomiaru = 4;
                trybText.setText("Tryb pomiaru: KALIBRACJA");
                tvpoz.setVisibility(View.GONE);
                tvpozlicz.setVisibility(View.GONE);
                kalibEnd = false;
                break;
            case 5: //interwencyjny
                switch (trybPomiaru) {
                    case -1:
                        break;
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    default:
                        break;
                }
                trybPomiaru = 5;
                trybText.setText("Tryb pomiaru: INTERWENCYJNY");
                tvpoz.setVisibility(View.GONE);
                tvpozlicz.setVisibility(View.GONE);
                break;
            case 6: //kontrola
                switch (trybPomiaru) {
                    case -1:
                        break;
                    case 0:
                        TextView tvblink = (TextView) findViewById(R.id.textView9);
                        tvblink.clearAnimation();
                        tvblink.setVisibility(View.INVISIBLE);
                        cdt.cancel();
                        dataprocesuBackup=dataprocesu;
                        dataprocesu = new Date();
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        dataprocesuBackup=dataprocesu;
                        dataprocesu = new Date();
                        break;
                    default:
                        break;
                }

                spinner.setEnabled(true);
                trybPomiaru = 6;
                blinkingControl();
                break;
            case 7: //stop proces
                switch (trybPomiaru) {
                    case -1:
                        break;
                    case 0:
                        showDialogCust("Uwaga!", "Wykryto 3 błedne elementy!\nZatrzymaj proces.\nSprawdź wszystkie wyprodukowane elementy.");
                        stopProc = true;
                        blinkingAlertStop();
                        edittext.setEnabled(false);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        showDialogCust("Uwaga","Kalibracja niepoprawna.\nZatrzymaj proces.");
                        blinkingAlertStop();
                        edittext.setEnabled(false);
                        break;
                    default:
                        break;
                }


            default:
                break;
        }
    }

    private void countDownProb() {
        if (trybPomiaru == 0) {
            liczSesja++;
            TextView tvpr = (TextView) findViewById(R.id.tvpoz);
            int currentcd = liczSesja;

            if ((currentcd) == Integer.parseInt(strOfInd[choosenInd][6]) * 10) {
                koniecpomiarow();
            } else {
                tvpr.setText(String.valueOf((Integer.parseInt(strOfInd[choosenInd][6]) * 10) - liczSesja));
            }
        }
    }

    @Override
    protected void onStart() {
        //registerReceiver(breceive, filter);
        //startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        super.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
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
        textV.setTextColor(Color.BLUE);
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
        pdfView.setPadding(0, 0, 0, 0);


    }

    private void zapiszWynik() {
        new DataSaver(this, newIndexPost);
    }

    private void showDialogCust(String title, String textAlert) {
        final TextView myView = new TextView(getApplicationContext());
        myView.setText(textAlert);
        myView.setTextSize(30);
        myView.setTextColor(Color.parseColor("#dddddd"));
        //myView.setPadding(10,5,10,5);
        Button btnStop = (Button) findViewById(R.id.stopprocbtn);
        TextView indtxt = (TextView) findViewById(R.id.indextv);
        Button indbtn = (Button) findViewById(R.id.zmianaindbtn);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom2);
        builder.setTitle(title)
                .setView(myView)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    private void showDialogCust(String title, String textAlert, final int action2) {
        final TextView myView = new TextView(getApplicationContext());
        myView.setText(textAlert);
        myView.setTextSize(30);
        myView.setTextColor(Color.parseColor("#dddddd"));
        //myView.setPadding(10,5,10,5);
        Button btnStop = (Button) findViewById(R.id.stopprocbtn);
        TextView indtxt = (TextView) findViewById(R.id.indextv);
        Button indbtn = (Button) findViewById(R.id.zmianaindbtn);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom2);
        builder.setTitle(title)
                .setView(myView)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (action2 == 1) {
                            registerReceiver(breceive, filter);
                        }
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
        //myView.setPadding(10,5,10,5);
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
        builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
        builder.setTitle("Uwaga")
                .setView(myView)
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (action == 1) {
                            stopProces("manual");

                        } else if (action == 2) {

                            indbtn.setEnabled(true);
                            indtv.setClickable(true);
                            TextView textView2 = (TextView) findViewById(R.id.pomiartv);
                            textView2.setText("0.0");
                            textView2.setTextColor(Color.parseColor("#000000"));
                            licznikPomiarowGlowny = 1;
                            liczProb = 1;
                            liczProbKalib = 1;
                            liczProbKont = 1;
                            licznikPomiarowTmp1 = 1;
                            licznikPomiarowTmp1Kal = 1;
                            licznikPomiarowTmp1Kont = 1;
                            trybKalibLicz = 0;
                            trybKalibLiczPom = 0;
                            liczSesja=0;
                            trybNormalLiczPom=0;
                            trybNormalLicz=0;
                            started = false;
                        } else if (action == 3) {
                            startclick(btnStart);
                            try {
                                pomiarAktualny = editText.getText().toString();
                                Double.parseDouble(pomiarAktualny);
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
                            } catch (NumberFormatException e) {
                                showDialogCust("Błąd", "Błędny pomiar");
                            }

                            editText.requestFocus();

                        } else if (action == 4) {
                            Button btnlog = (Button) findViewById(R.id.zalogbtn);
                            Button btnwylog = (Button) findViewById(R.id.wylogbtn);
                            tvloggedp.setText("----");
                            logged = false;
                            btnlog.setVisibility(View.VISIBLE);
                            btnwylog.setVisibility(View.INVISIBLE);

                        } else if (action == 5) {
                            zaloguj(textAlert.substring(textAlert.indexOf("KNT"), textAlert.indexOf("KNT") + 6), false,"");
                            registerReceiver(breceive, filter);
                        } else if (action == 6) {
                            Button btnlog = (Button) findViewById(R.id.zalogbtn);
                            Button btnwylog = (Button) findViewById(R.id.wylogbtn);
                            if (!savedLogin.equals("----")) {
                                tvloggedp.setText(savedLogin);
                                savedLogin = "----";
                                logged = true;
                                btnlog.setVisibility(View.INVISIBLE);
                                btnwylog.setVisibility(View.VISIBLE);
                            } else {
                                tvloggedp.setText("----");

                                logged = false;
                                btnlog.setVisibility(View.VISIBLE);
                                btnwylog.setVisibility(View.INVISIBLE);
                            }
                            ustawTryb(savedTryb);

                        } else if (action == 7) {
                            Button btnlog = (Button) findViewById(R.id.zalogbtn);
                            Button btnwylog = (Button) findViewById(R.id.wylogbtn);
                            tvloggedp.setText("----");
                            ustawTryb(savedTryb);
                            logged = false;
                            btnlog.setVisibility(View.VISIBLE);
                            btnwylog.setVisibility(View.INVISIBLE);
                        } else if (action == 8) {
                            zaloguj(textAlert.substring(textAlert.lastIndexOf("PRC"), textAlert.lastIndexOf("PRC") + 6), false,"");
                            registerReceiver(breceive, filter);
                        } else if (action == 9) {
                            zaloguj(textAlert.substring(textAlert.lastIndexOf("KNT"), textAlert.lastIndexOf("KNT") + 6), false,"");
                            registerReceiver(breceive, filter);
                        }
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if ((action == 9) || (action == 8) || (action == 5)) {
                            registerReceiver(breceive, filter);
                        }
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

    public void wyloguj(View v) {
        TextView tvloggedp = (TextView) findViewById(R.id.textView6);
        if (!tvloggedp.getText().toString().equals("----")) {
            if (tvloggedp.getText().toString().contains("KNT")) {
                showDialogCustYN("Zakończyć kontrolę i wylogować " + tvloggedp.getText().toString() + "?", 6);
            } else {
                showDialogCustYN("Czy wylogować użytkownika " + tvloggedp.getText().toString() + "?", 4);
            }
        } else {
            customToast("Nie jesteś zalogowany");
        }
    }

    public void zalogujbtnclick(View v) {
        DialogFragment newFragment = new LoginDialog();
        newFragment.show(getSupportFragmentManager(), "login");
    }

    private void zaloguj(String tagNfc, boolean comm2, String pin) {
        comm = comm2;
        new LoginTask(tagNfc, this,pin);

    }

    public void trybClick(View v) {
        Button btnStart = (Button) findViewById(R.id.startprocbtn);
        if (btnStart.isClickable()) {
            customToast("Aby zmienić tryb pomiaru rozpocznij proces");
        } else {
            indexPrev = -1;
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.tryb_dialog);
            final RadioButton rb1 = (RadioButton) dialog.findViewById(R.id.rbnormalny);
            final RadioButton rb2 = (RadioButton) dialog.findViewById(R.id.rbkryt);
            final RadioButton rb3 = (RadioButton) dialog.findViewById(R.id.rbkosz);
            final RadioButton rb4 = (RadioButton) dialog.findViewById(R.id.rbawaryjny);
            final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.rgroup1);
            Button btnok = (Button) dialog.findViewById(R.id.btnok);
            Button btnanuluj = (Button) dialog.findViewById(R.id.btnanuluj);
            TextView tvtryb = (TextView) findViewById(R.id.textView3);
            String tvtrybtext = tvtryb.getText().toString();
            if (tvtrybtext == "Wymagana ilość pomiarów:") {
                rb1.setChecked(true);
                indexPrev = 0;
            } else if (tvtrybtext == "Tryb pomiaru: KRYTYCZNY") {
                rb2.setChecked(true);
                indexPrev = 1;
            } else if (tvtrybtext == "Tryb pomiaru: KOSZ") {
                rb3.setChecked(true);
                indexPrev = 2;
            } else if (tvtrybtext == "Tryb pomiaru: AWARYJNY") {
                rb4.setChecked(true);
                indexPrev = 3;
            } else if (tvtrybtext == "Tryb pomiaru: KALIBRACJA") {
                indexPrev = 4;
            } else if (tvtrybtext == "Tryb pomiaru: INTERWENCYJNY") {
                indexPrev = 5;
            }

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                    trybPomiaru = radioGroup.indexOfChild(radioButton);
                    //System.out.println("klikniety index radio buttona:  " +  index);
                }
            });
            dialog.show();

            btnok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tvtrybin = (TextView) findViewById(R.id.textView3);
                    TextView tvpoz = (TextView) findViewById(R.id.tvpoz);
                    TextView tvpozlicz = (TextView) findViewById(R.id.textView4);
                    switch (indexPrev) {
                        case -1:
                            showDialogCust("Błąd", "Wystąpił nieznany błąd.");
                            break;
                        case 0:
                            switch (trybPomiaru) {
                                case -1:
                                    dialog.dismiss();
                                    break;
                                case 0:
                                    dialog.dismiss();
                                    break;
                                case 1:
                                    ustawTryb(1);
                                    dialog.dismiss();
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    break;
                                case 4:
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 1:
                            switch (trybPomiaru) {
                                case -1:
                                    dialog.dismiss();
                                    break;
                                case 0:
                                    break;
                                case 1:
                                    dialog.dismiss();
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    break;
                                case 4:
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 2:
                            switch (trybPomiaru) {
                                case -1:
                                    dialog.dismiss();
                                    break;
                                case 0:
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    dialog.dismiss();
                                    break;
                                case 3:
                                    break;
                                case 4:
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 3:
                            switch (trybPomiaru) {
                                case -1:
                                    dialog.dismiss();
                                    break;
                                case 0:
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    dialog.dismiss();
                                    break;
                                case 4:
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 4:
                            switch (trybPomiaru) {
                                case -1:
                                    dialog.dismiss();
                                    break;
                                case 0:
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    break;
                                case 4:
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 5:
                            switch (trybPomiaru) {
                                case -1:
                                    break;
                                case 0:
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    break;
                                case 4:
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                }
            });

            btnanuluj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    private BroadcastReceiver breceive = new BroadcastReceiver() {
        public static final String akcja1 = "akcja1";
        public static final String akcja2 = "akcja2";
        public static final String akcja3 = "akcja3";
        public static final String akcja4 = "akcja4";
        public static final String akcja5 = "akcja5";
        public static final String akcja6 = "akcja6";
        public static final String akcja7 = "akcja7";
        public static final String akcja8 = "akcja8";
        public static final String akcja9 = "akcja9";
        public static final String akcja10 = "akcja10";
        public static final String akcja11 = "akcja11";
        public static final String akcja12 = "akcja12";
        public static final String akcja13 = "akcja13";
        public static final String akcja14 = "akcja14";
        public static final String akcja15 = "akcja15";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("akcja1")) {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Pobieranie obrazu...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else if (intent.getAction().equals("akcja2")) {
                    liczSesja = 0;
                    Boolean outputFileB = intent.getBooleanExtra("outputFileB", false);
                    try {
                        openPDF();

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                    try {
                        if (outputFileB) {
                            progressDialog.dismiss();
                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, 3000);
                            progressDialog.dismiss();

                            //showDialogCust("Błąd", "Nie można pobrać obrazu");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        //Change button text if exception occurs

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 3000);
                        progressDialog.dismiss();
                        //showDialogCust("Błąd", "Nie można pobrać obrazu");


                    }
                } else if (intent.getAction().equals("akcja3")) {
                    //Toast.makeText(context, "testowanie", Toast.LENGTH_SHORT).show();
                    System.out.println("broadcast dziala akcja 3");
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Zapisywanie pomiaru...");
                    progressDialog.show();

                } else if (intent.getAction().equals("akcja4")) {
                    Integer resp = intent.getIntExtra("respCode", 0);
                    progressDialog.dismiss();
                    int iloscPunktow = Integer.parseInt(strOfInd[choosenInd][6]);
                    if (resp == 200) {
                        EditText editText = (EditText) findViewById(R.id.editText1);
                        TextView textView2 = (TextView) findViewById(R.id.pomiartv);
                        ScrollView svx = (ScrollView) findViewById(R.id.sv1);
                        countDownProb();
                        newRowMeasure = prepareRow();
                        addTabRow(newRowMeasure);

                        switch (trybPomiaru) {
                            case -1: //nieoznaczony
                                showDialogCust("Błąd", "Rozpocznij proces naciskając przycisk START PROCES.");
                                break;
                            case 0: //normalny
                                if (!kogut.equals("1")) {
                                    kogut = "1";
                                    if (usbService != null) { // if UsbService was correctly binded, Send data
                                        usbService.write(kogut.getBytes());
                                        //customToast("USB binded!!");
                                        //customToast("usb service toString:  "+usbService.toString());
                                        //customToast("usb data:  "+data);
                                    }
                                }
                                trybNormalLiczPom++;
                                if (pomiarOk) {
                                    if (trybNormalLiczPom == iloscPunktow && !elemOk) {
                                        trybNormalLiczPom = 0;
                                        trybNormalLicz++;
                                        elemOk = true;
                                        if (trybNormalLicz == 3) {
                                            //trybNormalLicz = 0;
                                            //ustawTryb(7);
                                            stopProc = true;
                                        }
                                    } else if (trybNormalLiczPom == iloscPunktow && elemOk) {
                                        trybNormalLiczPom = 0;
                                    }
                                } else {
                                    elemOk = false;
                                    if (trybNormalLiczPom == iloscPunktow && !elemOk) {
                                        trybNormalLiczPom = 0;
                                        trybNormalLicz++;
                                        elemOk = true;
                                        if (trybNormalLicz == 3) {
                                            //trybNormalLicz = 0;
                                            //ustawTryb(7);
                                            stopProc = true;
                                        }
                                    } else if (trybNormalLiczPom == iloscPunktow && elemOk) {
                                        trybNormalLiczPom = 0;
                                    }

                                }
                                editText.requestFocus();
                                break;
                            case 1: //krytyczny
                                break;
                            case 2: //
                                break;
                            case 3:
                                break;
                            case 4:
                                trybKalibLiczMain++;
                                if (trybKalibLiczPom == 0) {
                                    elemOk = true;
                                }
                                trybKalibLiczPom++;
                                if (pomiarOk) {
                                    if (trybKalibLiczPom == iloscPunktow && elemOk) {
                                        trybKalibLiczPom = 0;
                                        trybKalibLicz++;
                                        if (trybKalibLicz == 10) {
                                            kalibEnd = true;
                                            trybKalibLicz = 0;
                                            trybKalibLiczMain = 0;
                                            elemOk = true;
                                            break;
                                        }
                                    } else if (trybKalibLiczPom == iloscPunktow && !elemOk) {
                                        trybKalibLiczPom = 0;
                                        elemOk = true;
                                    }
                                    textView2.setTextColor(Color.parseColor("#55bb55"));
                                } else {
                                    if (trybKalibLiczPom == iloscPunktow) {
                                        trybKalibLiczPom = 0;
                                    }
                                    elemOk = false;
                                    trybKalibLicz = 0;
                                }

                                editText.requestFocus();
                                break;
                            case 5:
                                break;
                            case 6:
                                editText.requestFocus();
                                break;
                            default:
                                editText.requestFocus();
                                break;
                        }

                        if (kalibEnd) {
                            ustawTryb(0);
                        }
                        if(trybKalibLiczMain==(50*iloscPunktow)&&!kalibEnd){
                            ustawTryb(7);
                            stopProc=true;
                        }else{
                            kalibEnd=false;
                        }
                        editText.requestFocus();
                        svx.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //replace this line to scroll up or down
                                svx.fullScroll(ScrollView.FOCUS_DOWN);
                                editText.requestFocus();
                            }
                        }, 100L);
                    } else {
                        progressDialog.dismiss();
                        showDialogCust("Błąd", "Wystąpił problem z komunikacją.\nSkontaktuj się z administratorem.");
                    }
                    //EditText editTextMain = (EditText) findViewById(R.id.editText1);
                    //editTextMain.setClickable(true);
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
                    currentLog = tvLogged.getText().toString();
                    String newLog = intent.getStringExtra("nfcTag");
                    //tvLogged.setText(intent.getStringExtra("nfcTag"));
                    System.out.println(newLog);
                    if(!newLog.equals("uberadmin")) {
                        if (!currentLog.equals(newLog)) {
                            if (currentLog.equals("----")) {
                                if (!newLog.contains("KNT")) {
                                    zaloguj(newLog, true,"");
                                } else if (newLog.contains("KNT") && !started) {
                                    unregisterReceiver(breceive);
                                    showDialogCust("Uwaga", "Nie rozpoczęto jeszcze procesu.\nPrzejście w tryb kontroli niemożliwe.", 1);
                                } else if (newLog.contains("KNT") && started) {
                                    zaloguj(newLog, true,"");
                                }
                            } else {
                                if (currentLog.contains("KNT")) {
                                    if (newLog.contains("KNT")) {
                                        unregisterReceiver(breceive);
                                        showDialogCustYN("Obecnie zalogowany jest: " + currentLog + " w trybie kontroli.\nPrzelogować na: " + newLog + " w trybie kontroli?", 5);
                                    } else {
                                        customToast("Zalogowanie niemożliwe w trakcie kontroli!");
                                    }
                                } else {
                                    if (newLog.contains("KNT")) {
                                        if (started) {
                                            savedLogin = currentLog;
                                            unregisterReceiver(breceive);
                                            showDialogCustYN("Obecnie zalogowany jest: " + currentLog + ".\nPrzelogować na: " + newLog + " w trybie kontroli?", 9);

                                        } else {
                                            unregisterReceiver(breceive);
                                            showDialogCust("Uwaga", "Nie rozpoczęto jeszcze procesu.\nPrzejście w tryb kontroli niemożliwe.", 1);
                                        }
                                    } else {
                                        unregisterReceiver(breceive);
                                        showDialogCustYN("Obecnie zalogowany jest: " + currentLog + ".\nPrzelogować na: " + newLog + "?", 8);
                                    }

                                }
                            }
                        } else {
                            customToast("Jesteś już zalogowany");
                        }
                    }else{
                        launchApp("com.sand.airdroid");
                    }

                } else if (intent.getAction().equals("akcja7")) {
                    blinkingAlert();
                    try{
                        cdt.cancel();

                    }catch(Exception e){

                    }

                } else if (intent.getAction().equals("akcja8")) {
                    if (intent.getIntExtra("errortype", 1) == 0) {
                        showDialogCust("Błąd!", "Nie można zapisać obrazu.\nSprawdź uprawnienia aplikacji w ustawieniach systemu.");
                    } else if (intent.getIntExtra("errortype", 1) == 1) {
                        showDialogCust("Błąd!", "Nie można pobrać obrazu.\nSprawdź połączenie sieciowe.");
                    }
                } else if (intent.getAction().equals("akcja9")) {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Logowanie...");
                    progressDialog.show();
                } else if (intent.getAction().equals("akcja10")) {
                    progressDialog.dismiss();
                    String tag = intent.getStringExtra("tagNfc");
                    int res = intent.getIntExtra("result", -1);
                    TextView tvLogged = (TextView) findViewById(R.id.textView6);
                    if (res == 0) {
                        Button btnlog = (Button) findViewById(R.id.zalogbtn);
                        Button btnwylog = (Button) findViewById(R.id.wylogbtn);
                        prevLogin=tag;
                        if (tag.contains("KNT")) {
                            tvLogged.setText(tag);
                            if (comm) {
                                unregisterReceiver(breceive);
                                showDialogCust("Login", "Zalogowałeś się jako " + tag + "\n\n         TRYB KONTROLI", 1);

                            }
                            savedTryb = trybPomiaru;
                            ustawTryb(6);
                            kogutSaved = kogut;
                            if (!kogut.equals("4")) {
                                kogut = "4";
                                if (usbService != null) { // if UsbService was correctly binded, Send data
                                    usbService.write(kogut.getBytes());
                                    //customToast("USB binded!!");
                                    //customToast("usb service toString:  "+usbService.toString());
                                    //customToast("usb data:  "+data);
                                }
                            }
                            logged = true;
                            btnlog.setVisibility(View.INVISIBLE);
                            btnwylog.setVisibility(View.VISIBLE);

                        } else {
                            tvLogged.setText(tag);
                            if (comm) {
                                unregisterReceiver(breceive);
                                showDialogCust("Login", "Zalogowałeś się jako " + tag, 1);

                            }
                            logged = true;
                            btnlog.setVisibility(View.INVISIBLE);
                            btnwylog.setVisibility(View.VISIBLE);

                        }
                    } else if (res == 1) {
                        unregisterReceiver(breceive);
                        showDialogCust("Odmowa", "Nie masz uprawnień do zalogowania się na tym urządzeniu.", 1);
                    } else if (res == 2) {
                        unregisterReceiver(breceive);
                        showDialogCust("Błąd", "Użytkownik " + tag + " nie istnieje w bazie.", 1);
                    } else if (res == 3) {
                        unregisterReceiver(breceive);
                        showDialogCust("Błąd", "Problem z potwierdzeniem uprawnień.\nSprawdź połączenie sieciowe i VPN.", 1);
                    } else if (res == 4) {
                        unregisterReceiver(breceive);
                        showDialogCust("Błąd", "Nieprawidłowy PIN.", 1);
                    }
                } else if (intent.getAction().equals("akcja11")) {
                    if (!kogut.equals("2")) {
                        kogut = "2";
                        if (usbService != null) { // if UsbService was correctly binded, Send data
                            usbService.write(kogut.getBytes());
                            //customToast("USB binded!!");
                            //customToast("usb service toString:  "+usbService.toString());
                            //customToast("usb data:  "+data);
                        }
                    }
                } else if (intent.getAction().equals("akcja12")) {
                    if (!kogut.equals("3")) {
                        kogut = "3";
                        if (usbService != null) { // if UsbService was correctly binded, Send data
                            usbService.write(kogut.getBytes());
                            //customToast("USB binded!!");
                            //customToast("usb service toString:  "+usbService.toString());
                            //customToast("usb data:  "+data);
                        }
                    }
                } else if (intent.getAction().equals("akcja13")) {
                    customToast("Brak obrazu");
                } else if (intent.getAction().equals("akcja14")) {
                    TextView datetv = (TextView) findViewById(R.id.datatv);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("dd / MM / yyyy ");
                    datetv.setText("Data:   " + mdformat.format(calendar.getTime()));
                } else if (intent.getAction().equals("akcja15")) {
                    zaloguj(intent.getStringExtra("login"),true,intent.getStringExtra("pin"));
                }
            }
        }


    };

    protected void launchApp(String packageName) {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(
                packageName);
        if (mIntent != null) {
            try {
                startActivity(mIntent);
            } catch (ActivityNotFoundException err) {
                Toast t = Toast.makeText(getApplicationContext(),
                        "Nie ma takiej aplikacji", Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }

    private void blinkingAlert() {
        // btext = new BlinkingText(this);
        TextView tvblink = (TextView) findViewById(R.id.textView9);
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(700); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        tvblink.setVisibility(View.VISIBLE);
        tvblink.startAnimation(anim);

    }

    private void blinkingAlertStop() {
        // btext = new BlinkingText(this);
        TextView tvblink = (TextView) findViewById(R.id.textView11);
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(700); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        tvblink.setVisibility(View.VISIBLE);
        tvblink.startAnimation(anim);
    }

    private void blinkingControl() {
        TextView trybText = (TextView) findViewById(R.id.textView3);
        TextView tvpozlicz = (TextView) findViewById(R.id.tvpoz);
        TextView tvpoz = (TextView) findViewById(R.id.textView4);
        Button btnCtrlEnd = (Button) findViewById(R.id.btnControl);
        trybText.setText("TRYB KONTROLI");
        trybText.setTextColor(Color.RED);
        tvpozlicz.setVisibility(View.GONE);
        tvpoz.setVisibility(View.GONE);
        btnCtrlEnd.setVisibility(View.VISIBLE);
        Animation anim = new AlphaAnimation(0.1f, 1.0f);
        anim.setDuration(900); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        trybText.setVisibility(View.VISIBLE);
        trybText.startAnimation(anim);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TextView tvblink = (TextView) findViewById(R.id.textView9);
        if (tvblink.getVisibility() == View.VISIBLE) {
            tvblink.clearAnimation();
            tvblink.setVisibility(View.GONE);
        }
        return super.onTouchEvent(event);
    }

    private void koniecpomiarow() {
        final TextView tvloggedp = (TextView) findViewById(R.id.textView6);
        if (stopProc) {
            EditText editText = (EditText) findViewById(R.id.editText1);
            liczSesja = 0;
            showDialogCust("Uwaga", "Wykonałeś wszystkie pomiary. \nWykryto błędne elementy w ilości: "+trybNormalLicz+"\nZatrzymaj proces!");
            editText.setText("");
            TextView tvpr = (TextView) findViewById(R.id.tvpoz);
            tvpr.setText(String.valueOf((Integer.parseInt(strOfInd[choosenInd][6]) * 10)));
            trybNormalLicz=0;
            blinkingAlertStop();
            editText.setEnabled(false);
        } else {
            Button btnlog = (Button) findViewById(R.id.zalogbtn);
            Button btnwylog = (Button) findViewById(R.id.wylogbtn);
            EditText editText = (EditText) findViewById(R.id.editText1);
            liczSesja = 0;
            trybNormalLicz=0;
            trybNormalLiczPom=0;
            showDialogCust("Uwaga", "Wykonałeś wszystkie pomiary.");
            editText.setText("");
            TextView tvpr = (TextView) findViewById(R.id.tvpoz);
            tvpr.setText(String.valueOf((Integer.parseInt(strOfInd[choosenInd][6]) * 10)));
            cdt = new CountDown(MILLISMAIN, 1000, this);
            cdt.start();
            tvloggedp.setText("----");
            logged = false;
            btnlog.setVisibility(View.VISIBLE);
            btnwylog.setVisibility(View.INVISIBLE);

        }

    }

    private void stopProces(String am) {
        TextView tvpozost = (TextView) findViewById(R.id.tvpoz);
        Button btnStop = (Button) findViewById(R.id.stopprocbtn);
        Button btnStart = (Button) findViewById(R.id.startprocbtn);
        TextView indtv = (TextView) findViewById(R.id.indextv);
        EditText edittext = (EditText) findViewById(R.id.editText1);
        Button indbtn = (Button) findViewById(R.id.zmianaindbtn);
        final TextView wym = (TextView) findViewById(R.id.wymiartv);
        final TextView tplus = (TextView) findViewById(R.id.tplustv);
        final TextView tminus = (TextView) findViewById(R.id.tminustv);
        final TextView textView8 = (TextView) findViewById(R.id.textView8);
        final TextView textView5 = (TextView) findViewById(R.id.textView5);
        final TextView textViewLogged = (TextView) findViewById(R.id.textView6);
        TextView tvblink = (TextView) findViewById(R.id.textView11);
        TextView tvblinkWykonaj = (TextView) findViewById(R.id.textView9);
        if (logged) {
            if (textViewLogged.getText().toString().contains("KNT")) {
                Button btnlog = (Button) findViewById(R.id.zalogbtn);
                Button btnwylog = (Button) findViewById(R.id.wylogbtn);
                if (!savedLogin.equals("----")) {

                    textViewLogged.setText(savedLogin);
                    savedLogin = "----";
                    logged = true;
                    btnlog.setVisibility(View.INVISIBLE);
                    btnwylog.setVisibility(View.VISIBLE);
                } else {
                    textViewLogged.setText("----");
                    logged = false;
                    btnlog.setVisibility(View.VISIBLE);
                    btnwylog.setVisibility(View.INVISIBLE);
                }
                if (tvblink.getVisibility() == View.VISIBLE) {
                    tvblink.clearAnimation();
                    tvblink.setVisibility(View.GONE);
                }
                if (tvblinkWykonaj.getVisibility() == View.VISIBLE) {
                    tvblinkWykonaj.clearAnimation();
                    tvblinkWykonaj.setVisibility(View.GONE);
                }
                started = false;
                ustawTryb(0);
                indbtn.setEnabled(true);
                indtv.setClickable(true);
                TextView textView2 = (TextView) findViewById(R.id.pomiartv);
                textView2.setText("0.0");
                //wym.setText("-");
                //tplus.setText("-");
                //tminus.setText("-");
                //tvpozost.setText("");
                textView2.setTextColor(Color.parseColor("#000000"));
                licznikPomiarowGlowny = 1;
                liczProb = 1;
                liczProbKalib = 1;
                liczProbKont = 1;
                licznikPomiarowTmp1 = 1;
                trybKalibLicz = 0;
                trybKalibLiczPom = 0;
                trybPomiaru = -1;
                trybKalibLiczMain = 0;
                liczSesja = 0;
                trybNormalLiczPom = 0;
                trybNormalLicz = 0;
                try {
                    cdt.cancel();

                } catch (Exception e) {

                }
                textView8.setText("00:00:00");
                textView5.setTextColor(Color.RED);
                textView8.setTextColor(Color.RED);
                textView8.setVisibility(View.INVISIBLE);
                textView5.setText("PROCES ZATRZYMANY");
                textView5.setX(730);
                btnStart.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.INVISIBLE);
                tvpozost.setText(String.valueOf(Integer.parseInt(strOfInd[choosenInd][6]) * 10));
                edittext.setEnabled(true);
            } else {
                if (am.equals("auto")) {
                    if (tvblink.getVisibility() == View.VISIBLE) {
                        tvblink.clearAnimation();
                        tvblink.setVisibility(View.GONE);
                    }
                    if (tvblinkWykonaj.getVisibility() == View.VISIBLE) {
                        tvblinkWykonaj.clearAnimation();
                        tvblinkWykonaj.setVisibility(View.GONE);
                    }

                    if (started) {
                        if (stopProc) {
                            //edittext.setEnabled(true);
                            started = false;
                            ustawTryb(0);
                            indbtn.setEnabled(true);
                            indtv.setClickable(true);
                            TextView textView2 = (TextView) findViewById(R.id.pomiartv);
                            textView2.setText("0.0");
                            //wym.setText("-");
                            //tplus.setText("-");
                            //tminus.setText("-");
                            //tvpozost.setText("");
                            textView2.setTextColor(Color.parseColor("#000000"));
                            licznikPomiarowGlowny = 1;
                            liczProb = 1;
                            liczProbKalib = 1;
                            liczProbKont = 1;
                            licznikPomiarowTmp1 = 1;
                            trybKalibLicz = 0;
                            trybKalibLiczPom = 0;
                            trybKalibLiczMain = 0;
                            liczSesja = 0;
                            trybNormalLiczPom = 0;
                            trybNormalLicz = 0;
                            tvpozost.setText(String.valueOf(Integer.parseInt(strOfInd[choosenInd][6]) * 10));

                            trybPomiaru = -1;
                            try {
                                cdt.cancel();

                            } catch (Exception e) {

                            }

                            textView8.setText("00:00:00");
                            textView5.setTextColor(Color.RED);
                            textView8.setTextColor(Color.RED);
                            textView8.setVisibility(View.INVISIBLE);
                            textView5.setText("PROCES ZATRZYMANY");
                            textView5.setX(730);
                            btnStart.setVisibility(View.VISIBLE);
                            btnStop.setVisibility(View.INVISIBLE);
                            stopProc = false;
                            edittext.setEnabled(true);
                        } else {
                            showDialogCustYN("Zakończyc proces dla indeksu " + indtv.getText().toString() + "?", 1);
                        }
                    } else {
                        customToast("Nie rozpoczęto jeszcze procesu");
                    }
                    edittext.requestFocus();
                    //new BlinkingText(this);

                } else if (am.equals("manual")) {
                    if (tvblink.getVisibility() == View.VISIBLE) {
                        tvblink.clearAnimation();
                        tvblink.setVisibility(View.GONE);
                    }
                    if (tvblinkWykonaj.getVisibility() == View.VISIBLE) {
                        tvblinkWykonaj.clearAnimation();
                        tvblinkWykonaj.setVisibility(View.GONE);
                    }
                    started = false;
                    ustawTryb(0);
                    indbtn.setEnabled(true);
                    indtv.setClickable(true);
                    TextView textView2 = (TextView) findViewById(R.id.pomiartv);
                    textView2.setText("0.0");
                    //wym.setText("-");
                    //tplus.setText("-");
                    //tminus.setText("-");
                    //tvpozost.setText("");
                    textView2.setTextColor(Color.parseColor("#000000"));
                    licznikPomiarowGlowny = 1;
                    liczProb = 1;
                    liczProbKalib = 1;
                    liczProbKont = 1;
                    licznikPomiarowTmp1 = 1;
                    trybKalibLicz = 0;
                    trybKalibLiczPom = 0;
                    trybPomiaru = -1;
                    trybKalibLiczMain = 0;
                    liczSesja = 0;
                    trybNormalLiczPom = 0;
                    trybNormalLicz = 0;
                    try {
                        cdt.cancel();

                    } catch (Exception e) {

                    }
                    textView8.setText("00:00:00");
                    textView5.setTextColor(Color.RED);
                    textView8.setTextColor(Color.RED);
                    textView8.setVisibility(View.INVISIBLE);
                    textView5.setText("PROCES ZATRZYMANY");
                    textView5.setX(730);
                    btnStart.setVisibility(View.VISIBLE);
                    btnStop.setVisibility(View.INVISIBLE);
                    tvpozost.setText(String.valueOf(Integer.parseInt(strOfInd[choosenInd][6]) * 10));
                    edittext.setEnabled(true);
                }
            }
        } else {
            showDialogCust("Błąd", "Zaloguj się");
        }

    }

    //obsluga USB
    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    break;
                case UsbService.CTS_CHANGE:
                    //Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    // Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    //Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    //Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    ///Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    //Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    // Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void scheduleRun(){
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("rafal.activebreciver.CUSTOM_INTENT");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
    }

}