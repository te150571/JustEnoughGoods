package com.jeg.te.justenoughgoods;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class FailConfigurationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトル設定
        setTitle(R.string.app_name_fail_config);
        // 設定画面表示
        displayFailConfig();
    }

    /*
        画面遷移とコンポネント設定処理
     */
    // 子機異常検知設定表示
    public void displayFailConfig(){
        setContentView(R.layout.config_fail);

        // 設定完了ボタン（処理後の遷移はなし）
        findViewById(R.id.bt_failDone).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //displayConfig();
            }
        });

        // （設定キャンセルして）戻るボタン
        findViewById(R.id.bt_failCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
