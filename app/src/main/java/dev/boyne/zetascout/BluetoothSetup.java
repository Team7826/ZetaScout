package dev.boyne.zetascout;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.UUID;

public class BluetoothSetup extends AppCompatActivity {

    private void showError(String text, int duration) {
        runOnUiThread(() -> {
            /*Snackbar error = Snackbar.make(getWindow().getDecorView().getRootView(), text, duration);
            error.show();*/
        });

    }



    private void showError(String text) {
        showError(text, 5000);
    }

    private void onBluetoothSuccess(BluetoothServerSocket bluetoothSocket) {
        startActivity(Util.moveSetupGenerator(BluetoothSetup.this, BluetoothSync.class));
    }


    private boolean connectBluetoothSocket(BluetoothAdapter bluetoothAdapter) {



        // Listening on ZetaScout SDP socket
        ActivityResultLauncher<Intent> bluetoothDiscoverabilityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() != Activity.RESULT_OK && result.getResultCode() != 120) {
                            System.out.println(result.getResultCode());
                            showError("You denied Bluetooth discoverability. Please restart the app and try again.");
                        } else {

                            // Enable SDP server
                            try (BluetoothServerSocket serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("ZetaScout", UUID.fromString("c51f17b3-c044-4fe5-a62a-789a3bc92e1c"));) {
                                BluetoothConnectionManager.bluetoothSocket = serverSocket.accept();
                                runOnUiThread(() -> onBluetoothSuccess(serverSocket));
                            } catch (SecurityException ignored) {
                                showError("ZetaScout does not have permission to use your Bluetooth connection.");
                            } catch (IOException e) {
                                showError("An exception has occurred.");
                            }


                        }

                    }
                });

        Intent enableBtDiscoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

        Thread bluetoothDiscoveryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                bluetoothDiscoverabilityLauncher.launch(enableBtDiscoverIntent);
            }
        });

        bluetoothDiscoveryThread.start();
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothsetup);

        // Set up Bluetooth adapter and get device friendly name
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            showError("Your device doesn't support Bluetooth, so you will unfortunately be unable to use ZetaScout.");
            throw new RuntimeException();
        }

        try {
            String btName = bluetoothAdapter.getName();
                ((TextView) findViewById(R.id.btinfo)).setText("On the ZetaScout desktop app, select \"Add Device\", and then choose \"" + btName + "\".");
        } catch (SecurityException ignored) {
            System.out.println("Could not derive Bluetooth device name due to a security exception.");
        }

        // Requesting to enable Bluetooth if not turned on already
        ActivityResultLauncher<Intent> bluetoothActivationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() != Activity.RESULT_OK) {
                            showError("You denied Bluetooth activation. Please restart the app and try again.");
                        } else {
                            connectBluetoothSocket(bluetoothAdapter);
                        }
                    }
                });

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bluetoothActivationLauncher.launch(enableBtIntent);
        } else {
            connectBluetoothSocket(bluetoothAdapter);
        }



    }
}