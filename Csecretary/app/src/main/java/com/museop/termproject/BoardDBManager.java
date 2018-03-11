package com.museop.termproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * 각 게시판에 대한 정보를 저장할 BoardDBManager이다.
 */
public class BoardDBManager extends SQLiteOpenHelper {

    // DB명, 테이블명, DB 버전
    static final String     DB_NAME         = "Boards.db";
    static final String     TABLE_BOARDS    = "Boards";
    static final int        DB_VERSION      = 2;

    // Context
    Context mContext = null;

    // 객체는 싱글톤으로 구현
    private static BoardDBManager mDbManager = null;

    // BoardDBManager 인스턴스를 반환한다.
    public static BoardDBManager getInstance(Context context) {
        if ( mDbManager == null ) {
            mDbManager = new BoardDBManager( context, DB_NAME, null, DB_VERSION );
        }
        return mDbManager;
    }

    // 생성자
    private BoardDBManager(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super( context, dbName, factory, version );
        mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 테이블 설정하기
        db.execSQL( "create table if not exists " + TABLE_BOARDS + "("
                + "_id integer primary key autoincrement, "
                + "key text, "
                + "subject text, "
                + "professor text, "
                + "last_update text, "
                + "type integer );" );

        // 테이블의 정보를 위한 리소스들을 설정한다.
        String[] keyList        = mContext.getResources().getStringArray( R.array.key_list );
        String[] subjectList    = mContext.getResources().getStringArray( R.array.board_list );
        String[] professorList  = mContext.getResources().getStringArray( R.array.professors );
        int[]    typeList       = mContext.getResources().getIntArray( R.array.type_list );

        // 관련 정보들을 테이블에 넣는다.
        String insertSQL;
        for (int i = 0; i < subjectList.length; i++) {
            insertSQL = "insert into " + TABLE_BOARDS + " values(null, '"
                    + keyList[i] + "', '"
                    + subjectList[i] + "', '"
                    + professorList[i] + "', "
                    + "'00000000', "
                    + typeList[i] + ");";
            db.execSQL( insertSQL );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if ( oldVersion < newVersion ) {
            // 기존의 테이블을 삭제하고 새로운 테이블을 생성한다.
            db.execSQL( "drop table if exists " + TABLE_BOARDS );
            onCreate( db );
        }
    }

    public int update(ContentValues updateRowValue, String whereClause, String[] whereArgs) {
        return getWritableDatabase().update( TABLE_BOARDS, updateRowValue, whereClause, whereArgs );
    }

    public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return getReadableDatabase().query( TABLE_BOARDS, columns, selection, selectionArgs, groupBy, having, orderBy );
    }
}
