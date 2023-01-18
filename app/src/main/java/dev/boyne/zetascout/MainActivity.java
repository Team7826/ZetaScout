package dev.boyne.zetascout;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listview;
    Button addButton;
    int currentMatch;
    String[] InternalMatchList = new String[] {};

    ZetaScout application;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.application = ((ZetaScout) this.getApplication());
        listview = findViewById(R.id.matchlist);
        addButton = findViewById(R.id.addmatchbutton);
        currentMatch = 0;

        final List<String> MatchList = new ArrayList<>(Arrays.asList(InternalMatchList));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>
                (MainActivity.this, android.R.layout.simple_list_item_1, MatchList);
        listview.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMatch++;
                MatchList.add("Match " + currentMatch);
                application.addMatch();
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Added match", Toast.LENGTH_SHORT).show();
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                System.out.println(MatchList.get(index));
            }
        });
        listview.setOnItemLongClickListener((adapterView, view, index, id) -> {
            Toast.makeText(getApplicationContext(), "Match data cleared for " + MatchList.get(index), Toast.LENGTH_SHORT).show();
            return true;
        });
    }
}