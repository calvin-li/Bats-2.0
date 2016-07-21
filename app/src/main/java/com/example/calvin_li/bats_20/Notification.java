package com.example.calvin_li.bats_20;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

public class Notification extends BroadcastReceiver {

    public static final String BATS_STOP = "app.calvin.bats.Notification.BATS_STOP";
    public static final int notificationID = 23;

    protected static AlarmManager manager;
    protected static PendingIntent alarmPendingIntent;
    protected static final int updateInterval = 1000*60;

    private static NotificationCompat.Builder batteryInfoBuilder;
    private static Intent batteryStatus;
    private static Boolean charging;
    private static int level, voltage;
    private static double temperature, lastChange;
    private static String plugged, status;
    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(context == null){
            context = new MainActivity();
            MainActivity.makeToast("com.example.calvin_li.bats_20.MainActivity died", context);
        }

        //if startup, create a com.example.calvin_li.bats_20.MainActivity to start AlarmManager, then return
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, MainActivity.class);
            context.startService(serviceIntent);
            return;
        }

        batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        level = getBatteryInfo(BatteryManager.EXTRA_LEVEL, context);
        plugged = plugToMessage(getBatteryInfo(BatteryManager.EXTRA_PLUGGED, context));
        status = statusToMessage(getBatteryInfo(BatteryManager.EXTRA_STATUS, context));
        temperature = getBatteryInfo(BatteryManager.EXTRA_TEMPERATURE, context) / 10.0;
        voltage = getBatteryInfo(BatteryManager.EXTRA_VOLTAGE, context);

        long timeInMilli = SystemClock.elapsedRealtime() - (long)lastChange;
        int days = (int)(timeInMilli / (1000 * 60 * 60 * 24)),
                hours = (int)(timeInMilli / (1000 * 60 * 60)%24),
                minutes = (int)(timeInMilli / (1000 * 60)%60),
                secs = (int)(timeInMilli / 1000 % 60);
        String time = "Time formatting error";
        if(days > 0) {
            String formatString = "%d:%02d:%02d:%02d";
            time = String.format(formatString, days, hours, minutes, secs);
        }
        else if(days == 0){
            String formatString = "%d:%02d:%02d";
            time = String.format(formatString, hours, minutes, secs);
        }

        String notificationTitle = status + plugged + " for " + time;
        String notificationText = temperature + "\u00b0C / " + voltage + "mV";

        if(batteryInfoBuilder == null){
            batteryInfoBuilder = new NotificationCompat.Builder(context)
                .setOngoing(true)
                .setShowWhen(false);

            Intent resultIntent = new Intent(context, MainActivity.class);

            resultIntent.putExtra("Action", "Stop");
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
            batteryInfoBuilder.setContentIntent(resultPendingIntent);
        }

        int icon = context.getResources().getIdentifier("a" + level, "mipmap", context.getPackageName());
        batteryInfoBuilder
                .setSmallIcon(icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, batteryInfoBuilder.build());
    }

    private String plugToMessage(int state) {
        //TODO: put state into memory so it persists between alarms
        String ret = " ";
        if(state == BatteryManager.BATTERY_PLUGGED_AC)
            return ret + "(AC)";
        else if(state == BatteryManager.BATTERY_PLUGGED_USB)
            return ret + "(USB)";
        else if(state == BatteryManager.BATTERY_PLUGGED_WIRELESS)
            return ret + "(Wireless)";
        else {
            return "";
        }
    }

    private String statusToMessage(int state) {
        if(charging == null){
            charging = !(state == BatteryManager.BATTERY_STATUS_NOT_CHARGING ||
                    state == BatteryManager.BATTERY_STATUS_DISCHARGING);
            lastChange = SystemClock.elapsedRealtime();
        }

        if(state == BatteryManager.BATTERY_STATUS_CHARGING) {
            if( !charging) {
                changeChargeState();
            }
            return "Charging";
        }
        else if(state == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            if(charging) {
                changeChargeState();
            }
            return "Discharging";
        }
        else if(state == BatteryManager.BATTERY_STATUS_FULL) {
            if(!charging) {
                changeChargeState();
            }
            return "Full";
        }
        else if(state == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            if(charging) {
                changeChargeState();
            }
            return "Not charging";
        }
        else if(state == BatteryManager.BATTERY_STATUS_UNKNOWN) {
            return "Status unknown";
        }
        else
            return "-1";
    }

    private void changeChargeState() {
        charging = !charging;
        lastChange = SystemClock.elapsedRealtime();
    }

    protected int getBatteryInfo(String extra, Context context){
        int defaultValue = -1;
        int ret = batteryStatus.getIntExtra(extra, defaultValue);
        String errorMessage = "Attribute " + extra + " could not be fetched.";
        if(ret == defaultValue) {
           MainActivity.makeToast(errorMessage, context.getApplicationContext());
        }
        return ret;
    }

}
