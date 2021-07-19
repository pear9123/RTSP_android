package com.bae.message.sqlite;

import android.provider.BaseColumns;

public class CameraContract {

    private CameraContract() {

    }

    public static class CameraEntry implements BaseColumns {
        public static final String TABLE_NAME = "camera";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENTS = "contents";
        public static final String COLUMN_NAME_IP = "ip";
        public static final String COLUMN_NAME_PORT = "port";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PW = "pw";
    }
}
