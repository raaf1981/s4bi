package com.example.rafalzaborowski.suwmiarkiromb;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {

    private static String userName = "admin";
    private static String passw = "RSZx9Hqz8B";
    private static final String TAG = "Download Task";
    private Context context;
    private Context context2;
    private int picNum;
    private String downloadUrl, downloadFileName;
    private static int flags = Base64.NO_WRAP | Base64.URL_SAFE;
    private static byte[] upbyte = (userName + ":" + passw).getBytes();


    public DownloadTask(int picNum,Activity act) {
        this.context = (Context) act.getBaseContext();
        downloadUrl = "http://app.s4bi.pl/miarki/index.php/api/rest/pic/"+picNum;
        downloadFileName = "index.pdf";
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            Intent i = new Intent("akcja1");
            context.sendBroadcast(i);
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void result) {

            Intent i = new Intent("akcja2");
            i.putExtra("chosenind",String.valueOf(picNum));
            if(outputFile!=null){
                i.putExtra("outputFileB",true);
            }
            context.sendBroadcast(i);
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                String encoded = Base64.encodeToString(upbyte, flags);
                c.setRequestProperty("Authorization", "Basic " + encoded);
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }


                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) {

                    apkStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + "downloadedPdf");
                } else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }
}
