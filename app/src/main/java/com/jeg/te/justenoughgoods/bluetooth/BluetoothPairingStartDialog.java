package com.jeg.te.justenoughgoods.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.jeg.te.justenoughgoods.amount.AmountViewActivity;
import com.jeg.te.justenoughgoods.R;

public class BluetoothPairingStartDialog extends DialogFragment {
    // ダイアログが生成された時に呼ばれるメソッド
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // ダイアログ生成  AlertDialogのBuilderクラスを指定してインスタンス化
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        // タイトル設定
        dialogBuilder.setTitle(R.string.bluetooth_pairing_start_title);
        // 表示する文章設定
        dialogBuilder.setMessage(R.string.bluetooth_pairing_start_text);

        // ボタン作成(はい)
        dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AmountViewActivity amountViewActivity = (AmountViewActivity) getActivity();
                amountViewActivity.showBluetoothDeviceListActivity();
            }
        });

        // ボタン作成(いいえ)
        dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 何もしないで閉じる
            }
        });

        // dialogBuilderを返す
        return dialogBuilder.create();
    }
}
