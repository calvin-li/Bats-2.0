package com.example.calvin_li.bats_20

/**
 * Created by Calvin-PC on 7/20/2016.
 */

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var mNotificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //check if we should cancel notification instead of updating
        if (intent.hasExtra("Action")) {
            BatNotification.manager!!.cancel(BatNotification.alarmPendingIntent)
            BatNotification.alarmPendingIntent = null
            mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager!!.cancel(BatNotification.notificationID)
        } else if (BatNotification.alarmPendingIntent == null) {
            val alarmIntent = Intent(applicationContext, BatNotification::class.java)
            BatNotification.alarmPendingIntent = PendingIntent.getBroadcast(applicationContext, 0, alarmIntent, 0)

            BatNotification.manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            BatNotification.manager!!.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                BatNotification.updateInterval.toLong(), BatNotification.alarmPendingIntent)
            makeToast("Bats started", this)
        }

        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {

        private val TAG = "com.example.calvin_li.bats_20.MainActivity"

        fun makeToast(message: String, context: Context) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
