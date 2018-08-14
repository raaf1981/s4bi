package com.example.rafalzaborowski.suwmiarkiromb;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginTask {
    Context context;
    String tagNfc;
    int nfcman;
    InputStreamReader odpowiedz;
    String readJsonString;
    JSONObject jObj;
    private int endResult = -1;


    public LoginTask(String tagNfc, Activity act, int nfcman) {
        this.context = (Context) act.getBaseContext();
        this.tagNfc = tagNfc;
        this.nfcman = nfcman;
        new LoggingTask().execute();
    }


    private class LoggingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Intent i = new Intent("akcja9");
            context.sendBroadcast(i);
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void result) {

            Intent i = new Intent("akcja10");
            i.putExtra("tagNfc", String.valueOf(tagNfc));
            i.putExtra("result", endResult);
            context.sendBroadcast(i);
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nfcman == 0) {
                String logstr;
                if (tagNfc.contains("KNT")) {
                    logstr = "{\"member_login\":\"" + tagNfc + "\",\"member_role\":\"Kontrola\"}";
                } else {
                    logstr = "{\"member_login\":\"" + tagNfc + "\",\"member_role\":\"" + MainActivity.MIEJSCE + "\"}";
                }
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
                        endResult = 0;

                    } else if (stat.equals("false") && mes.equals("Rola nie znaleziona")) {
                        endResult = 1;

                    } else if (stat.equals("false") && mes.equals("User could not be found")) {
                        endResult = 2;
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                } catch (JSONException e) {
                    endResult = 3;

                    e.printStackTrace();
                }
                //System.out.println(indeksy.toString());
            } else if (nfcman == 1) {

            }
            return null;

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
}
