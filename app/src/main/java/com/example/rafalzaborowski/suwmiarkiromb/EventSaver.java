package com.example.rafalzaborowski.suwmiarkiromb;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class EventSaver {
    private Context context;
    private String newEventPost;
    int response;

    public EventSaver(String newEventPost) {
        this.newEventPost = newEventPost;
        new EventSaving().execute();
    }


    public EventSaver(Activity act, String newEventPost) {
        this.context = act.getBaseContext();
        this.newEventPost = newEventPost;
        new EventSaving().execute();
    }

    private class EventSaving extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            response = HttpHandler.doPostEvent(newEventPost);

            return null;
        }
    }
}




