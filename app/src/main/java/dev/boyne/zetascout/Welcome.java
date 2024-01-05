package dev.boyne.zetascout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button nextButton = findViewById(R.id.next);

        nextButton.setOnClickListener(v -> startActivity(Util.moveSetupGenerator(Welcome.this, BluetoothSetup.class)));
    }
}