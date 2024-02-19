package com.sandbox.calvin_li.bats

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sandbox.calvin_li.bats.ui.theme.BatsTheme

class MainActivity : ComponentActivity() {
    private var mNotificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BatsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        //check if we should cancel notification instead of updating
        if (intent.hasExtra("Action")) {
            BatNotification.manager?.cancel(BatNotification.alarmPendingIntent!!)
            BatNotification.alarmPendingIntent = null
            mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager!!.cancel(BatNotification.notificationID)
        } else if (BatNotification.alarmPendingIntent == null) {
            BatNotification.displayNotification(this)

            val alarmIntent = Intent(applicationContext, BatNotification::class.java)
            BatNotification.alarmPendingIntent = PendingIntent.getBroadcast(applicationContext, 0, alarmIntent,
                PendingIntent.FLAG_IMMUTABLE)

            BatNotification.manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            BatNotification.manager!!.setRepeating(
                AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                BatNotification.updateInterval.toLong(), BatNotification.alarmPendingIntent!!)
            MainActivity.Companion.makeToast("Bats started", this)
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

    companion object {
        fun makeToast(message: String, context: Context) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BatsTheme {
        Greeting("Android")
    }
}