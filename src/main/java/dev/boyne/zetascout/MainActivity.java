package dev.boyne.zetascout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listview;
    Button addButton;
    Button exportButton;
    EditText teamIdInput;
    String[] InternalTeamList = new String[] {};

    ZetaScout application;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        this.application = ((ZetaScout) this.getApplication());
        listview = findViewById(R.id.teamlist);
        addButton = findViewById(R.id.addteambutton);
        exportButton = findViewById(R.id.export);
        teamIdInput = findViewById(R.id.teamidinput);

        final List<String> TeamList = new ArrayList<>(Arrays.asList(InternalTeamList));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>
                (MainActivity.this, android.R.layout.simple_list_item_1, TeamList);
        listview.setAdapter(adapter);


        String data = readFile();

        System.out.println("Read: " + data);

        if (data.equals("NOTFOUND")) {
            Toast.makeText(getApplicationContext(), "Previous export not found.", Toast.LENGTH_SHORT).show();
            createFile();
        } else if (data.equals("ERROR")) {
            Toast.makeText(getApplicationContext(), "An error occured. Check logs if in debugging mode for details.", Toast.LENGTH_SHORT).show();
        } else {

            try {

                application.matchData = new ObjectMapper().readValue(data, new TypeReference<HashMap>() {});
                for (String team : application.matchData.keySet()) {
                    TeamList.add(team);
                    adapter.notifyDataSetChanged();
                }
                System.out.println("Team data loaded: " + application.matchData);
                Toast.makeText(getApplicationContext(), "Loaded last export.", Toast.LENGTH_SHORT).show();
                application.didLoadExport = true;

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "An error occured: " + e, Toast.LENGTH_SHORT).show();
            }
        }

        // Cool dialog box to prevent stupidity
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = teamIdInput.getText().toString();
                if (id.contains(".")) {
                    Toast.makeText(getApplicationContext(), "No decimals allowed!", Toast.LENGTH_SHORT).show();
                }
                else if (TeamList.contains(id)) {
                    Toast.makeText(getApplicationContext(), "That team's already in the list!", Toast.LENGTH_SHORT).show();
                }
                else if (!id.equals("")) {
                    TeamList.add(id);
                    application.addTeam(id);
                    teamIdInput.setText("");


                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Added match", Toast.LENGTH_SHORT).show();
                }
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
                System.out.println("Index: " + index + ", get: " + TeamList.get(index));
                Intent match = new Intent(MainActivity.this, TeamActivity.class);
                match.putExtra("teamID", TeamList.get(index));
                startActivity(match);
            }
        });
        listview.setOnItemLongClickListener((adapterView, view, index, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete the team?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            application.clearMatchData(index);
                            TeamList.remove(index);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Deleted team", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });


    }

    @Override
    public void onBackPressed() {}

    public void writeFile(String matchData) {


        //Create a new file that points to the root directory, with the given name:
        File file = new File(Environment.getExternalStoragePublicDirectory("Documents") + "/zetascout.json");
        System.out.println(file.getAbsolutePath());

        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;
        try {
            boolean works = file.createNewFile();
            if (!works) {
                System.out.println("Deleted? " + file.delete());
                file.createNewFile();
            }
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(matchData.getBytes());
            outputStream.flush();
            outputStream.close();
            System.out.println("Wrote successfully");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void createFile() {

        File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("Documents")));
        if (!root.exists()) {
            root.mkdirs();
        }
        File gpxfile = new File(Environment.getExternalStoragePublicDirectory("Documents"), "zetascout.json");
        Toast.makeText(getApplicationContext(), "Created new file", Toast.LENGTH_SHORT).show();
    }

    public String readFile() {


        //This point and below is responsible for the write operation
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(Environment.getExternalStoragePublicDirectory("Documents") + "/zetascout.json"));

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
                System.out.println("ERROR:");
                e.printStackTrace();
            }
        }
        return "ERROR";
    }
}