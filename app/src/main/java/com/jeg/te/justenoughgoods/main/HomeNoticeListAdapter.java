package com.jeg.te.justenoughgoods.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.list_item_data_class.Notice;

import java.util.ArrayList;

public class HomeNoticeListAdapter extends BaseAdapter {
    ArrayList<Notice> notices;
    private LayoutInflater noticesInflater;

    public HomeNoticeListAdapter(Activity activity){
        super();
        notices = new ArrayList<>();
        noticesInflater = activity.getLayoutInflater();
    }

    public void addNotices(Notice notice){
        if(!notices.contains(notice)){
            notices.add(notice);
            notifyDataSetChanged();
        }
    }

    public void clearNotices(){
        notices.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return notices.size();
    }

    @Override
    public Object getItem( int position )
    {
        return notices.get( position );
    }

    @Override
    public long getItemId( int position )
    {
        return position;
    }

    static class ViewHolder {
        TextView textViewNoticeText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ) {
        HomeNoticeListAdapter.ViewHolder viewHolder;
        if( null == convertView ) {
            convertView = noticesInflater.inflate( R.layout.home_notice_listitem, parent, false );
            viewHolder = new HomeNoticeListAdapter.ViewHolder();
            viewHolder.textViewNoticeText = convertView.findViewById(R.id.textView_noticeText);
            convertView.setTag( viewHolder );
        }
        else {
            viewHolder = (HomeNoticeListAdapter.ViewHolder)convertView.getTag();
        }

        Notice notice = notices.get(position);

        viewHolder.textViewNoticeText.setText(notice.getNoticeText());

        return convertView;
    }
}
