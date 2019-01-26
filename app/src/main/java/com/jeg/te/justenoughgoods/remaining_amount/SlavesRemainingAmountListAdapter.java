package com.jeg.te.justenoughgoods.remaining_amount;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jeg.te.justenoughgoods.list_item_data_class.Slave;
import com.jeg.te.justenoughgoods.utilities.MyApplication;
import com.jeg.te.justenoughgoods.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.jeg.te.justenoughgoods.utilities.DateTimeConvertUtilities.convertLongToDateFormatDefault;

public class SlavesRemainingAmountListAdapter extends BaseAdapter {
    private ArrayList<Slave> slaveList;
    private LayoutInflater slavesInflater;

    public SlavesRemainingAmountListAdapter(Activity activity )
    {
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
        TextView viewSlaveId;
        TextView viewSlaveName;
        ProgressBar viewSlaveAmountBar;
        TextView viewSlaveAmountValue;
        TextView viewSlaveMarginValue;
        TextView viewSlaveNotificationValue;
        TextView viewSlaveLastUpdateValue;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent )
    {
        SlavesRemainingAmountListAdapter.ViewHolder viewHolder;
        // General ListView optimization code.
        if( null == convertView )
        {
            convertView = slavesInflater.inflate( R.layout.fragment_remaining_amount_listitem, parent, false );
            viewHolder = new SlavesRemainingAmountListAdapter.ViewHolder();
            viewHolder.viewSlaveId = convertView.findViewById( R.id.textView_slaveId);
            viewHolder.viewSlaveName = convertView.findViewById( R.id.textView_slaveName );
            viewHolder.viewSlaveAmountBar = convertView.findViewById( R.id.progressBar_amount );
            viewHolder.viewSlaveAmountValue = convertView.findViewById( R.id.textView_slaveAmountValue );
            viewHolder.viewSlaveMarginValue = convertView.findViewById( R.id.textView_slaveMarginValue );
            viewHolder.viewSlaveNotificationValue = convertView.findViewById( R.id.textView_slaveNotificationValue );
            viewHolder.viewSlaveLastUpdateValue = convertView.findViewById( R.id.textView_slaveLastUpdateValue );
            convertView.setTag( viewHolder );
        }
        else
        {
            viewHolder = (SlavesRemainingAmountListAdapter.ViewHolder)convertView.getTag();
        }

        Slave slave = slaveList.get( position );

        viewHolder.viewSlaveId.setText( slave.getSId() );
        String slaveName = slave.getName();
        if( null != slaveName && 0 < slaveName.length() )
        {
            viewHolder.viewSlaveName.setText( slaveName );
        }
        else
        {
            viewHolder.viewSlaveName.setText( R.string.unknown_slave );
        }

        viewHolder.viewSlaveAmountBar.setMax((int) (slave.getNotificationAmount() * 5000.0));
        int amount = (int) (slave.getAmount() * 1000.0);
        int notification = (int) (slave.getNotificationAmount() * 1000.0);

        viewHolder.viewSlaveAmountBar.setSecondaryProgress( amount );
        viewHolder.viewSlaveAmountBar.setProgress( notification );

        if(amount < 2000){
            viewHolder.viewSlaveAmountValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_now_value, (amount)) );
        }
        else {
            viewHolder.viewSlaveAmountValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_now_over) );
        }

        viewHolder.viewSlaveMarginValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_margin_value, (amount - notification)) );

        viewHolder.viewSlaveNotificationValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_notification_value, notification) );

        viewHolder.viewSlaveLastUpdateValue.setText( MyApplication.getContext().getResources().getString( R.string.amount_date_value, convertLongToDateFormatDefault(slave.getLastUpdate())) );

        if(amount < notification){
            viewHolder.viewSlaveName.setTextColor( MyApplication.getContext().getColor(R.color.amountWarning) );
            viewHolder.viewSlaveAmountValue.setTextColor( MyApplication.getContext().getColor(R.color.amountWarning) );
            viewHolder.viewSlaveMarginValue.setTextColor( MyApplication.getContext().getColor(R.color.amountWarning) );
        }
        else {
            viewHolder.viewSlaveName.setTextColor( MyApplication.getContext().getColor(R.color.defaultNormal) );
            viewHolder.viewSlaveAmountValue.setTextColor( MyApplication.getContext().getColor(R.color.defaultNormal) );
            viewHolder.viewSlaveMarginValue.setTextColor( MyApplication.getContext().getColor(R.color.defaultNormal) );
        }
        return convertView;
    }

    public ArrayList<String> getLackList(){
        ArrayList<String> lackList = new ArrayList<>();
        for(Slave slave : slaveList){
            if((slave.getAmount() - slave.getNotificationAmount()) <= 0){
                lackList.add(slave.getName());
            }
        }
        return lackList;
    }
}
