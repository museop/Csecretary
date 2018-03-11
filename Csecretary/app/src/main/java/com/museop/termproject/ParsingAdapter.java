package com.museop.termproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/*
 * NoticeBoardActivity의 리스트뷰에서 사용될 Adapter이다.
 */
public class ParsingAdapter extends BaseAdapter {

    Context                mContext        = null;
    ArrayList<ParsingData> mData           = null;
    LayoutInflater         mLayoutInflater = null;

    // 성능 향상을 위한 뷰 재사용
    class ViewHolder {
        TextView mTitleTv;
    }

    ParsingAdapter(Context context, ArrayList<ParsingData> data) {
        mContext        = context;
        mData           = data;
        mLayoutInflater = LayoutInflater.from( mContext );
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemLayout = convertView;
        ViewHolder viewHolder;

        // 어댑터뷰가 재사용할 뷰를 넘겨주지 않는 경우에만 새로운 뷰를 생성한다.
        if ( itemLayout == null ) {
            itemLayout = mLayoutInflater.inflate( R.layout.list_view_item_layout, null );
            viewHolder = new ViewHolder();
            viewHolder.mTitleTv = (TextView) itemLayout.findViewById( R.id.title_text );
            // 뷰 저장
            itemLayout.setTag( viewHolder );
        }
        else {
            // 재사용하는 경우 저장했던 뷰를 가져온다.
            viewHolder = (ViewHolder) itemLayout.getTag();
        }

        viewHolder.mTitleTv.setText( mData.get(position).title );
        return itemLayout;
    }
}
