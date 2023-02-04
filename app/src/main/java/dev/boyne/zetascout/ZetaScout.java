package dev.boyne.zetascout;

import android.app.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ZetaScout extends Application {

    // This contains the teams on every match, and the data for each team
    public ArrayList<HashMap<String, HashMap>> matchData = new ArrayList<>();

    HashMap newMatch = new HashMap();
    HashMap newTeam = new HashMap<Integer, HashMap>();

    boolean didLoadExport = false;

    public ZetaScout() {
        super.onCreate();

    }

    public void addMatch() {

        matchData.add((HashMap) newMatch.clone());
    }

    public void clearMatchData(int id) {
        matchData.get(id).clear();
    }

    public void addTeam(int matchID, String teamID) {

        matchData.get(matchID).put(teamID, (HashMap) newTeam.clone());
    }

    public void populateTeamData(int matchID, String teamID, HashMap teamData) {
        matchData.get(matchID).put(teamID, teamData);
    }

    public HashMap getTeamsInMatch(int matchID) {

        return matchData.get(matchID);
    }

    public void removeTeam(int matchID, String teamID) {
        matchData.get(matchID).remove(teamID);
    }
}