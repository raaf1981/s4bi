package com.example.rafalzaborowski.suwmiarkiromb;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class LoginDialog extends DialogFragment{
    ;
    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0;
    private ImageButton buttonBack;
    Context context;

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
        View mainview = inflater.inflate(R.layout.logindialog, null);
        builder.setView(mainview);
        super.onCreate(savedInstanceState);
        RadioButton rb1 = (RadioButton) mainview.findViewById(R.id.rb1);
        RadioButton rb2 = (RadioButton) mainview.findViewById(R.id.rb2);
        TextView tvkntprc = (TextView) mainview.findViewById(R.id.tvprcknt);
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvkntprc.setText("PRC");
            }
        });

        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvkntprc.setText("KNT");
            }
        });

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
        TextView tvmustlog = (TextView) mainview.findViewById(R.id.tvlmustlog);
        TextView tvmustpin = (TextView) mainview.findViewById(R.id.tvmustpin);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvmustlog.setVisibility(View.INVISIBLE);
                tvmustpin.setVisibility(View.INVISIBLE);
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
                tvmustlog.setVisibility(View.INVISIBLE);
                tvmustpin.setVisibility(View.INVISIBLE);
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
                tvmustlog.setVisibility(View.INVISIBLE);
                tvmustpin.setVisibility(View.INVISIBLE);
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
                tvmustlog.setVisibility(View.INVISIBLE);
                tvmustpin.setVisibility(View.INVISIBLE);
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
                tvmustlog.setVisibility(View.INVISIBLE);
                tvmustpin.setVisibility(View.INVISIBLE);
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
                tvmustlog.setVisibility(View.INVISIBLE);
                tvmustpin.setVisibility(View.INVISIBLE);
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
                tvmustlog.setVisibility(View.INVISIBLE);
                tvmustpin.setVisibility(View.INVISIBLE);
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
                tvmustlog.setVisibility(View.INVISIBLE);
                tvmustpin.setVisibility(View.INVISIBLE);
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
                tvmustlog.setVisibility(View.INVISIBLE);
                tvmustpin.setVisibility(View.INVISIBLE);
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
                tvmustlog.setVisibility(View.INVISIBLE);
                tvmustpin.setVisibility(View.INVISIBLE);
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

        TextView tvprcknt = (TextView) mainview.findViewById(R.id.tvprcknt);

        Button buttonOk = (Button) mainview.findViewById(R.id.bOk);
        Button buttonAnul = (Button) mainview.findViewById(R.id.bAn);
        EditText etLogin = (EditText) mainview.findViewById(R.id.loginEd);
        EditText etPin = (EditText) mainview.findViewById(R.id.passEd);
        if(!MainActivity.prevLogin.equals("")){
            etLogin.setText(MainActivity.prevLogin.subSequence(3,MainActivity.prevLogin.length()));
            etLogin.setSelectAllOnFocus(true);
            //etLogin.setSelection(etLogin.getText().length());
        }
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!etLogin.getText().toString().equals("") && !etPin.getText().toString().equals("")){
                    Intent i = new Intent("akcja15");
                    i.putExtra("login", tvprcknt.getText().toString()+etLogin.getText().toString());
                    i.putExtra("pin", etPin.getText().toString());
                    context.sendBroadcast(i);
                    dismiss();
                }else if(etLogin.getText().toString().equals("") && etPin.getText().toString().equals("")){
                    tvmustlog.setVisibility(View.VISIBLE);
                    tvmustpin.setVisibility(View.VISIBLE);
                }else if(!etLogin.getText().toString().equals("") && etPin.getText().toString().equals("")){
                    tvmustlog.setVisibility(View.INVISIBLE);
                    tvmustpin.setVisibility(View.VISIBLE);
                }else if(etLogin.getText().toString().equals("") && !etPin.getText().toString().equals("")){
                    tvmustlog.setVisibility(View.VISIBLE);
                    tvmustpin.setVisibility(View.INVISIBLE);
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



}


