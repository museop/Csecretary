package com.museop.termproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
/*
 * 애플리케이션의 메인 액티비티로 네비게이션 역할을 수행한다.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {

        switch ( view.getId() ) {
            // 통합게시판 메인으로
            case R.id.go_noticeboard_list:
            {
                startActivity( new Intent(this, NoticeBoardMainActivity.class) );
                break;
            }
            // 컴퓨터실습실 사용현황으로
            case R.id.go_computer_room:
            {
                startActivity( new Intent(this, ComputerRoom.class) );
                break;
            }
            // 커뮤니티 게시판으로
            case R.id.go_community:
            {
                startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse("http://52.78.20.89/")) );
                break;
            }
            // 설정창으로
            case R.id.go_setting:
            {
                startActivity( new Intent(this, SettingActivity.class) );
                break;
            }
            // 앱 정보창으로
            case R.id.go_app_info:
            {
                startActivity( new Intent(this, AppInformation.class) );
                break;
            }
        }
    }
}
