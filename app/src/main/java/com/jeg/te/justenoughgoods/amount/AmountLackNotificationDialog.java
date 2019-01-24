package com.jeg.te.justenoughgoods.amount;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.jeg.te.justenoughgoods.R;

import java.util.ArrayList;

public class AmountLackNotificationDialog extends DialogFragment {
    // ダイアログが生成された時に呼ばれるメソッド
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // ダイアログ生成  AlertDialogのBuilderクラスを指定してインスタンス化
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        // タイトル設定
        dialogBuilder.setTitle(R.string.amount_lack_notification_title);

        // 不足デバイスリストを取得
        AmountViewActivity amountViewActivity = (AmountViewActivity) getActivity();
        ArrayList<String> lacks = amountViewActivity.getLacks();
        String text = new String();
        for(String value : lacks){
            text += value + "\n";
        }

        // 表示する文章設定
        dialogBuilder.setMessage(
                text + "\n" + getString(R.string.amount_lack_notification_text)
        );

        // ボタン作成(確認)
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // dialogBuilderを返す
        return dialogBuilder.create();
    }
}
