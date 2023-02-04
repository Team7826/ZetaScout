package dev.boyne.zetascout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listview;
    Button addButton;
    Button exportButton;
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
        exportButton = findViewById(R.id.export);
        currentMatch = 0;

        final List<String> MatchList = new ArrayList<>(Arrays.asList(InternalMatchList));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>
                (MainActivity.this, android.R.layout.simple_list_item_1, MatchList);
        listview.setAdapter(adapter);


        String data = readFile();

        if (data.equals("NOTFOUND")) {
            Toast.makeText(getApplicationContext(), "Previous export not found.", Toast.LENGTH_SHORT).show();
        } else if (data.equals("ERROR")) {
            Toast.makeText(getApplicationContext(), "An error occured. Check logs if in debugging mode for details.", Toast.LENGTH_SHORT).show();
        } else {

            try {

                application.matchData = new ObjectMapper().readValue(data, new TypeReference<ArrayList>() {});
                for (int i = 0; i < application.matchData.size(); i++) {
                    currentMatch++;
                    MatchList.add("Match " + currentMatch);
                    adapter.notifyDataSetChanged();
                }
                System.out.println("Match data loaded: " + application.matchData);
                Toast.makeText(getApplicationContext(), "Loaded last export.", Toast.LENGTH_SHORT).show();
                application.didLoadExport = true;

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "An error occured: " + e, Toast.LENGTH_SHORT).show();
            }
        }




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
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String json = new ObjectMapper().writeValueAsString(application.matchData);
                    writeFile(json);
                    Toast.makeText(getApplicationContext(), "Export appears to be successful", Toast.LENGTH_SHORT).show();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                    Toast.makeText(getApplicationContext(), "An error occured: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                System.out.println(MatchList.get(index));
                Intent match = new Intent(MainActivity.this, MatchActivity.class);
                match.putExtra("matchID", index);
                startActivity(match);
            }
        });
        listview.setOnItemLongClickListener((adapterView, view, index, id) -> {
            application.clearMatchData(index);
            Toast.makeText(getApplicationContext(), "Match data cleared for " + MatchList.get(index), Toast.LENGTH_SHORT).show();
            return true;
        });


    }

    @Override
    public void onBackPressed() {}

    public void writeFile(String matchData) {


        //Create a new file that points to the root directory, with the given name:
        File file = new File(Environment.getExternalStoragePublicDirectory("Documents"), "zetascout.json");

        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(matchData.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readFile() {


        //This point and below is responsible for the write operation
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(Environment.getExternalStoragePublicDirectory("Documents"), "zetascout.json"));

            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
            String result = total.toString();
            inputStream.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e.toString().contains("ENOENT")) {
                return "NOTFOUND";
            } else {
                e.printStackTrace();
            }
        }
        return "ERROR";
    }
}