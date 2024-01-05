package dev.boyne.zetascout;

import android.content.Context;
import android.content.Intent;

import java.io.InputStream;

public class Util {
    public static Intent moveSetupGenerator(Context origin, Class<?> target) {
        Intent nextIntent = new Intent(origin, target);
        nextIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return nextIntent;
    }


}
