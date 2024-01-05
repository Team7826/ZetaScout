package dev.boyne.zetascout;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class BluetoothConnectionManager {
    public static Thread bluetoothListenerThread;
    public static BluetoothSocket bluetoothSocket = null;
    enum Signals {
        GET_FIELDS, // Wants to get the list of scouting criterion
        END_RESPONSE,
        READY_SCOUT // Ready to scout a team
    }

    static String trimZeros(String str) {
        int pos = str.indexOf(0);
        return pos == -1 ? str : str.substring(0, pos);
    }

    public static String readUntil(String finish) {
        InputStream input;
        try {
            input = bluetoothSocket.getInputStream();
            StringBuilder read = new StringBuilder();

            System.out.println("Reading");
            while (true) {
                byte[] tempBuffer = new byte[128];
                int numBytes = input.read(tempBuffer);
                read.append(new String(tempBuffer));
                String readString = trimZeros(read.toString());
                if (readString.endsWith(finish) || numBytes == 0) {
                    return readString.substring(0, readString.length() - finish.length());
                }
            }
        } catch (IOException e) {
            return "";
        }
    }
    public static String sync() {
        InputStream input;
        OutputStream output;
        try {
            output = bluetoothSocket.getOutputStream();
            output.write(Signals.GET_FIELDS.toString().getBytes());
            String read = readUntil(Signals.END_RESPONSE.toString());
            System.out.println(read);

        } catch (IOException e) {
            return "";
        }

        return "";

    }
}
