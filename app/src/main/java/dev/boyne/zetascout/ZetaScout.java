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

    public ZetaScout() {
        super.onCreate();
    }

    public void addMatch() {

        matchData.add((HashMap) newMatch.clone());
    }
}