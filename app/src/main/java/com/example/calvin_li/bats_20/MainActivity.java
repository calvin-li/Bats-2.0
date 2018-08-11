package com.example.calvin_li.bats_20; /**
 * Created by Calvin-PC on 7/20/2016.
 */

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "com.example.calvin_li.bats_20.MainActivity";
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if we should cancel notification instead of updating
        if(getIntent().hasExtra("Action")) {
            Notification.manager.cancel(Notification.alarmPendingIntent);
            Notification.alarmPendingIntent = null;
            mNotificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Notification.notificationID);
        }
        else if(Notification.alarmPendingIntent == null) {
            makeToast("Bats started", this);
            Intent alarmIntent = new Intent(getApplicationContext(), Notification.class);
            Notification.alarmPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);

            Notification.manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Notification.manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    Notification.updateInterval, Notification.alarmPendingIntent);
        }

        finish();
    }

    public static void makeToast(String message, Context context){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
