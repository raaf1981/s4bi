package com.example.rafalzaborowski.suwmiarkiromb;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class HttpHandler {

    private static String userName = "admin";
    private static String passw = "RSZx9Hqz8B";
    private static String urlConGetIndexes = "http://app.s4bi.pl/miarki/index.php/api/rest/indeks/";
    private static String urlConPostNewIndex = "http://app.s4bi.pl/miarki/index.php/api/rest/pomiar";
    private static String urlConPostLogin = "http//app.s4bi.pl/miarki/index.php/api/rest/login";
    private static int flags = Base64.NO_WRAP | Base64.URL_SAFE;
    private static byte[] upbyte = (userName + ":" + passw).getBytes();

    public static InputStreamReader doGet() {

        try {
            URL getEndpoint = new URL(urlConGetIndexes);
            HttpURLConnection myConnection = (HttpURLConnection) getEndpoint.openConnection();
            String encoded = Base64.encodeToString(upbyte, flags);
            myConnection.setRequestProperty("Authorization", "Basic " + encoded);
            System.out.println("respone code = " + myConnection.getResponseCode());
            System.out.println("connection string  = " + urlConGetIndexes);
            if (myConnection.getResponseCode() == 200) {
                InputStream responseBody = myConnection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                return responseBodyReader;

            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static int doPost(String newIndex){
        int response=-1;
        try {

            URL getEndpoint = new URL(urlConPostNewIndex);
            HttpURLConnection myConnection = (HttpURLConnection) getEndpoint.openConnection();
            String encoded = Base64.encodeToString(upbyte, flags);
            myConnection.setDoOutput(true);

            myConnection.setRequestProperty("Authorization", "Basic " + encoded);
            //DataOutputStream os = new DataOutputStream(myConnection.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            myConnection.getOutputStream().write(newIndex.getBytes());
            System.out.println("respone code = " + myConnection.getResponseCode());
            System.out.println("connection string  = " + urlConPostNewIndex);
            response = myConnection.getResponseCode();
            return myConnection.getResponseCode();


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }

    }


}