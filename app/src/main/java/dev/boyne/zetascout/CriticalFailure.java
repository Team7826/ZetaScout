package dev.boyne.zetascout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CriticalFailure extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        Button exitButton = findViewById(R.id.next);

        exitButton.setOnClickListener(v -> this.finishAffinity());
    }
}