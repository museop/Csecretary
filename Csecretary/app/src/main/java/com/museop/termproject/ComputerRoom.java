package com.museop.termproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ComputerRoom extends AppCompatActivity {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        private Handler handler = new Handler();
        private ViewUpdateTast timerTask = null;
        private Timer timer;

        private TextView tv_currentTime;
        public TextView[] mondayTextViews, tuesdayTextViews, wednesdayTextViews, thursdayTextViews;

        private final static int[] mondayIds    = { R.id.time_0900_mon, R.id.time_1030_mon, R.id.time_1200_mon, R.id.time_1330_mon, R.id.time_1500_mon, R.id.time_1630_mon, R.id.time_1800_mon };
        private final static int[] tuesdayIds   = { R.id.time_0900_tue, R.id.time_1030_tue, R.id.time_1200_tue, R.id.time_1330_tue, R.id.time_1500_tue, R.id.time_1630_tue, R.id.time_1800_tue };
        private final static int[] wednesdayIds = { R.id.time_0900_wed, R.id.time_1030_wed, R.id.time_1200_wed, R.id.time_1330_wed, R.id.time_1500_wed, R.id.time_1630_wed, R.id.time_1800_wed };
        private final static int[] thursdayIds  = { R.id.time_0900_thu, R.id.time_1030_thu, R.id.time_1200_thu, R.id.time_1330_thu, R.id.time_1500_thu, R.id.time_1630_thu, R.id.time_1800_thu };

        private String[] mondaySubjects, tuesdaySubjects, wednesdaySubjects, thursdaySubjects;

        class ViewUpdateTast extends TimerTask {
            @Override
            public void run() {
                handler.post( mUpdateTimerTask );
            }
        }

        // 실행 유닛
        private Runnable mUpdateTimerTask = new Runnable() {
            @Override
            public void run() {
                // 현재 날짜를 받아온다.
                Calendar now = Calendar.getInstance();
                int year   = now.get( Calendar.YEAR );
                int month  = now.get( Calendar.MONTH ) + 1;
                int day    = now.get( Calendar.DAY_OF_MONTH );
                int week   = now.get( Calendar.DAY_OF_WEEK );
                int hour   = now.get( Calendar.HOUR );
                int minute = now.get( Calendar.MINUTE );
                int second = now.get( Calendar.SECOND );
                int amPm   = now.get( Calendar.AM_PM );

                // 오전, 오후 구분없이 24시로 구분하기 위해 오후 시간에는 12를 더해준다.
                if (amPm == Calendar.PM ) {
                    hour += 12;
                }

                // 현재 시간을 구분하기 위해 시간을 재구성한다. (월일시분, 총 7 ~ 8자리 정수)
                int time = hour * 100 + minute;

                int index;
                if (time >= 900 && time < 1030) {
                    index = 0;
                } else if (time >= 1030 && time < 1200) {
                    index = 1;
                } else if (time >= 1300 && time < 1500) {
                    index = 3;
                } else if (time >= 1500 && time < 1630) {
                    index = 4;
                } else if (time >= 1630 && time < 1800) {
                    index = 5;
                } else if (time >= 1800 && time < 2300) {
                    index = 6;
                } else {
                    index = -2; // index -2는 시간표에 없는 시간
                }

                String weekString = "";
                // 현재 시간이 시간표에 있는 경우에만
                if (index != -2) {
                    switch (week) {
                        case Calendar.MONDAY:
                            weekString = "월";
                            // 해당 텍스트에 임팩트 주기
                            mondayTextViews[index].setBackgroundResource(R.drawable.selector3);
                            break;
                        case Calendar.TUESDAY:
                            weekString = "화";
                            tuesdayTextViews[index].setBackgroundResource(R.drawable.selector3);
                            break;
                        case Calendar.WEDNESDAY:
                            weekString = "수";
                            wednesdayTextViews[index].setBackgroundResource(R.drawable.selector3);
                            break;
                        case Calendar.THURSDAY:
                            weekString = "목";
                            thursdayTextViews[index].setBackgroundResource(R.drawable.selector3);
                            break;
                        case Calendar.FRIDAY:
                            weekString = "금";
                            break;
                        case Calendar.SATURDAY:
                            weekString = "토";
                            break;
                        case Calendar.SUNDAY:
                            weekString = "일";
                    }
                }

                // 시간 찍기
                String dateString = year + "년 " + month + "월 " + day + "일 " + weekString + "요일 " + hour + "시 " + minute + "분 " + second + "초 ";
                tv_currentTime.setText(dateString);

            }
        };

        public PlaceholderFragment() {

        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            setTimer();
            View rootView = inflater.inflate(R.layout.fragment_computer_room, container, false);

            tv_currentTime = (TextView) rootView.findViewById(R.id.section_label);

            mondayTextViews    = new TextView[7];
            tuesdayTextViews   = new TextView[7];
            wednesdayTextViews = new TextView[7];
            thursdayTextViews  = new TextView[7];

            // 텍스트뷰-리소스 바인딩
            for (int i = 0; i < 7; i++) {
                mondayTextViews[i] = (TextView) rootView.findViewById(mondayIds[i]);
                tuesdayTextViews[i] = (TextView) rootView.findViewById(tuesdayIds[i]);
                wednesdayTextViews[i] = (TextView) rootView.findViewById(wednesdayIds[i]);
                thursdayTextViews[i]  = (TextView) rootView.findViewById(thursdayIds[i]);
            }

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 0: {   // 6408
                    mondaySubjects = getResources().getStringArray( R.array.MON1 );
                    tuesdaySubjects = getResources().getStringArray( R.array.TUE1 );
                    wednesdaySubjects = getResources().getStringArray(R.array.WED1);
                    thursdaySubjects = getResources().getStringArray( R.array.THU1 );
                    break;
                }
                case 1: {   // 6409
                    mondaySubjects = getResources().getStringArray( R.array.MON2 );
                    tuesdaySubjects = getResources().getStringArray( R.array.TUE2 );
                    wednesdaySubjects = getResources().getStringArray( R.array.WED2 );
                    thursdaySubjects = getResources().getStringArray( R.array.THU2 );
                    break;
                }
                case 2:  {  // 6409-1
                    mondaySubjects = getResources().getStringArray( R.array.MON3 );
                    tuesdaySubjects = getResources().getStringArray( R.array.TUE3 );
                    wednesdaySubjects = getResources().getStringArray( R.array.WED3 );
                    thursdaySubjects = getResources().getStringArray( R.array.THU3 );
                    break;
                }
            }
            // 과목들 출력
            setViewContents();

            return rootView;
        }

        // 과목들 출력하기
        public void setViewContents() {
            for (int i = 0; i < 7; i++) {
                mondayTextViews[i].setText( mondaySubjects[i] );
                tuesdayTextViews[i].setText( tuesdaySubjects[i] );
                wednesdayTextViews[i].setText( wednesdaySubjects[i] );
                thursdayTextViews[i].setText( thursdaySubjects[i] );
            }
        }

        // 타이머 설정하기
        public void setTimer() {
            if (timerTask == null) {
                timerTask = new ViewUpdateTast();
                timer = new Timer();
                timer.schedule(timerTask, 500, 1000); // 0.5초 뒤부터 1초 간격으로
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            timer.cancel(); // 타이머 죽이기
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "6408";
                case 1:
                    return "6409";
                case 2:
                    return "6409-1";
            }
            return null;
        }
    }
}
