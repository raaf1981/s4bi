package com.example.rafalzaborowski.suwmiarkiromb;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Order extends DialogFragment{
    ;
    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0;
    private ImageButton buttonBack;
    Context context;
    InputStreamReader zlecenia;
    String readJsonString;
    JSONArray jArr;
    private String[][] strOfZlec;
    String[] zleceniaStr;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.TOP);
        getDialog().getWindow().setLayout(1300, 770);

    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mainview = inflater.inflate(R.layout.order_layout, null);
        builder.setView(mainview);
        super.onCreate(savedInstanceState);
        pobierzZlecenia();
        zleceniaStr = new String[strOfZlec.length];
        for (int i=1;i<strOfZlec.length;i++){
            zleceniaStr[i] = strOfZlec[strOfZlec.length-i][1] + "   |   " + strOfZlec[strOfZlec.length-i][1];
            if (i==10){
                break;
            }
        }

        final ListView lv = (ListView) mainview.findViewById(R.id.lvorders);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_list2, zleceniaStr);
        lv.setAdapter(adapter);

        button1 = (Button) mainview.findViewById(R.id.button1);
        button2 = (Button) mainview.findViewById(R.id.button2);
        button3 = (Button) mainview.findViewById(R.id.button3);
        button4 = (Button) mainview.findViewById(R.id.button4);
        button5 = (Button) mainview.findViewById(R.id.button5);
        button6 = (Button) mainview.findViewById(R.id.button6);
        button7 = (Button) mainview.findViewById(R.id.button7);
        button8 = (Button) mainview.findViewById(R.id.button8);
        button9 = (Button) mainview.findViewById(R.id.button9);
        button0 = (Button) mainview.findViewById(R.id.button0);
        buttonBack = (ImageButton) mainview.findViewById(R.id.buttonBack);
        TextView tvmustnumer = (TextView) mainview.findViewById(R.id.tvMustnumer);
        TextView tvmustilosc = (TextView) mainview.findViewById(R.id.tvMustilosc);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustnumer.setVisibility(View.INVISIBLE);
                tvmustilosc.setVisibility(View.INVISIBLE);
                if (((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText("1");
                } else {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString() + "1");
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustnumer.setVisibility(View.INVISIBLE);
                tvmustilosc.setVisibility(View.INVISIBLE);
                if (((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText("2");
                } else {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString() + "2");
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustnumer.setVisibility(View.INVISIBLE);
                tvmustilosc.setVisibility(View.INVISIBLE);
                if (((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText("3");
                } else {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString() + "3");
                }
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustnumer.setVisibility(View.INVISIBLE);
                tvmustilosc.setVisibility(View.INVISIBLE);
                if (((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText("4");
                } else {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString() + "4");
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustnumer.setVisibility(View.INVISIBLE);
                tvmustilosc.setVisibility(View.INVISIBLE);
                if (((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText("5");
                } else {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString() + "5");
                }
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustnumer.setVisibility(View.INVISIBLE);
                tvmustilosc.setVisibility(View.INVISIBLE);
                if (((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText("6");
                } else {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString() + "6");
                }
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustnumer.setVisibility(View.INVISIBLE);
                tvmustilosc.setVisibility(View.INVISIBLE);
                if (((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText("7");
                } else {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString() + "7");
                }
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustnumer.setVisibility(View.INVISIBLE);
                tvmustilosc.setVisibility(View.INVISIBLE);
                if (((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText("8");
                } else {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString() + "8");
                }
            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustnumer.setVisibility(View.INVISIBLE);
                tvmustilosc.setVisibility(View.INVISIBLE);
                if (((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText("9");
                } else {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString() + "9");
                }
            }
        });
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustnumer.setVisibility(View.INVISIBLE);
                tvmustilosc.setVisibility(View.INVISIBLE);
                if (((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText("0");
                } else {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString() + "0");
                }
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((EditText) (getDialog()).getCurrentFocus()).getText().toString().equals("")) {
                    ((EditText) (getDialog()).getCurrentFocus()).setText(((EditText) (getDialog()).getCurrentFocus()).getText().toString().subSequence(0, ((EditText) (getDialog()).getCurrentFocus()).getText().toString().length() - 1));
                }
            }
        });

        Button buttonOk = (Button) mainview.findViewById(R.id.btnOk);
        Button buttonAnul = (Button) mainview.findViewById(R.id.btnAn);
        EditText etnrzlec = (EditText) mainview.findViewById(R.id.etNrzlec);
        EditText etilosc = (EditText) mainview.findViewById(R.id.etIlosc);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!etnrzlec.getText().toString().equals("") && !etilosc.getText().toString().equals("")){
                    Intent i = new Intent("akcja16");
                    i.putExtra("nrzlec", "W"+etnrzlec.getText().toString());
                    i.putExtra("ilosc", etilosc.getText().toString());
                    context.sendBroadcast(i);
                    dismiss();
                }else if(etnrzlec.getText().toString().equals("") && etilosc.getText().toString().equals("")){
                    tvmustnumer.setVisibility(View.VISIBLE);
                    tvmustilosc.setVisibility(View.VISIBLE);
                }else if(!etnrzlec.getText().toString().equals("") && etilosc.getText().toString().equals("")){
                    tvmustnumer.setVisibility(View.INVISIBLE);
                    tvmustilosc.setVisibility(View.VISIBLE);
                }else if(etnrzlec.getText().toString().equals("") && !etilosc.getText().toString().equals("")){
                    tvmustnumer.setVisibility(View.VISIBLE);
                    tvmustilosc.setVisibility(View.INVISIBLE);
                }
            }
        });
        buttonAnul.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }

    private void pobierzZlecenia(){
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                zlecenia = HttpHandler.doGet();
                try {
                    readJsonString = readFromStream(zlecenia);
                    //System.out.println("printuję zlecenia:  " + readJsonString);
                    jArr = new JSONArray(readJsonString);
                    strOfZlec = new String[jArr.length()][3];
                    //JSONArray jArr = jObj.getJSONArray("indeks");
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject obj = jArr.getJSONObject(i);
                        strOfZlec[i][0] = obj.getString("idzlec");
                        strOfZlec[i][1] = obj.getString("nrzlec");
                        strOfZlec[i][2] = obj.getString("ilosc");
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

}


