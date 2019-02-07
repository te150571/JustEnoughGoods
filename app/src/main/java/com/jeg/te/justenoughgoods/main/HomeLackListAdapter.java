package com.jeg.te.justenoughgoods.main;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.BootstrapProgressBarGroup;
import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.list_item_data_class.Slave;
import com.jeg.te.justenoughgoods.utilities.MyApplication;

import java.util.ArrayList;

import static com.jeg.te.justenoughgoods.utilities.DateTimeConvertUtilities.convertLongToDateFormatDefault;

public class HomeLackListAdapter extends BaseAdapter {
    private ArrayList<Slave> lacks;
    private LayoutInflater lacksInflater;

    public HomeLackListAdapter(Activity activity){
        super();
        lacks = new ArrayList<>();
        lacksInflater = activity.getLayoutInflater();
    }

    public void addSlaves(Slave slave){
        if( !lacks.contains(slave) )
        {
            lacks.add(slave);
            notifyDataSetChanged();
        }
    }

    public void clearSlaves(){
        lacks.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return lacks.size();
    }

    @Override
    public Object getItem( int position )
    {
        return lacks.get( position );
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
        TextView viewSlaveAmountValue;
        TextView viewSlaveNotificationValue;
        TextView viewSlaveLastUpdateValue;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent )
    {
        HomeLackListAdapter.ViewHolder viewHolder;
        // General ListView optimization code.
        if( null == convertView )
        {
            convertView = lacksInflater.inflate( R.layout.home_lack_listitem, parent, false );
            viewHolder = new HomeLackListAdapter.ViewHolder();
            viewHolder.viewSlaveId = convertView.findViewById( R.id.textView_slaveId);
            viewHolder.viewSlaveName = convertView.findViewById( R.id.textView_slaveName );
            viewHolder.viewSlaveAmountValue = convertView.findViewById( R.id.textView_slaveAmountValue );
            viewHolder.viewSlaveNotificationValue = convertView.findViewById( R.id.textView_slaveNotificationValue );
            viewHolder.viewSlaveLastUpdateValue = convertView.findViewById( R.id.textView_slaveLastUpdateValue );
            convertView.setTag( viewHolder );
        }
        else
        {
            viewHolder = (HomeLackListAdapter.ViewHolder)convertView.getTag();
        }

        Slave slave = lacks.get( position );

        viewHolder.viewSlaveId.setText( slave.getSId() );
        String slaveName = slave.getName();
        if( null != slaveName && 0 < slaveName.length() ) {
            viewHolder.viewSlaveName.setText( slaveName );
        }
        else {
            viewHolder.viewSlaveName.setText(R.string.unknown_slave);
        }

        int amount = (int) (slave.getAmount() * 1000.0);
        int notification = (int) (slave.getNotificationAmount() * 1000.0);

        if(amount < 2000){
            viewHolder.viewSlaveAmountValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_now_value, (amount)) );
        }
        else {
            viewHolder.viewSlaveAmountValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_now_over) );
        }

        viewHolder.viewSlaveNotificationValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_notification_value, notification) );

        viewHolder.viewSlaveLastUpdateValue.setText( MyApplication.getContext().getResources().getString( R.string.amount_date_value, convertLongToDateFormatDefault(slave.getLastUpdate())) );

//        if(amount < notification){
//            viewHolder.viewSlaveName.setTextColor( MyApplication.getContext().getColor(R.color.amountWarning) );
//            viewHolder.viewSlaveAmountValue.setTextColor( MyApplication.getContext().getColor(R.color.amountWarning) );
//        }
//        else {
//            viewHolder.viewSlaveName.setTextColor( MyApplication.getContext().getColor(R.color.defaultNormal) );
//            viewHolder.viewSlaveAmountValue.setTextColor( MyApplication.getContext().getColor(R.color.defaultNormal) );
//        }
        return convertView;
    }
}
