package dev.boyne.zetascout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TeamActivity extends AppCompatActivity {

    ZetaScout application;

    String teamID;
    int matchID;
    TextView teamHeader;

    TabLayout pointtabs;

    int tabID = 0;

    HashMap<Integer, HashMap> teleopAndAutonData = new HashMap();

    HashMap<String, Integer> defaultData = new HashMap();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        defaultData.put("cone-top", 0);
        defaultData.put("cone-mid", 0);
        defaultData.put("cone-bot", 0);
        defaultData.put("cube-top", 0);
        defaultData.put("cube-mid", 0);
        defaultData.put("cube-bot", 0);

        defaultData.put("bal-eng", 0);
        defaultData.put("bal", 0);

        teleopAndAutonData.put(0, (HashMap) defaultData.clone());
        teleopAndAutonData.put(1, (HashMap) defaultData.clone());

        teamID = getIntent().getStringExtra("teamID");
        matchID = getIntent().getIntExtra("matchID", 0);

        this.application = ((ZetaScout) this.getApplication());

        teamHeader = findViewById(R.id.teamname);

        teamHeader.setText("Match " + (matchID+1) + " / Team " + teamID);

        pointtabs = findViewById(R.id.pointcategories);

        pointtabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // O for teleop, 1 for auton
                tabID = tab.getText().equals("Teleop") ? 0 : 1;
                System.out.println(tabID);
                switchTabs();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}

            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        setButtonModifier(R.id.balancesuccessesinput, R.id.balancesuccessminus, -1);
        setButtonModifier(R.id.balancesuccessesinput, R.id.balancesuccessplus, 1);

        setButtonModifier(R.id.balancesuccessesengagedinput, R.id.balancesuccessengagedminus, -1);
        setButtonModifier(R.id.balancesuccessesengagedinput, R.id.balancesuccessengagedplus, 1);

        setButtonModifier(R.id.balanceunsuccessesinput, R.id.balanceunsuccessminus, -1);
        setButtonModifier(R.id.balanceunsuccessesinput, R.id.balanceunsuccessplus, 1);

        setButtonModifier(R.id.linksinput, R.id.linksminus, -1);
        setButtonModifier(R.id.linksinput, R.id.linksplus, 1);

        setButtonModifier(R.id.penaltiesinput, R.id.penaltiesminus, -1);
        setButtonModifier(R.id.penaltiesinput, R.id.penaltiesplus, 1);

        setButtonModifier(R.id.upperconeinput, R.id.coneupperminus, -1);
        setButtonModifier(R.id.middleconeinput, R.id.conemiddleminus, -1);
        setButtonModifier(R.id.lowerconeinput, R.id.conelowerminus, -1);
        setButtonModifier(R.id.upperconeinput, R.id.coneupperplus, 1);
        setButtonModifier(R.id.middleconeinput, R.id.conemiddleplus, 1);
        setButtonModifier(R.id.lowerconeinput, R.id.conelowerplus, 1);

        setButtonModifier(R.id.uppercubeinput, R.id.cubeupperminus, -1);
        setButtonModifier(R.id.middlecubeinput, R.id.cubemiddleminus, -1);
        setButtonModifier(R.id.lowercubeinput, R.id.cubelowerminus, -1);
        setButtonModifier(R.id.uppercubeinput, R.id.cubeupperplus, 1);
        setButtonModifier(R.id.middlecubeinput, R.id.cubemiddleplus, 1);
        setButtonModifier(R.id.lowercubeinput, R.id.cubelowerplus, 1);

        setSliderModifier(R.id.totalpinsseeker, R.id.totalpinsinflictedvalue);
        setSliderModifier(R.id.totalpinstakenseeker, R.id.totalpinstakenvalue);

        setSliderModifier(R.id.penaltiesseeker, R.id.penaltiesvalue);

        setSliderModifier(R.id.defenseseeker, R.id.defensevalue, 1);

        setSliderModifier(R.id.cooperationseeker, R.id.cooperationvalue, 1);

        setSliderModifier(R.id.drivingseeker, R.id.drivingvalue, 1);

        /*
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                System.out.println(TeamList.get(index));
                startActivity(new Intent(MatchActivity.this, MainActivity.class));

            }
        });*/
    }

    private void switchTabs() {
        switchElementTab(R.id.upperconeinput, "cone-top");
        switchElementTab(R.id.middleconeinput, "cone-mid");
        switchElementTab(R.id.lowerconeinput, "cone-bot");

        switchElementTab(R.id.uppercubeinput, "cube-top");
        switchElementTab(R.id.middlecubeinput, "cube-mid");
        switchElementTab(R.id.lowercubeinput, "cube-bot");

        switchElementTab(R.id.balancesuccessesinput, "bal");
        switchElementTab(R.id.balancesuccessesengagedinput, "bal-eng");
    }

    private void switchElementTab(int textView, String value) {
        TextView view = ((TextView) findViewById(textView));
        HashMap newTab = teleopAndAutonData.get(tabID==0 ? 1 : 0);
        HashMap oldTab = teleopAndAutonData.get(tabID);

        oldTab.put(value, view.getText());
        view.setText((String) newTab.get(value).toString());
    }

    private void setSliderModifier(int slider, int textView) {
        setSliderModifier(slider, textView, 0);
    }

    private void setSliderModifier(int slider, int textView, int offset) {
        ((SeekBar) findViewById(slider)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar view, int value, boolean fromUser) {
                ((TextView) findViewById(textView)).setText(Integer.toString(value + offset));
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    private void setButtonModifier(int input, int btn, int mod) {
        findViewById(btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyValue(findViewById(input), mod);
            }
        });
    }
    private void modifyValue(EditText input, int mod) {
        input.setText(Integer.toString(Integer.parseInt(input.getText().toString()) + mod));
    }
}