package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import android.util.Log;

/**
 * Created by Lorenzo on 16/11/2016.
 */

public class ResourceUtilities {

    public static String getResName(String path){
        if (path == null){
            return null;
        }
        String[] words = path.split("[/.]");
        int length = words.length;
        return words[length-2];
    }
}
