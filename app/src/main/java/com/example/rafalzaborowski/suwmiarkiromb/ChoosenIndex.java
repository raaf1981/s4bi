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
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Arrays;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ChoosenIndex extends DialogFragment implements AdapterView.OnItemClickListener{
    String[][] indIncome = MainActivity.strOfInd;
    String[] indexes;
    String[] indexesTMP;
    public static int choosenInd;
    String tplus, tminus;
    private PopupWindow mPopupWindow;
    private Button button1,button2,button3,button4,button5,button6,button7,button8,button9,button10,button11,buttonP,buttonX;
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
                buttonP = (Button) customView.findViewById(R.id.buttonP);
                buttonX = (Button) customView.findViewById(R.id.buttonX);
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
                buttonP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"P",true);
                        }else{
                            sv.setQuery("P",true);
                        }
                    }
                });
                buttonX.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sv.getQuery().equals("")){
                            sv.setQuery(sv.getQuery()+"X",true);
                        }else{
                            sv.setQuery("X",true);
                        }
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
        TextView tvMain = (TextView) getActivity().findViewById(R.id.indextv);
        String data = (String) parent.getItemAtPosition(position);
        choosenInd = Arrays.asList(indexes).indexOf(data);

        if(!tvMain.getText().equals(indexesTMP[choosenInd])) {


            dismiss();

            TextView tvtest = (TextView) getActivity().findViewById(R.id.test);
            TextView tvwym = (TextView) getActivity().findViewById(R.id.wymiartv);
            TextView tvtolp = (TextView) getActivity().findViewById(R.id.tplustv);
            TextView tvtolm = (TextView) getActivity().findViewById(R.id.tminustv);
            TableLayout tabLay1 = (TableLayout) getActivity().findViewById(R.id.tabLay);

            tvMain.setText(indexesTMP[choosenInd]);
            tvtest.setText(String.valueOf(choosenInd));
            tvwym.setText(indIncome[choosenInd][3]);
            if (indIncome[choosenInd][4] == "null") {
                tvtolp.setText("0.0");
            } else {
                tvtolp.setText(indIncome[choosenInd][4]);

            }
            if (indIncome[choosenInd][5] == "null") {
                tvtolm.setText("0.0");
            } else {
                tvtolm.setText(indIncome[choosenInd][5]);
            }
            MainActivity.choosenInd = choosenInd;
            MainActivity.indexchoosen = true;
            EditText editText = (EditText) getActivity().findViewById(R.id.editText1);
            editText.requestFocus();
            TextView tvpoz = (TextView) getActivity().findViewById(R.id.tvpoz);
            tvpoz.setText(String.valueOf(Integer.parseInt(indIncome[choosenInd][6]) * 10));
            tabLay1.removeAllViews();
            downloadPDF();

        }else{
            dismiss();
        }

    }

    private void downloadPDF(){
        try{
            MainActivity activity = (MainActivity) getActivity();
            if(indIncome[choosenInd][1].equals("null")){
                Intent i = new Intent("akcja13");
                Context context = getActivity().getBaseContext();
                context.sendBroadcast(i);
                ImageView iv = (ImageView) getActivity().findViewById(R.id.image);
                iv.setPadding(50,130,50,130);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setImageResource(R.drawable.logo_romb_biale);
            }else {
                new DownloadTask(Integer.parseInt(indIncome[choosenInd][1]), activity);
            }
        }catch(Exception e){
            e.printStackTrace();
            ImageView iv = (ImageView) getActivity().findViewById(R.id.image);
            iv.setPadding(50,130,50,130);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(R.drawable.logo_romb_biale);
        }


    }


}


