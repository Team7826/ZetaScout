package dev.boyne.zetascout;

import android.app.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ZetaScout extends Application {

    public ArrayList<HashMap> matchData = new ArrayList<>();

    public void addMatch() {
        HashMap newMatch = new HashMap();
        matchData.add(newMatch);
    }
}