package com.example.rafalzaborowski.suwmiarkiromb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.nfc.NfcAdapter;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View.OnKeyListener;
import android.view.View;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity", MIEJSCE = "Pila";
    public static String[][] strOfInd;
    public static int choosenInd;
    InputStreamReader indeksy, odpowiedz;
    String readJsonString;
    JSONArray jArr;
    JSONObject jObj;
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
    private int licznikPomiarowGlowny = 1, trybKalibLicz = 0, trybKalibLiczPom = 0,trybNormalLicz = 0,trybNormalLiczPom = 0;
    private int liczProb = 1, indexPrev;
    private int licznikPomiarowTmp1 = 1;
    private boolean pomiarOk, timerStart;
    private String[] headerRow = {"ID", "Godzina", "Indeks", "Wymiar", "T+", "T-", "Pomiar", "Punkt", "Tryb"};
    private String[] trybRow = {"CYKL", "KRYT", "WRF", "AWR", "KLB", "INT"};
    private String pomiarAktualny;
    private String newIndexPost;
    private String[] lista = new String[]{"Piła 1", "Piła 2", "Piła 3", "Piła 4", "Piła 5", "Piła 6", "Piła 7", "Obróbka 1", "Obróbka 2","Obróbka 3"};
    private ProgressDialog progressDialog;
    CharSequence[] values = {"Zwykły", "Krytyczny", "Kosz", "Awaryjny"};
    private int trybPomiaru = -1;
    private boolean elemOk = true,started=false, firstmeasure=true, lastmeasure=false;
    public static boolean indexchoosen=false;
    FileNotFoundException exception1=null;


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
        filter.addAction("akcja7");
        filter.addAction("akcja8");
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

                if ((keyevent.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    TextView tvblink = (TextView) findViewById(R.id.textView9);
                    if (tvblink.getVisibility() == View.VISIBLE) {
                        tvblink.clearAnimation();
                        tvblink.setVisibility(View.GONE);
                    }
                    int iloscPunktow = Integer.parseInt(strOfInd[choosenInd][6]);
                    currentDate = new Date();
                    switch (trybPomiaru) {
                        case -1: //nieoznaczony
                            if(indexchoosen && !started){
                                showDialogCustYN("Rozpocząć proces?", 3);
                            }else if(){

                            }

                            break;
                        case 0: //normalny
                            if (!tvpozostal.getText().toString().equals("")) {
                                if (Integer.parseInt(tvpozostal.getText().toString()) > 0 && !btnStart1.isClickable()) {
                                    try {
                                        pomiarAktualny = editText.getText().toString();
                                        Double.parseDouble(pomiarAktualny);
                                        textView2.setText(editText.getText());
                                        editText.setText("");
                                        checkMeasure();
                                        trybNormalLiczPom++;
                                        if (pomiarOk) {
                                            if (trybNormalLiczPom == iloscPunktow && !elemOk) {
                                                trybNormalLiczPom = 0;
                                                trybNormalLicz++;
                                                elemOk = true;
                                                if (trybNormalLicz == 2) {
                                                    trybNormalLicz = 0;
                                                    showDialogCust("Uwaga!","Wykryto 2 błedne elementy!\nZatrzymaj proces.\nSprawdź wszystkie wyprodukowane elementy.");
                                                    //ustawTryb(2);
                                                }
                                            } else if (trybNormalLiczPom == iloscPunktow && elemOk) {
                                                trybNormalLiczPom = 0;
                                            }
                                            textView2.setTextColor(Color.parseColor("#55bb55"));
                                        } else {
                                            if (trybNormalLiczPom == iloscPunktow && !elemOk) {
                                                trybNormalLiczPom = 0;
                                                trybNormalLicz++;
                                                elemOk = true;
                                                if (trybNormalLicz == 2) {
                                                    trybNormalLicz = 0;
                                                    showDialogCust("Uwaga!","Wykryto 2 błedne elementy!\nZatrzymaj proces.\nSprawdź wszystkie wyprodukowane elementy.");
                                                    //ustawTryb(2);
                                                }
                                            } else if (trybNormalLiczPom == iloscPunktow && elemOk) {
                                                trybNormalLiczPom = 0;
                                            }
                                            elemOk = false;
                                            textView2.setTextColor(Color.RED);
                                        }
                                        newIndexPost = prepNewIndPost(textView2.getText().toString());
                                        zapiszWynik();
                                    } catch (NumberFormatException e) {
                                        showDialogCust("Błąd", "Błędny pomiar");
                                    }
                                    editText.requestFocus();
                                } else {
                                    showDialogCust("Uwaga", "Wykonałeś wszystkie pomiary");
                                }
                            } else {
                                showDialogCust("Błąd", "Nie wybrano indeksu");
                            }
                            return true;

                        //break;
                        case 1: //krytyczny
                            boolean end = false;
                            try {
                                pomiarAktualny = editText.getText().toString();
                                Double.parseDouble(pomiarAktualny);
                                textView2.setText(editText.getText());
                                editText.setText("");
                                checkMeasure();
                                trybKalibLiczPom++;
                                if (pomiarOk) {
                                    if (trybKalibLiczPom == iloscPunktow && elemOk) {
                                        trybKalibLiczPom = 0;
                                        trybKalibLicz++;
                                        if (trybKalibLicz == 3) {
                                            trybKalibLicz = 0;
                                            elemOk = true;
                                            end = true;
                                        }
                                    } else if (trybKalibLiczPom == iloscPunktow && !elemOk) {
                                        trybKalibLiczPom = 0;
                                    }
                                    textView2.setTextColor(Color.parseColor("#55bb55"));
                                } else {
                                    elemOk = false;
                                    textView2.setTextColor(Color.RED);
                                }
                                newIndexPost = prepNewIndPost(textView2.getText().toString());
                                zapiszWynik();
                                if (end) {
                                    ustawTryb(0);
                                }
                            } catch (NumberFormatException e) {
                                showDialogCust("Błąd", "Błędny pomiar");
                            }
                            editText.requestFocus();
                            break;
                        case 2: //
                            break;
                        case 3:
                            break;
                        case 4:
                            try {
                                pomiarAktualny = editText.getText().toString();
                                Double.parseDouble(pomiarAktualny);
                                textView2.setText(editText.getText());
                                editText.setText("");
                                checkMeasure();
                                if (trybKalibLiczPom == 0) {
                                    elemOk = true;
                                }
                                trybKalibLiczPom++;
                                if (pomiarOk) {
                                    if (trybKalibLiczPom == iloscPunktow && elemOk) {
                                        trybKalibLiczPom = 0;
                                        trybKalibLicz++;
                                        if (trybKalibLicz == 1) {
                                            trybKalibLicz = 0;
                                            elemOk = true;
                                            newIndexPost = prepNewIndPost(textView2.getText().toString());
                                            zapiszWynik();
                                            ustawTryb(0);
                                            return true;
                                            //break;
                                        }
                                    } else if (trybKalibLiczPom == iloscPunktow && !elemOk) {
                                        trybKalibLiczPom = 0;
                                    }
                                    textView2.setTextColor(Color.parseColor("#55bb55"));
                                } else {
                                    elemOk = false;
                                    trybKalibLicz = 0;
                                    textView2.setTextColor(Color.RED);
                                }
                                newIndexPost = prepNewIndPost(textView2.getText().toString());
                                zapiszWynik();
                            } catch (NumberFormatException e) {
                                showDialogCust("Błąd", "Błędny pomiar");
                            }

                            editText.requestFocus();
                            break;
                        default:
                            editText.requestFocus();
                            break;
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
        //spinner.setSelection(0);

    }

    public void testMet(View v) {
        TextView tvlogged = (TextView) findViewById(R.id.textView6);
        if (strOfInd == null) {
            showDialogCust("Błąd", "Nie można pobrać bazy indeksów.\nWystąpił problem z komunikacją.\nSkontaktuj się z administratorem.");
            pobierzIndeksy();
        } else {
            if (tvlogged.getText().toString().equals("----")) {
                showDialogCust("Błąd", "Zaloguj się!");
            } else {
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
        for (int i = 0; i < 9; i++) {
            TextView newTV = new TextView(this);
            newTV.setText(tabTV[i]);
            if (pomiarOk) {
                newTV.setTextColor(Color.parseColor("#55bb55"));
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
                        newTV.setPadding(49, 2, 17, 2);
                        break;
                    case 8:
                        newTV.setPadding(34, 2, 17, 2);
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
                        newTV.setPadding(16, 2, 17, 2);
                        break;
                    case 6:
                        newTV.setPadding(18, 2, 17, 2);
                        break;
                    case 7:
                        newTV.setPadding(44, 2, 17, 2);
                        break;
                    case 8:
                        newTV.setPadding(39, 2, 17, 2);
                        break;
                }
            } else {
                switch (i) {
                    case 0:
                        newTV.setPadding(10, 2, 17, 2);
                        break;
                    case 1:
                        newTV.setPadding(30, 2, 17, 2);
                        break;
                    case 2:
                        newTV.setPadding(21, 2, 17, 2);
                        break;
                    case 3:
                        newTV.setPadding(30, 2, 17, 2);
                        break;
                    case 4:
                        newTV.setPadding(38, 2, 17, 2);
                        break;
                    case 5:
                        newTV.setPadding(30, 2, 17, 2);
                        break;
                    case 6:
                        newTV.setPadding(30, 2, 17, 2);
                        break;
                    case 7:
                        newTV.setPadding(45, 2, 17, 2);
                        break;
                    case 8:
                        newTV.setPadding(40, 2, 17, 2);
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
        if (liczProb <= Integer.parseInt(strOfInd[choosenInd][6])) {
            newRowToAdd[7] = String.valueOf(liczProb);
            liczProb += 1;
        } else {
            liczProb = 1;
            newRowToAdd[7] = String.valueOf(liczProb);
            liczProb += 1;
        }
        newRowToAdd[8] = trybRow[trybPomiaru];
        if (liczProb > Integer.parseInt(strOfInd[choosenInd][6])) {
            licznikPomiarowTmp1 += 1;
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
        TextView tvloggedp = (TextView) findViewById(R.id.textView6);
        String postStr = "{\"ididx\":\"" + strOfInd[choosenInd][0] + "\",\"idelem\":\"" + String.valueOf(licznikPomiarowTmp1) + "\", \"wymiar\":\"" + pomiarAkt + "\", \"operator\":\"" + tvloggedp.getText().toString() + "\", \"data\":\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate) + "\", \"maszyna\":\"Piła1\",\"dataprocesu\":\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataprocesu) + "\"}";
        return postStr;
    }

    public void startclick(View v) {
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
        }else if(indexchoosen && started){
            customToast("Proces został już rozpoczęty");
        }else {
            if (trybPomiaru != 4) {
                ustawTryb(4);
            }

            textView5.setTextColor(Color.GREEN);
            textView8.setTextColor(Color.GREEN);
            indbtn.setEnabled(false);
            licznikPomiarowTmp1 = 1;
            dataprocesu = new Date();
            started=true;
        }

        //cdt.cancel();

    }

    public void stopclick(View v) {
        TextView tvpozost = (TextView) findViewById(R.id.tvpoz);
        Button btnStop = (Button) findViewById(R.id.stopprocbtn);
        Button btnStart = (Button) findViewById(R.id.startprocbtn);
        TextView indtxt = (TextView) findViewById(R.id.indextv);
        Button indbtn = (Button) findViewById(R.id.zmianaindbtn);
        final TextView wym = (TextView) findViewById(R.id.wymiartv);
        final TextView tplus = (TextView) findViewById(R.id.tplustv);
        final TextView tminus = (TextView) findViewById(R.id.tminustv);
        if (!tvpozost.getText().equals("")) {
            if (Integer.parseInt(tvpozost.getText().toString()) > 0) {
                showDialogCustYN("Nie wykonałeś wszystkich pomiarów\nPozostało " + tvpozost.getText().toString() + "\nCzy na pewno zakończyć proces?", 2);
            } else {
                btnStart.setClickable(true);
                indbtn.setEnabled(true);
                tabLay1 = (TableLayout) findViewById(R.id.tabLay);
                tabLay1.removeAllViews();
                TextView textView2 = (TextView) findViewById(R.id.pomiartv);
                textView2.setText("0.0");
                //wym.setText("-");
                //tplus.setText("-");
                //tminus.setText("-");
                tvpozost.setText("");
                textView2.setTextColor(Color.parseColor("#000000"));
                licznikPomiarowGlowny = 1;
                liczProb = 1;
                licznikPomiarowTmp1 = 1;
                showDialogCust("Uwaga", "Zakończono proces dla indeksu " + indtxt);
            }
        } else {
            customToast("Nie rozpoczęto jeszcze procesu");
        }
        //cdt.start();
        //new BlinkingText(this);
    }

    private void ustawTryb(int tryb) {
        TextView trybText = (TextView) findViewById(R.id.textView3);
        TextView tvpozlicz = (TextView) findViewById(R.id.tvpoz);
        TextView tvpoz = (TextView) findViewById(R.id.textView4);
        switch (tryb) {
            case -1: //nieustawiony
                break;
            case 0: //zwykły
                switch (trybPomiaru) {
                    case -1:
                        break;
                    case 0:
                        break;
                    case 1:
                        liczProb = 1;
                        showDialogCust("Uwaga", "Zmierzono 3 poprawne elementy w punkcie krytycznym\nSystem w trybie normalnym");
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        liczProb = 1;
                        showDialogCust("Uwaga", "Kalibracja zakończona poprawnie\nSystem w trybie normalnym");
                        cdt.start();
                        break;
                    default:
                        break;
                }
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
                trybPomiaru = 4;
                trybText.setText("Tryb pomiaru: KALIBRACJA");
                tvpoz.setVisibility(View.GONE);
                tvpozlicz.setVisibility(View.GONE);
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
            default:
                break;
        }
    }

    private void countDownProb() {
        if (trybPomiaru == 0) {
            TextView tvpr = (TextView) findViewById(R.id.tvpoz);
            int currentcd = Integer.parseInt(tvpr.getText().toString());

            if ((currentcd-1)==0){
                koniecpomiarow();
            }else{
                tvpr.setText(String.valueOf((Integer.parseInt(strOfInd[choosenInd][6]) * 10) - liczProb));
            }
        }
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
                            licznikPomiarowGlowny = 1;
                            liczProb = 1;
                            licznikPomiarowTmp1 = 1;
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

    public void wyloguj(View v) {
        TextView tvloggedp = (TextView) findViewById(R.id.textView6);
        if (!tvloggedp.getText().toString().equals("----")) {
            showDialogCustYN("Czy wylogować użytkownika " + tvloggedp.getText().toString() + "?", 4);
        } else {
            customToast("Nie jesteś zalogowany");
        }
    }

    private void zaloguj(String tagNfc) {
        String logstr = "{\"member_login\":\"" + tagNfc + "\",\"member_role\":\"" + MIEJSCE + "\"}";
        TextView tvLogged = (TextView) findViewById(R.id.textView6);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String stat = "", mes = "";
                odpowiedz = HttpHandler.doPostLogin(logstr);
                try {
                    readJsonString = readFromStream(odpowiedz);
                    System.out.println("printuję odpowiedz:  " + readJsonString);
                    jObj = new JSONObject(readJsonString);
                    //JSONArray jArr = jObj.getJSONArray("indeks");
                    stat = jObj.getString("status");
                    mes = jObj.getString("message");

                    if (stat.equals("true") && mes.equals("Login OK")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                tvLogged.setText(tagNfc);
                                showDialogCust("Login", "Zalogowałeś się jako " + tagNfc);
                            }
                        });
                    } else if (stat.equals("false") && mes.equals("Rola nie znaleziona")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                showDialogCust("Odmowa", "Nie masz uprawnień do zalogowania się na tym urządzeniu");
                            }
                        });
                    } else if (stat.equals("false") && mes.equals("User could not be found")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                showDialogCust("Błąd", "Użytkownik " + tagNfc + " nie istnieje w bazie");
                            }
                        });
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

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("akcja1")) {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Pobieranie obrazu...");
                    progressDialog.show();
                } else if (intent.getAction().equals("akcja2")) {
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
                    if (resp == 200) {
                        EditText editTextMain = (EditText) findViewById(R.id.editText1);
                        ScrollView svx = (ScrollView) findViewById(R.id.sv1);
                        newRowMeasure = prepareRow();
                        addTabRow(newRowMeasure);
                        countDownProb();
                        editTextMain.requestFocus();
                        svx.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //replace this line to scroll up or down
                                svx.fullScroll(ScrollView.FOCUS_DOWN);
                                editTextMain.requestFocus();
                            }
                        }, 100L);
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
                    //tvLogged.setText(intent.getStringExtra("nfcTag"));
                    if (tvLogged.getText().toString().equals("----")) {
                        zaloguj(intent.getStringExtra("nfcTag"));
                    }
                    //Toast.makeText(context, "NFC tag: "+intent.getStringExtra("nfcTag"), Toast.LENGTH_SHORT).show();
                } else if (intent.getAction().equals("akcja7")) {
                    blinkingAlert();
                }else if (intent.getAction().equals("akcja8")) {
                    if(intent.getIntExtra("errortype",1)==0){
                        showDialogCust("Błąd!","Nie można zapisać obrazu.\nSprawdź uprawnienia aplikacji w ustawieniach systemu.");
                    }else if(intent.getIntExtra("errortype",1)==1){
                        showDialogCust("Błąd!","Nie można pobrać obrazu.\nSprawdź połączenie sieciowe");
                    }
                }
            }
        }


    };

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TextView tvblink = (TextView) findViewById(R.id.textView9);
        if (tvblink.getVisibility() == View.VISIBLE) {
            tvblink.clearAnimation();
            tvblink.setVisibility(View.GONE);
        }
        return super.onTouchEvent(event);
    }

    private void koniecpomiarow(){

    }
}