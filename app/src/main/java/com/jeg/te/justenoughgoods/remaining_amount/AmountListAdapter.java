package com.jeg.te.justenoughgoods.remaining_amount;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jeg.te.justenoughgoods.MyApplication;
import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.Slave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.jeg.te.justenoughgoods.Utilities.convertLongToDateFormatDefault;

public class AmountListAdapter extends BaseAdapter {
    private ArrayList<Slave> slaveList;
    private LayoutInflater slavesInflater;

    public AmountListAdapter(Activity activity )
    {
        super();
        slaveList = new ArrayList<>();
        slavesInflater = activity.getLayoutInflater();
    }

    // 子機リストへの追加
    public void addSlaves(Slave slave){
        if( !slaveList.contains(slave) )
        {    // 加えられていなければ加える
            slaveList.add(slave);
            notifyDataSetChanged();    // ListViewの更新
        }
    }

    // 子機リストのクリア
    public void clearSlaves(){
        slaveList.clear();
        notifyDataSetChanged();    // ListViewの更新
    }

    // 子機リストのソート
    public void sortSlaves(){
        Comparator<Slave> slavesComparator = new Comparator<Slave>() {
            @Override
            public int compare(Slave o1, Slave o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        Collections.sort(slaveList, slavesComparator);
        notifyDataSetChanged();    // ListViewの更新
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
        AmountListAdapter.ViewHolder viewHolder;
        // General ListView optimization code.
        if( null == convertView )
        {
            convertView = slavesInflater.inflate( R.layout.amount_view_listitem, parent, false );
            viewHolder = new AmountListAdapter.ViewHolder();
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
            viewHolder = (AmountListAdapter.ViewHolder)convertView.getTag();
        }

        Slave slave = slaveList.get( position );

        // 子機CID（非表示）と登録名
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

        // 残量などの計算
        viewHolder.viewSlaveAmountBar.setMax((int) (slave.getNotificationAmount() * 5000.0));
        int amount = (int) (slave.getAmount() * 1000.0);
        int notification = (int) (slave.getNotificationAmount() * 1000.0);

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
        viewHolder.viewSlaveLastUpdateValue.setText( MyApplication.getContext().getResources().getString( R.string.amount_date_value, convertLongToDateFormatDefault(slave.getLastUpdate())) );

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

    // 不足しているリストを取得
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
