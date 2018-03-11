package com.museop.termproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
/*
 * 통합 게시판 메인으로 각 게시판에 대한 네비게이션 역할을 한다.
 */
public class NoticeBoardMainActivity extends AppCompatActivity {

    public ListView listview;
    public SQLiteDatabase db;

    public Cursor cursor;

    public TagDBManager tagDBManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board_main);

        listview = (ListView) findViewById( R.id.list_view_tag );
        tagDBManager = TagDBManager.getInstance( this );

        selectDB();

        // 리스트뷰 아이템 클릭 시 수행될 작업 설정
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                String tag = cursor.getString(cursor.getColumnIndex("Tag"));
                int type = cursor.getInt(cursor.getColumnIndex("Type"));
                Intent intent = new Intent();
                intent.setClass(NoticeBoardMainActivity.this, NoticeBoardActivity.class);
                intent.putExtra( "TAG", tag );
                intent.putExtra( "SUBJECT", "DEFAULT" );
                intent.putExtra( "TYPE", type );

                startActivity(intent);
            }
        });

    }

    // 리스트뷰와 DB질의 결과를 바인딩한다.
    private void selectDB() {
        String sql;
        db = tagDBManager.getDatabase();
        sql = "select * from Tag;";

        cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            startManagingCursor(cursor);
            TagDBAdapter dbAdapter = new TagDBAdapter(this, cursor);
            listview.setAdapter(dbAdapter);
        }
    }
}
