package com.jeg.te.justenoughgoods;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.jeg.te.justenoughgoods.Utilities.convertLongToDateFormatDefault;

public class SlavesListAdapter extends BaseAdapter {
    private ArrayList<Slaves> slavesList;
    private LayoutInflater slavesInflater;

    public SlavesListAdapter( Activity activity )
    {
        super();
        slavesList = new ArrayList();
        slavesInflater = activity.getLayoutInflater();
    }

    // 子機リストへの追加
    public void addSlaves(Slaves slaves){
        if( !slavesList.contains( slaves ) )
        {    // 加えられていなければ加える
            slavesList.add( slaves );
            notifyDataSetChanged();    // ListViewの更新
        }
    }

    // 子機リストのクリア
    public void clearSlaves(){
        slavesList.clear();
        notifyDataSetChanged();    // ListViewの更新
    }

    // 子機リストのソート
    public void sortSlaves(){
        Comparator<Slaves> slavesComparator = new Comparator<Slaves>() {
            @Override
            public int compare(Slaves o1, Slaves o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        Collections.sort(slavesList, slavesComparator);
        notifyDataSetChanged();    // ListViewの更新
    }

    @Override
    public int getCount()
    {
        return slavesList.size();
    }

    @Override
    public Object getItem( int position )
    {
        return slavesList.get( position );
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
        SlavesListAdapter.ViewHolder viewHolder;
        // General ListView optimization code.
        if( null == convertView )
        {
            convertView = slavesInflater.inflate( R.layout.amount_view_listitem, parent, false );
            viewHolder = new SlavesListAdapter.ViewHolder();
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
            viewHolder = (SlavesListAdapter.ViewHolder)convertView.getTag();
        }

        Slaves slaves     = slavesList.get( position );

        // 子機CID（非表示）と登録名
        viewHolder.viewSlaveId.setText( slaves.getSId() );
        String slaveName = slaves.getName();
        if( null != slaveName && 0 < slaveName.length() )
        {
            viewHolder.viewSlaveName.setText( slaveName );
        }
        else
        {
            viewHolder.viewSlaveName.setText( R.string.unknown_slave );
        }

        // 残量などの計算
        viewHolder.viewSlaveAmountBar.setMax((int) (slaves.getNotificationAmount() * 5000.0));
        int amount = (int) (slaves.getAmount() * 1000.0);
        int notification = (int) (slaves.getNotificationAmount() * 1000.0);
        viewHolder.viewSlaveAmountBar.setSecondaryProgress( amount );
        viewHolder.viewSlaveAmountBar.setProgress( notification );
        // 残量の表示
        if(amount < 2000){
            viewHolder.viewSlaveAmountValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_now_value, (amount)) );
        }
        else {
            viewHolder.viewSlaveAmountValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_now_over) );
        }
        // 通知量までの表示
        viewHolder.viewSlaveMarginValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_margin_value, (amount - notification)) );
        // 通知量の表示
        viewHolder.viewSlaveNotificationValue.setText( MyApplication.getContext().getResources().getString(R.string.amount_notification_value, notification) );

        // 最終更新日時
        viewHolder.viewSlaveLastUpdateValue.setText( MyApplication.getContext().getResources().getString( R.string.amount_date_value, convertLongToDateFormatDefault(slaves.getLastUpdate())) );

        // 色の変更
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
}
