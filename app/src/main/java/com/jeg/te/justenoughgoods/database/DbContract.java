package com.jeg.te.justenoughgoods.database;

import android.provider.BaseColumns;

public final class DbContract {
    private DbContract() {}

    // テーブル名、列名の定数定義
    // 子機管理テーブル
    public static class SlavesTable implements BaseColumns {
        public static final String TABLE_NAME = "slaves";
        public static final String S_ID = "s_id";
        public static final String NAME = "name";
        public static final String NOTIFICATION_AMOUNT = "notification_amount";
        public static final String AMOUNT_NOTIFICATION_ENABLE = "amount_notification_enable";
        public static final String EXCEPTION_FLAG = "exception_flag";
        public static final String EXCEPTION_NOTIFICATION_ENABLE = "exception_notification_enable";
    }

    // 計測データテーブル
    public static class MeasurementDataTable implements BaseColumns {
        public static final String TABLE_NAME = "m_data";
        public static final String _ID = "id";
        public static final String S_ID = "s_id";
        public static final String AMOUNT = "amount";
        public static final String DATETIME = "datetime";
        public static final String MONTH_NUM = "month_num";
    }
}
