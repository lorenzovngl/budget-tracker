package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.HomeActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;

public class NotificationUtils {

    public static void showNotification(Context context, String title, String[] lines) {
         /* Invoking the default notification service */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        /* Creates an explicit intent for an Activity in your app */
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        mBuilder.setContentTitle(title);
        mBuilder.setTicker(title);
        if (lines.length == 1){
            mBuilder.setContentText(lines[0]);
        } else {
            mBuilder.setContentText(context.getResources().getString(R.string.tap_to_view));
        }
        // TODO on api 16 cause crash, need to use png
        //mBuilder.setSmallIcon(R.drawable.ic_menu_movements);
        mBuilder.setContentIntent(pi);

        /* Increase notification number every time a new notification arrives */
        //mBuilder.setNumber(++numMessages);

        /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(title);

        // Moves events into the big view
        for (int i = 0; i < lines.length; i++) {
            inboxStyle.addLine(lines[i]);
            Log.i("LINE", lines[i]);
        }

        mBuilder.setStyle(inboxStyle);

        /* Adds the Intent that starts the Activity to the top of the stack */
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(0, mBuilder.build());
    }

    public static void notifyForNextMovements(Context context) {
        DaoMovements daoMovements = new DaoMovements(Const.DB1_NAME, Const.DBMode.READ, context);
        List<Movement> list = daoMovements.getUpcomings(null, 3, Const.Categories.Type.ALL);
        if (list != null) {
            List<String> lines = new ArrayList<String>();
            for (int i=0; i<list.size(); i++){
                String amount = NumberUtils.doubleToCurrency(list.get(i).getAmount(), true);
                lines.add(list.get(i).getStringDate() + "  " + list.get(i).getDescription() + "  " + amount);
            }
            NotificationUtils.showNotification(context, context.getResources().getString(R.string.there_are_upcoming_movements), StringUtils.listToArray(lines));
        }
    }
}
