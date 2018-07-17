package com.example.rafalzaborowski.suwmiarkiromb;


import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import uk.co.senab.photoview.PhotoViewAttacher;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ChoosenIndex extends DialogFragment implements AdapterView.OnItemClickListener{
    String[][] indIncome = MainActivity.strOfInd;
    String[] indexes;
    String[] indexesTMP;
    public static int choosenInd;
    String tplus, tminus;
    private PopupWindow mPopupWindow;
    private Button button1,button2,button3,button4,button5,button6,button7,button8,button9,button10,button11;
    private ImageButton button12;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.TOP);
        getDialog().getWindow().setLayout(900, 770);

    }

    @Override
    public void onResume() {
        super.onResume();
        //getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        indexes = new String[indIncome.length];
        indexesTMP = new String[indIncome.length];
        for (int i=0;i<indIncome.length;i++){
            indexesTMP[i] = indIncome[i][2];
            if(indIncome[i][4] == "null"){
                tplus = "0.0";
            }else{
                tplus = indIncome[i][4];
            }
            if(indIncome[i][5] == "null"){
                tminus = "0.0";
            }else{
                tminus = indIncome[i][5];
            }
            indexes[i] = "Indeks: "+indIncome[i][2] + "   |   Wymiar: " +indIncome[i][3]+"   |  +"+tplus+"   |  -"+tminus;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.chooseind, null);
        builder.setView(view);
        super.onCreate(savedInstanceState);
        final ListView lv = (ListView) view.findViewById(R.id.listView1);
        final SearchView sv = (SearchView) view.findViewById(R.id.searchView1);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_list1, indexes);
        lv.setAdapter(adapter);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                adapter.getFilter().filter(text);
                return false;
            }
        });

        //final TextView tvtest = (TextView) view.findViewById(R.id.textView);
        final RelativeLayout mRelativeLayout = (RelativeLayout) view.findViewById(R.id.rl);
        //final AbsoluteLayout mRelativeLayout = (AbsoluteLayout) getActivity().findViewById(R.id.rl2);
        sv.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.custom_layout,null);
                ViewGroup.MarginLayoutParams mlplv = (ViewGroup.MarginLayoutParams) sv.getLayoutParams();
                mlplv.setMargins(0,180,0,0);
                sv.setLayoutParams(mlplv);
                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                mPopupWindow.setElevation(5.0f);
                button1 = (Button) customView.findViewById(R.id.button1);
                button2 = (Button) customView.findViewById(R.id.button2);
                button3 = (Button) customView.findViewById(R.id.button3);
                button4 = (Button) customView.findViewById(R.id.button4);
                button5 = (Button) customView.findViewById(R.id.button5);
                button6 = (Button) customView.findViewById(R.id.button6);
                button7 = (Button) customView.findViewById(R.id.button7);
                button8 = (Button) customView.findViewById(R.id.button8);
                button9 = (Button) customView.findViewById(R.id.button9);
                button10 = (Button) customView.findViewById(R.id.button10);
                button11 = (Button) customView.findViewById(R.id.button11);
                button12 = (ImageButton) customView.findViewById(R.id.button12);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"1",true);
                        }else{
                            sv.setQuery("1",true);
                        }

                        //mPopupWindow.dismiss();
                    }
                });
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"2",true);
                        }else{
                            sv.setQuery("2",true);
                        }
                    }
                });
                button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"3",true);
                        }else{
                            sv.setQuery("3",true);
                        };
                    }
                });
                button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"4",true);
                        }else{
                            sv.setQuery("4",true);
                        }
                    }
                });
                button5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"5",true);
                        }else{
                            sv.setQuery("5",true);
                        }
                    }
                });
                button6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"6",true);
                        }else{
                            sv.setQuery("6",true);
                        }
                    }
                });
                button7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"7",true);
                        }else{
                            sv.setQuery("7",true);
                        }
                    }
                });
                button8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"8",true);
                        }else{
                            sv.setQuery("8",true);
                        }
                    }
                });
                button9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"9",true);
                        }else{
                            sv.setQuery("9",true);
                        }
                    }
                });
                button10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"-",true);
                        }else{
                            sv.setQuery("-",true);
                        }
                    }
                });
                button11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"0",true);
                        }else{
                            sv.setQuery("0",true);
                        }
                    }
                });
                button12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(sv.getQuery().length()>0) {
                            sv.setQuery(sv.getQuery().subSequence(0, sv.getQuery().length() - 1), true);
                        }
                        //mPopupWindow.dismiss();
                    }
                });
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.NO_GRAVITY, 180,80);
            }
        });

        lv.setOnItemClickListener(this);

        return builder.create();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mPopupWindow!=null){
            mPopupWindow.dismiss();
        }
        String data = (String) parent.getItemAtPosition(position);
        dismiss();
        TextView tvMain = (TextView) getActivity().findViewById(R.id.indextv);
        TextView tvtest = (TextView) getActivity().findViewById(R.id.test);
        TextView tvwym = (TextView) getActivity().findViewById(R.id.wymiartv);
        TextView tvtolp = (TextView) getActivity().findViewById(R.id.tplustv);
        TextView tvtolm = (TextView) getActivity().findViewById(R.id.tminustv);
        choosenInd = Arrays.asList(indexes).indexOf(data);
        tvMain.setText(indexesTMP[choosenInd]);
        tvtest.setText(String.valueOf(choosenInd));
        tvwym.setText(indIncome[choosenInd][3]);
        if (indIncome[choosenInd][4] == "null"){
            tvtolp.setText("0.0");
        }else{
            tvtolp.setText(indIncome[choosenInd][4]);

        }
        if (indIncome[choosenInd][5] == "null"){
            tvtolm.setText("0.0");
        }else{
            tvtolm.setText(indIncome[choosenInd][5]);
        }
        MainActivity.choosenInd=choosenInd;
        MainActivity.indexchoosen=true;
        EditText editText = (EditText) getActivity().findViewById(R.id.editText1);
        editText.requestFocus();
        TextView tvpoz = (TextView) getActivity().findViewById(R.id.tvpoz);
        tvpoz.setText(String.valueOf(Integer.parseInt(indIncome[choosenInd][6])*10));
        downloadPDF();

    }

    private void downloadPDF(){
        MainActivity activity = (MainActivity) getActivity();
        new DownloadTask(Integer.parseInt(indIncome[choosenInd][1]), activity);

    }


}


