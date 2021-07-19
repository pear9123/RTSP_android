package com.bae.message.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CameraDbHelper extends SQLiteOpenHelper {
    private static CameraDbHelper sInstance;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Camera.db";
    private static final String SQL_CREATE_ENTRIES =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    CameraContract.CameraEntry.TABLE_NAME,
                    CameraContract.CameraEntry._ID,
                    CameraContract.CameraEntry.COLUMN_NAME_TITLE,
                    CameraContract.CameraEntry.COLUMN_NAME_CONTENTS,
                    CameraContract.CameraEntry.COLUMN_NAME_IP,
                    CameraContract.CameraEntry.COLUMN_NAME_PORT,
                    CameraContract.CameraEntry.COLUMN_NAME_ID,
                    CameraContract.CameraEntry.COLUMN_NAME_PW);

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CameraContract.CameraEntry.TABLE_NAME;

    public static CameraDbHelper getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new CameraDbHelper(context);
        }
        return sInstance;
    }

    public CameraDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
