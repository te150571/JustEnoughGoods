package com.jeg.te.justenoughgoods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AmountCheckActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトル設定
        setTitle(R.string.app_name);
        // メイン画面表示
        displayCheckAmount();
    }

    // オプションメニュー作成時の処理
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.main_menu, menu );
        return true;
    }

    // オプションメニューのアイテム選択時の処理
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case R.id.menu_item_log:
                Intent intent = new Intent(getApplication(), LogViewActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_amount_config:
                intent = new Intent(getApplication(), AmountConfigurationActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_slaves_config:
                intent = new Intent(getApplication(), SlavesConfigurationActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_fail_config:
                intent = new Intent(getApplication(), FailConfigurationActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_raspberry_config:
                intent = new Intent(getApplication(), RaspberryConfigurationActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_init_config:
                //intent = new Intent(getApplication(), LogViewActivity.class);
                //startActivity(intent);
                return true;
        }
        return false;
    }

    /*
        以下 画面遷移処理
     */
    // 残量画面表示
    private void displayCheckAmount(){
        setContentView(R.layout.amount_view);

        // GUIアイテム設定
    }
}



