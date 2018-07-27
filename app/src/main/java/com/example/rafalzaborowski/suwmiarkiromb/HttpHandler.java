package com.example.rafalzaborowski.suwmiarkiromb;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import org.apache.http.conn.ConnectTimeoutException;
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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class HttpHandler {
    private static final String ADRESTEST = "192.168.1.30", BAZATEST = "miarki", ADRESMAIN = "192.168.1.33:81", BAZAMAIN = "qcontrol", ADRESPROD = "";
    private static final int HTTPTIMEOUT = 3000;
    private static String userName = "admin";
    private static String passw = "RSZx9Hqz8B";
    private static String adres = ADRESTEST;
    private static String baza = BAZATEST;
    private static String urlConGetIndexes = "http://" + adres + "/" + baza + "/index.php/api/rest/indeks/";
    private static String urlConPostNewIndex = "http://" + adres + "/" + baza + "/index.php/api/rest/pomiar";
    private static String urlConPostLogin = "http://" + adres + "/" + baza + "/index.php/api/rest/login";
    private static int flags = Base64.NO_WRAP | Base64.URL_SAFE;
    private static byte[] upbyte = (userName + ":" + passw).getBytes();

    public static InputStreamReader doGet() {

        try {
            URL getEndpoint = new URL(urlConGetIndexes);
            HttpURLConnection myConnection = (HttpURLConnection) getEndpoint.openConnection();
            myConnection.setConnectTimeout(HTTPTIMEOUT);
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


    public static int doPost(String newIndex) {
        int response = -1;
        try {

            URL getEndpoint = new URL(urlConPostNewIndex);
            HttpURLConnection myConnection = (HttpURLConnection) getEndpoint.openConnection();
            myConnection.setConnectTimeout(HTTPTIMEOUT);
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

    public static InputStreamReader doPostLogin(String newLog) {
        try {

            URL getEndpoint = new URL(urlConPostLogin);
            HttpURLConnection myConnection = (HttpURLConnection) getEndpoint.openConnection();
            myConnection.setConnectTimeout(HTTPTIMEOUT);
            String encoded = Base64.encodeToString(upbyte, flags);
            myConnection.setDoOutput(true);

            myConnection.setRequestProperty("Authorization", "Basic " + encoded);
            //DataOutputStream os = new DataOutputStream(myConnection.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            myConnection.getOutputStream().write(newLog.getBytes());
            System.out.println("response code = " + myConnection.getResponseCode());
            System.out.println("connection string  = " + urlConPostLogin);
            if (myConnection.getResponseCode() == 200) {
                InputStream responseBody = myConnection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                return responseBodyReader;

            } else if (myConnection.getResponseCode() == 404) {
                InputStream responseBody = myConnection.getErrorStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                return responseBodyReader;

            } else {

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();


        }
        return null;


    }


}