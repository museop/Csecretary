package com.museop.termproject;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
/*
 * 업데이트 알림 여부, 관련 세부 정보들을 설정한다.
 */
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    // DB연동을 위한 BoardDBManager 매니저
    public BoardDBManager mDbManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_settings);

        SwitchPreference alarmEnable = (SwitchPreference) findPreference("alarmEnable");
        alarmEnable.setOnPreferenceClickListener( this );

        // 각각의 프레퍼런스의 ClickListener를 설정해준다.
        String[] keys = getResources().getStringArray( R.array.key_list );
        for (int i = 0; i < keys.length; i++) {
            CheckBoxPreference preference = (CheckBoxPreference) findPreference(keys[i]);
            preference.setOnPreferenceClickListener( this );
        }

        mDbManager = BoardDBManager.getInstance( this );
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            // 알람 설정 여부
            case "alarmEnable": {
                SwitchPreference switchPreference = (SwitchPreference) preference;
                if (switchPreference.isChecked()) {
                    // 설정을 On하면 서비스 시작
                    setLastUpdate(); // DB 업데이트
                    Intent intent = new Intent(this, MyService.class);
                    startService( intent );
                }
                else {
                    // 설정을 Off하면 서비스 종료
                    Intent intent = new Intent(this, MyService.class);
                    stopService( intent );
                }
            }
            // 각각의 프레퍼런스 설정시 DB의 last_update를 갱신해주어야 한다.
            default: {
                int time = getCurrentTimeFormat();
                // DB 갱신
                ContentValues updateRowValue = new ContentValues();
                updateRowValue.put( "last_update", String.valueOf( time ) );
                mDbManager.update( updateRowValue, "key='" + preference.getKey() + "'", null );
            }
        }
        return false;
    }

    // 현재 시간을 DB에 저장할 포맷으로 반환한다.
    private int getCurrentTimeFormat() {
        // 현재 시간 가져오기
        Calendar now = Calendar.getInstance();
        int month  = now.get( Calendar.MONTH ) + 1;
        int day    = now.get( Calendar.DAY_OF_MONTH );
        int hour   = now.get( Calendar.HOUR );
        int minute = now.get( Calendar.MINUTE );
        int amPm   = now.get( Calendar.AM_PM );

        // 오전, 오후 구분없이 24시로 구분하기 위해 오후 시간에는 12를 더해준다.
        if (amPm == Calendar.PM ) {
            hour += 12;
        }

        // DB에 갱신할 시간 정보 포맷
        return minute + 100 * hour + 10000 * day + 1000000 * month;
    }

    // Board 데이터베이스의 Boards 테이블에서 현재 프레퍼런스값이 true인 내용은 last_update를 현재 시간으로 설정한다.
    private void setLastUpdate() {

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences( this );
        String[] keys = getResources().getStringArray(R.array.key_list);

        int time = getCurrentTimeFormat();
        for (int i = 0; i < keys.length; i++) {
            if ( mPref.getBoolean( keys[i], false ) ) {
                ContentValues updateRowValue = new ContentValues();
                updateRowValue.put( "last_update", String.valueOf( time ) );
                mDbManager.update( updateRowValue, "key='" + keys[i] + "'", null );
            }
        }
    }

}
