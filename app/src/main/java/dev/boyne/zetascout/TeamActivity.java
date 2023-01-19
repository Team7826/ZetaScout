package dev.boyne.zetascout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MatchActivity extends AppCompatActivity {

    int matchID;

    ListView listview;
    Button addButton;

    EditText teamID;
    String[] InternalTeamList = new String[] {};

    ZetaScout application;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        this.application = ((ZetaScout) this.getApplication());
        listview = findViewById(R.id.teamlist);
        addButton = findViewById(R.id.addteambutton);
        teamID = findViewById(R.id.teamidinput);
        matchID = getIntent().getIntExtra("matchID", 0);

        final List<String> TeamList = new ArrayList<>(Arrays.asList(InternalTeamList));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>
                (MatchActivity.this, android.R.layout.simple_list_item_1, TeamList);

        Object[] currentTeams = application.getTeamsInMatch(matchID).keySet().toArray();

        for (int i = 0; i < currentTeams.length; i++) {
            TeamList.add((String) currentTeams[i]);
        }

        listview.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamList.add(teamID.getText().toString());
                application.addTeam(matchID, teamID.getText().toString());
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Added team", Toast.LENGTH_SHORT).show();
                teamID.setText("");
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                System.out.println(TeamList.get(index));
                startActivity(new Intent(MatchActivity.this, MainActivity.class));

            }
        });
        listview.setOnItemLongClickListener((adapterView, view, index, id) -> {
            Toast.makeText(getApplicationContext(), "Removed team " + TeamList.get(index), Toast.LENGTH_SHORT).show();
            application.removeTeam(matchID, TeamList.get(index));
            TeamList.remove(index);
            //adapter.remove(TeamList.get(index));
            adapter.notifyDataSetChanged();
            return true;
        });
    }
}