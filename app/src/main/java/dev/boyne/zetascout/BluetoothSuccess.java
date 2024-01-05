package dev.boyne.zetascout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BluetoothSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothsuccess);
        Button nextButton = findViewById(R.id.next);

        nextButton.setOnClickListener(v -> startActivity(new Intent(BluetoothSuccess.this, Waiting.class)));
    }
}