package com.jeg.te.justenoughgoods.slave_list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.list_item_data_class.Slave;
import com.jeg.te.justenoughgoods.utilities.DateTimeConvertUtilities;
import com.jeg.te.justenoughgoods.utilities.MyApplication;

import java.util.ArrayList;

public class SlaveListAdapter extends BaseAdapter {
    private ArrayList<Slave> slaveList;
    private LayoutInflater slavesInflater;

    public SlaveListAdapter(Activity activity){
        super();
        slaveList = new ArrayList<>();
        slavesInflater = activity.getLayoutInflater();
    }

    public void addSlaves(Slave slave){
        if( !slaveList.contains(slave) )
        {
            slaveList.add(slave);
            notifyDataSetChanged();
        }
    }

    public void clearSlaves(){
        slaveList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return slaveList.size();
    }

    @Override
    public Object getItem( int position )
    {
        return slaveList.get( position );
    }

    @Override
    public long getItemId( int position )
    {
        return position;
    }

    static class ViewHolder
    {
        TextView textViewSlaveSId;
        TextView textViewSlaveName;
        TextView textViewLastReceived;
        AwesomeTextView awesomeTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ) {
        SlaveListAdapter.ViewHolder viewHolder;
        // General ListView optimization code.
        if (null == convertView) {
            convertView = slavesInflater.inflate(R.layout.fragment_slave_list_listitem, parent, false);
            viewHolder = new SlaveListAdapter.ViewHolder();

            viewHolder.textViewSlaveSId = convertView.findViewById(R.id.textView_slaveId);
            viewHolder.textViewSlaveName = convertView.findViewById(R.id.textView_slaveName);
            viewHolder.textViewLastReceived = convertView.findViewById(R.id.textView_lastReceived);
            viewHolder.awesomeTextView = convertView.findViewById(R.id.awesomeTextView_newSlave);
            convertView.setTag( viewHolder );
        }
        else {
            viewHolder = (SlaveListAdapter.ViewHolder)convertView.getTag();
        }

        Slave slave = slaveList.get( position );

        viewHolder.textViewSlaveSId.setText(slave.getSId());
        viewHolder.textViewSlaveName.setText(MyApplication.getContext().getString(R.string.slave_list_slave_name, slave.getName()));
        viewHolder.textViewLastReceived.setText(MyApplication.getContext().getString(R.string.slave_list_lastReceived, DateTimeConvertUtilities.convertLongToDateFormatDefault(slave.getLastUpdate())));

        if(slave.getIsNew() == 1){
            viewHolder.awesomeTextView.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.awesomeTextView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
