package com.jeg.te.justenoughgoods;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // メニュー表示
        dispMenu();
    }

    public void dispMenu(){
        setContentView(R.layout.menu);
        setTitle("メニュー");

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispChargeCheck();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispMonthLog();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispConf();
            }
        });
    }

    public void dispChargeCheck(){
        setContentView(R.layout.chargecheck);
        setTitle("残量表示");

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispMenu();
            }
        });
    }

    public void dispMonthLog(){
        setContentView(R.layout.monthlog);
        setTitle("月間ログ表示");

        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispDay3Log();
            }
        });

        findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispYearLog();
            }
        });

        findViewById(R.id.button9).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispMenu();
            }
        });
    }

    public void dispDay3Log(){
        setContentView(R.layout.day3log);
        setTitle("直近ログ表示");

        findViewById(R.id.button12).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispMonthLog();
            }
        });

        findViewById(R.id.button13).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispYearLog();
            }
        });

        findViewById(R.id.button14).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispMenu();
            }
        });
    }

    public void dispYearLog(){
        setContentView(R.layout.yearlog);
        setTitle("年間ログ表示");

        findViewById(R.id.button15).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispDay3Log();
            }
        });

        findViewById(R.id.button16).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispMonthLog();
            }
        });

        findViewById(R.id.button17).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispMenu();
            }
        });
    }

    public void dispConf(){
        setContentView(R.layout.conf);
        setTitle("設定");

        findViewById(R.id.button18).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispLineConf();
            }
        });

        findViewById(R.id.button19).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispAbnormalConf();
            }
        });

        findViewById(R.id.button20).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispOyakoConf();
            }
        });

       /* findViewById(R.id.button21).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispReset();
            }
        }); */

        findViewById(R.id.button22).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispMenu();
            }
        });
    }

    public void dispLineConf(){
        setContentView(R.layout.lineconf);
        setTitle("残量通知ライン設定");

        findViewById(R.id.button23).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispConf();
            }
        });

        findViewById(R.id.button24).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispConf();
            }
        });
    }

    public void dispAbnormalConf(){
        setContentView(R.layout.abnormalconf);
        setTitle("異常通知ライン設定");

        findViewById(R.id.button25).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispConf();
            }
        });

        findViewById(R.id.button26).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispConf();
            }
        });
    }

    public void dispOyakoConf(){
        setContentView(R.layout.oyakoconf);
        setTitle("親機子機設定");

        findViewById(R.id.button27).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispMeasurementConf();
            }
        });

        findViewById(R.id.button28).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispLabelName();
            }
        });

        findViewById(R.id.button29).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispKoaddConf();
            }
        });

       findViewById(R.id.button30).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispConf();
            }
        });

        findViewById(R.id.button31).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispMenu();
            }
        });
    }

    public void dispMeasurementConf(){
        setContentView(R.layout.measurementconf);
        setTitle("計測間隔設定");

        findViewById(R.id.button32).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispOyakoConf();
            }
        });

        findViewById(R.id.button33).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispOyakoConf();
            }
        });
    }

    public void dispLabelName(){
        setContentView(R.layout.labelname);
        setTitle("子機ラベル名称設定");

        findViewById(R.id.button34).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispOyakoConf();
            }
        });

        findViewById(R.id.button35).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispOyakoConf();
            }
        });
    }

    public void dispKoaddConf(){
        setContentView(R.layout.koaddconf);
        setTitle("子機追加");

        findViewById(R.id.button36).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispOyakoConf();
            }
        });

        findViewById(R.id.button37).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispOyakoConf();
            }
        });
    }
}



