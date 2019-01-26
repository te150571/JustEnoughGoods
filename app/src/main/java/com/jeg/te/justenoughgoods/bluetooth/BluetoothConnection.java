package com.jeg.te.justenoughgoods.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.jeg.te.justenoughgoods.utilities.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class BluetoothConnection {

    private static BluetoothConnection bluetoothConnection = new BluetoothConnection();

    // スキャンしたBluetooth機器リスト
    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    // 受信した計測データリスト
    private ArrayList<String> measurementData = new ArrayList<>();
    // 計測データ受信中フラグ
    private boolean receivingData = false;
    private boolean updatable = false;

    //Bluetoothのなんやかんや
    static public class BluetoothService
    {
        // 定数（Bluetooth UUID）
        private static final UUID UUID_SPP = UUID.fromString( "00001101-0000-1000-8000-00805f9b34fb" );

        // 定数
        public static final int MESSAGE_STATE_CHANGE = 1;
        public static final int MESSAGE_READ           = 2;
        public static final int MESSAGE_WRITTEN        = 3;
        public static final int STATE_NONE             = 0;
        public static final int STATE_CONNECT_START    = 1;
        public static final int STATE_CONNECT_FAILED   = 2;
        public static final int STATE_CONNECTED        = 3;
        public static final int STATE_CONNECTION_LOST  = 4;
        public static final int STATE_DISCONNECT_START = 5;
        public static final int STATE_DISCONNECTED     = 6;

        // メンバー変数
        private int mState;
        private ConnectionThread mConnectionThread;
        private Handler mHandler;

        // 接続時処理用のスレッド
        private class ConnectionThread extends Thread
        {
            private BluetoothSocket mBluetoothSocket;
            private InputStream mInput;
            private OutputStream mOutput;

            // コンストラクタ
            public ConnectionThread( BluetoothDevice bluetoothdevice )
            {
                try
                {
                    mBluetoothSocket = bluetoothdevice.createRfcommSocketToServiceRecord( UUID_SPP );
                    mInput = mBluetoothSocket.getInputStream();
                    mOutput = mBluetoothSocket.getOutputStream();
                }
                catch( IOException e )
                {
                    Log.w("Failed", "Connection Error.");
                }
            }

            // 処理
            public void run()
            {
                int availableBytes = 0;
                int bytes;

                while( STATE_DISCONNECTED != mState )
                {
                    switch( mState )
                    {
                        case STATE_NONE:
                            break;
                        case STATE_CONNECT_START:    // 接続開始
                            try
                            {
                                // BluetoothSocketオブジェクトを用いて、Bluetoothデバイスに接続を試みる。
                                mBluetoothSocket.connect();
                            }
                            catch( IOException e )
                            {    // 接続失敗
                                Log.w("Failed", "mBluetoothSocket.connect()");
                                setState( STATE_CONNECT_FAILED );
                                cancel();    // スレッド終了。
                                return;
                            }
                            // 接続成功
                            setState( STATE_CONNECTED );
                            break;
                        case STATE_CONNECT_FAILED:        // 接続失敗
                            // 接続失敗時の処理の実体は、cancel()。
                            break;
                        case STATE_CONNECTED:        // 接続済み（Bluetoothデバイスから送信されるデータ受信）
                            try
                            {
                                availableBytes = mInput.available();
                                if(availableBytes > 0){
                                    byte[] buf = new byte[availableBytes];
                                    bytes = mInput.read( buf );
                                    mHandler.obtainMessage( MESSAGE_READ, bytes, -1, buf ).sendToTarget();
                                }
                                else SystemClock.sleep(500);
                            }
                            catch( IOException e )
                            {
                                setState( STATE_CONNECTION_LOST );
                                cancel();    // スレッド終了。
                                break;
                            }
                            break;
                        case STATE_CONNECTION_LOST:    // 接続ロスト
                            // 接続ロスト時の処理の実体は、cancel()。
                            break;
                        case STATE_DISCONNECT_START:    // 切断開始
                            // 切断開始時の処理の実体は、cancel()。
                            break;
                        case STATE_DISCONNECTED:    // 切断完了
                            // whileの条件式により、STATE_DISCONNECTEDの場合は、whileを抜けるので、このケース分岐は無意味。
                            break;
                    }
                }
                synchronized( this )
                {    // 親クラスが保持する自スレッドオブジェクトの解放（自分自身の解放）
                    mConnectionThread = null;
                }
            }

            // キャンセル（接続を終了する。ステータスをSTATE_DISCONNECTEDにすることによってスレッドも終了する）
            public void cancel()
            {
                try
                {
                    mBluetoothSocket.close();
                }
                catch( IOException e )
                {
                    Log.w("Failed", "mBluetoothSocket.close()");
                }
                setState( STATE_DISCONNECTED );
            }

            // バイト列送信
            public void write( byte[] buf )
            {
                try
                {
                    synchronized( BluetoothService.this )
                    {
                        mOutput.write( buf );
                    }
                    mHandler.obtainMessage( MESSAGE_WRITTEN ).sendToTarget();
                }
                catch( IOException e )
                {
                    Log.w("Failed", "mBluetoothSocket.close()");
                }
            }

        }

        // コンストラクタ
        public BluetoothService(Context context, Handler handler, BluetoothDevice device )
        {
            mHandler = handler;
            mState = STATE_NONE;

            // 接続時処理用スレッドの作成と開始
            mConnectionThread = new ConnectionThread( device );
            mConnectionThread.start();
        }

        // ステータス設定
        private synchronized void setState( int state )
        {
            mState = state;
            mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1 ).sendToTarget();
        }

        // 接続開始時の処理
        public synchronized void connect()
        {
            if( STATE_NONE != mState )
            {   // １つのBluetoothServiceオブジェクトに対して、connect()は１回だけ呼べる。
                // ２回目以降の呼び出しは、処理しない。
                return;
            }

            // ステータス設定
            setState( STATE_CONNECT_START );
        }

        // 接続切断時の処理
        public synchronized void disconnect()
        {
            if( STATE_CONNECTED != mState )
            {    // 接続中以外は、処理しない。
                return;
            }

            // ステータス設定
            setState( STATE_DISCONNECT_START );

            mConnectionThread.cancel();
        }

        // バイト列送信（非同期）
        public void write( byte[] out )
        {
            ConnectionThread connectionThread;
            synchronized( this )
            {
                if( STATE_CONNECTED != mState )
                {
                    return;
                }
                connectionThread = mConnectionThread;
            }
            // 非同期送信
            // （送受信で同期（送信と受信を排他処理（≒同期処理））させる実装も可能だが、
            // 　そうすると、mInput.read( buf ) が完了するまで、mOutput.write( buf ) が実施されなくなる。
            // 　mInput.read( buf ) は文字列を受信すると完了するので、文字列を受信しなければいつまでたっても完了しない。
            // 　文字列が頻繁に送信されてくる場合はよいが、文字列がぜんぜん送信されてこない場合は、
            // 　こちらからの送信がいつまでたっても実施されないことになる。なので、受信と送信は非同期。）
            connectionThread.write( out );
        }
    }

    // ブロードキャストレシーバー
    private final BroadcastReceiver blueBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent )
        {
            String action = intent.getAction();

            // Bluetooth端末発見
            if( BluetoothDevice.ACTION_FOUND.equals( action ) )
            {
                final BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
                bluetoothDevices.add(device);
                return;
            }
            // Bluetooth端末検索終了
            if( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals( action ) )
            {
                scanning = false;
                return;
            }
        }
    };

    // 定数
    private static final int READ_BUFFER_SIZE = 1024;    // 受信バッファーのサイズ

    // メンバー変数
    private BluetoothAdapter mBluetoothAdapter;    // BluetoothAdapter : Bluetooth処理で必要
    private BluetoothService mBluetoothService;    // BluetoothService : Bluetoothデバイスとの通信処理を担う
    private byte[] mReadBuffer        = new byte[READ_BUFFER_SIZE];
    private int    mReadBufferCounter = 0;

    private boolean scanning = false;                // スキャン中かどうか

    // Bluetoothサービスから情報を取得するハンドラ
    private final Handler mHandler = new Handler()
    {
        // ハンドルメッセージ
        // UIスレッドの処理なので、UI処理について、runOnUiThread対応は、不要。
        @Override
        public void handleMessage( Message msg )
        {
            switch( msg.what )
            {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch( msg.arg1 )
                    {
                        case BluetoothService.STATE_NONE:            // 未接続
                            break;
                        case BluetoothService.STATE_CONNECT_START:        // 接続開始
                            break;
                        case BluetoothService.STATE_CONNECT_FAILED:            // 接続失敗
                            break;
                        case BluetoothService.STATE_CONNECTED:    // 接続完了
                            break;
                        case BluetoothService.STATE_CONNECTION_LOST:            // 接続ロスト
                            //Toast.makeText( MainActivity.this, "Lost connection to the device.", Toast.LENGTH_SHORT ).show();
                            break;
                        case BluetoothService.STATE_DISCONNECT_START:    // 切断開始
                            break;
                        case BluetoothService.STATE_DISCONNECTED:            // 切断完了
                            mBluetoothService = null;    // BluetoothServiceオブジェクトの解放
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_READ:
                    byte[] aByteRead = (byte[])msg.obj;
                    int iCountBuf = msg.arg1;
                    for( int i = 0; i < iCountBuf; i++ )
                    {
                        byte c = aByteRead[i];
                        if( c == 10 )
                        {    // 終端
                            mReadBuffer[mReadBufferCounter] = '\0';
                            dataRead(new String( mReadBuffer, 0, mReadBufferCounter));
                            mReadBufferCounter = 0;
                        }
                        else if( '\n' == c )
                        {
                            ;    // 何もしない
                        }
                        else
                        {    // 途中
                            if( ( READ_BUFFER_SIZE - 1 ) > mReadBufferCounter )
                            {    // mReadBuffer[READ_BUFFER_SIZE - 2] までOK。
                                // mReadBuffer[READ_BUFFER_SIZE - 1] は、バッファー境界内だが、「\0」を入れられなくなるのでNG。
                                mReadBuffer[mReadBufferCounter] = c;
                                mReadBufferCounter++;
                            }
                            else
                            {    // バッファーあふれ。初期化
                                mReadBufferCounter = 0;
                            }
                        }
                    }
                    break;
                case BluetoothService.MESSAGE_WRITTEN:
                    break;
            }
        }
    };

    // コンストラクタ
    private BluetoothConnection(){
        // Bluetoothアダプタの取得
        BluetoothManager bluetoothManager = (BluetoothManager)MyApplication.getContext().getSystemService( Context.BLUETOOTH_SERVICE );
        if( bluetoothManager != null)
            mBluetoothAdapter = bluetoothManager.getAdapter();

        if(mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
    }

    // インスタンスを取得する
    public static BluetoothConnection getBluetoothConnection(){
        return bluetoothConnection;
    }

    // デバイスがBluetoothに対応しているか
    public BluetoothAdapter checkBluetoothSupport(){
        return mBluetoothAdapter;
    }

    // デバイスがBluetoothオンになっているか
    public boolean checkBluetoothEnable(){
        return mBluetoothAdapter.isEnabled();
    }

    // 機器スキャン中か
    public boolean checkScanning(){
        return scanning;
    }

    // 機器が更新を受信中か
    public boolean checkReceiving(){
        return receivingData;
    }

    // 更新データがあるか
    public boolean checkUpdatable(){
        return updatable;
    }

    // 親機のアドレスを取得
    public String getRaspberryAddress(){
        // アプリのデータから親機の情報を取得する
        SharedPreferences data = MyApplication.getContext().getSharedPreferences("jegAppData", MODE_PRIVATE);
        return data.getString("raspberryAddress", "");
    }

    // 検索結果のリストを返す
    public ArrayList<BluetoothDevice> getBluetoothDevices(){
        return bluetoothDevices;
    }

    // データを返す
    public ArrayList<String> getUpdateData(){
        updatable = false;
        return measurementData;
    }

    // 機器スキャン開始
    public void startDiscovery(){
        if(!scanning){
            // ブロードキャストレシーバーの登録
            MyApplication.getContext().registerReceiver(blueBroadcastReceiver, new IntentFilter( BluetoothDevice.ACTION_FOUND ) );
            MyApplication.getContext().registerReceiver(blueBroadcastReceiver, new IntentFilter( BluetoothAdapter.ACTION_DISCOVERY_FINISHED ) );

            startScan();
        }
    }

    // 機器スキャン停止
    public void stopDiscovery(){
        if(scanning){
            // ブロードキャストレシーバーの登録解除
            MyApplication.getContext().unregisterReceiver(blueBroadcastReceiver);

            stopScan();
        }
    }

    // スキャンの開始
    private void startScan(){
        scanning = true;
        if (bluetoothDevices != null)
            bluetoothDevices.clear();
        mBluetoothAdapter.startDiscovery();    // 約 12 秒間の問い合わせのスキャンが行われる
    }

    // スキャンの停止
    private void stopScan(){
        scanning = false;
        mBluetoothAdapter.cancelDiscovery();
    }

    // 接続処理
    public void connect()
    {
        String deviceAddress = getRaspberryAddress();

        if( deviceAddress.equals( "" ) )
        {    // DeviceAddressが空の場合は処理しない
            return;
        }

        if( null != mBluetoothService )
        {    // mBluetoothServiceがnullでないなら接続済みか、接続中。
            return;
        }

        // 接続
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice( deviceAddress );
        mBluetoothService = new BluetoothService( null, mHandler, device );
        mBluetoothService.connect();

        measurementData.clear();
    }

    // 切断処理
    public void disconnect()
    {
        if( null == mBluetoothService )
        {    // mBluetoothServiceがnullなら切断済みか、切断中。
            return;
        }

        write("0");

        // 切断
        mBluetoothService.disconnect();
        mBluetoothService = null;
    }

    // 文字列受信処理
    private void dataRead(String received){
        Log.d("RECEIVED String", received);
        if(received.equals("1")){
            receivingData = true;
            updatable = true;
            measurementData.clear();
        }

        if(receivingData){
            if(received.equals("0")){
                receivingData = false;
            }
            else{
                measurementData.add(received);
            }
        }
    }

    // 文字列送信
    public void write( String string )
    {
        if( null == mBluetoothService )
        {    // mBluetoothServiceがnullなら切断済みか、切断中。
            return;
        }

        // 終端に改行コードを付加
        String stringSend = string + "\r\n";

        // バイト列送信
        mBluetoothService.write( stringSend.getBytes() );
    }
}
