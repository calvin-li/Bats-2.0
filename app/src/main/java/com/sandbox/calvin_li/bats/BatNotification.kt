package com.sandbox.calvin_li.bats

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.BatteryManager
import android.os.SystemClock
import android.app.NotificationChannel
import android.content.*
import java.time.LocalDateTime


class BatNotification : BroadcastReceiver() {
    companion object {
        private const val statusCharging = "Charging"
        private const val statusDischarging = "Discharging"
        private const val statusFull = "Full"
        private const val statusNotCharging = "Not charging"
        private const val statusUnknown = "Status unknown"
        private const val statusNotRecognized = "Status code not recognized"
        private const val statusNull = "NULL"

        const val notificationID = 23
        private const val channelId = "Battery status"
        private const val channelId_time = "Minutes clock"
        private const val lastStatus = "Last status"
        private const val lastChange = "Last change"

        internal var manager: AlarmManager? = null
        internal var alarmPendingIntent: PendingIntent? = null
        internal const val updateInterval = 1000 * 60

        fun displayNotification(context: Context) {
            val statusGetter: Intent =  context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))!!

            val level = getBatteryInfo(BatteryManager.EXTRA_LEVEL, context, statusGetter)
            val plugged = plugToMessage(getBatteryInfo(BatteryManager.EXTRA_PLUGGED, context, statusGetter))
            val status = statusToMessage(getBatteryInfo(BatteryManager.EXTRA_STATUS, context, statusGetter))
            val temperature = getBatteryInfo(BatteryManager.EXTRA_TEMPERATURE, context, statusGetter) / 10.0
            val voltage = getBatteryInfo(BatteryManager.EXTRA_VOLTAGE, context, statusGetter)

            val sharedPreferences = context.getSharedPreferences("Last charging state", Context.MODE_PRIVATE)
            val prefEdit = sharedPreferences.edit()
            prefEdit.putString(lastStatus, status)
            if(sharedPreferences.getString(lastStatus, statusNull) != status){
                prefEdit.putLong(lastChange, SystemClock.elapsedRealtime())
            }
            prefEdit.apply()
            val time = formatTime(sharedPreferences.getLong(lastChange, SystemClock.elapsedRealtime()))

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_LOW))
            notificationManager.createNotificationChannel(
                NotificationChannel(channelId_time, channelId_time, NotificationManager.IMPORTANCE_LOW))

            val icon = context.resources.getIdentifier("a$level", "mipmap", context.packageName)
            val notificationTitle = temperature.toString() + "\u00b0C" + " • " + voltage + "mV"
            val notificationText = "$status$plugged for $time"
            val resultPendingIntent =
                PendingIntent.getActivity(context, 0, Intent(Intent.ACTION_POWER_USAGE_SUMMARY),
                    PendingIntent.FLAG_IMMUTABLE)

            val minutes = LocalDateTime.now().minute
            val clock = context.resources.getIdentifier("a$minutes", "mipmap", context.packageName)

            listOf(Pair(channelId, icon), Pair(channelId_time, clock)).map {
                notificationManager.notify(
                    notificationID,
                    Notification.Builder(context, it.first)
                        .setOngoing(true)
                        .setShowWhen(false)
                        .setSmallIcon(it.second)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        .setContentIntent(resultPendingIntent)
                        .build()
                )
            }
        }

        private fun formatTime(lastUpdated: Long): String {
            var time = "Time formatting error"
            val timeInMilli = SystemClock.elapsedRealtime() - lastUpdated
            val days = (timeInMilli / (1000 * 60 * 60 * 24)).toInt()
            val hours = (timeInMilli / (1000 * 60 * 60) % 24).toInt()
            val minutes = (timeInMilli / (1000 * 60) % 60).toInt()
            val secs = (timeInMilli / 1000 % 60).toInt()
            if (days > 0) {
                time = String.format("%d:%02d:%02d:%02d", days, hours, minutes, secs)
            } else if (days == 0) {
                time = String.format("%d:%02d:%02d", hours, minutes, secs)
            }
            return time
        }

        private fun plugToMessage(state: Int): String {
            val ret = " "
            return when (state) {
                BatteryManager.BATTERY_PLUGGED_AC -> "$ret(AC)"
                BatteryManager.BATTERY_PLUGGED_USB -> "$ret(USB)"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "$ret(Wireless)"
                else -> ""
            }
        }

        private fun statusToMessage(state: Int): String {
            return when (state) {
                BatteryManager.BATTERY_STATUS_CHARGING -> statusCharging
                BatteryManager.BATTERY_STATUS_DISCHARGING -> statusDischarging
                BatteryManager.BATTERY_STATUS_FULL -> statusFull
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> statusNotCharging
                BatteryManager.BATTERY_STATUS_UNKNOWN -> statusUnknown
                else -> statusNotRecognized
            }
        }

        private fun getBatteryInfo(extra: String, context: Context, statusGetter: Intent): Int {
            val defaultValue = -1
            val status = statusGetter.getIntExtra(extra, defaultValue)
            if (status == defaultValue) {
                MainActivity.makeToast("Attribute $extra could not be fetched.", context)
            }
            return status
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, MainActivity::class.java)
            context.startService(serviceIntent)
            return
        }

        displayNotification(context)
    }
}
