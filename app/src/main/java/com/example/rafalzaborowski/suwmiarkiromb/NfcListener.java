package com.example.rafalzaborowski.suwmiarkiromb;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class NfcListener  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parcelable[] ndefMessageArray = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage ndefMessage = (NdefMessage) ndefMessageArray[0];
        String msg = new String(ndefMessage.getRecords()[0].getPayload());
        String substr=msg.substring(3);
        Intent intSend = new Intent("akcja6");
        intSend.putExtra("nfcTag",substr);
        getApplicationContext().sendBroadcast(intSend);
        finish();
    }

}
