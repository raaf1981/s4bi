package com.example.rafalzaborowski.suwmiarkiromb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ScrollView;

public class DataSaver {
    private Context context;
    private String newIndexPost;
    int response;


    public DataSaver(Activity act, String newIndexPost) {
        this.context = act.getBaseContext();
        this.newIndexPost = newIndexPost;
        new DataSaving().execute();
    }

    private class DataSaving extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Intent i = new Intent("akcja3");
            context.sendBroadcast(i);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent i = new Intent("akcja4");
            i.putExtra("respCode", response);
            context.sendBroadcast(i);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            response = HttpHandler.doPost(newIndexPost);

            return null;
        }
    }
}




