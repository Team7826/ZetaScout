package dev.boyne.zetascout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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

    Button saveButton;

    HashMap<String, HashMap> matchData = new HashMap(); // Index 0 is the teleop, index 1 is the auton, and index 2 is the non-categorized data

    HashMap<String, Integer> categorizedData = new HashMap();
    HashMap<String, Object> uncategorizedData = new HashMap();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        teamID = getIntent().getStringExtra("teamID");
        matchID = getIntent().getIntExtra("matchID", 0);

        this.application = ((ZetaScout) this.getApplication());
        System.out.println(this.application.matchData.get(matchID).get(teamID));
        if (!this.application.matchData.get(matchID).get(teamID).isEmpty()) {
            matchData = this.application.matchData.get(matchID).get(teamID);
            updateData();
        } else {
            categorizedData.put("cone-top", 0);
            categorizedData.put("cone-mid", 0);
            categorizedData.put("cone-bot", 0);
            categorizedData.put("cube-top", 0);
            categorizedData.put("cube-mid", 0);
            categorizedData.put("cube-bot", 0);

            categorizedData.put("bal-eng", 0);
            categorizedData.put("bal", 0);

            uncategorizedData.put("bal-fail", 0);
            uncategorizedData.put("got-mob", false);
            uncategorizedData.put("got-park", false);
            uncategorizedData.put("links", 0);
            uncategorizedData.put("penalty-points", 0);

            uncategorizedData.put("can-cone-low", false);
            uncategorizedData.put("can-cone-mid", false);
            uncategorizedData.put("can-cone-hi", false);
            uncategorizedData.put("can-cube-low", false);
            uncategorizedData.put("can-cube-mid", false);
            uncategorizedData.put("can-cube-hi", false);

            uncategorizedData.put("pin-inf", 0);
            uncategorizedData.put("pin-take", 0);
            uncategorizedData.put("rat-penalt", 0);
            uncategorizedData.put("rat-defense", 0);
            uncategorizedData.put("rat-coop", 0);
            uncategorizedData.put("rat-driving", 0);
            uncategorizedData.put("notes", "No notes");

            matchData.put("0", (HashMap) categorizedData.clone());
            matchData.put("1", (HashMap) categorizedData.clone());
            matchData.put("2", (HashMap) uncategorizedData.clone());
        }

        teamHeader = findViewById(R.id.teamname);

        saveButton = findViewById(R.id.save);

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateDataState();
                application.populateTeamData(matchID, teamID, matchData);
            }
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

    @Override
    public void onBackPressed() {
        populateDataState();
        application.populateTeamData(matchID, teamID, matchData);
        finish();
    }

    /* Fills our hashmap of data with the correct values */
    private void populateDataState() {
        // Update data with tabs
        updateCategorizedElement(R.id.upperconeinput, "cone-top");
        updateCategorizedElement(R.id.middleconeinput, "cone-mid");
        updateCategorizedElement(R.id.lowerconeinput, "cone-bot");
        updateCategorizedElement(R.id.uppercubeinput, "cube-top");
        updateCategorizedElement(R.id.middlecubeinput, "cube-mid");
        updateCategorizedElement(R.id.lowercubeinput, "cube-bot");

        updateCategorizedElement(R.id.balancesuccessesengagedinput, "bal-eng");
        updateCategorizedElement(R.id.balancesuccessesinput, "bal");

        // Update data without tabs
        updateUncategorizedElement(R.id.balanceunsuccessesinput, "bal-fail");
        updateUncategorizedElement(R.id.mobilitycheck, "got-mob", CheckBox.class);
        updateUncategorizedElement(R.id.parkedcheck, "got-park", CheckBox.class);
        updateUncategorizedElement(R.id.linksinput, "links");
        updateUncategorizedElement(R.id.penaltiesinput, "penalty-points");

        updateUncategorizedElement(R.id.coneonlowcheck, "can-cone-low", CheckBox.class);
        updateUncategorizedElement(R.id.coneonmidcheck, "can-cone-mid", CheckBox.class);
        updateUncategorizedElement(R.id.coneonhighcheck, "can-cone-hi", CheckBox.class);
        updateUncategorizedElement(R.id.cubeonlowcheck, "can-cube-low", CheckBox.class);
        updateUncategorizedElement(R.id.cubeonmidcheck, "can-cube-mid", CheckBox.class);
        updateUncategorizedElement(R.id.cubeonhighcheck, "can-cube-hi", CheckBox.class);

        updateUncategorizedElement(R.id.totalpinsseeker, "pin-inf", SeekBar.class);
        updateUncategorizedElement(R.id.totalpinstakenseeker, "pin-take", SeekBar.class);
        updateUncategorizedElement(R.id.defenseseeker, "rat-defense", SeekBar.class);
        updateUncategorizedElement(R.id.penaltiesseeker, "rat-penalties", SeekBar.class);
        updateUncategorizedElement(R.id.defenseseeker, "rat-defense", SeekBar.class);
        updateUncategorizedElement(R.id.cooperationseeker, "rat-coop", SeekBar.class);
        updateUncategorizedElement(R.id.drivingseeker, "rat-driving", SeekBar.class);

        updateUncategorizedElement(R.id.notes, "notes");

        System.out.println(matchData);
    }
    private void setupData(int id, String value, int category) {
        Object data = matchData.get(String.valueOf(category)).get(value);
        View item = findViewById(id);

        if (item instanceof EditText) {
            if (data instanceof Integer) {
                ((TextView) item).setText(String.valueOf(data));
            } else if (data instanceof SpannableStringBuilder) {
                ((TextView) item).setText("" + data);
            } else {
                ((TextView) item).setText((String) data);
            }
        }
        else if (item instanceof CheckBox) {
            ((CheckBox) item).setChecked((boolean) data);
        }
        else if (item instanceof SeekBar) {
            ((SeekBar) item).setProgress((int) data);
        }
        else {
            System.out.println(item + " - " + data);
        }
    }
    private void updateData() {
        System.out.println(matchData);
        // Update data with tabs
        setupData(R.id.upperconeinput, "cone-top", 1);
        setupData(R.id.middleconeinput, "cone-mid", 1);
        setupData(R.id.lowerconeinput, "cone-bot", 1);
        setupData(R.id.uppercubeinput, "cube-top", 1);
        setupData(R.id.middlecubeinput, "cube-mid", 1);
        setupData(R.id.lowercubeinput, "cube-bot", 1);

        setupData(R.id.balancesuccessesengagedinput, "bal-eng", 1);
        setupData(R.id.balancesuccessesinput, "bal", 1);

        // Update data without tabs
        setupData(R.id.balanceunsuccessesinput, "bal-fail", 2);
        setupData(R.id.mobilitycheck, "got-mob", 2);
        setupData(R.id.parkedcheck, "got-park", 2);
        setupData(R.id.linksinput, "links", 2);
        setupData(R.id.penaltiesinput, "penalty-points", 2);

        setupData(R.id.coneonlowcheck, "can-cone-low", 2);
        setupData(R.id.coneonmidcheck, "can-cone-mid", 2);
        setupData(R.id.coneonhighcheck, "can-cone-hi", 2);
        setupData(R.id.cubeonlowcheck, "can-cube-low", 2);
        setupData(R.id.cubeonmidcheck, "can-cube-mid", 2);
        setupData(R.id.cubeonhighcheck, "can-cube-hi", 2);

        setupData(R.id.totalpinsseeker, "pin-inf", 2);
        setupData(R.id.totalpinstakenseeker, "pin-take", 2);
        setupData(R.id.defenseseeker, "rat-defense", 2);
        setupData(R.id.penaltiesseeker, "rat-penalties", 2);
        setupData(R.id.defenseseeker, "rat-defense", 2);
        setupData(R.id.cooperationseeker, "rat-coop", 2);
        setupData(R.id.drivingseeker, "rat-driving", 2);

        setupData(R.id.notes, "notes", 2);

        System.out.println(matchData);
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

    private void updateCategorizedElement(int source, String valueName) {
        // We need only do the current selected as the other tab has either not been modified or has been already saved
        matchData.get(tabID==0 ? "1" : "0").put(valueName, ((TextView) findViewById(source)).getText().toString());
    }

    private void updateUncategorizedElement(int source, String valueName, Class sourceType) {
        Object value;
        if (sourceType.equals(TextView.class)) {
            value = ((TextView) findViewById(source)).getText().toString();
        } else if (sourceType.equals(CheckBox.class)) {
            value = ((CheckBox) findViewById(source)).isChecked();
        } else if (sourceType.equals(SeekBar.class)) {
            value = ((SeekBar) findViewById(source)).getProgress();
        } else {
            value = "¯\\_(ツ)_/¯";
        }
        matchData.get("2").put(valueName, value);
    }

    private void updateUncategorizedElement(int source, String valueName) {
        updateUncategorizedElement(source, valueName, TextView.class);
    }

    private void switchElementTab(int textView, String value) {
        TextView view = ((TextView) findViewById(textView));
        HashMap newTab = matchData.get(tabID==0 ? "1" : "0");
        HashMap oldTab = matchData.get(String.valueOf(tabID));

        oldTab.put(value, view.getText().toString());
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