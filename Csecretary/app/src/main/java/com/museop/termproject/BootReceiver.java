package com.museop.termproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/*
 * BootReceiver는 Device의 전원이 켜졌을 때,
 * android:intent:action.BOOT_COMPLETED 인텐트를 받아
 * onRecive()가 호출된다.
 */
public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            // 알림 매니저, 알림 작업
            NotificationManager notificationManager;
            Notification.Builder notification;

            // PendingIntent
            PendingIntent pendingIntent;

            // 프레퍼런스
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = mPref.edit();

            // 업데이트 알림 여부를 일단 false로 설정한다.
            editor.putBoolean("alarmEnable", false);
            editor.apply();

            // 사용자에게 업데이트 알림 여부를 설정하라고 공지한다.
            notification = new Notification.Builder( context );
            notificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );

            pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, SettingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT );

            notification.setSmallIcon(R.drawable.ic_cse);
            notification.setContentTitle("[CSECRETARY] 가 알려드립니다!");
            notification.setContentText("게시판 알림 여부를 설정해주세요!");
            notification.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            notification.setContentIntent(pendingIntent);
            notification.setAutoCancel(true);
            notificationManager.notify(7777, notification.build());
        }
    }


}
