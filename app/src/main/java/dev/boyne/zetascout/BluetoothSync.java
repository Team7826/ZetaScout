package dev.boyne.zetascout;

import android.bluetooth.BluetoothServerSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class BluetoothSync extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothsync);

        System.out.println(BluetoothConnectionManager.sync());


        startActivity(Util.moveSetupGenerator(BluetoothSync.this, BluetoothSuccess.class));
    }
}