package com.example.rafalzaborowski.suwmiarkiromb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

public class CountDown extends CountDownTimer {
    private Context context;
    private Activity act;


    public CountDown(long millisInFuture, long countDownInterval, Activity act) {
        super(millisInFuture, countDownInterval);
        this.context = (Context) act.getBaseContext();
        this.act = act;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        final TextView textView8 = (TextView) act.findViewById(R.id.textView8);
        final TextView textView5 = (TextView) act.findViewById(R.id.textView5);
        long seconds = millisUntilFinished / 1000;
        long minutes = seconds / 60;
        if (seconds > 120) {
            textView5.setTextColor(Color.GREEN);
            textView8.setTextColor(Color.GREEN);
            if (minutes < 10) {
                if ((seconds - (minutes * 60)) < 10) {
                    textView8.setText("00:0" + String.valueOf(minutes) + ":0" + String.valueOf(seconds - (minutes * 60)));
                } else {
                    textView8.setText("00:0" + String.valueOf(minutes) + ":" + String.valueOf(seconds - (minutes * 60)));
                }

            } else {
                if ((seconds - (minutes * 60)) < 10) {
                    textView8.setText("00:" + String.valueOf(minutes) + ":0" + String.valueOf(seconds - (minutes * 60)));
                } else {
                    textView8.setText("00:" + String.valueOf(minutes) + ":" + String.valueOf(seconds - (minutes * 60)));
                }
            }

        } else {
            if (seconds > 60) {
                textView5.setTextColor(Color.RED);
                textView8.setTextColor(Color.RED);
                textView8.setText("00:00:0" + String.valueOf(seconds));
            } else {
                textView5.setTextColor(Color.RED);
                textView8.setTextColor(Color.RED);
                textView8.setText("00:00:" + String.valueOf(seconds));
            }

        }
    }

    @Override
    public void onFinish() {
        final TextView textView8 = (TextView) act.findViewById(R.id.textView8);
        textView8.setText("00:00:00");
        Intent intent = new Intent("akcja7");
        context.sendBroadcast(intent);

    }
}
