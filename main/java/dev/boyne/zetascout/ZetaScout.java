package dev.boyne.zetascout;

import android.app.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ZetaScout extends Application {

    // This contains the teams on every match, and the data for each team
    public HashMap<String, HashMap> matchData = new HashMap<>();

    HashMap newTeam = new HashMap<Integer, HashMap>();

    boolean didLoadExport = false;

    public ZetaScout() {
        super.onCreate();

    }

    public void clearMatchData(String id) {
        matchData.remove(id);
    }

    public void addTeam(String teamID) {

        matchData.put(teamID, (HashMap) newTeam.clone());
    }

    public void populateTeamData(String teamID, HashMap teamData) {
        matchData.put(teamID, teamData);
    }

    public void removeTeam(String teamID) {
        matchData.remove(teamID);
    }
}