package fi.zmengames.zen;

import android.app.Activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import fr.neamar.kiss.BuildConfig;
import fr.neamar.kiss.R;

import android.os.Vibrator;

import java.util.Calendar;
import java.util.Date;

import static fi.zmengames.zen.LauncherService.ALARM_ENTERED_TEXT;
import static fr.neamar.kiss.MainActivity.ALARM_IN_ACTION;

public class AlarmActivity extends Activity {
    public static final String ALARM_ID = "com.zmengames.zenlauncher.ALARM_ID";
    public static final String ALARM_EXTRA = "com.zmengames.zenlauncher.ALARM_EXTRA";
    public static final String ALARM_TIME = "com.zmengames.zenlauncher.ALARM_TIME";
    Ringtone r;
    private Intent mAlarmIntent;
    private static final String TAG = AlarmActivity.class.getSimpleName();
    private String mAlarmId;
    String alarmText = "";

    @Override
    public void onNewIntent(Intent newIntent) {
        if (BuildConfig.DEBUG) Log.d(TAG,"onNewIntent:");
        this.setIntent(newIntent);

        // Now getIntent() returns the updated Intent
        mAlarmIntent = getIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) Log.d(TAG,"onCreate:");
        if ( getIntent()!=null){
            mAlarmIntent = getIntent();
        }
        if (mAlarmIntent!=null && mAlarmIntent.getStringExtra(ALARM_TIME) != null) {
            alarmText = getIntent().getStringExtra(ALARM_TIME) + "\n" + mAlarmIntent.getStringExtra(ALARM_EXTRA);
            if (BuildConfig.DEBUG) Log.d(TAG,"1:"+  getIntent().getStringExtra(ALARM_TIME));Log.d(TAG,"alarmText:"+alarmText);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_alarm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        disableDnd();
    }

    private void disableDnd() {

        // Get the notification manager instance
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // If api level minimum 23
            /*
                boolean isNotificationPolicyAccessGranted ()
                    Checks the ability to read/modify notification policy for the calling package.
                    Returns true if the calling package can read/modify notification policy.
                    Request policy access by sending the user to the activity that matches the
                    system intent action ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS.

                    Use ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED to listen for
                    user grant or denial of this access.

                Returns
                    boolean

            */
            // If notification policy access granted for this package
            if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                /*
                    void setInterruptionFilter (int interruptionFilter)
                        Sets the current notification interruption filter.

                        The interruption filter defines which notifications are allowed to interrupt
                        the user (e.g. via sound & vibration) and is applied globally.

                        Only available if policy access is granted to this package.

                    Parameters
                        interruptionFilter : int
                        Value is INTERRUPTION_FILTER_NONE, INTERRUPTION_FILTER_PRIORITY,
                        INTERRUPTION_FILTER_ALARMS, INTERRUPTION_FILTER_ALL
                        or INTERRUPTION_FILTER_UNKNOWN.
                */

                // Set the interruption filter
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            } else {
                /*
                    String ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                        Activity Action : Show Do Not Disturb access settings.
                        Users can grant and deny access to Do Not Disturb configuration from here.

                    Input : Nothing.
                    Output : Nothing.
                    Constant Value : "android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"
                */
                // If notification policy access not granted for this package
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Button snooze = findViewById(R.id.snooze);
        TextView textView = findViewById(R.id.alarmText);
        if (!alarmText.isEmpty()) {
            textView.setText(alarmText);
        }
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String snoozeButtonText = getText(R.string.snooze) + " " + i + " " + getText(R.string.minutes);
                snooze.setText(snoozeButtonText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        String snoozeButtonText = getText(R.string.snooze) + " " + seekBar.getProgress() + " " + getText(R.string.minutes);
        ;
        snooze.setText(snoozeButtonText);
        String finalAlarmText = alarmText;
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snoozeAlarm(finalAlarmText, seekBar.getProgress());
                stopSound();
                returnHome(mAlarmIntent);
            }
        });
        Button awake = findViewById(R.id.awake);
        awake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSound();
                returnHome(mAlarmIntent);
            }
        });

        // Get instance of Vibrator from current Context
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        // Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {300, 200, 300, 200, 100, 200, 100, 200, 100, 200, 300, 200, 100,
                300, 200, 300, 200, 100, 200, 100, 200, 100, 200, 300, 200, 100,
                300, 200, 300, 200, 100, 200, 100, 200, 100, 200, 300, 200, 100,
                300, 200, 300, 200, 100, 200, 100, 200, 100, 200, 300, 200, 100,
                300, 200, 300, 200, 100, 200, 100, 200, 100, 200, 300, 200, 100};
        if (vib != null && vib.hasVibrator()) {
            vib.vibrate(pattern, -1);
        }

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alert == null) {
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if (alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }


        r = RingtoneManager.getRingtone(getApplicationContext(), alert);
        r.play();
    }

    private void returnHome(Intent intent) {
        AlarmUtils.cancelAlarm(getApplicationContext(), getIntent().getLongExtra(ALARM_ID,0));
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }

    private void stopSound() {
        if (BuildConfig.DEBUG) Log.d(TAG, "stopSound");
        if (r.isPlaying()) {
            r.stop();
        }
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vib != null && vib.hasVibrator()) {
            vib.cancel();
        }

    }

    private void snoozeAlarm(String alarmText, int minutes) {
        Intent snoozeIntent = new Intent(getApplicationContext(), LauncherService.class);
        snoozeIntent.putExtra(ALARM_ENTERED_TEXT, alarmText + "\n" + "SNOOZED!");
        snoozeIntent.putExtra(ZenProvider.mMinutes, minutes);
        snoozeIntent.setAction(ALARM_IN_ACTION);
        startService(snoozeIntent);
    }

    @Override
    public void onBackPressed() {
      /*  if (r.isPlaying()){
            r.stop();
        }
        super.onBackPressed(); */
    }

    @Override
    public void onStop() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onStop");
        super.onStop();
        stopSound();
    }

}
