package com.museop.termproject;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * NoticeBoardMainActivity의 리스트뷰에서 사용될 Adapter이다.
 */
public class TagDBAdapter extends CursorAdapter {   // CursorAdapter는 DB에 질의한 결과 데이터인 커서를 기반으로 한다.

    public TagDBAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // 리스트뷰 아이쳄 레이아웃을 생성한다.
        LayoutInflater inflater = LayoutInflater.from( context );
        View v = inflater.inflate( R.layout.list_view_tag_layout, parent, false );
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // 생성된 아이템뷰에 데이터를 적용한다.
        final ImageView image = (ImageView) view.findViewById( R.id.image );
        final TextView tag = (TextView) view.findViewById( R.id.name );
        final TextView update = (TextView) view.findViewById( R.id.update );

        image.setImageResource( R.drawable.ic_school_black_24dp );
        tag.setText( cursor.getString(cursor.getColumnIndex("Tag")) );
        update.setText( cursor.getString(cursor.getColumnIndex("isUpdate")) );
    }
}
