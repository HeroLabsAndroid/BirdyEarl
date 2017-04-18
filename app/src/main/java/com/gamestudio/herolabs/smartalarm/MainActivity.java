package com.gamestudio.herolabs.smartalarm;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gamestudio.herolabs.smartalarm.Interfaces.GameInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    boolean set = false;

    Button toggleBtn;

    TextView timeTextView;

    AlarmManager alarmManager;

    TimePicker timePicker;

    Calendar calendar;

    PendingIntent pendingIntent;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        initAlarmManager();
        initTimePicker();

        timeTextView = (TextView) findViewById(R.id.timeTextView);

        toggleBtn = (Button) findViewById(R.id.toggleBtn);
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    private void sendIntent() {
        intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
    }

    private void initTimePicker() {
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
    }

    private void initAlarmManager() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    private void toggle() {
        if(set) {
            set = false;
            toggleBtn.setText("Set");

            alarmManager.cancel(pendingIntent);

            timeTextView.setText("No alarm set");
        } else {
            set = true;
            toggleBtn.setText("Unset");

            calendar = Calendar.getInstance();

            if(calendar.get(Calendar.HOUR_OF_DAY)+(calendar.get(Calendar.MINUTE)/60) >= timePicker.getHour() + (timePicker.getMinute()/60))
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);

            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());

            int hours = timePicker.getHour();
            int minutes = timePicker.getMinute();
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            if(minutes >= 10) timeTextView.setText("Alarm set for "+hours+":"+minutes+", "+day+"."+month); else
                timeTextView.setText("Alarm set for "+hours+":0"+minutes+", "+day+"."+month);

            sendIntent();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessage(Message event){
        set = false;
        toggleBtn.setText("Set");

        alarmManager.cancel(pendingIntent);

        timeTextView.setText("No alarm set");

        Intent stopIntent = new Intent(this, RingtonePlayingService.class);
        stopIntent.putExtra("state", false);
        startService(stopIntent);
        Log.v("onMessage","intent sent");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
