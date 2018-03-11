package com.museop.termproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * NoticeBoardMainActivity에서 각 태그들에 대한 정보를 저장할 DBManager이다.
 */
public class TagDBManager extends SQLiteOpenHelper {

    static final String DB_NAME     = "Tag.db";
    static final String TABLE_TAG   = "Tag";
    static final int    DB_VERSION  = 3;

    Context context;

    private static TagDBManager mDbManager = null;

    public static TagDBManager getInstance(Context context) {
        if ( mDbManager == null ) {
            mDbManager = new TagDBManager(context, DB_NAME, null, DB_VERSION);
        }
        return mDbManager;
    }

    public TagDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_TAG + " (_id integer primary key autoincrement, "
                + "Tag text, isUpdate text, Type integer );";

        db.execSQL( sql );

        db.execSQL( "insert into " + TABLE_TAG + " values (null, '내 목록', ' ', -1);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '정보컴퓨터공학부', ' ', 0);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '차의영', ' ', 1);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '이정태', ' ', 2);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '권혁철', ' ', 3);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '조환규', ' ', 4);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '류광렬', ' ', 5);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '염근혁', ' ', 6);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '이도훈', ' ', 7);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '김정구', ' ', 8);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '백윤주', ' ', 9);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '김종덕', ' ', 10);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '채흥석', ' ', 11);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '탁성우', ' ', 12);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '김호원', ' ', 13);");
        db.execSQL( "insert into " + TABLE_TAG + " values (null, '최윤호', ' ', 14);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL( "drop table if exists " + TABLE_TAG );
            onCreate(db);
        }
    }

    public int update(ContentValues updateRowValue, String whereClause, String[] whereArgs) {
        return getWritableDatabase().update( TABLE_TAG, updateRowValue, whereClause, whereArgs );
    }

    public SQLiteDatabase getDatabase() {
        return getWritableDatabase();
    }
}
