package com.example.rafalzaborowski.suwmiarkiromb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class BlinkingText{
    Context context;

    public BlinkingText(Activity act) {
        this.context = (Context) act.getBaseContext();
        new BlinkText().execute();
    }

    private class BlinkText extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            for (int i=0;i<10;i++){
                if(i%2 != 0){
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent("akcja5");
                context.sendBroadcast(intent);
            }
            return null;
        }
    }
}


