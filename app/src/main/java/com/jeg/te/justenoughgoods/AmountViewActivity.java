package com.jeg.te.justenoughgoods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

public class AmountViewActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    private SlavesListAdapter slavesListAdapter;    // リストビューの内容

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
            case R.id.menu_item_raspberry_config:
                Intent intent = new Intent(getApplication(), RaspberryConfigurationActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_init_config:
                //intent = new Intent(getApplication(), LogViewActivity.class);
                //startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
        Intent slaveLogIntent = new Intent(getApplication(), LogViewActivity.class);
        startActivity(slaveLogIntent);
        return;
    }

    @Override
    public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ){
        Intent slaveConfigIntent = new Intent(getApplication(), SlaveConfigurationActivity.class);
        startActivity(slaveConfigIntent);
        return true;
    }

    // 画面表示
    private void displayCheckAmount(){
        setContentView(R.layout.amount_list);
        setActionBar((Toolbar) findViewById(R.id.toolbar_main));

        // GUIアイテム設定
        // リストビューの設定
        slavesListAdapter = new SlavesListAdapter( this ); // ビューアダプターの初期化
        ListView listView = findViewById( R.id.listView_slavesAmount);    // リストビューの取得
        listView.setAdapter( slavesListAdapter ); // リストビューにビューアダプターをセット
        listView.setOnItemClickListener( this ); // クリックリスナーオブジェクトのセット
        listView.setOnItemLongClickListener( this ); // ロングクリックリスナーオブジェクトのセット

        // 表示確認用データ
        Slaves testSlave1 = new Slaves();
        testSlave1.setCId("TestCID1");
        testSlave1.setName("醤油（DEBUG）");
        testSlave1.setAmount(new Double(0.52345));
        testSlave1.setNotificationAmount(new Double(0.2));
        testSlave1.setRecentDate("2018/12/20");
        slavesListAdapter.addSlaves(testSlave1);
        Slaves testSlave2 = new Slaves();
        testSlave2.setCId("TestCID2");
        testSlave2.setName("酢（DEBUG）");
        testSlave2.setAmount(new Double(0.32345));
        testSlave2.setNotificationAmount(new Double(0.1));
        testSlave2.setRecentDate("2018/12/20");
        slavesListAdapter.addSlaves(testSlave2);
        Slaves testSlave3 = new Slaves();
        testSlave3.setCId("TestCID3");
        testSlave3.setName("サラダ油（DEBUG）");
        testSlave3.setAmount(new Double(2.0));
        testSlave3.setNotificationAmount(new Double(0.5));
        testSlave3.setRecentDate("2018/12/20");
        slavesListAdapter.addSlaves(testSlave3);
        Slaves testSlave4 = new Slaves();
        testSlave4.setCId("TestCID4");
        testSlave4.setName("シャンプー（DEBUG）");
        testSlave4.setAmount(new Double(0.45));
        testSlave4.setNotificationAmount(new Double(0.5));
        testSlave4.setRecentDate("2018/12/20");
        slavesListAdapter.addSlaves(testSlave4);
    }
}



