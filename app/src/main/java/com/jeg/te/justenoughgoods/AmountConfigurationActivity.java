package com.jeg.te.justenoughgoods;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class AmountConfigurationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトル設定
        setTitle(R.string.app_name_amount_config);
        // 設定画面表示
        displayAmountNotificationConfig();
    }

    /*
        画面遷移とコンポネント設定処理
     */
    // 残量通知設定表示
    public void displayAmountNotificationConfig(){
        setContentView(R.layout.config_amount_notification);

        // 設定完了ボタン（処理後の遷移はなし）
        findViewById(R.id.bt_amountNotifiDone).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //displayConfig();
            }
        });

        // （設定キャンセルして）戻るボタン
        findViewById(R.id.bt_amountNotifiCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
