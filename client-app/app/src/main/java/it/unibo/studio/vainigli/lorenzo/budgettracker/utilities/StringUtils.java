package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringUtils {

    public static String[] listToArray(List<String> stringList){
        if (stringList == null){
            return null;
        }
        String[] array = new String[stringList.size()];
        return stringList.toArray(array);
    }

    public static String[] setToArray(Set<String> stringSet){
        if (stringSet == null){
            return null;
        }
        String[] array = stringSet.toArray(new String[stringSet.size()]);
        return stringSet.toArray(array);
    }

    public static Set<String> listToSet(List<String> stringList){
        return new HashSet<String>(stringList);
    }

    public static List<String> setToList(Set<String> stringSet){
        return arrayToList(setToArray(stringSet));
    }

    public static List<String> arrayToList(String[] stringArray){
        return Arrays.asList(stringArray);
    }

    public static String printStringArray(String[] stringArray){
        if (stringArray == null) return "null";
        StringBuilder builder = new StringBuilder();
        boolean notFirst = false;
        for(String s : stringArray) {
            if (notFirst){
                builder.append(", ");
            }
            builder.append(s);
            notFirst = true;
        }
        return builder.toString();
    }

    public static String printStringList(List<String> stringList){
        if (stringList == null) return "null";
        StringBuilder builder = new StringBuilder();
        boolean notFirst = false;
        for(String s : stringList) {
            if (notFirst){
                builder.append(", ");
            }
            builder.append(s);
            notFirst = true;
        }
        return builder.toString();
    }
}
