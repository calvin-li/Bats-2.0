package com.sandbox.calvin_li.bats

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val workManager = WorkManager.getInstance(this)
        val batCheckImmediate: WorkRequest = OneTimeWorkRequestBuilder<BatCheckWorker>().build()
        val batCheckPeriodicBuilder = PeriodicWorkRequestBuilder<BatCheckWorker>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.MILLISECONDS
        )

        workManager.enqueue(batCheckImmediate)
        workManager.enqueueUniquePeriodicWork(
            "batCheckPeriodic",
            ExistingPeriodicWorkPolicy.UPDATE,
            (batCheckPeriodicBuilder.build())
        )
        /*
        workManager.enqueue(batCheckPeriodicBuilder.setInitialDelay(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS/2,
            TimeUnit.MILLISECONDS
        ).build())
         */

        makeToast("Bats started", this)
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
        private fun makeToast(message: String, context: Context) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
