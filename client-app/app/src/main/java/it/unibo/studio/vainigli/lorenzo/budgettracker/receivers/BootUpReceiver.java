package it.unibo.studio.vainigli.lorenzo.budgettracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NotificationUtils;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.notifyForNextMovements(context);
    }
}
