package com.jeg.te.justenoughgoods;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utilities {

    /**
     * 日付データの処理
     */
    static DateFormat dateFormatDefault = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    /**
     * Long の数字を日付フォーマットに変換。
     */
    public static String convertLongToDateFormatDefault(Long date) {
        return dateFormatDefault.format(new Date(date));
    }

    /**
     * 取得した日時データ(Long)で現在時からの差を計算
     */
    public static long getDifferenceFromNow(Long measurementDateTime){
        long nowDateTime = System.currentTimeMillis(); // 現在日時のミリ秒

        // 現在日時との差を計算し、分に変換
        return TimeUnit.MILLISECONDS.toMinutes(measurementDateTime - nowDateTime);
    }

    /**
     * 受け取った時間（分）をミリ秒に変換して日付フォーマットへ
     */
    public static String convertMinutesToDate(long minutes){
        long dateTimeMillis = TimeUnit.MINUTES.toMillis(minutes);
        long datetime = System.currentTimeMillis() + dateTimeMillis; // 現在日時のミリ秒

        return convertLongToDateFormatDefault(datetime);
    }
}
