package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import android.util.Log;

import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;

public class ListUtils {

    public static void printListItems(List<Movement> list){
        for (int i=0; i<list.size(); i++){
            Log.i("List", i+": "+list.get(i).getStringDate()+", "+list.get(i).getDescription());
        }
    }

    /*public static List<Object> cursorToList(Cursor cursor, Class<?> type){
        List<Object> buffer = new ArrayList<Object>();
        if (cursor.getCount() == 0) {
            return null;
        }
        if (type == Account.class){
            while (cursor.moveToNext()) {
                Account account = new Account();
                account.setId(Integer.parseInt(cursor.getString(Const.Accounts.ID)));
                account.setDescription(cursor.getString(Const.Accounts.DESC));
                buffer.add(account);
            }
        }
        return buffer;
    }*/
}
