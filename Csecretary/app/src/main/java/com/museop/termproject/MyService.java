package com.museop.termproject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
/*
 * 백그라운드 서비스로 주기적으로 알람을 호출한다.
 */
public class MyService extends Service {

    public AlarmManager alarmManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "[CSECRETARY] 서비스를 시작합니다", Toast.LENGTH_LONG).show();
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 주기적으로 서버와 통신할 수 있도록 알람을 설정한다.
        Intent intent1 = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent1, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);

        // 60초 간격으로 반복
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, sender);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 서버와 통신하는 알람을 제거한다.
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.cancel(sender);

        // 만약 억지로 죽는 서비스일 경우 재시작할 수 있도록 재시작 알림을 보낸다.
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        if ( mPref.getBoolean( "alarmEnable", false ) ) {
            // 알림 매니저, 알림 작업
            NotificationManager notificationManager;
            Notification.Builder notification;

            // PendingIntent
            PendingIntent pendingIntent;

            // 프레퍼런스
            SharedPreferences.Editor editor = mPref.edit();

            // 업데이트 알림 여부를 일단 false로 설정한다.
            editor.putBoolean("alarmEnable", false);
            editor.apply();

            // 사용자에게 업데이트 알림 여부를 설정하라고 공지한다.
            notification = new Notification.Builder( this );
            notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, SettingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT );

            notification.setSmallIcon(R.drawable.ic_cse);
            notification.setContentTitle("[CSECRETARY] 가 알려드립니다!");
            notification.setContentText("서비스를 재설정해주세요!");
            notification.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            notification.setContentIntent(pendingIntent);
            notification.setAutoCancel(true);
            notificationManager.notify(7777, notification.build());
        }
    }
}